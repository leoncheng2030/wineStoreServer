package vip.xiaonuo.wine.modular.order.param;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineOrderCancelParam {

    @Schema(description = "主键id",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;
    @Schema(description = "取消原因",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;
}
