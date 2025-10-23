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
package vip.xiaonuo.wine.modular.productcategory.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
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
import vip.xiaonuo.wine.modular.productcategory.entity.WineProductCategory;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryAddParam;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryEditParam;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryIdParam;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryPageParam;
import vip.xiaonuo.wine.modular.productcategory.service.WineProductCategoryService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 酒品分类表控制器
 *
 * @author jetox
 * @date  2025/07/24 08:11
 */
@Tag(name = "酒品分类表控制器")
@RestController
@Validated
public class WineProductCategoryController {

    @Resource
    private WineProductCategoryService wineProductCategoryService;

    /**
     * 获取酒品分类表分页
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    @Operation(summary = "获取酒品分类表分页")
    @SaCheckPermission("/wine/productcategory/page")
    @GetMapping("/wine/productcategory/page")
    public CommonResult<Page<WineProductCategory>> page(WineProductCategoryPageParam wineProductCategoryPageParam) {
        return CommonResult.data(wineProductCategoryService.page(wineProductCategoryPageParam));
    }

    @Operation(summary = "添加酒品分类树")
    @SaCheckPermission("/wine/productcategory/tree")
    @PostMapping("/wine/productcategory/tree")
    public CommonResult<List<Tree<String>>> tree() {
        return CommonResult.data(wineProductCategoryService.tree());
    }


    /**
     * 添加酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    @Operation(summary = "添加酒品分类表")
    @CommonLog("添加酒品分类表")
    @SaCheckPermission("/wine/productcategory/add")
    @PostMapping("/wine/productcategory/add")
    public CommonResult<String> add(@RequestBody @Valid WineProductCategoryAddParam wineProductCategoryAddParam) {
        wineProductCategoryService.add(wineProductCategoryAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    @Operation(summary = "编辑酒品分类表")
    @CommonLog("编辑酒品分类表")
    @SaCheckPermission("/wine/productcategory/edit")
    @PostMapping("/wine/productcategory/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineProductCategoryEditParam wineProductCategoryEditParam) {
        wineProductCategoryService.edit(wineProductCategoryEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    @Operation(summary = "删除酒品分类表")
    @CommonLog("删除酒品分类表")
    @SaCheckPermission("/wine/productcategory/delete")
    @PostMapping("/wine/productcategory/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineProductCategoryIdParam> wineProductCategoryIdParamList) {
        wineProductCategoryService.delete(wineProductCategoryIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取酒品分类表详情
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    @Operation(summary = "获取酒品分类表详情")
    @SaCheckPermission("/wine/productcategory/detail")
    @GetMapping("/wine/productcategory/detail")
    public CommonResult<WineProductCategory> detail(@Valid WineProductCategoryIdParam wineProductCategoryIdParam) {
        return CommonResult.data(wineProductCategoryService.detail(wineProductCategoryIdParam));
    }

    /**
     * 下载酒品分类表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    @Operation(summary = "下载酒品分类表导入模板")
    @GetMapping(value = "/wine/productcategory/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineProductCategoryService.downloadImportTemplate(response);
    }

    /**
     * 导入酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    @Operation(summary = "导入酒品分类表")
    @CommonLog("导入酒品分类表")
    @SaCheckPermission("/wine/productcategory/importData")
    @PostMapping("/wine/productcategory/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineProductCategoryService.importData(file));
    }

    /**
     * 导出酒品分类表
     *
     * @author jetox
     * @date  2025/07/24 08:11
     */
    @Operation(summary = "导出酒品分类表")
    @SaCheckPermission("/wine/productcategory/exportData")
    @PostMapping(value = "/wine/productcategory/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineProductCategoryIdParam> wineProductCategoryIdParamList, HttpServletResponse response) throws IOException {
        wineProductCategoryService.exportData(wineProductCategoryIdParamList, response);
    }
}
