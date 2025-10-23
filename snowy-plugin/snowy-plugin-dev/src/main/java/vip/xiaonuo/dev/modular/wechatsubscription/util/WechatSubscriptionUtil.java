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
package vip.xiaonuo.dev.modular.wechatsubscription.util;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import vip.xiaonuo.dev.modular.subscriptionsendlog.entity.WechatSubscriptionSendLog;
import vip.xiaonuo.dev.modular.subscriptionsendlog.service.WechatSubscriptionSendLogService;
import vip.xiaonuo.dev.modular.usersubscription.service.WechatUserSubscriptionService;
import vip.xiaonuo.common.exception.CommonException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 微信小程序订阅消息工具类
 *
 * @author xuyuxiang
 * @date 2024/12/25 10:00
 **/
@Slf4j
public class WechatSubscriptionUtil {

    /**
     * 发送订阅消息（带业务参数和记录写入）
     *
     * @param openid 用户openid
     * @param templateId 模板ID
     * @param data 消息数据
     * @param page 跳转页面
     * @param miniprogramState 小程序状态（developer、trial、formal）
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param clientUserId 客户端UserId
     * @return 发送结果
     */
    public static String sendSubscriptionMessageWithLog(String openid, String templateId, 
                                                        Map<String, WxMaSubscribeMessage.MsgData> data,
                                                        String page, String miniprogramState,
                                                        String businessType, String businessId, String clientUserId) {
        
        // 获取发送记录服务
        WechatSubscriptionSendLogService sendLogService = SpringUtil.getBean(WechatSubscriptionSendLogService.class);
        
        // 检查当日是否已发送成功
        if (sendLogService != null && sendLogService.hasSentSuccessToday(openid, templateId, businessType, businessId)) {
            log.info("用户当日已发送过成功的订阅消息，跳过发送 - openid: {}, templateId: {}, businessType: {}, businessId: {}", 
                    openid, templateId, businessType, businessId);
            return "今日已发送，跳过重复发送";
        }
        
        // 创建发送记录
        WechatSubscriptionSendLog sendLog = new WechatSubscriptionSendLog();
        sendLog.setId(IdWorker.getIdStr());
        sendLog.setClientUserId(clientUserId);
        sendLog.setOpenid(openid);
        sendLog.setTemplateId(templateId);
        sendLog.setSendData(JSONUtil.toJsonStr(data));
        sendLog.setBusinessType(businessType);
        sendLog.setBusinessId(businessId);
        sendLog.setSendTime(new Date());
        
        try {
            // 获取微信小程序服务
            WxMaService wxMaService = getWxMaService();
            
            // 构建订阅消息
            WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                    .toUser(openid)
                    .templateId(templateId)
                    .page(page)
                    .miniprogramState(StrUtil.isNotBlank(miniprogramState) ? miniprogramState : "formal")
                    .build();
                    
            // 设置数据
            List<WxMaSubscribeMessage.MsgData> dataList = new java.util.ArrayList<>();
            data.forEach((key, value) -> {
                dataList.add(new WxMaSubscribeMessage.MsgData(key, value.getValue()));
            });
            subscribeMessage.setData(dataList);

            log.info("发送微信订阅消息 - openid: {}, templateId: {}, businessType: {}, businessId: {}", 
                    openid, templateId, businessType, businessId);
            log.debug("消息数据: {}", JSONUtil.toJsonStr(data));

            // 发送消息
            WxMaMsgService msgService = wxMaService.getMsgService();
            msgService.sendSubscribeMsg(subscribeMessage);
            
            // 记录成功状态
            sendLog.setSendStatus("1"); // 成功
            sendLog.setResponseData("发送成功");
            
            log.info("微信订阅消息发送成功 - openid: {}, templateId: {}, businessType: {}", 
                    openid, templateId, businessType);
            
            // 写入发送记录
            if (sendLogService != null) {
                sendLogService.save(sendLog);
                log.debug("订阅消息发送记录已写入 - logId: {}", sendLog.getId());
            }
            
            // 更新用户订阅表的发送记录
            WechatUserSubscriptionService userSubscriptionService = SpringUtil.getBean(WechatUserSubscriptionService.class);
            if (userSubscriptionService != null) {
                boolean updateResult = userSubscriptionService.updateSendRecord(openid, templateId, businessType, businessId);
                if (updateResult) {
                    log.debug("用户订阅表发送记录更新成功 - openid: {}, templateId: {}", openid, templateId);
                } else {
                    log.warn("用户订阅表发送记录更新失败，但不影响消息发送 - openid: {}, templateId: {}", openid, templateId);
                }
            }
            
            return "success";
            
        } catch (WxErrorException e) {
            // 记录失败状态
            sendLog.setSendStatus("0"); // 失败
            sendLog.setErrorCode(String.valueOf(e.getError().getErrorCode()));
            sendLog.setErrorMsg(e.getError().getErrorMsg());
            sendLog.setResponseData(JSONUtil.toJsonStr(e.getError()));
            
            String errorMsg = String.format("微信订阅消息发送失败 - openid: %s, templateId: %s, 错误码: %s, 错误信息: %s", 
                    openid, templateId, e.getError().getErrorCode(), e.getError().getErrorMsg());
            log.error(errorMsg, e);
            
            // 写入失败记录
            if (sendLogService != null) {
                sendLogService.save(sendLog);
                log.debug("订阅消息发送失败记录已写入 - logId: {}, errorCode: {}", 
                        sendLog.getId(), sendLog.getErrorCode());
            }
            
            // 根据错误码提供更具体的错误信息
            if (e.getError().getErrorCode() == 47003) {
                log.error("模板参数错误详情：{}", e.getError().getErrorMsg());
                log.error("请检查模板ID: {} 对应的字段名称和数据类型是否正确", templateId);
                throw new CommonException("模板参数不正确，请检查模板字段配置: " + e.getError().getErrorMsg());
            } else if (e.getError().getErrorCode() == 20001) {
                throw new CommonException("模板ID不存在，请检查模板配置");
            } else if (e.getError().getErrorCode() == 43101) {
                log.warn("用户订阅关系已失效（微信订阅消息一次性特性）- openid: {}, templateId: {}", openid, templateId);
                return "订阅关系已失效：微信订阅消息为一次性，发送后自动失效，需要用户重新订阅";
            }
            
            throw new CommonException(errorMsg);
        } catch (Exception e) {
            // 记录异常状态
            sendLog.setSendStatus("0"); // 失败
            sendLog.setErrorMsg(e.getMessage());
            sendLog.setResponseData("系统异常: " + e.getClass().getSimpleName());
            
            String errorMsg = String.format("微信订阅消息发送异常 - openid: %s, templateId: %s", openid, templateId);
            log.error(errorMsg, e);
            
            // 写入异常记录
            if (sendLogService != null) {
                sendLogService.save(sendLog);
                log.debug("订阅消息发送异常记录已写入 - logId: {}", sendLog.getId());
            }
            
            throw new CommonException(errorMsg);
        }
    }
    public static String sendSubscriptionMessage(String openid, String templateId, 
                                                Map<String, WxMaSubscribeMessage.MsgData> data,
                                                String page, String miniprogramState) {
        try {
            // 获取微信小程序服务
            WxMaService wxMaService = getWxMaService();
            
            // 构建订阅消息
            WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                    .toUser(openid)
                    .templateId(templateId)
                    .page(page)
                    .miniprogramState(StrUtil.isNotBlank(miniprogramState) ? miniprogramState : "formal")
                    .build();
                    
            // 设置数据
            List<WxMaSubscribeMessage.MsgData> dataList = new java.util.ArrayList<>();
            data.forEach((key, value) -> {
                dataList.add(new WxMaSubscribeMessage.MsgData(key, value.getValue()));
            });
            subscribeMessage.setData(dataList);

            log.info("发送微信订阅消息 - openid: {}, templateId: {}, page: {}", openid, templateId, page);
            log.debug("消息数据: {}", JSONUtil.toJsonStr(data));

            // 发送消息
            WxMaMsgService msgService = wxMaService.getMsgService();
            msgService.sendSubscribeMsg(subscribeMessage);
            
            log.info("微信订阅消息发送成功 - openid: {}, templateId: {}", openid, templateId);
            return "success";
            
        } catch (WxErrorException e) {
            String errorMsg = String.format("微信订阅消息发送失败 - openid: %s, templateId: %s, 错误码: %s, 错误信息: %s", 
                    openid, templateId, e.getError().getErrorCode(), e.getError().getErrorMsg());
            log.error(errorMsg, e);
            
            // 根据错误码提供更具体的错误信息
            if (e.getError().getErrorCode() == 47003) {
                log.error("模板参数错误详情：{}", e.getError().getErrorMsg());
                log.error("请检查模板ID: {} 对应的字段名称和数据类型是否正确", templateId);
                throw new CommonException("模板参数不正确，请检查模板字段配置: " + e.getError().getErrorMsg());
            } else if (e.getError().getErrorCode() == 20001) {
                throw new CommonException("模板ID不存在，请检查模板配置");
            } else if (e.getError().getErrorCode() == 43101) {
                log.warn("用户订阅关系已失效（微信订阅消息一次性特性）- openid: {}, templateId: {}", openid, templateId);
                return "订阅关系已失效：微信订阅消息为一次性，发送后自动失效，需要用户重新订阅";
            }
            
            throw new CommonException(errorMsg);
        } catch (Exception e) {
            String errorMsg = String.format("微信订阅消息发送异常 - openid: %s, templateId: %s", openid, templateId);
            log.error(errorMsg, e);
            throw new CommonException(errorMsg);
        }
    }

