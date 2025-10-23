package vip.xiaonuo.wine.modular.useraccount.param;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineUserAccountUserIdParam {
    /** 主键ID */
    @Schema(description = "userId", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "userId不能为空")
    private String userId;
}
