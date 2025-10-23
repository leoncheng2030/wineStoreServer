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
package vip.xiaonuo.dbs.modular.database.controller;

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
import vip.xiaonuo.dbs.modular.database.entity.ExtDatabase;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabaseAddParam;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabaseEditParam;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabaseIdParam;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabasePageParam;
import vip.xiaonuo.dbs.modular.database.service.ExtDatabaseService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 数据源控制器
 *
 * @author jetox
 * @date  2025/07/28 00:35
 */
@Tag(name = "数据源控制器")
@RestController
@Validated
public class ExtDatabaseController {

    @Resource
    private ExtDatabaseService extDatabaseService;

    /**
     * 获取数据源分页
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    @Operation(summary = "获取数据源分页")
    @SaCheckPermission("/dbs/database/page")
    @GetMapping("/dbs/database/page")
    public CommonResult<Page<ExtDatabase>> page(ExtDatabasePageParam extDatabasePageParam) {
        return CommonResult.data(extDatabaseService.page(extDatabasePageParam));
    }

    /**
     * 添加数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    @Operation(summary = "添加数据源")
    @CommonLog("添加数据源")
    @SaCheckPermission("/dbs/database/add")
    @PostMapping("/dbs/database/add")
    public CommonResult<String> add(@RequestBody @Valid ExtDatabaseAddParam extDatabaseAddParam) {
        extDatabaseService.add(extDatabaseAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    @Operation(summary = "编辑数据源")
    @CommonLog("编辑数据源")
    @SaCheckPermission("/dbs/database/edit")
    @PostMapping("/dbs/database/edit")
    public CommonResult<String> edit(@RequestBody @Valid ExtDatabaseEditParam extDatabaseEditParam) {
        extDatabaseService.edit(extDatabaseEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    @Operation(summary = "删除数据源")
    @CommonLog("删除数据源")
    @SaCheckPermission("/dbs/database/delete")
    @PostMapping("/dbs/database/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<ExtDatabaseIdParam> extDatabaseIdParamList) {
        extDatabaseService.delete(extDatabaseIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取数据源详情
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    @Operation(summary = "获取数据源详情")
    @SaCheckPermission("/dbs/database/detail")
    @GetMapping("/dbs/database/detail")
    public CommonResult<ExtDatabase> detail(@Valid ExtDatabaseIdParam extDatabaseIdParam) {
        return CommonResult.data(extDatabaseService.detail(extDatabaseIdParam));
    }

    /**
     * 下载数据源导入模板
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    @Operation(summary = "下载数据源导入模板")
    @GetMapping(value = "/dbs/database/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        extDatabaseService.downloadImportTemplate(response);
    }

    /**
     * 导入数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    @Operation(summary = "导入数据源")
    @CommonLog("导入数据源")
    @SaCheckPermission("/dbs/database/importData")
    @PostMapping("/dbs/database/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(extDatabaseService.importData(file));
    }

    /**
     * 导出数据源
     *
     * @author jetox
     * @date  2025/07/28 00:35
     */
    @Operation(summary = "导出数据源")
    @SaCheckPermission("/dbs/database/exportData")
    @PostMapping(value = "/dbs/database/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<ExtDatabaseIdParam> extDatabaseIdParamList, HttpServletResponse response) throws IOException {
        extDatabaseService.exportData(extDatabaseIdParamList, response);
    }
}
