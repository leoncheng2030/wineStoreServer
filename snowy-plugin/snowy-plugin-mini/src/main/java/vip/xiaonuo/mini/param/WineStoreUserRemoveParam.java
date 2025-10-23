package vip.xiaonuo.mini.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineStoreUserRemoveParam {
    @Schema(description = "门店id")
    private String storeId;
    @Schema(description = "用户id")
    private String userId;
}
