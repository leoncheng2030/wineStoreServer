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
package vip.xiaonuo.dev.modular.usersubscription.controller;

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
import vip.xiaonuo.dev.modular.usersubscription.entity.WechatUserSubscription;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionAddParam;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionEditParam;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionIdParam;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionPageParam;
import vip.xiaonuo.dev.modular.usersubscription.service.WechatUserSubscriptionService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 用户订阅消息授权表控制器
 *
 * @author jetox
 * @date  2025/09/29 00:49
 */
@Tag(name = "用户订阅消息授权表控制器")
@RestController
@Validated
public class WechatUserSubscriptionController {

    @Resource
    private WechatUserSubscriptionService wechatUserSubscriptionService;

    /**
     * 获取用户订阅消息授权表分页
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "获取用户订阅消息授权表分页")
    @SaCheckPermission("/dev/usersubscription/page")
    @GetMapping("/dev/usersubscription/page")
    public CommonResult<Page<WechatUserSubscription>> page(WechatUserSubscriptionPageParam wechatUserSubscriptionPageParam) {
        return CommonResult.data(wechatUserSubscriptionService.page(wechatUserSubscriptionPageParam));
    }

    /**
     * 添加用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "添加用户订阅消息授权表")
    @CommonLog("添加用户订阅消息授权表")
    @SaCheckPermission("/dev/usersubscription/add")
    @PostMapping("/dev/usersubscription/add")
    public CommonResult<String> add(@RequestBody @Valid WechatUserSubscriptionAddParam wechatUserSubscriptionAddParam) {
        wechatUserSubscriptionService.add(wechatUserSubscriptionAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "编辑用户订阅消息授权表")
    @CommonLog("编辑用户订阅消息授权表")
    @SaCheckPermission("/dev/usersubscription/edit")
    @PostMapping("/dev/usersubscription/edit")
    public CommonResult<String> edit(@RequestBody @Valid WechatUserSubscriptionEditParam wechatUserSubscriptionEditParam) {
        wechatUserSubscriptionService.edit(wechatUserSubscriptionEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "删除用户订阅消息授权表")
    @CommonLog("删除用户订阅消息授权表")
    @SaCheckPermission("/dev/usersubscription/delete")
    @PostMapping("/dev/usersubscription/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WechatUserSubscriptionIdParam> wechatUserSubscriptionIdParamList) {
        wechatUserSubscriptionService.delete(wechatUserSubscriptionIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取用户订阅消息授权表详情
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "获取用户订阅消息授权表详情")
    @SaCheckPermission("/dev/usersubscription/detail")
    @GetMapping("/dev/usersubscription/detail")
    public CommonResult<WechatUserSubscription> detail(@Valid WechatUserSubscriptionIdParam wechatUserSubscriptionIdParam) {
        return CommonResult.data(wechatUserSubscriptionService.detail(wechatUserSubscriptionIdParam));
    }

    /**
     * 下载用户订阅消息授权表导入模板
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "下载用户订阅消息授权表导入模板")
    @GetMapping(value = "/dev/usersubscription/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        wechatUserSubscriptionService.downloadImportTemplate(response);
    }

    /**
     * 导入用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "导入用户订阅消息授权表")
    @CommonLog("导入用户订阅消息授权表")
    @SaCheckPermission("/dev/usersubscription/importData")
    @PostMapping("/dev/usersubscription/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(wechatUserSubscriptionService.importData(file));
    }

    /**
     * 导出用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    @Operation(summary = "导出用户订阅消息授权表")
    @SaCheckPermission("/dev/usersubscription/exportData")
    @PostMapping(value = "/dev/usersubscription/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WechatUserSubscriptionIdParam> wechatUserSubscriptionIdParamList, HttpServletResponse response) throws IOException {
        wechatUserSubscriptionService.exportData(wechatUserSubscriptionIdParamList, response);
    }
}
