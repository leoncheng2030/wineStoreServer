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
package vip.xiaonuo.dev.api;

/**
 * 微信订阅消息API接口
 *
 * @author xuyuxiang
 * @date 2024/12/25 11:10
 **/
public interface WechatSubscriptionApi {

    /**
     * 发送订单状态订阅消息
     *
     * @param openid 用户openid
     * @param orderSn 订单号
     * @param orderStatus 订单状态
     * @param orderAmount 订单金额
     * @param orderTime 订单时间
     * @return 发送结果
     */
    String sendOrderStatusNotification(String openid, String orderSn, String orderStatus, 
                                     String orderAmount, String orderTime);

    /**
     * 发送库存预警订阅消息
     *
     * @param openid 用户openid
     * @param deviceId 设备ID
     * @param currentStock 当前库存
     * @param warningLevel 预警级别
     * @param warningTime 预警时间
     * @return 发送结果
     */
    String sendStockAlertNotification(String openid, String deviceId, String currentStock,
                                    String warningLevel, String warningTime);

    /**
     * 发送系统通知订阅消息
     *
     * @param openid 用户openid
     * @param title 通知标题
     * @param content 通知内容
     * @param notifyTime 通知时间
     * @return 发送结果
     */
    String sendSystemNotification(String openid, String title, String content, String notifyTime);

    /**
     * 检查用户订阅状态
     *
     * @param openid 用户openid
     * @param templateId 模板ID
     * @return 是否已订阅
     */
    boolean checkUserSubscription(String openid, String templateId);
}