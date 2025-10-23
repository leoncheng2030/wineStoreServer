package vip.xiaonuo.wine.modular.deviceuser.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Getter
@Setter
public class WineDeviceUserCommissionRateParam {

    @Schema(description = "关系ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "关系ID不能为空")
    private String id;

    @Schema(description = "佣金比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "佣金比例不能为空")
    @DecimalMin(value = "0", message = "佣金比例不能小于0")
    private BigDecimal commissionRate;
}
