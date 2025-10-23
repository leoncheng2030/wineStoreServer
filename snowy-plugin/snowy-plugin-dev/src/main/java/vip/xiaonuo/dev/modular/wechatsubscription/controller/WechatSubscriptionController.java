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
package vip.xiaonuo.dev.modular.wechatsubscription.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.modular.wechatsubscription.param.WechatSubscriptionSendParam;
import vip.xiaonuo.dev.modular.wechatsubscription.service.WechatSubscriptionService;
import vip.xiaonuo.dev.modular.wechatsubscription.util.WechatSubscriptionUtil;

import java.util.Map;

/**
 * 微信订阅消息控制器
 *
 * @author xuyuxiang
 * @date 2024/12/25 11:00
 **/
@Tag(name = "微信订阅消息控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class WechatSubscriptionController {

    @Resource
    private WechatSubscriptionService wechatSubscriptionService;

    /**
     * 发送订阅消息
     *
     * @author xuyuxiang
     * @date 2024/12/25 11:00
     */
    @ApiOperationSupport(order = 1)
    @Operation(summary = "发送订阅消息")
    @CommonLog("发送订阅消息")
    @PostMapping("/dev/wechatsubscription/send")
    public CommonResult<String> sendSubscriptionMessage(@RequestBody @Valid WechatSubscriptionSendParam param) {
        return CommonResult.data(wechatSubscriptionService.sendSubscriptionMessage(param));
    }

    /**
     * 检查用户订阅状态
     *
     * @author xuyuxiang
     * @date 2024/12/25 11:00
     */
    @ApiOperationSupport(order = 2)
    @Operation(summary = "检查用户订阅状态")
    @CommonLog("检查用户订阅状态")
    @PostMapping("/dev/wechatsubscription/checkSubscription")
    public CommonResult<Boolean> checkUserSubscription(@RequestBody CheckSubscriptionParam param) {
        return CommonResult.data(wechatSubscriptionService.checkUserSubscription(param.getOpenid(), param.getTemplateId()));
    }

    /**
     * 验证模板ID有效性
     *
     * @author xuyuxiang
     * @date 2024/12/25 11:30
     */
    @ApiOperationSupport(order = 3)
    @Operation(summary = "验证模板ID有效性")
    @CommonLog("验证模板ID有效性")
    @PostMapping("/dev/wechatsubscription/validateTemplate")
    public CommonResult<Boolean> validateTemplateId(@RequestBody ValidateTemplateParam param) {
        return CommonResult.data(WechatSubscriptionUtil.validateTemplateId(param.getTemplateId(), param.getTestOpenid()));
    }

    /**
     * 获取模板类型列表
     *
     * @author xuyuxiang
     * @date 2024/12/25 11:30
     */
    @ApiOperationSupport(order = 4)
    @Operation(summary = "获取模板类型列表")
    @GetMapping("/dev/wechatsubscription/templateTypes")
    public CommonResult<Map<String, String>> getTemplateTypes() {
        return CommonResult.data(WechatSubscriptionUtil.getTemplateTypes());
    }

    /**
     * 检查订阅参数类
     */
    public static class CheckSubscriptionParam {
        private String openid;
        private String templateId;

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
    }

    /**
     * 验证模板参数类
     */
    public static class ValidateTemplateParam {
        private String templateId;
        private String testOpenid;

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public String getTestOpenid() {
            return testOpenid;
        }

        public void setTestOpenid(String testOpenid) {
            this.testOpenid = testOpenid;
        }
    }
}