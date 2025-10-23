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
package vip.xiaonuo.wine.modular.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fhs.core.trans.anno.Trans;
import com.fhs.core.trans.constant.TransType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.product.entity.WineProduct;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表实体
 *
 * @author jetox
 * @date  2025/07/24 08:47
 **/
@Getter
@Setter
@TableName("wine_order")
public class WineOrder extends CommonEntity {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 订单号 */
    @Schema(description = "订单号")
    private String orderNo;

    /** 微信支付订单号 */
    @Schema(description = "微信支付订单号")
    private String transactionId;

    /** 用户ID */
    @Schema(description = "用户ID")
    @Trans(type = TransType.RPC,targetClassName = "vip.xiaonuo.client.modular.user.entity.ClientUser",fields = "name",alias = "client")
    private String userId;

    /** 设备ID */
    @Schema(description = "设备ID")
    @Trans(type = TransType.SIMPLE,target = WineDevice.class,fields = {"deviceName","deviceCode"})
    private String deviceId;

    /** 酒品ID */
    @Schema(description = "酒品ID")
    @Trans(type = TransType.SIMPLE,target = WineProduct.class,fields = {"imageUrl"},alias = "product")
    private String productId;

    /** 酒品名称 */
    @Schema(description = "酒品名称")
    private String productName;

    /** 出酒量(ml) */
    @Schema(description = "出酒量(ml)")
    private Integer quantity;

    /** 剩余出酒量 */
    @Schema(description = "剩余出酒量")
    private Integer leftQuantity;

    /** 单价(元/ml) */
    @Schema(description = "单价(元/ml)")
    private BigDecimal unitPrice;

    /** 总金额 */
    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    /** 订单状态 */
    @Schema(description = "订单状态")
    private String status;

    /** 支付时间 */
    @Schema(description = "支付时间")
    private Date payTime;

    /** 开始出酒时间 */
    @Schema(description = "开始出酒时间")
    private Date dispenseStartTime;

    /** 出酒完成时间 */
    @Schema(description = "出酒完成时间")
    private Date dispenseEndTime;

    /** 取消时间 */
    @Schema(description = "取消时间")
    private Date cancelTime;

    /** 取消原因 */
    @Schema(description = "取消原因")
    private String cancelReason;

    /** 退款时间 */
    @Schema(description = "退款时间")
    private Date refundTime;

    /** 退款金额 */
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

    /** 分账状态 */
    @Schema(description = "分账状态")
    private String profitSharingStatus;

    /** 分账失败原因 */
    @Schema(description = "分账失败原因")
    private String profitSharingFailureReason;

    @Schema(description = "门店名称")
    @TableField(exist = false)
    private String storeName;

    @Schema(description = "设备位置")
    @TableField(exist = false)
    private String deviceLocation;

    @Schema(description = "设备名称")
    @TableField(exist = false)
    private String deviceName;

    @Schema(description = "设备编码")
    @TableField(exist = false)
    private String deviceCode;

    @Schema(description = "脉冲数")
    private Integer pulse;

}
