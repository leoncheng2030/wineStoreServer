/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.dev.modular.wechatsubscription.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.modular.subscriptiontemplate.entity.WechatSubscriptionTemplate;
import vip.xiaonuo.dev.modular.subscriptiontemplate.service.WechatSubscriptionTemplateService;
import vip.xiaonuo.dev.modular.usersubscription.entity.WechatUserSubscription;
import vip.xiaonuo.dev.modular.wechatsubscription.param.WechatSubscriptionSendParam;
import vip.xiaonuo.dev.modular.wechatsubscription.service.WechatSubscriptionService;
import vip.xiaonuo.dev.modular.wechatsubscription.util.WechatSubscriptionUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import vip.xiaonuo.wine.api.WineDeviceApi;
import vip.xiaonuo.wine.api.WineStoreAPI;
import vip.xiaonuo.dev.modular.usersubscription.service.WechatUserSubscriptionService;

import java.util.Date;
import java.util.Map;

/**
 * 微信订阅消息服务实现类
 *
 * @author xuyuxiang
 * @date 2024/12/25 10:40
 **/
@Slf4j
@Service
public class WechatSubscriptionServiceImpl implements WechatSubscriptionService {

    @Resource
    private WechatSubscriptionTemplateService wechatSubscriptionTemplateService;
    
    @Resource
    private WineDeviceApi wineDeviceApi;
    
    @Resource
    private WineStoreAPI wineStoreApi;
    
    @Resource
    private WechatUserSubscriptionService wechatUserSubscriptionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String sendSubscriptionMessage(WechatSubscriptionSendParam param) {
        // 参数验证
        validateSendParam(param);

        try {
            // 检查用户订阅状态
            if (!checkUserSubscription(param.getOpenid(), param.getTemplateId())) {
                log.warn("用户未订阅该模板消息 - openid: {}, templateId: {}", param.getOpenid(), param.getTemplateId());
                return "用户未订阅该模板消息";
            }

            // 构建消息数据
            Map<String, WxMaSubscribeMessage.MsgData> messageData = 
                WechatSubscriptionUtil.buildCustomMessageData(param.getData());

            // 发送消息
            String result = WechatSubscriptionUtil.sendSubscriptionMessage(
                param.getOpenid(),
                param.getTemplateId(),
                messageData,
                param.getPage(),
                param.getMiniprogramState()
            );

            // 减少订阅次数
            decreaseSubscriptionCount(param.getOpenid(), param.getTemplateId());

            // 记录发送日志
            saveSendLog(param, "1", result, null, null);
            
            // 更新用户订阅发送记录
            wechatUserSubscriptionService.updateSendRecord(param.getOpenid(), param.getTemplateId(), 
                    param.getBusinessType(), param.getBusinessId());

            log.info("订阅消息发送成功 - openid: {}, templateId: {}", param.getOpenid(), param.getTemplateId());
            return result;

        } catch (Exception e) {
            // 记录失败日志
            saveSendLog(param, "0", null, "SEND_ERROR", e.getMessage());
            
            log.error("订阅消息发送失败 - openid: {}, templateId: {}", param.getOpenid(), param.getTemplateId(), e);
            throw new CommonException("订阅消息发送失败: " + e.getMessage());
        }
    }

    @Override
    public String sendOrderStatusNotification(String openid, String orderSn, String orderStatus, 
                                            String orderAmount, String orderTime) {
        // 直接获取完整的模板配置，避免重复查询
        WechatSubscriptionTemplate template = wechatSubscriptionTemplateService.lambdaQuery()
                .eq(WechatSubscriptionTemplate::getTemplateType, "ORDER_STATUS")
                .eq(WechatSubscriptionTemplate::getStatus, "ENABLE")
                .one();
        
        if (template == null) {
            log.warn("订单状态通知模板未配置或未启用");
            return "订单状态通知模板未配置或未启用";
        }

        WechatSubscriptionSendParam param = new WechatSubscriptionSendParam();
        param.setOpenid(openid);
        param.setTemplateId(template.getTemplateId());
        param.setBusinessType("ORDER_STATUS");
        param.setBusinessId(orderSn);
        
        // 只有当模板配置了jumpPage时才设置跳转页面
        if (StrUtil.isNotBlank(template.getJumpPage())) {
            // 使用占位符替换方式，支持灵活的参数配置
            // 例如：数据库配置 "/pages/order/detail?orderSn={orderSn}&status={orderStatus}"
            Map<String, String> params = Map.of(
                "orderSn", orderSn,
                "orderStatus", orderStatus,
                "orderAmount", orderAmount,
                "orderTime", orderTime
            );
            String jumpPage = replacePlaceholders(template.getJumpPage(), params);
            param.setPage(jumpPage);
            log.debug("从数据库获取订单跳转页面配置并替换参数 - jumpPage: {}", jumpPage);
        } else {
            log.warn("模板未配置跳转页面，消息将不包含跳转链接 - templateType: ORDER_STATUS");
        }

        // 构建订单状态消息数据
        Map<String, String> data = Map.of(
            "thing1", "订单状态更新",
            "character_string2", orderSn,
            "thing3", orderStatus,
            "amount4", orderAmount,
            "time5", orderTime
        );
        param.setData(data);

        return sendSubscriptionMessage(param);
    }

