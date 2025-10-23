package vip.xiaonuo.pay.modular.wx.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 支付订单参数
 */
@Getter
@Setter
public class PayOrder {

    @Schema(description = "商户订单号")
    private String outTradeNo;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "附加数据")
    private String attach;

    @Schema(description = "用户标识")
    private String openid;

    @Schema(description = "终端IP")
    private String spbillCreateIp;

    @Schema(description = "通知地址")
    private String notifyUrl;

    @Schema(description = "交易类型")
    private String tradeType;
}

