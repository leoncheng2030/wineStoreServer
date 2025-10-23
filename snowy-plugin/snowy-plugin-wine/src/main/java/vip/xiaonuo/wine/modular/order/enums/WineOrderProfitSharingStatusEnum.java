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
 * 订单分账状态枚举
 *
 * @author jetox
 * @date 2025/09/19
 **/
@Getter
public enum WineOrderProfitSharingStatusEnum {
    /** 无需分账 */
    NO_NEED("NO_NEED", "无需分账"),
    
    /** 待分账 */
    PENDING("PENDING", "待分账"),
    
    /** 分账中 */
    PROCESSING("PROCESSING", "分账中"),
    
    /** 分账成功 */
    SUCCESS("SUCCESS", "分账成功"),
    
    /** 分账失败 */
    FAILED("FAILED", "分账失败"),
    
    /** 分账异常 */
    ERROR("ERROR", "分账异常");

    private final String value;
    private final String desc;

    WineOrderProfitSharingStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
