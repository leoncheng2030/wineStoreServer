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
import vip.xiaonuo.wine.modular.store.entity.WineStoreUser;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserAddParam;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserEditParam;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserIdParam;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserPageParam;
import vip.xiaonuo.wine.modular.store.service.WineStoreUserService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 店员表控制器
 *
 * @author jetox
 * @date  2025/08/20 19:08
 */
@Tag(name = "店员表控制器")
@RestController
@Validated
public class WineStoreUserController {

    @Resource
    private WineStoreUserService wineStoreUserService;

    /**
     * 获取店员表分页
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    @Operation(summary = "获取店员表分页")
    @SaCheckPermission("/wine/storeuser/page")
    @GetMapping("/wine/storeuser/page")
    public CommonResult<Page<WineStoreUser>> page(WineStoreUserPageParam wineStoreUserPageParam) {
        return CommonResult.data(wineStoreUserService.page(wineStoreUserPageParam));
    }

    /**
     * 添加店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    @Operation(summary = "添加店员表")
    @CommonLog("添加店员表")
    @SaCheckPermission("/wine/storeuser/add")
    @PostMapping("/wine/storeuser/add")
    public CommonResult<String> add(@RequestBody @Valid WineStoreUserAddParam wineStoreUserAddParam) {
        wineStoreUserService.add(wineStoreUserAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    @Operation(summary = "编辑店员表")
    @CommonLog("编辑店员表")
    @SaCheckPermission("/wine/storeuser/edit")
    @PostMapping("/wine/storeuser/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineStoreUserEditParam wineStoreUserEditParam) {
        wineStoreUserService.edit(wineStoreUserEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    @Operation(summary = "删除店员表")
    @CommonLog("删除店员表")
    @SaCheckPermission("/wine/storeuser/delete")
    @PostMapping("/wine/storeuser/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineStoreUserIdParam> wineStoreUserIdParamList) {
        wineStoreUserService.delete(wineStoreUserIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取店员表详情
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    @Operation(summary = "获取店员表详情")
    @SaCheckPermission("/wine/storeuser/detail")
    @GetMapping("/wine/storeuser/detail")
    public CommonResult<WineStoreUser> detail(@Valid WineStoreUserIdParam wineStoreUserIdParam) {
        return CommonResult.data(wineStoreUserService.detail(wineStoreUserIdParam));
    }

    /**
     * 下载店员表导入模板
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    @Operation(summary = "下载店员表导入模板")
    @GetMapping(value = "/wine/storeuser/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineStoreUserService.downloadImportTemplate(response);
    }

    /**
     * 导入店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    @Operation(summary = "导入店员表")
    @CommonLog("导入店员表")
    @SaCheckPermission("/wine/storeuser/importData")
    @PostMapping("/wine/storeuser/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineStoreUserService.importData(file));
    }

    /**
     * 导出店员表
     *
     * @author jetox
     * @date  2025/08/20 19:08
     */
    @Operation(summary = "导出店员表")
    @SaCheckPermission("/wine/storeuser/exportData")
    @PostMapping(value = "/wine/storeuser/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineStoreUserIdParam> wineStoreUserIdParamList, HttpServletResponse response) throws IOException {
        wineStoreUserService.exportData(wineStoreUserIdParamList, response);
    }
}
