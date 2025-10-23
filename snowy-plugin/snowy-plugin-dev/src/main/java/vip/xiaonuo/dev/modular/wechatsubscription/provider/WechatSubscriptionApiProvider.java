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
package vip.xiaonuo.dev.modular.wechatsubscription.provider;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.xiaonuo.dev.api.WechatSubscriptionApi;
import vip.xiaonuo.dev.modular.wechatsubscription.service.WechatSubscriptionService;

/**
 * 微信订阅消息API实现类
 *
 * @author xuyuxiang
 * @date 2024/12/25 11:15
 **/
@Slf4j
@Service
public class WechatSubscriptionApiProvider implements WechatSubscriptionApi {

    @Resource
    private WechatSubscriptionService wechatSubscriptionService;

    @Override
    public String sendOrderStatusNotification(String openid, String orderSn, String orderStatus,
                                            String orderAmount, String orderTime) {
        try {
            return wechatSubscriptionService.sendOrderStatusNotification(openid, orderSn, orderStatus, orderAmount, orderTime);
        } catch (Exception e) {
            log.error("发送订单状态订阅消息失败 - openid: {}, orderSn: {}", openid, orderSn, e);
            return "发送失败: " + e.getMessage();
        }
    }

    @Override
    public String sendStockAlertNotification(String openid, String deviceId, String currentStock,
                                           String warningLevel, String warningTime) {
        try {
            return wechatSubscriptionService.sendStockAlertNotification(openid, deviceId, currentStock, warningLevel, warningTime);
        } catch (Exception e) {
            log.error("发送库存预警订阅消息失败 - openid: {}, deviceId: {}", openid, deviceId, e);
            return "发送失败: " + e.getMessage();
        }
    }

    @Override
    public String sendSystemNotification(String openid, String title, String content, String notifyTime) {
        try {
            return wechatSubscriptionService.sendSystemNotification(openid, title, content, notifyTime);
        } catch (Exception e) {
            log.error("发送系统通知订阅消息失败 - openid: {}, title: {}", openid, title, e);
            return "发送失败: " + e.getMessage();
        }
    }

    @Override
    public boolean checkUserSubscription(String openid, String templateId) {
        try {
            return wechatSubscriptionService.checkUserSubscription(openid, templateId);
        } catch (Exception e) {
            log.error("检查用户订阅状态失败 - openid: {}, templateId: {}", openid, templateId, e);
            return false;
        }
    }
}