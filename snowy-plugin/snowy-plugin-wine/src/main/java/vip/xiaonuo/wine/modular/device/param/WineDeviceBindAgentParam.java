package vip.xiaonuo.wine.modular.device.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 设备绑定代理商参数
 *
 * @author AI Assistant
 * @date 2025/01/30
 */
@Getter
@Setter
public class WineDeviceBindAgentParam {

    /** 设备编码 */
    @Schema(description = "设备编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    /** 代理商用户ID（由后端自动设置） */
    @Schema(description = "代理商用户ID（由后端自动设置）")
    private String agentUserId;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}