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
package vip.xiaonuo.wine.core.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.common.timer.CommonTimerTaskRunner;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.api.DevMessageApi;
import vip.xiaonuo.dev.api.DevSseApi;
import vip.xiaonuo.dev.api.WechatSubscriptionApi;
import vip.xiaonuo.sys.api.SysUserApi;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;
import vip.xiaonuo.wine.modular.deviceuser.service.WineDeviceUserService;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备库存预警定时任务
 *
 * @author xuyuxiang
 * @date 2025/8/21 16:30
 **/
@Slf4j
@Component
public class WineDeviceStockWarningTask implements CommonTimerTaskRunner {

    @Resource
    private WineDeviceService wineDeviceService;
    
    @Resource
    private WineDeviceUserService wineDeviceUserService;

    @Resource
    private DevConfigApi devConfigApi;

    @Resource
    private DevMessageApi devMessageApi;
    
    @Resource
    private SysUserApi sysUserApi;
    
    @Resource
    private DevSseApi devSseApi;
    
    @Resource
    private ClientApi clientApi;
    
    @Resource
    private WechatSubscriptionApi wechatSubscriptionApi;

    @Override
    public void action(String extJson) {
        log.info(">>> 设备库存预警任务执行开始 <<<");
        try {
            // 获取库存预警阈值
            String stockWarningThresholdStr = devConfigApi.getValueByKey("STOCK_WARNING");
            final Integer stockWarningThreshold; // 声明为final
            int tempThreshold = 100; // 默认阈值为100
            if (ObjectUtil.isNotEmpty(stockWarningThresholdStr)) {
                try {
                    tempThreshold = Integer.parseInt(stockWarningThresholdStr);
                } catch (NumberFormatException e) {
                    log.warn("库存预警阈值配置不是有效数字，使用默认值100，当前配置值：{}", stockWarningThresholdStr);
                }
            }
            stockWarningThreshold = tempThreshold; // 赋值给final变量

            // 查询所有库存低于阈值的设备
            List<WineDevice> lowStockDevices = wineDeviceService.list().stream()
                    .filter(device -> ObjectUtil.isNotEmpty(device.getStock()) && device.getStock() < stockWarningThreshold)
                    .collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(lowStockDevices)) {
                log.info("发现 {} 个设备库存低于预警阈值 {}", lowStockDevices.size(), stockWarningThreshold);
                
                // 向这些设备的管理员发送站内信
                for (WineDevice device : lowStockDevices) {
                    // 获取设备管理员列表
                    List<String> deviceManagerIds = wineDeviceUserService.getUserIdsByDeviceRole(device.getId(), "DEVICE_MANAGER");
                    
                    if (CollectionUtil.isNotEmpty(deviceManagerIds)) {
                        // 构造消息内容
//                        Map<String, Object> messageMap = new HashMap<>();
//                        messageMap.put("deviceId", device.getId());
//                        messageMap.put("deviceName", device.getDeviceName());
//                        messageMap.put("currentStock", device.getStock());
//                        messageMap.put("warningThreshold", stockWarningThreshold);
                        
//                        String content = String.format("设备【%s】当前库存为 %d，低于预警阈值 %d，请及时补充库存。",
//                                device.getDeviceName(), device.getStock(), stockWarningThreshold);
//                        // 发送SSE库存预警推送
//                        sendStockWarningSSE(device, stockWarningThreshold);
                        
                        // 发送微信订阅消息给设备管理员
                        sendWechatSubscriptionToManagers(device, stockWarningThreshold, deviceManagerIds);
                        
                        // 发送微信订阅消息给代理商
                        if (ObjectUtil.isNotEmpty(device.getAgentUserId())) {
                            sendWechatSubscriptionToAgent(device, stockWarningThreshold, device.getAgentUserId());
                        }
                        
                        log.info("已向设备管理员发送库存预警消息，设备ID：{}，管理员数量：{}", device.getId(), deviceManagerIds.size());
                    } else {
                        log.warn("设备 {} 无管理员，无法发送库存预警消息", device.getId());
                    }
                }
            } else {
                log.info("当前没有设备库存低于预警阈值 {}", stockWarningThreshold);
            }
        } catch (Exception e) {
            log.error(">>> 设备库存预警任务执行异常 <<<", e);
        }
        log.info(">>> 设备库存预警任务执行结束 <<<");
    }
    
    /**
     * 发送库存预警SSE推送
     * 
     * @param device 设备信息
     * @param warningThreshold 顤警阈值
     */
    private void sendStockWarningSSE(WineDevice device, Integer warningThreshold) {
        try {
            // 构造SSE消息
            Map<String, Object> sseMessage = new HashMap<>();
            sseMessage.put("type", "DEVICE_STOCK_WARNING");
            sseMessage.put("deviceId", device.getId());
            sseMessage.put("deviceName", device.getDeviceName());
            sseMessage.put("currentStock", device.getStock());
            sseMessage.put("warningThreshold", warningThreshold);
            sseMessage.put("storeId", device.getStoreId());
            sseMessage.put("timestamp", System.currentTimeMillis());
            
            String messageJson = JSONUtil.toJsonStr(sseMessage);
            
            // 使用角色系统获取设备管理员列表
            List<String> deviceManagerIds = wineDeviceUserService.getUserIdsByDeviceRole(device.getId(), "DEVICE_MANAGER");
            for (String managerId : deviceManagerIds) {
                String clientId = devSseApi.getClientIdByLoginId(managerId);
                if (ObjectUtil.isNotEmpty(clientId)) {
                    devSseApi.sendMessageToOneClient(clientId, messageJson);
                    log.info("已向设备管理员发送库存预警SSE消息，设备ID：{}，管理员ID：{}", device.getId(), managerId);
                }
            }
            
            // 推送给代理商
            if (ObjectUtil.isNotEmpty(device.getAgentUserId())) {
                String agentClientId = devSseApi.getClientIdByLoginId(device.getAgentUserId());
                if (ObjectUtil.isNotEmpty(agentClientId)) {
                    devSseApi.sendMessageToOneClient(agentClientId, messageJson);
                    log.info("已向代理商发送库存预警SSE消息，设备ID：{}，代理商ID：{}", device.getId(), device.getAgentUserId());
                }
            }
            
        } catch (Exception e) {
            log.error("发送库存预警SSE推送失败", e);
        }
    }
    
    /**
     * 发送微信订阅消息给设备管理员
     * 
     * @param device 设备信息
     * @param warningThreshold 预警阈值
     * @param deviceManagerIds 设备管理员ID列表
     */
    private void sendWechatSubscriptionToManagers(WineDevice device, Integer warningThreshold, List<String> deviceManagerIds) {
        try {
            for (String managerId : deviceManagerIds) {
                // 获取用户openId
                String openId = clientApi.getOpenId(managerId);
                if (ObjectUtil.isNotEmpty(openId)) {
                    // 发送库存预警订阅消息
                    String result = wechatSubscriptionApi.sendStockAlertNotification(
                        openId,
                        device.getId(), // 使用设备ID而不是设备名称
                        String.valueOf(device.getStock()), // 当前库存
                        "低库存", // 预警级别（暂时未使用）
                        String.valueOf(System.currentTimeMillis()) // 预警时间（暂时未使用）
                    );
                    log.info("已向设备管理员发送库存预警微信订阅消息，设备ID：{}，管理员ID：{}，发送结果：{}", 
                            device.getId(), managerId, result);
                } else {
                    log.warn("设备管理员 {} 未绑定微信，无法发送订阅消息", managerId);
                }
            }
        } catch (Exception e) {
            log.error("发送微信订阅消息给设备管理员失败，设备ID：{}", device.getId(), e);
        }
    }
    
    /**
     * 发送微信订阅消息给代理商
     * 
     * @param device 设备信息
     * @param warningThreshold 预警阈值
     * @param agentUserId 代理商用户ID
     */
    private void sendWechatSubscriptionToAgent(WineDevice device, Integer warningThreshold, String agentUserId) {
        try {
            // 获取代理商openId
            String openId = clientApi.getOpenId(agentUserId);
            if (ObjectUtil.isNotEmpty(openId)) {
                // 发送库存预警订阅消息
                String result = wechatSubscriptionApi.sendStockAlertNotification(
                    openId,
                    device.getId(), // 使用设备ID而不是设备名称
                    String.valueOf(device.getStock()), // 当前库存
                    "低库存", // 预警级别（暂时未使用）
                    String.valueOf(System.currentTimeMillis()) // 预警时间（暂时未使用）
                );
                log.info("已向代理商发送库存预警微信订阅消息，设备ID：{}，代理商ID：{}，发送结果：{}", 
                        device.getId(), agentUserId, result);
            } else {
                log.warn("代理商 {} 未绑定微信，无法发送订阅消息", agentUserId);
            }
        } catch (Exception e) {
            log.error("发送微信订阅消息给代理商失败，设备ID：{}，代理商ID：{}", device.getId(), agentUserId, e);
        }
    }
}