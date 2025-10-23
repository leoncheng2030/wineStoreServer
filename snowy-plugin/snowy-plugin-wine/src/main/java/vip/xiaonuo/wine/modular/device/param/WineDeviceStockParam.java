package vip.xiaonuo.wine.modular.device.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineDeviceStockParam {
    /**
     * 设备ID
     */
    @Schema(description = "设备ID",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 库存数量
     */
    @Schema(description = "库存数量",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stockNum;
}