    @Override
    public String sendStockAlertNotification(String openid, String deviceId, String currentStock,
                                           String warningLevel, String warningTime) {
        // 直接获取完整的模板配置，避免重复查询
        WechatSubscriptionTemplate template = wechatSubscriptionTemplateService.lambdaQuery()
                .eq(WechatSubscriptionTemplate::getTemplateType, "STOCK_ALERT")
                .eq(WechatSubscriptionTemplate::getStatus, "ENABLE")
                .one();
        
        if (template == null) {
            log.warn("库存预警通知模板未配置或未启用");
            return "库存预警通知模板未配置或未启用";
        }
        
        WechatSubscriptionSendParam param = new WechatSubscriptionSendParam();
        param.setOpenid(openid);
        param.setTemplateId(template.getTemplateId());
        param.setBusinessType("STOCK_ALERT");
        param.setBusinessId(deviceId);
        
        // 从数据库获取设备真实信息
        String deviceAddress = "默认设备地址"; // 默认值
        String deviceCode = deviceId; // 默认使用设备ID
        String deviceName = "未知设备"; // 默认设备名称
        
        try {
            // 通过API根据设备ID获取设备信息
            Map<String, Object> deviceInfo = wineDeviceApi.getDeviceInfoById(deviceId);
            
            if (!deviceInfo.isEmpty()) {
                // 获取设备编码和名称
                Object deviceCodeObj = deviceInfo.get("deviceCode");
                if (deviceCodeObj != null && StrUtil.isNotBlank(deviceCodeObj.toString())) {
                    deviceCode = deviceCodeObj.toString();
                }
                
                Object deviceNameObj = deviceInfo.get("deviceName");
                if (deviceNameObj != null && StrUtil.isNotBlank(deviceNameObj.toString())) {
                    deviceName = deviceNameObj.toString();
                }
                
                // 获取门店地址信息
                Object storeIdObj = deviceInfo.get("storeId");
                if (storeIdObj != null && StrUtil.isNotBlank(storeIdObj.toString())) {
                    Map<String, Object> storeInfo = wineStoreApi.getStoreAddressById(storeIdObj.toString());
                    if (!storeInfo.isEmpty()) {
                        Object fullAddressObj = storeInfo.get("fullAddress");
                        Object storeNameObj = storeInfo.get("storeName");
                        
                        if (fullAddressObj != null && StrUtil.isNotBlank(fullAddressObj.toString())) {
                            deviceAddress = fullAddressObj.toString();
                        } else if (storeNameObj != null && StrUtil.isNotBlank(storeNameObj.toString())) {
                            deviceAddress = storeNameObj.toString(); // 如果地址为空，使用门店名称
                        }
                    }
                }
                
                log.info("获取到设备信息 - 设备ID: {}, 设备名称: {}, 设备编码: {}, 门店地址: {}", 
                        deviceId, deviceName, deviceCode, deviceAddress);
            } else {
                log.warn("未找到设备ID为 {} 的设备信息，使用默认值", deviceId);
            }
        } catch (Exception e) {
            log.error("查询设备信息失败 - 设备ID: {}", deviceId, e);
        }
        
        // 配置跳转页面，完全从数据库模板配置中获取
        if (StrUtil.isNotBlank(template.getJumpPage())) {
            // 使用占位符替换方式，支持灵活的参数配置
            // 例如：数据库配置 "/pages/device/detail?deviceId={deviceId}&storeId={storeId}"
            Map<String, String> params = Map.of(
                "deviceId", deviceId,
                "deviceCode", deviceCode,
                "deviceName", deviceName,
                "deviceAddress", deviceAddress,
                "currentStock", currentStock,
                "warningLevel", warningLevel,
                "warningTime", warningTime
            );
            String jumpPage = replacePlaceholders(template.getJumpPage(), params);
            param.setPage(jumpPage);
            log.debug("从数据库获取跳转页面配置并替换参数 - jumpPage: {}", jumpPage);
        } else {
            log.warn("模板未配置跳转页面，消息将不包含跳转链接 - templateType: STOCK_ALERT");
        }
        
        // 构建完整的库存预警消息数据
        // 根据模板定义的关键字构建数据：thing2(设备地址), thing4(商品名称), character_string7(设备ID), number5(当前库存)
        Map<String, String> data = Map.of(
            "thing2", deviceAddress, // 设备地址
            "thing4", deviceName, // 商品名称（设备名称）
            "character_string7", deviceCode, // 设备ID（使用设备编码）
            "number5", currentStock // 当前库存
        );
        param.setData(data);

        log.info("发送库存预警通知 - 设备ID: {}, 设备名称: {}, 当前库存: {}, 预警级别: {}, 预警时间: {}, 设备地址: {}, 设备编码: {}", 
                deviceId, deviceName, currentStock, warningLevel, warningTime, deviceAddress, deviceCode);

        // 发送订阅消息
        return sendSubscriptionMessage(param);
    }

