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
package vip.xiaonuo.dev.modular.subscriptiontemplate.controller;

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
import vip.xiaonuo.dev.modular.subscriptiontemplate.entity.WechatSubscriptionTemplate;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplateAddParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplateEditParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplateIdParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplatePageParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.service.WechatSubscriptionTemplateService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 微信订阅消息模板表控制器
 *
 * @author jetox
 * @date  2025/09/29 00:49
 */
@Tag(name = "微信订阅消息模板表控制器")
@RestController
@Validated
public class WechatSubscriptionTemplateController {

    @Resource
    private WechatSubscriptionTemplateService wechatSubscriptionTemplateService;

    /**
     * 获取微信订阅消息模板表分页
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "获取微信订阅消息模板表分页")
    @SaCheckPermission("/dev/subscriptiontemplate/page")
    @GetMapping("/dev/subscriptiontemplate/page")
    public CommonResult<Page<WechatSubscriptionTemplate>> page(WechatSubscriptionTemplatePageParam wechatSubscriptionTemplatePageParam) {
        return CommonResult.data(wechatSubscriptionTemplateService.page(wechatSubscriptionTemplatePageParam));
    }

    /**
     * 添加微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "添加微信订阅消息模板表")
    @CommonLog("添加微信订阅消息模板表")
    @SaCheckPermission("/dev/subscriptiontemplate/add")
    @PostMapping("/dev/subscriptiontemplate/add")
    public CommonResult<String> add(@RequestBody @Valid WechatSubscriptionTemplateAddParam wechatSubscriptionTemplateAddParam) {
        wechatSubscriptionTemplateService.add(wechatSubscriptionTemplateAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "编辑微信订阅消息模板表")
    @CommonLog("编辑微信订阅消息模板表")
    @SaCheckPermission("/dev/subscriptiontemplate/edit")
    @PostMapping("/dev/subscriptiontemplate/edit")
    public CommonResult<String> edit(@RequestBody @Valid WechatSubscriptionTemplateEditParam wechatSubscriptionTemplateEditParam) {
        wechatSubscriptionTemplateService.edit(wechatSubscriptionTemplateEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "删除微信订阅消息模板表")
    @CommonLog("删除微信订阅消息模板表")
    @SaCheckPermission("/dev/subscriptiontemplate/delete")
    @PostMapping("/dev/subscriptiontemplate/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WechatSubscriptionTemplateIdParam> wechatSubscriptionTemplateIdParamList) {
        wechatSubscriptionTemplateService.delete(wechatSubscriptionTemplateIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取微信订阅消息模板表详情
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "获取微信订阅消息模板表详情")
    @SaCheckPermission("/dev/subscriptiontemplate/detail")
    @GetMapping("/dev/subscriptiontemplate/detail")
    public CommonResult<WechatSubscriptionTemplate> detail(@Valid WechatSubscriptionTemplateIdParam wechatSubscriptionTemplateIdParam) {
        return CommonResult.data(wechatSubscriptionTemplateService.detail(wechatSubscriptionTemplateIdParam));
    }

    /**
     * 下载微信订阅消息模板表导入模板
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "下载微信订阅消息模板表导入模板")
    @GetMapping(value = "/dev/subscriptiontemplate/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wechatSubscriptionTemplateService.downloadImportTemplate(response);
    }

    /**
     * 导入微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "导入微信订阅消息模板表")
    @CommonLog("导入微信订阅消息模板表")
    @SaCheckPermission("/dev/subscriptiontemplate/importData")
    @PostMapping("/dev/subscriptiontemplate/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wechatSubscriptionTemplateService.importData(file));
    }

    /**
     * 导出微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "导出微信订阅消息模板表")
    @SaCheckPermission("/dev/subscriptiontemplate/exportData")
    @PostMapping(value = "/dev/subscriptiontemplate/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WechatSubscriptionTemplateIdParam> wechatSubscriptionTemplateIdParamList, HttpServletResponse response) throws IOException {
        wechatSubscriptionTemplateService.exportData(wechatSubscriptionTemplateIdParamList, response);
    }
}
