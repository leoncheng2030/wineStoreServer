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
package vip.xiaonuo.wine.modular.devicerole.controller;

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
import vip.xiaonuo.wine.modular.devicerole.entity.WineDeviceRole;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRoleAddParam;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRoleEditParam;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRoleIdParam;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRolePageParam;
import vip.xiaonuo.wine.modular.devicerole.service.WineDeviceRoleService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 设备角色定义表控制器
 *
 * @author jetox
 * @date  2025/09/21 09:16
 */
@Tag(name = "设备角色定义表控制器")
@RestController
@Validated
public class WineDeviceRoleController {

    @Resource
    private WineDeviceRoleService wineDeviceRoleService;

    /**
     * 获取设备角色定义表分页
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    @Operation(summary = "获取设备角色定义表分页")
    @SaCheckPermission("/wine/devicerole/page")
    @GetMapping("/wine/devicerole/page")
    public CommonResult<Page<WineDeviceRole>> page(WineDeviceRolePageParam wineDeviceRolePageParam) {
        return CommonResult.data(wineDeviceRoleService.page(wineDeviceRolePageParam));
    }

    /**
     * 添加设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    @Operation(summary = "添加设备角色定义表")
    @CommonLog("添加设备角色定义表")
    @SaCheckPermission("/wine/devicerole/add")
    @PostMapping("/wine/devicerole/add")
    public CommonResult<String> add(@RequestBody @Valid WineDeviceRoleAddParam wineDeviceRoleAddParam) {
        wineDeviceRoleService.add(wineDeviceRoleAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    @Operation(summary = "编辑设备角色定义表")
    @CommonLog("编辑设备角色定义表")
    @SaCheckPermission("/wine/devicerole/edit")
    @PostMapping("/wine/devicerole/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineDeviceRoleEditParam wineDeviceRoleEditParam) {
        wineDeviceRoleService.edit(wineDeviceRoleEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    @Operation(summary = "删除设备角色定义表")
    @CommonLog("删除设备角色定义表")
    @SaCheckPermission("/wine/devicerole/delete")
    @PostMapping("/wine/devicerole/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineDeviceRoleIdParam> wineDeviceRoleIdParamList) {
        wineDeviceRoleService.delete(wineDeviceRoleIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取设备角色定义表详情
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    @Operation(summary = "获取设备角色定义表详情")
    @SaCheckPermission("/wine/devicerole/detail")
    @GetMapping("/wine/devicerole/detail")
    public CommonResult<WineDeviceRole> detail(@Valid WineDeviceRoleIdParam wineDeviceRoleIdParam) {
        return CommonResult.data(wineDeviceRoleService.detail(wineDeviceRoleIdParam));
    }

    /**
     * 下载设备角色定义表导入模板
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    @Operation(summary = "下载设备角色定义表导入模板")
    @GetMapping(value = "/wine/devicerole/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineDeviceRoleService.downloadImportTemplate(response);
    }

    /**
     * 导入设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    @Operation(summary = "导入设备角色定义表")
    @CommonLog("导入设备角色定义表")
    @SaCheckPermission("/wine/devicerole/importData")
    @PostMapping("/wine/devicerole/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineDeviceRoleService.importData(file));
    }

    /**
     * 导出设备角色定义表
     *
     * @author jetox
     * @date  2025/09/21 09:16
     */
    @Operation(summary = "导出设备角色定义表")
    @SaCheckPermission("/wine/devicerole/exportData")
    @PostMapping(value = "/wine/devicerole/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineDeviceRoleIdParam> wineDeviceRoleIdParamList, HttpServletResponse response) throws IOException {
        wineDeviceRoleService.exportData(wineDeviceRoleIdParamList, response);
    }
}
