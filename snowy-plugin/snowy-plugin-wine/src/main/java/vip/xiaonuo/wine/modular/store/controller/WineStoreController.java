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
package vip.xiaonuo.wine.modular.store.controller;

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
import vip.xiaonuo.wine.modular.product.param.WineProductIdListParam;
import vip.xiaonuo.wine.modular.store.entity.WineStore;
import vip.xiaonuo.wine.modular.store.param.*;
import vip.xiaonuo.wine.modular.store.service.WineStoreService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 门店管理表控制器
 *
 * @author jetox
 * @date  2025/07/24 08:01
 */
@Tag(name = "门店管理表控制器")
@RestController
@Validated
public class WineStoreController {

    @Resource
    private WineStoreService wineStoreService;

    /**
     * 获取门店管理表分页
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "获取门店管理表分页")
    @SaCheckPermission("/wine/store/page")
    @GetMapping("/wine/store/page")
    public CommonResult<Page<WineStore>> page(WineStorePageParam wineStorePageParam) {
        return CommonResult.data(wineStoreService.page(wineStorePageParam));
    }

    /**
     * 获取门店管理表列表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "获取门店管理表列表")
    @SaCheckPermission("/wine/store/list")
    @GetMapping("/wine/store/list")
    public CommonResult<List<WineStore>> list() {
        return CommonResult.data(wineStoreService.list());
    }

    /**
     * 添加门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "添加门店管理表")
    @CommonLog("添加门店管理表")
    @SaCheckPermission("/wine/store/add")
    @PostMapping("/wine/store/add")
    public CommonResult<String> add(@RequestBody @Valid WineStoreAddParam wineStoreAddParam) {
        wineStoreService.add(wineStoreAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "编辑门店管理表")
    @CommonLog("编辑门店管理表")
    @SaCheckPermission("/wine/store/edit")
    @PostMapping("/wine/store/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineStoreEditParam wineStoreEditParam) {
        wineStoreService.edit(wineStoreEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "删除门店管理表")
    @CommonLog("删除门店管理表")
    @SaCheckPermission("/wine/store/delete")
    @PostMapping("/wine/store/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineStoreIdParam> wineStoreIdParamList) {
        wineStoreService.delete(wineStoreIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取门店管理表详情
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "获取门店管理表详情")
    @SaCheckPermission("/wine/store/detail")
    @GetMapping("/wine/store/detail")
    public CommonResult<WineStore> detail(@Valid WineStoreIdParam wineStoreIdParam) {
        return CommonResult.data(wineStoreService.detail(wineStoreIdParam));
    }

    /**
     * 下载门店管理表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "下载门店管理表导入模板")
    @GetMapping(value = "/wine/store/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineStoreService.downloadImportTemplate(response);
    }

    /**
     * 导入门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "导入门店管理表")
    @CommonLog("导入门店管理表")
    @SaCheckPermission("/wine/store/importData")
    @PostMapping("/wine/store/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineStoreService.importData(file));
    }

    /**
     * 导出门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "导出门店管理表")
    @SaCheckPermission("/wine/store/exportData")
    @PostMapping(value = "/wine/store/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineStoreIdParam> wineStoreIdParamList, HttpServletResponse response) throws IOException {
        wineStoreService.exportData(wineStoreIdParamList, response);
    }

    /**
     * 门店列表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "门店列表")
    @SaCheckPermission("/wine/store/idList")
    @PostMapping("/wine/store/idList")
    public CommonResult<List<WineStore>> list(@RequestBody WineStoreIdListParam wineStoreIdListParam) {
        return CommonResult.data(wineStoreService.idList(wineStoreIdListParam));
    }
}
