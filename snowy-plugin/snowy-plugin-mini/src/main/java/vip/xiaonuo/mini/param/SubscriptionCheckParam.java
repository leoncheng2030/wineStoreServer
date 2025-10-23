package vip.xiaonuo.mini.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 订阅状态检查参数
 *
 * @author xuyuxiang
 * @date 2025/09/29
 */
@Getter
@Setter
public class SubscriptionCheckParam {

    /** 模板ID */
    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "模板ID不能为空")
    private String templateId;
}