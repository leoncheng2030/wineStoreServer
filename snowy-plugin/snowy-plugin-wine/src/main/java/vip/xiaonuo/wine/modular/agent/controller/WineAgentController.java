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
package vip.xiaonuo.wine.modular.agent.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.modular.agent.dto.WineAgentDetailDto;
import vip.xiaonuo.wine.modular.agent.entity.WineAgent;
import vip.xiaonuo.wine.modular.agent.param.WineAgentAddParam;
import vip.xiaonuo.wine.modular.agent.param.WineAgentEditParam;
import vip.xiaonuo.wine.modular.agent.param.WineAgentIdParam;
import vip.xiaonuo.wine.modular.agent.param.WineAgentPageParam;
import vip.xiaonuo.wine.modular.agent.service.WineAgentService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * wine_agent控制器
 *
 * @author jetox
 * @date  2025/09/19 07:13
 */
@Tag(name = "wine_agent控制器")
@RestController
@Validated
public class WineAgentController {

    @Resource
    private WineAgentService wineAgentService;

    /**
     * 获取wine_agent分页
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    @Operation(summary = "获取wine_agent分页")
    @SaCheckPermission("/wine/agent/page")
    @GetMapping("/wine/agent/page")
    public CommonResult<Page<WineAgent>> page(WineAgentPageParam wineAgentPageParam) {
        return CommonResult.data(wineAgentService.page(wineAgentPageParam));
    }

    /**
     * 添加wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    @Operation(summary = "添加wine_agent")
    @CommonLog("添加wine_agent")
    @SaCheckPermission("/wine/agent/add")
    @PostMapping("/wine/agent/add")
    public CommonResult<String> add(@RequestBody @Valid WineAgentAddParam wineAgentAddParam) {
        wineAgentService.add(wineAgentAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    @Operation(summary = "编辑wine_agent")
    @CommonLog("编辑wine_agent")
    @SaCheckPermission("/wine/agent/edit")
    @PostMapping("/wine/agent/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineAgentEditParam wineAgentEditParam) {
        wineAgentService.edit(wineAgentEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    @Operation(summary = "删除wine_agent")
    @CommonLog("删除wine_agent")
    @SaCheckPermission("/wine/agent/delete")
    @PostMapping("/wine/agent/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineAgentIdParam> wineAgentIdParamList) {
        wineAgentService.delete(wineAgentIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取wine_agent详情
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    @Operation(summary = "获取wine_agent详情")
    @SaCheckPermission("/wine/agent/detail")
    @GetMapping("/wine/agent/detail")
    public CommonResult<WineAgent> detail(@Valid WineAgentIdParam wineAgentIdParam) {
        return CommonResult.data(wineAgentService.detail(wineAgentIdParam));
    }

    /**
     * 下载wine_agent导入模板
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    @Operation(summary = "下载wine_agent导入模板")
    @GetMapping(value = "/wine/agent/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineAgentService.downloadImportTemplate(response);
    }

    /**
     * 导入wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    @Operation(summary = "导入wine_agent")
    @CommonLog("导入wine_agent")
    @SaCheckPermission("/wine/agent/importData")
    @PostMapping("/wine/agent/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineAgentService.importData(file));
    }

    /**
     * 导出wine_agent
     *
     * @author jetox
     * @date  2025/09/19 07:13
     */
    @Operation(summary = "导出wine_agent")
    @SaCheckPermission("/wine/agent/exportData")
    @PostMapping(value = "/wine/agent/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineAgentIdParam> wineAgentIdParamList, HttpServletResponse response) throws IOException {
        wineAgentService.exportData(wineAgentIdParamList, response);
    }

    /**
     * 根据用户ID获取代理商详情
     *
     * @author lingming
     * @date  2025/09/30 14:30
     */
    @Operation(summary = "根据用户ID获取代理商详情")
    @SaCheckPermission("/wine/agent/detailByUserId")
    @GetMapping("/wine/agent/detailByUserId")
    public CommonResult<WineAgent> detailByUserId(@RequestParam String userId) {
        return CommonResult.data(wineAgentService.detailByUserId(userId));
    }

    /**
     * 根据用户ID获取代理商详情（包含申请信息）
     *
     * @author lingming
     * @date  2025/09/30 14:30
     */
    @Operation(summary = "根据用户ID获取代理商详情（包含申请信息）")
    @SaCheckPermission("/wine/agent/getAgentDetailWithApplyInfo")
    @GetMapping("/wine/agent/getAgentDetailWithApplyInfo")
    public CommonResult<WineAgentDetailDto> getAgentDetailWithApplyInfo(@RequestParam String userId) {
        return CommonResult.data(wineAgentService.getAgentDetailWithApplyInfo(userId));
    }
}
