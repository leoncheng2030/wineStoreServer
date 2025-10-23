package vip.xiaonuo.wine.modular.withdrawrecord.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineWithdrawRecordRejectWithdrawParam {
    /** 主键ID */
    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 拒绝原因 */
    @Schema(description = "拒绝原因")
    private String failReason;
}
