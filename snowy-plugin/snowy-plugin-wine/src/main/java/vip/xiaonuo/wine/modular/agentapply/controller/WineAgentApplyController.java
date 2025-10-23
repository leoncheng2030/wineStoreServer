/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.wine.modular.agentapply.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.modular.agentapply.entity.WineAgentApply;
import vip.xiaonuo.wine.modular.agentapply.param.WineAgentApplyAuditParam;
import vip.xiaonuo.wine.modular.agentapply.param.WineAgentApplyIdParam;
import vip.xiaonuo.wine.modular.agentapply.param.WineAgentApplyPageParam;
import vip.xiaonuo.wine.modular.agentapply.service.WineAgentApplyService;

import javax.validation.Valid;
import java.util.List;

/**
 * 代理商申请控制器
 *
 * @author Qoder
 * @date 2024/09/20
 */
@Tag(name = "代理商申请管理")
@RestController
@Validated
public class WineAgentApplyController {

    @Resource
    private WineAgentApplyService wineAgentApplyService;

    /**
     * 获取代理商申请分页
     *
     * @author Qoder
     * @date 2024/09/20
     */
    @Operation(summary = "获取代理商申请分页")
    @SaCheckPermission("/wine/agentApply/page")
    @GetMapping("/wine/agentApply/page")
    public CommonResult<Page<WineAgentApply>> page(@Valid WineAgentApplyPageParam wineAgentApplyPageParam) {
        return CommonResult.data(wineAgentApplyService.page(wineAgentApplyPageParam));
    }

    /**
     * 获取代理商申请列表
     *
     * @author Qoder
     * @date 2024/09/20
     */
    @Operation(summary = "获取代理商申请列表")
    @SaCheckPermission("/wine/agentApply/list")
    @GetMapping("/wine/agentApply/list")
    public CommonResult<List<WineAgentApply>> list(WineAgentApplyPageParam wineAgentApplyPageParam) {
        return CommonResult.data(wineAgentApplyService.list(wineAgentApplyPageParam));
    }

    /**
     * 获取代理商申请详情
     *
     * @author Qoder
     * @date 2024/09/20
     */
    @Operation(summary = "获取代理商申请详情")
    @SaCheckPermission("/wine/agentApply/detail")
    @GetMapping("/wine/agentApply/detail")
    public CommonResult<WineAgentApply> detail(@Valid WineAgentApplyIdParam wineAgentApplyIdParam) {
        return CommonResult.data(wineAgentApplyService.detail(wineAgentApplyIdParam));
    }

    /**
     * 删除代理商申请
     *
     * @author Qoder
     * @date 2024/09/20
     */
    @Operation(summary = "删除代理商申请")
    @CommonLog("删除代理商申请")
    @SaCheckPermission("/wine/agentApply/delete")
    @PostMapping("/wine/agentApply/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                       List<WineAgentApplyIdParam> wineAgentApplyIdParamList) {
        wineAgentApplyService.delete(wineAgentApplyIdParamList);
        return CommonResult.ok();
    }

    /**
     * 审核代理商申请
     *
     * @author Qoder
     * @date 2024/09/20
     */
    @Operation(summary = "审核代理商申请")
    @CommonLog("审核代理商申请")
    @SaCheckPermission("/wine/agentApply/audit")
    @PostMapping("/wine/agentApply/audit")
    public CommonResult<String> audit(@RequestBody @Valid WineAgentApplyAuditParam wineAgentApplyAuditParam) {
        wineAgentApplyService.auditBatch(wineAgentApplyAuditParam);
        return CommonResult.ok();
    }

    /**
     * 获取代理商申请统计
     *
     * @author Qoder
     * @date 2024/09/20
     */
    @Operation(summary = "获取代理商申请统计")
    @SaCheckPermission("/wine/agentApply/statistics")
    @GetMapping("/wine/agentApply/statistics")
    public CommonResult<Object> statistics() {
        return CommonResult.data(wineAgentApplyService.getStatistics());
    }
}