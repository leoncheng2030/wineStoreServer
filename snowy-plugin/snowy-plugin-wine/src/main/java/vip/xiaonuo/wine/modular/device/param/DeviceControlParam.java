package vip.xiaonuo.wine.modular.device.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceControlParam {
    /** 设备UUID */
    @Schema(description = "设备蓝牙UUID标识")
    private String uuid;

    /** 设备Code */
    @Schema(description = "设备Code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String deviceCode;

    /** 订单ID（充电ID） */
    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String chargeId;

    /** 控制时长-分钟 */
    @Schema(description = "控制时长-分钟")
    private Integer minute;

    /** 控制时长-秒 */
    @Schema(description = "控制时长-秒")
    private Integer second;

    /** 脉冲数 */
    @Schema(description = "脉冲数")
    private Integer quantity;
}
