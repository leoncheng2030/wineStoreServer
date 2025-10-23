package vip.xiaonuo.wine.modular.device.param;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineDevicePulseRatioParam {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "单位脉冲数")
    private Double pulseRatio;
}