    /**
     * 批量发送订阅消息
     *
     * @param openids 用户openid列表
     * @param templateId 模板ID
     * @param data 消息数据
     * @param page 跳转页面
     * @param miniprogramState 小程序状态
     * @return 发送结果统计
     */
    public static String batchSendSubscriptionMessage(List<String> openids, String templateId,
                                                    Map<String, WxMaSubscribeMessage.MsgData> data,
                                                    String page, String miniprogramState) {
        if (ObjectUtil.isEmpty(openids)) {
            throw new CommonException("用户openid列表不能为空");
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder errorDetails = new StringBuilder();

        for (String openid : openids) {
            try {
                sendSubscriptionMessage(openid, templateId, data, page, miniprogramState);
                successCount++;
            } catch (Exception e) {
                failCount++;
                errorDetails.append(String.format("openid[%s]: %s; ", openid, e.getMessage()));
                log.error("批量发送订阅消息失败 - openid: {}", openid, e);
            }
        }

        String result = String.format("批量发送完成 - 成功: %d, 失败: %d", successCount, failCount);
        if (failCount > 0) {
            result += ", 失败详情: " + errorDetails.toString();
        }
        
        log.info(result);
        return result;
    }

    /**
     * 构建消息数据
     *
     * @param keyword1 关键词1
     * @param keyword2 关键词2
     * @param keyword3 关键词3
     * @param keyword4 关键词4
     * @param keyword5 关键词5
     * @return 消息数据Map
     */
    public static Map<String, WxMaSubscribeMessage.MsgData> buildMessageData(String keyword1, String keyword2,
                                                                           String keyword3, String keyword4,
                                                                           String keyword5) {
        Map<String, WxMaSubscribeMessage.MsgData> data = new java.util.HashMap<>();
        
        if (StrUtil.isNotBlank(keyword1)) {
            data.put("thing1", new WxMaSubscribeMessage.MsgData("thing1", keyword1));
        }
        if (StrUtil.isNotBlank(keyword2)) {
            data.put("thing2", new WxMaSubscribeMessage.MsgData("thing2", keyword2));
        }
        if (StrUtil.isNotBlank(keyword3)) {
            data.put("thing3", new WxMaSubscribeMessage.MsgData("thing3", keyword3));
        }
        if (StrUtil.isNotBlank(keyword4)) {
            data.put("thing4", new WxMaSubscribeMessage.MsgData("thing4", keyword4));
        }
        if (StrUtil.isNotBlank(keyword5)) {
            data.put("thing5", new WxMaSubscribeMessage.MsgData("thing5", keyword5));
        }
        
        return data;
    }

    /**
     * 构建自定义消息数据
     *
     * @param dataMap 数据映射
     * @return 消息数据Map
     */
    public static Map<String, WxMaSubscribeMessage.MsgData> buildCustomMessageData(Map<String, String> dataMap) {
        if (ObjectUtil.isEmpty(dataMap)) {
            return new java.util.HashMap<>();
        }
        
        Map<String, WxMaSubscribeMessage.MsgData> data = new java.util.HashMap<>();
        dataMap.forEach((key, value) -> {
            if (StrUtil.isNotBlank(value)) {
                data.put(key, new WxMaSubscribeMessage.MsgData(key, value));
            }
        });
        
        return data;
    }

    /**
     * 获取微信小程序服务
     *
     * @return WxMaService
     */
    private static WxMaService getWxMaService() {
        WxMaService wxMaService = SpringUtil.getBean(WxMaService.class);
        if (wxMaService == null) {
            throw new CommonException("微信小程序服务未配置，请检查WxMaConfiguration配置");
        }
        
        // 验证配置
        try {
            String appId = wxMaService.getWxMaConfig().getAppid();
            String appSecret = wxMaService.getWxMaConfig().getSecret();
            
            if (StrUtil.isBlank(appId) || StrUtil.isBlank(appSecret)) {
                throw new CommonException("微信小程序AppID或AppSecret未配置，请在后台管理系统中配置APP_ID和APP_SECRET");
            }
        } catch (Exception e) {
            log.error("获取微信小程序配置失败", e);
            throw new CommonException("微信小程序配置验证失败: " + e.getMessage());
        }
        
        return wxMaService;
    }

    /**
     * 验证模板ID格式
     *
     * @param templateId 模板ID
     * @return 是否有效
     */
    public static boolean isValidTemplateId(String templateId) {
        return StrUtil.isNotBlank(templateId) && templateId.length() >= 20;
    }

    /**
     * 验证openid格式
     *
     * @param openid 用户openid
     * @return 是否有效
     */
    public static boolean isValidOpenid(String openid) {
        return StrUtil.isNotBlank(openid) && openid.length() >= 28;
    }

    /**
     * 验证模板ID是否有效（通过尝试发送测试消息）
     * 注意：此方法会消耗用户的订阅次数，仅用于模板验证
     *
     * @param templateId 模板ID
     * @param testOpenid 测试用的openid
     * @return 验证结果
     */
    public static boolean validateTemplateId(String templateId, String testOpenid) {
        try {
            WxMaService wxMaService = getWxMaService();
            
            // 构建测试消息数据（使用空数据）
            Map<String, WxMaSubscribeMessage.MsgData> testData = new java.util.HashMap<>();
            
            WxMaSubscribeMessage testMessage = WxMaSubscribeMessage.builder()
                    .toUser(testOpenid)
                    .templateId(templateId)
                    .miniprogramState("developer") // 使用开发版，避免影响正式环境
                    .build();
                    
            // 设置消息数据
            List<WxMaSubscribeMessage.MsgData> dataList = new java.util.ArrayList<>();
            testData.forEach((key, value) -> {
                dataList.add(new WxMaSubscribeMessage.MsgData(key, value.getValue()));
            });
            testMessage.setData(dataList);

            // 尝试发送测试消息
            WxMaMsgService msgService = wxMaService.getMsgService();
            msgService.sendSubscribeMsg(testMessage);
            
            log.info("模板ID验证成功: {}", templateId);
            return true;
            
        } catch (Exception e) {
            log.warn("模板ID验证失败: {}, 错误: {}", templateId, e.getMessage());
            return false;
        }
    }

    /**
     * 获取可用的模板类型枚举
     * 用于前端下拉选择
     *
     * @return 模板类型列表
     */
    public static Map<String, String> getTemplateTypes() {
        Map<String, String> types = new java.util.HashMap<>();
        types.put("ORDER_STATUS", "订单状态通知");
        types.put("STOCK_ALERT", "库存预警通知");
        types.put("SYSTEM_NOTICE", "系统通知");
        types.put("PAYMENT_SUCCESS", "支付成功通知");
        types.put("REFUND_NOTICE", "退款通知");
        types.put("ACTIVITY_NOTICE", "活动通知");
        return types;
    }
}