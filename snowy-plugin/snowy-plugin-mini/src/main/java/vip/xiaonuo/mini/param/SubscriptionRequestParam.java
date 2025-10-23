package vip.xiaonuo.mini.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * 小程序订阅消息申请参数
 *
 * @author xuyuxiang
 * @date 2025/09/29
 */
@Getter
@Setter
public class SubscriptionRequestParam {

    /** 模板ID列表 */
    @Schema(description = "模板ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "模板ID列表不能为空")
    private List<String> templateIds;

    /** 场景值 */
    @Schema(description = "场景值，用于区分不同业务场景（如agent:代理商, manager:设备管理员）")
    private String scene;

    /** 设备ID（可选，用于记录订阅与设备的关联） */
    @Schema(description = "设备ID")
    private String deviceId;
    
    /** 订阅结果（微信返回的订阅状态） */
    @Schema(description = "订阅结果，键为模板ID，值为accept/reject/ban")
    private Map<String, String> subscriptionResult;
}