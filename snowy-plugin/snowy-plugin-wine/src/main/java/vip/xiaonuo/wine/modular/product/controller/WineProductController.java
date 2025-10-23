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
package vip.xiaonuo.wine.modular.product.controller;

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
import vip.xiaonuo.wine.modular.product.entity.WineProduct;
import vip.xiaonuo.wine.modular.product.param.*;
import vip.xiaonuo.wine.modular.product.service.WineProductService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 酒品列表控制器
 *
 * @author jetox
 * @date  2025/07/24 08:20
 */
@Tag(name = "酒品列表控制器")
@RestController
@Validated
public class WineProductController {

    @Resource
    private WineProductService wineProductService;

    /**
     * 获取酒品列表分页
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    @Operation(summary = "获取酒品列表分页")
    @SaCheckPermission("/wine/product/page")
    @GetMapping("/wine/product/page")
    public CommonResult<Page<WineProduct>> page(WineProductPageParam wineProductPageParam) {
        return CommonResult.data(wineProductService.page(wineProductPageParam));
    }

    /**
     * 获取酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    @Operation(summary = "获取酒品列表")
    @SaCheckPermission("/wine/product/list")
    @GetMapping("/wine/product/list")
    public CommonResult<List<WineProduct>> list() {
        return CommonResult.data(wineProductService.list());
    }

    /**
     * 添加酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    @Operation(summary = "添加酒品列表")
    @CommonLog("添加酒品列表")
    @SaCheckPermission("/wine/product/add")
    @PostMapping("/wine/product/add")
    public CommonResult<String> add(@RequestBody @Valid WineProductAddParam wineProductAddParam) {
        wineProductService.add(wineProductAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    @Operation(summary = "编辑酒品列表")
    @CommonLog("编辑酒品列表")
    @SaCheckPermission("/wine/product/edit")
    @PostMapping("/wine/product/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineProductEditParam wineProductEditParam) {
        wineProductService.edit(wineProductEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    @Operation(summary = "删除酒品列表")
    @CommonLog("删除酒品列表")
    @SaCheckPermission("/wine/product/delete")
    @PostMapping("/wine/product/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineProductIdParam> wineProductIdParamList) {
        wineProductService.delete(wineProductIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取酒品列表详情
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    @Operation(summary = "获取酒品列表详情")
    @SaCheckPermission("/wine/product/detail")
    @GetMapping("/wine/product/detail")
    public CommonResult<WineProduct> detail(@Valid WineProductIdParam wineProductIdParam) {
        return CommonResult.data(wineProductService.detail(wineProductIdParam));
    }

    /**
     * 下载酒品列表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    @Operation(summary = "下载酒品列表导入模板")
    @GetMapping(value = "/wine/product/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineProductService.downloadImportTemplate(response);
    }

    /**
     * 导入酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    @Operation(summary = "导入酒品列表")
    @CommonLog("导入酒品列表")
    @SaCheckPermission("/wine/product/importData")
    @PostMapping("/wine/product/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineProductService.importData(file));
    }

    /**
     * 导出酒品列表
     *
     * @author jetox
     * @date  2025/07/24 08:20
     */
    @Operation(summary = "导出酒品列表")
    @SaCheckPermission("/wine/product/exportData")
    @PostMapping(value = "/wine/product/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineProductIdParam> wineProductIdParamList, HttpServletResponse response) throws IOException {
        wineProductService.exportData(wineProductIdParamList, response);
    }

    @Operation(summary = "获取酒品列表列表")
    @SaCheckPermission("/wine/product/idList")
    @PostMapping("/wine/product/idList")
    public CommonResult<List<WineProduct>> idList(@RequestBody  WineProductIdListParam wineProductIdListParam) {
        return CommonResult.data(wineProductService.idList(wineProductIdListParam));
    }
}
