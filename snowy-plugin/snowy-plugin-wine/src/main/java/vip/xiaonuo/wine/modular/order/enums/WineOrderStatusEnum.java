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
package vip.xiaonuo.wine.modular.order.enums;

import lombok.Getter;

/**
 * 订单表枚举
 *
 * @author jetox
 * @date  2025/07/24 08:47
 **/
@Getter
public enum WineOrderStatusEnum {
    /** 待付款 */
    WAIT_PAY("WAIT_PAY","待付款"),

    /** 待取酒 */
    WAIT_DELIVER("WAIT_DELIVER", "待取酒"),

    /** 取酒中 */
    DELIVERING("DELIVERING", "取酒中"),

    /** 交易完成 */
    TRADE_SUCCESS("TRADE_SUCCESS", "交易完成"),

    /** 退款中 */
    REFUNDING("REFUNDING", "退款中"),

    /** 交易关闭 */
    TRADE_CLOSED("TRADE_CLOSED", "交易关闭");

    private final String value;
    private final String desc;

    WineOrderStatusEnum(String value,String desc) {
        this.value = value;
        this.desc = desc;
    }
}
