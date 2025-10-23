package vip.xiaonuo.wine.modular.agentapply.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 代理商申请ID参数
 *
 * @author jetox
 * @date 2025/09/20
 */
@Getter
@Setter
public class WineAgentApplyIdParam {

    /** ID */
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "ID不能为空")
    private String id;
}