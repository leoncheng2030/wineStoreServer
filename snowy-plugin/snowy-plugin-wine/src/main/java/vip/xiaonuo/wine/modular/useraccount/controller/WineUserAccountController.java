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
package vip.xiaonuo.wine.modular.useraccount.controller;

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
import vip.xiaonuo.wine.modular.useraccount.entity.WineUserAccount;
import vip.xiaonuo.wine.modular.useraccount.param.*;
import vip.xiaonuo.wine.modular.useraccount.service.WineUserAccountService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 账户列表控制器
 *
 * @author jetox
 * @date  2025/07/24 08:55
 */
@Tag(name = "账户列表控制器")
@RestController
@Validated
public class WineUserAccountController {

    @Resource
    private WineUserAccountService wineUserAccountService;

    /**
     * 获取账户列表分页
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    @Operation(summary = "获取账户列表分页")
    @SaCheckPermission("/wine/useraccount/page")
    @GetMapping("/wine/useraccount/page")
    public CommonResult<Page<WineUserAccount>> page(WineUserAccountPageParam wineUserAccountPageParam) {
        return CommonResult.data(wineUserAccountService.page(wineUserAccountPageParam));
    }

    /**
     * 添加账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    @Operation(summary = "添加账户列表")
    @CommonLog("添加账户列表")
    @SaCheckPermission("/wine/useraccount/add")
    @PostMapping("/wine/useraccount/add")
    public CommonResult<String> add(@RequestBody @Valid WineUserAccountAddParam wineUserAccountAddParam) {
        wineUserAccountService.add(wineUserAccountAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    @Operation(summary = "编辑账户列表")
    @CommonLog("编辑账户列表")
    @SaCheckPermission("/wine/useraccount/edit")
    @PostMapping("/wine/useraccount/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineUserAccountEditParam wineUserAccountEditParam) {
        wineUserAccountService.edit(wineUserAccountEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    @Operation(summary = "删除账户列表")
    @CommonLog("删除账户列表")
    @SaCheckPermission("/wine/useraccount/delete")
    @PostMapping("/wine/useraccount/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineUserAccountIdParam> wineUserAccountIdParamList) {
        wineUserAccountService.delete(wineUserAccountIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取账户列表详情
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    @Operation(summary = "获取账户列表详情")
    @SaCheckPermission("/wine/useraccount/detail")
    @GetMapping("/wine/useraccount/detail")
    public CommonResult<WineUserAccount> detail(@Valid WineUserAccountIdParam wineUserAccountIdParam) {
        return CommonResult.data(wineUserAccountService.detail(wineUserAccountIdParam));
    }

    /**
     * 根据用户ID获取详情
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    @Operation(summary = "根据用户ID获取详情")
    @GetMapping("/wine/useraccount/getUserInfo")
    public CommonResult<WineUserAccount> getUserInfo(WineUserAccountUserIdParam wineUserAccountUserIdParam) {
        return CommonResult.data(wineUserAccountService.getUserInfo(wineUserAccountUserIdParam.getUserId()));
    }

    /**
     * 下载账户列表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    @Operation(summary = "下载账户列表导入模板")
    @GetMapping(value = "/wine/useraccount/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wineUserAccountService.downloadImportTemplate(response);
    }

    /**
     * 导入账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    @Operation(summary = "导入账户列表")
    @CommonLog("导入账户列表")
    @SaCheckPermission("/wine/useraccount/importData")
    @PostMapping("/wine/useraccount/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wineUserAccountService.importData(file));
    }

    /**
     * 导出账户列表
     *
     * @author jetox
     * @date  2025/07/24 08:55
     */
    @Operation(summary = "导出账户列表")
    @SaCheckPermission("/wine/useraccount/exportData")
    @PostMapping(value = "/wine/useraccount/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineUserAccountIdParam> wineUserAccountIdParamList, HttpServletResponse response) throws IOException {
        wineUserAccountService.exportData(wineUserAccountIdParamList, response);
    }
}
