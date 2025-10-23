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
package vip.xiaonuo.dev.modular.district.controller;

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
import vip.xiaonuo.dev.modular.district.entity.DevDistrict;
import vip.xiaonuo.dev.modular.district.param.DevDistrictAddParam;
import vip.xiaonuo.dev.modular.district.param.DevDistrictEditParam;
import vip.xiaonuo.dev.modular.district.param.DevDistrictIdParam;
import vip.xiaonuo.dev.modular.district.param.DevDistrictPageParam;
import vip.xiaonuo.dev.modular.district.service.DevDistrictService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 地区表控制器
 *
 * @author jetox
 * @date  2025/08/12 07:53
 */
@Tag(name = "地区表控制器")
@RestController
@Validated
public class DevDistrictController {

    @Resource
    private DevDistrictService devDistrictService;

    /**
     * 获取地区表分页
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    @Operation(summary = "获取地区表分页")
    @SaCheckPermission("/dev/district/page")
    @GetMapping("/dev/district/page")
    public CommonResult<Page<DevDistrict>> page(DevDistrictPageParam devDistrictPageParam) {
        return CommonResult.data(devDistrictService.page(devDistrictPageParam));
    }

    /**
     * 添加地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    @Operation(summary = "添加地区表")
    @CommonLog("添加地区表")
    @SaCheckPermission("/dev/district/add")
    @PostMapping("/dev/district/add")
    public CommonResult<String> add(@RequestBody @Valid DevDistrictAddParam devDistrictAddParam) {
        devDistrictService.add(devDistrictAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    @Operation(summary = "编辑地区表")
    @CommonLog("编辑地区表")
    @SaCheckPermission("/dev/district/edit")
    @PostMapping("/dev/district/edit")
    public CommonResult<String> edit(@RequestBody @Valid DevDistrictEditParam devDistrictEditParam) {
        devDistrictService.edit(devDistrictEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    @Operation(summary = "删除地区表")
    @CommonLog("删除地区表")
    @SaCheckPermission("/dev/district/delete")
    @PostMapping("/dev/district/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<DevDistrictIdParam> devDistrictIdParamList) {
        devDistrictService.delete(devDistrictIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取地区表详情
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    @Operation(summary = "获取地区表详情")
    @SaCheckPermission("/dev/district/detail")
    @GetMapping("/dev/district/detail")
    public CommonResult<DevDistrict> detail(@Valid DevDistrictIdParam devDistrictIdParam) {
        return CommonResult.data(devDistrictService.detail(devDistrictIdParam));
    }

    /**
     * 下载地区表导入模板
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    @Operation(summary = "下载地区表导入模板")
    @GetMapping(value = "/dev/district/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        devDistrictService.downloadImportTemplate(response);
    }

    /**
     * 导入地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    @Operation(summary = "导入地区表")
    @CommonLog("导入地区表")
    @SaCheckPermission("/dev/district/importData")
    @PostMapping("/dev/district/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(devDistrictService.importData(file));
    }

    /**
     * 导出地区表
     *
     * @author jetox
     * @date  2025/08/12 07:53
     */
    @Operation(summary = "导出地区表")
    @SaCheckPermission("/dev/district/exportData")
    @PostMapping(value = "/dev/district/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<DevDistrictIdParam> devDistrictIdParamList, HttpServletResponse response) throws IOException {
        devDistrictService.exportData(devDistrictIdParamList, response);
    }
}
