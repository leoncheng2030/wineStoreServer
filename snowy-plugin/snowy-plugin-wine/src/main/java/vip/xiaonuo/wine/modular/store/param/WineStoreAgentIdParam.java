package vip.xiaonuo.wine.modular.store.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineStoreAgentIdParam {
    /** 主键 */
    @Schema(description = "代理商用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "agentClientUserId不能为空")
    private String agentClientUserId;
}
