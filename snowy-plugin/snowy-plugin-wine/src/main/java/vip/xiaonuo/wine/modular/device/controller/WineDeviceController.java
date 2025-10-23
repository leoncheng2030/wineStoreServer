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
package vip.xiaonuo.wine.modular.device.controller;

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
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.param.WineDeviceAddParam;
import vip.xiaonuo.wine.modular.device.param.WineDeviceEditParam;
import vip.xiaonuo.wine.modular.device.param.WineDeviceIdParam;
import vip.xiaonuo.wine.modular.device.param.WineDevicePageParam;
import vip.xiaonuo.wine.modular.device.param.WineDeviceBatchUpdatePulseRatioParam;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;

import java.io.IOException;
import java.util.List;

/**
 * 设备信息表控制器
 *
 * @author jetox
 * @date  2025/07/24 07:57
 */
@Tag(name = "设备信息表控制器")
@RestController
@Validated
public class WineDeviceController {

    @Resource
    private WineDeviceService wineDeviceService;

    /**
     * 获取设备信息表分页
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    @Operation(summary = "获取设备信息表分页")
    @SaCheckPermission("/wine/device/page")
    @GetMapping("/wine/device/page")
    public CommonResult<Page<WineDevice>> page(WineDevicePageParam wineDevicePageParam) {
        return CommonResult.data(wineDeviceService.page(wineDevicePageParam));
    }

    /**
     * 添加设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    @Operation(summary = "添加设备信息表")
    @CommonLog("添加设备信息表")
    @SaCheckPermission("/wine/device/add")
    @PostMapping("/wine/device/add")
    public CommonResult<String> add(@RequestBody @Valid WineDeviceAddParam wineDeviceAddParam) {
        wineDeviceService.add(wineDeviceAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    @Operation(summary = "编辑设备信息表")
    @CommonLog("编辑设备信息表")
    @SaCheckPermission("/wine/device/edit")
    @PostMapping("/wine/device/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineDeviceEditParam wineDeviceEditParam) {
        wineDeviceService.edit(wineDeviceEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    @Operation(summary = "删除设备信息表")
    @CommonLog("删除设备信息表")
    @SaCheckPermission("/wine/device/delete")
    @PostMapping("/wine/device/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineDeviceIdParam> wineDeviceIdParamList) {
        wineDeviceService.delete(wineDeviceIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取设备信息表详情
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    @Operation(summary = "获取设备信息表详情")
    @SaCheckPermission("/wine/device/detail")
    @GetMapping("/wine/device/detail")
    public CommonResult<WineDevice> detail(@Valid WineDeviceIdParam wineDeviceIdParam) {
        return CommonResult.data(wineDeviceService.detail(wineDeviceIdParam));
    }

    /**
     * 下载设备信息表导入模板
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    @Operation(summary = "下载设备信息表导入模板")
    @GetMapping(value = "/wine/device/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineDeviceService.downloadImportTemplate(response);
    }

    /**
     * 导入设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    @Operation(summary = "导入设备信息表")
    @CommonLog("导入设备信息表")
    @SaCheckPermission("/wine/device/importData")
    @PostMapping("/wine/device/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineDeviceService.importData(file));
    }

    /**
     * 导出设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    @Operation(summary = "导出设备信息表")
    @SaCheckPermission("/wine/device/exportData")
    @PostMapping(value = "/wine/device/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineDeviceIdParam> wineDeviceIdParamList, HttpServletResponse response) throws IOException {
        wineDeviceService.exportData(wineDeviceIdParamList, response);
    }

    /**
     * 生成带参数的小程序二维码
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    @Operation(summary = "生成带参数的小程序二维码")
    @PostMapping("/wine/device/createQrCode")
    public CommonResult<String> createQrCode(@RequestBody @Valid WineDeviceIdParam wineDeviceIdParam) {
        return CommonResult.data(wineDeviceService.createQrCode(wineDeviceIdParam));
    }

    /**
     * 批量更新所有设备的脉冲数
     *
     * @author AI Assistant
     * @date 2025/01/30
     */
    @Operation(summary = "批量更新所有设备的脉冲数")
    @CommonLog("批量更新所有设备的脉冲数")
    @SaCheckPermission("/wine/device/updateAllPulseRatio")
    @PostMapping("/wine/device/updateAllPulseRatio")
    public CommonResult<String> updateAllPulseRatio(@RequestBody @Valid WineDeviceBatchUpdatePulseRatioParam param) {
        wineDeviceService.updateAllDevicesPulseRatio(param.getPulseRatio());
        return CommonResult.ok();
    }
}
