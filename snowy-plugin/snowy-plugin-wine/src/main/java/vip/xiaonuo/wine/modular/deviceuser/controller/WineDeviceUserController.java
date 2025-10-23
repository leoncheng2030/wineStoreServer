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
package vip.xiaonuo.wine.modular.deviceuser.controller;

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
import vip.xiaonuo.wine.modular.deviceuser.entity.WineDeviceUser;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserAddParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserEditParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserIdParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserPageParam;
import vip.xiaonuo.wine.modular.deviceuser.service.WineDeviceUserService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 分佣配置控制器
 *
 * @author jetox
 * @date  2025/07/24 08:44
 */
@Tag(name = "分佣配置控制器")
@RestController
@Validated
public class WineDeviceUserController {

    @Resource
    private WineDeviceUserService wineDeviceUserService;

    /**
     * 获取分佣配置分页
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    @Operation(summary = "获取分佣配置分页")
    @SaCheckPermission("/wine/deviceuser/page")
    @GetMapping("/wine/deviceuser/page")
    public CommonResult<Page<WineDeviceUser>> page(WineDeviceUserPageParam wineDeviceUserPageParam) {
        return CommonResult.data(wineDeviceUserService.page(wineDeviceUserPageParam));
    }

    /**
     * 添加分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    @Operation(summary = "添加分佣配置")
    @CommonLog("添加分佣配置")
    @SaCheckPermission("/wine/deviceuser/add")
    @PostMapping("/wine/deviceuser/add")
    public CommonResult<String> add(@RequestBody @Valid WineDeviceUserAddParam wineDeviceUserAddParam) {
        wineDeviceUserService.add(wineDeviceUserAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    @Operation(summary = "编辑分佣配置")
    @CommonLog("编辑分佣配置")
    @SaCheckPermission("/wine/deviceuser/edit")
    @PostMapping("/wine/deviceuser/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineDeviceUserEditParam wineDeviceUserEditParam) {
        wineDeviceUserService.edit(wineDeviceUserEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    @Operation(summary = "删除分佣配置")
    @CommonLog("删除分佣配置")
    @SaCheckPermission("/wine/deviceuser/delete")
    @PostMapping("/wine/deviceuser/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineDeviceUserIdParam> wineDeviceUserIdParamList) {
        wineDeviceUserService.delete(wineDeviceUserIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取分佣配置详情
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    @Operation(summary = "获取分佣配置详情")
    @SaCheckPermission("/wine/deviceuser/detail")
    @GetMapping("/wine/deviceuser/detail")
    public CommonResult<WineDeviceUser> detail(@Valid WineDeviceUserIdParam wineDeviceUserIdParam) {
        return CommonResult.data(wineDeviceUserService.detail(wineDeviceUserIdParam));
    }

    /**
     * 下载分佣配置导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    @Operation(summary = "下载分佣配置导入模板")
    @GetMapping(value = "/wine/deviceuser/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineDeviceUserService.downloadImportTemplate(response);
    }

    /**
     * 导入分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    @Operation(summary = "导入分佣配置")
    @CommonLog("导入分佣配置")
    @SaCheckPermission("/wine/deviceuser/importData")
    @PostMapping("/wine/deviceuser/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineDeviceUserService.importData(file));
    }

    /**
     * 导出分佣配置
     *
     * @author jetox
     * @date  2025/07/24 08:44
     */
    @Operation(summary = "导出分佣配置")
    @SaCheckPermission("/wine/deviceuser/exportData")
    @PostMapping(value = "/wine/deviceuser/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineDeviceUserIdParam> wineDeviceUserIdParamList, HttpServletResponse response) throws IOException {
        wineDeviceUserService.exportData(wineDeviceUserIdParamList, response);
    }
}
