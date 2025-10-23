package vip.xiaonuo.wine.modular.agentapply.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 代理商申请审核参数
 *
 * @author jetox
 * @date 2025/09/20
 */
@Getter
@Setter
public class WineAgentApplyAuditParam {

    /** 申请ID */
    @Schema(description = "申请ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请ID不能为空")
    private String id;

    /** 子商户id */
    @Schema(description = "子商户id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subMerId;

    /** 审核状态 */
    @Schema(description = "审核状态：APPROVED-通过，REJECTED-拒绝", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "审核状态不能为空")
    private String status;

    /** 审核意见 */
    @Schema(description = "审核意见")
    private String auditRemark;
}