package vip.xiaonuo.mini.param;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceUpdateInfoParam {

    @Schema(description = "设备id")
    private String deviceId;

    @Schema(description = "门店ID")
    private String storeId;

    @Schema(description = "酒品")
    private String productId;

    @Schema(description = "管理员")
    private String manageId;
}
