package vip.xiaonuo.wine.modular.device.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineDeviceCodeParam {

    @Schema(description = "设备编号")
    private String deviceCode;
}