    @Override
    public String sendSystemNotification(String openid, String title, String content, String notifyTime) {
        // 直接获取完整的模板配置，避免重复查询
        WechatSubscriptionTemplate template = wechatSubscriptionTemplateService.lambdaQuery()
                .eq(WechatSubscriptionTemplate::getTemplateType, "SYSTEM_NOTICE")
                .eq(WechatSubscriptionTemplate::getStatus, "ENABLE")
                .one();
        
        if (template == null) {
            log.warn("系统通知模板未配置或未启用");
            return "系统通知模板未配置或未启用";
        }

        WechatSubscriptionSendParam param = new WechatSubscriptionSendParam();
        param.setOpenid(openid);
        param.setTemplateId(template.getTemplateId());
        param.setBusinessType("SYSTEM_NOTICE");
        
        // 只有当模板配置了jumpPage时才设置跳转页面
        if (StrUtil.isNotBlank(template.getJumpPage())) {
            // 使用占位符替换方式，支持灵活的参数配置
            // 例如：数据库配置 "/pages/notice/detail?title={title}&time={notifyTime}"
            Map<String, String> params = Map.of(
                "title", title,
                "content", content,
                "notifyTime", notifyTime
            );
            String jumpPage = replacePlaceholders(template.getJumpPage(), params);
            param.setPage(jumpPage);
            log.debug("从数据库获取系统通知跳转页面配置并替换参数 - jumpPage: {}", jumpPage);
        } else {
            log.warn("模板未配置跳转页面，消息将不包含跳转链接 - templateType: SYSTEM_NOTICE");
        }

        // 构建系统通知消息数据
        Map<String, String> data = Map.of(
            "thing1", title,
            "thing2", content,
            "time3", notifyTime
        );
        param.setData(data);

        return sendSubscriptionMessage(param);
    }

