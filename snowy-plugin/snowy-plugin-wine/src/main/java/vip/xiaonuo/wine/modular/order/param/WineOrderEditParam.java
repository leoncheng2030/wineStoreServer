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
package vip.xiaonuo.wine.modular.order.param;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表编辑参数
 *
 * @author jetox
 * @date  2025/07/24 08:47
 **/
@Getter
@Setter
public class WineOrderEditParam {

    /** 主键 */
    @ExcelProperty("主键")
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 微信支付订单号 */
    @Schema(description = "微信支付订单号")
    private String transactionId;

    /** 订单号 */
    @ExcelProperty("订单号")
    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "orderNo不能为空")
    private String orderNo;

    /** 用户ID */
    @ExcelProperty("用户ID")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "userId不能为空")
    private String userId;

    /** 设备ID */
    @ExcelProperty("设备ID")
    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "deviceId不能为空")
    private String deviceId;

    /** 酒品ID */
    @ExcelProperty("酒品ID")
    @Schema(description = "酒品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "productId不能为空")
    private String productId;

    /** 酒品名称 */
    @ExcelProperty("酒品名称")
    @Schema(description = "酒品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "productName不能为空")
    private String productName;

    /** 出酒量(ml) */
    @ExcelProperty("出酒量(ml)")
    @Schema(description = "出酒量(ml)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "quantity不能为空")
    private Integer quantity;

    /** 单价(元/ml) */
    @ExcelProperty("单价(元/ml)")
    @Schema(description = "单价(元/ml)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "unitPrice不能为空")
    private BigDecimal unitPrice;

    /** 总金额 */
    @ExcelProperty("总金额")
    @Schema(description = "总金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "totalAmount不能为空")
    private BigDecimal totalAmount;

    /** 订单状态 */
    @ExcelProperty("订单状态")
    @Schema(description = "订单状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "status不能为空")
    private String status;

    /** 支付时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("支付时间")
    @Schema(description = "支付时间")
    private Date payTime;

    /** 开始出酒时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("开始出酒时间")
    @Schema(description = "开始出酒时间")
    private Date dispenseStartTime;

    /** 出酒完成时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("出酒完成时间")
    @Schema(description = "出酒完成时间")
    private Date dispenseEndTime;

    /** 取消时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("取消时间")
    @Schema(description = "取消时间")
    private Date cancelTime;

    /** 取消原因 */
    @ExcelProperty("取消原因")
    @Schema(description = "取消原因")
    private String cancelReason;

    /** 退款时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("退款时间")
    @Schema(description = "退款时间")
    private Date refundTime;

    /** 退款金额 */
    @ExcelProperty("退款金额")
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    /** 备注 */
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @ExcelProperty("扩展信息")
    @Schema(description = "扩展信息")
    private String extJson;

}
