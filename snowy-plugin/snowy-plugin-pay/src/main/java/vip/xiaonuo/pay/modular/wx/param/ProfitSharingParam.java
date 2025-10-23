package vip.xiaonuo.pay.modular.wx.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProfitSharingParam {
    /** 商户订单编号 */
    @Schema(description = "商户订单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "outTradeNo不能为空")
    private String outTradeNo;

    /** 接收方OpenId */
    @Schema(description = "接收方OpenId")
    private String receiverOpenId;

    /** 分账金额 */
    @Schema(description = "分账金额")
    private BigDecimal amount;

    /** 分账描述 */
    @Schema(description = "分账描述")
    private String description;
}