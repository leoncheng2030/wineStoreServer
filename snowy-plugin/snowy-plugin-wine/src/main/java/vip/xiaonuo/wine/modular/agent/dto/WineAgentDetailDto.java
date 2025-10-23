package vip.xiaonuo.wine.modular.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.wine.modular.agent.entity.WineAgent;
import vip.xiaonuo.wine.modular.agentapply.entity.WineAgentApply;

/**
 * 代理商详情DTO
 *
 * @author lingming
 * @date 2025/09/30
 */
@Getter
@Setter
@Schema(description = "代理商详情DTO")
public class WineAgentDetailDto {

    /** 代理商信息 */
    @Schema(description = "代理商信息")
    private WineAgent agentInfo;

    /** 代理商申请信息 */
    @Schema(description = "代理商申请信息")
    private WineAgentApply applyInfo;
}