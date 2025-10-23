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
package vip.xiaonuo.wine.modular.spec.controller;

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
import vip.xiaonuo.wine.modular.spec.entity.WineSpec;
import vip.xiaonuo.wine.modular.spec.param.WineSpecAddParam;
import vip.xiaonuo.wine.modular.spec.param.WineSpecEditParam;
import vip.xiaonuo.wine.modular.spec.param.WineSpecIdParam;
import vip.xiaonuo.wine.modular.spec.param.WineSpecPageParam;
import vip.xiaonuo.wine.modular.spec.service.WineSpecService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 规格控制器
 *
 * @author jetox
 * @date  2025/09/28 19:06
 */
@Tag(name = "规格控制器")
@RestController
@Validated
public class WineSpecController {

    @Resource
    private WineSpecService wineSpecService;

    /**
     * 获取规格分页
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    @Operation(summary = "获取规格分页")
    @SaCheckPermission("/wine/spec/page")
    @GetMapping("/wine/spec/page")
    public CommonResult<Page<WineSpec>> page(WineSpecPageParam wineSpecPageParam) {
        return CommonResult.data(wineSpecService.page(wineSpecPageParam));
    }

    /**
     * 添加规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    @Operation(summary = "添加规格")
    @CommonLog("添加规格")
    @SaCheckPermission("/wine/spec/add")
    @PostMapping("/wine/spec/add")
    public CommonResult<String> add(@RequestBody @Valid WineSpecAddParam wineSpecAddParam) {
        wineSpecService.add(wineSpecAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    @Operation(summary = "编辑规格")
    @CommonLog("编辑规格")
    @SaCheckPermission("/wine/spec/edit")
    @PostMapping("/wine/spec/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineSpecEditParam wineSpecEditParam) {
        wineSpecService.edit(wineSpecEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    @Operation(summary = "删除规格")
    @CommonLog("删除规格")
    @SaCheckPermission("/wine/spec/delete")
    @PostMapping("/wine/spec/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineSpecIdParam> wineSpecIdParamList) {
        wineSpecService.delete(wineSpecIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取规格详情
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    @Operation(summary = "获取规格详情")
    @SaCheckPermission("/wine/spec/detail")
    @GetMapping("/wine/spec/detail")
    public CommonResult<WineSpec> detail(@Valid WineSpecIdParam wineSpecIdParam) {
        return CommonResult.data(wineSpecService.detail(wineSpecIdParam));
    }

    /**
     * 下载规格导入模板
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    @Operation(summary = "下载规格导入模板")
    @GetMapping(value = "/wine/spec/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineSpecService.downloadImportTemplate(response);
    }

    /**
     * 导入规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    @Operation(summary = "导入规格")
    @CommonLog("导入规格")
    @SaCheckPermission("/wine/spec/importData")
    @PostMapping("/wine/spec/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineSpecService.importData(file));
    }

    /**
     * 导出规格
     *
     * @author jetox
     * @date  2025/09/28 19:06
     */
    @Operation(summary = "导出规格")
    @SaCheckPermission("/wine/spec/exportData")
    @PostMapping(value = "/wine/spec/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineSpecIdParam> wineSpecIdParamList, HttpServletResponse response) throws IOException {
        wineSpecService.exportData(wineSpecIdParamList, response);
    }
}