    @Override
    public boolean checkUserSubscription(String openid, String templateId) {
        try {
            // 查询 WECHAT_USER_SUBSCRIPTION 表
            // 检查用户是否已授权该模板且剩余次数大于0
            WechatUserSubscription subscription = wechatUserSubscriptionService.lambdaQuery()
                    .eq(WechatUserSubscription::getOpenid, openid)
                    .eq(WechatUserSubscription::getTemplateId, templateId)
                    .eq(WechatUserSubscription::getSubscriptionStatus, "1") // 已授权
                    .one();
            
            if (subscription == null) {
                log.warn("用户未订阅该模板消息 - openid: {}, templateId: {}", openid, templateId);
                return false;
            }
            
            // 检查是否过期
            if (subscription.getExpireTime() != null && subscription.getExpireTime().before(new Date())) {
                log.warn("用户订阅已过期 - openid: {}, templateId: {}, expireTime: {}", 
                        openid, templateId, subscription.getExpireTime());
                return false;
            }
            
            // 检查剩余次数
            Integer remainingTimes = subscription.getRemainingTimes();
            if (remainingTimes != null && remainingTimes <= 0) {
                log.warn("用户订阅次数已用完 - openid: {}, templateId: {}, remainingTimes: {}", 
                        openid, templateId, remainingTimes);
                return false;
            }
            
            log.debug("用户订阅状态检查通过 - openid: {}, templateId: {}, remainingTimes: {}", 
                    openid, templateId, remainingTimes);
            return true;
            
        } catch (Exception e) {
            log.error("检查用户订阅状态失败 - openid: {}, templateId: {}", openid, templateId, e);
            return false;
        }
    }

    @Override
    public boolean decreaseSubscriptionCount(String openid, String templateId) {
        try {
            // 查找用户订阅记录
            WechatUserSubscription subscription = wechatUserSubscriptionService.lambdaQuery()
                    .eq(WechatUserSubscription::getOpenid, openid)
                    .eq(WechatUserSubscription::getTemplateId, templateId)
                    .eq(WechatUserSubscription::getSubscriptionStatus, "1") // 已授权
                    .one();
            
            if (subscription == null) {
                log.warn("未找到用户订阅记录，无法减少订阅次数 - openid: {}, templateId: {}", openid, templateId);
                return false;
            }
            
            // 更新剩余次数和最后发送时间
            Integer remainingTimes = subscription.getRemainingTimes();
            if (remainingTimes != null && remainingTimes > 0) {
                subscription.setRemainingTimes(remainingTimes - 1);
            }
            subscription.setLastSentTime(new Date());
            
            // 更新发送次数
            Integer totalSent = subscription.getTotalSent();
            subscription.setTotalSent(totalSent == null ? 1 : totalSent + 1);
            
            boolean result = wechatUserSubscriptionService.updateById(subscription);
            
            if (result) {
                log.debug("渐少用户订阅次数成功 - openid: {}, templateId: {}, remainingTimes: {} -> {}", 
                        openid, templateId, remainingTimes, subscription.getRemainingTimes());
            } else {
                log.warn("渐少用户订阅次数失败 - openid: {}, templateId: {}", openid, templateId);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("渐少用户订阅次数失败 - openid: {}, templateId: {}", openid, templateId, e);
            return false;
        }
    }

    /**
     * 参数验证
     */
    private void validateSendParam(WechatSubscriptionSendParam param) {
        if (ObjectUtil.isEmpty(param)) {
            throw new CommonException("发送参数不能为空");
        }
        
        if (!WechatSubscriptionUtil.isValidOpenid(param.getOpenid())) {
            throw new CommonException("用户openid格式无效");
        }
        
        if (!WechatSubscriptionUtil.isValidTemplateId(param.getTemplateId())) {
            throw new CommonException("模板ID格式无效");
        }
    }

    /**
     * 替换jumpPage中的占位符
     * 
     * @param jumpPage 原始跳转页面配置
     * @param params 参数键值对集合
     * @return 替换后的jumpPage
     */
    private String replacePlaceholders(String jumpPage, Map<String, String> params) {
        if (StrUtil.isBlank(jumpPage) || ObjectUtil.isEmpty(params)) {
            return jumpPage;
        }
        
        String result = jumpPage;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replace(placeholder, value);
        }
        
        log.debug("占位符替换结果 - 原始: {}, 替换后: {}", jumpPage, result);
        return result;
    }



    /**
     * 保存发送日志
     */
    private void saveSendLog(WechatSubscriptionSendParam param, String sendStatus, String result,
                           String errorCode, String errorMsg) {
        try {
            // 这里需要保存到 WECHAT_SUBSCRIPTION_SEND_LOG 表
            // TODO: 实现日志保存逻辑
            log.debug("保存订阅消息发送日志 - openid: {}, templateId: {}, status: {}", 
                    param.getOpenid(), param.getTemplateId(), sendStatus);
            
        } catch (Exception e) {
            log.error("保存订阅消息发送日志失败", e);
        }
    }
}