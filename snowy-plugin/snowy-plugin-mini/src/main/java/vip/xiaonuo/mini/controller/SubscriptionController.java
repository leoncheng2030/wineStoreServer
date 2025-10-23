package vip.xiaonuo.mini.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.api.WechatSubscriptionApi;
import vip.xiaonuo.dev.modular.subscriptiontemplate.entity.WechatSubscriptionTemplate;
import vip.xiaonuo.dev.modular.subscriptiontemplate.service.WechatSubscriptionTemplateService;
import vip.xiaonuo.dev.modular.usersubscription.entity.WechatUserSubscription;
import vip.xiaonuo.dev.modular.usersubscription.service.WechatUserSubscriptionService;
import vip.xiaonuo.mini.param.SubscriptionCheckParam;
import vip.xiaonuo.mini.param.SubscriptionRequestParam;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序订阅消息控制器
 *
 * @author xuyuxiang
 * @date 2025/09/29
 */
@Slf4j
@Tag(name = "小程序订阅消息控制器")
@RestController
@Validated
public class SubscriptionController {

    @Resource
    private WechatSubscriptionApi wechatSubscriptionApi;

    @Resource
    private ClientApi clientApi;
    
    @Resource
    private WechatUserSubscriptionService userSubscriptionService;
    
    @Resource
    private WechatSubscriptionTemplateService templateService;

    /**
     * 申请订阅消息
     *
     * @param param 订阅申请参数
     * @return 申请结果
     */
    @Operation(summary = "申请订阅消息")
    @CommonLog("申请订阅消息")
    @PostMapping("/mini/subscription/request")
    public CommonResult<Map<String, Object>> requestSubscription(@RequestBody @Valid SubscriptionRequestParam param) {
        // 获取当前登录用户
        String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
        try {

            log.info("用户 {} 申请订阅消息，模板列表：{}, 场景：{}, 订阅结果：{}", 
                    userId, param.getTemplateIds(), param.getScene(), param.getSubscriptionResult());

            // 获取用户openId（用于后续发送消息时使用）
            String openId = clientApi.getOpenId(userId);
            
            if (openId == null) {
                log.warn("用户 {} 未绑定微信，无法记录订阅信息", userId);
                return CommonResult.error("用户未绑定微信，无法记录订阅信息");
            }
            
            // 处理订阅结果，写入用户订阅表
            int successCount = 0;
            int failCount = 0;
            
            if (param.getSubscriptionResult() != null && !param.getSubscriptionResult().isEmpty()) {
                for (String templateId : param.getTemplateIds()) {
                    String subscriptionStatus = param.getSubscriptionResult().get(templateId);
                    
                    try {
                        if ("accept".equals(subscriptionStatus)) {
                            // 用户同意订阅，写入订阅表
                            saveUserSubscription(userId, openId, templateId, param.getScene());
                            successCount++;
                            log.info("用户 {} 订阅模板 {} 成功记录", userId, templateId);
                        } else {
                            log.info("用户 {} 拒绝或取消订阅模板 {}", userId, templateId);
                            failCount++;
                        }
                    } catch (Exception e) {
                        log.error("记录用户 {} 订阅模板 {} 失败", userId, templateId, e);
                        failCount++;
                    }
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订阅申请已记录");
            result.put("userId", userId);
            result.put("hasOpenId", true);
            result.put("templateIds", param.getTemplateIds());
            result.put("scene", param.getScene());
            result.put("successCount", successCount);
            result.put("failCount", failCount);

            log.info("用户 {} 订阅申请处理完成，成功：{}，失败：{}", userId, successCount, failCount);
            
            return CommonResult.data(result);
        } catch (Exception e) {
            log.error("申请订阅消息失败 - userId: {}, templateIds: {}, scene: {}", 
                    userId, param.getTemplateIds(), param.getScene(), e);
            return CommonResult.error("申请订阅消息失败：" + e.getMessage());
        }
    }

    /**
     * 获取可用模板列表
     *
     * @return 模板列表
     */
    @Operation(summary = "获取可用模板列表")
    @GetMapping("/mini/subscription/templates")
    public CommonResult<Map<String, Object>> getTemplateList() {
        try {
            Map<String, Object> templates = new HashMap<>();
            
            // 库存预警模板
            Map<String, Object> stockAlert = new HashMap<>();
            stockAlert.put("id", "STOCK_ALERT");
            stockAlert.put("name", "库存预警通知");
            stockAlert.put("description", "设备库存低于预警阈值时通知");
            stockAlert.put("scene", List.of("agent", "manager"));
            
            templates.put("STOCK_ALERT", stockAlert);
            
            Map<String, Object> result = new HashMap<>();
            result.put("templates", templates);
            result.put("total", templates.size());
            
            return CommonResult.data(result);
        } catch (Exception e) {
            log.error("获取模板列表失败", e);
            return CommonResult.error("获取模板列表失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户订阅状态
     *
     * @param param 检查参数
     * @return 订阅状态
     */
    @Operation(summary = "检查用户订阅状态")
    @GetMapping("/mini/subscription/check")
    public CommonResult<Map<String, Object>> checkSubscription(@Valid SubscriptionCheckParam param) {
        // 获取当前登录用户
        String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
        try {

            String openId = clientApi.getOpenId(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("templateId", param.getTemplateId());
            result.put("hasOpenId", openId != null);
            
            if (openId != null) {
                // 调用API检查订阅状态
                boolean isSubscribed = wechatSubscriptionApi.checkUserSubscription(openId, param.getTemplateId());
                result.put("isSubscribed", isSubscribed);
                result.put("status", isSubscribed ? "已订阅" : "未订阅");
            } else {
                result.put("isSubscribed", false);
                result.put("status", "用户未绑定微信");
            }
            
            return CommonResult.data(result);
        } catch (Exception e) {
            log.error("检查订阅状态失败 - userId: {}, templateId: {}", userId, param.getTemplateId(), e);
            return CommonResult.error("检查订阅状态失败：" + e.getMessage());
        }
    }

    /**
     * 发送测试订阅消息（仅用于测试）
     *
     * @return 发送结果
     */
    @Operation(summary = "发送测试订阅消息")
    @CommonLog("发送测试订阅消息")
    @PostMapping("/mini/subscription/test")
    public CommonResult<String> sendTestMessage() {
        // 获取当前登录用户
        String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
        try {

            String openId = clientApi.getOpenId(userId);
            
            if (openId == null) {
                return CommonResult.error("用户未绑定微信，无法发送测试消息");
            }
            
            // 发送测试库存预警消息
            String result = wechatSubscriptionApi.sendStockAlertNotification(
                openId,
                "test_device_001", // 使用测试设备ID
                "50",
                "低库存",
                String.valueOf(System.currentTimeMillis())
            );
            
            log.info("测试订阅消息发送完成，用户：{}，结果：{}", userId, result);
            
            return CommonResult.data("测试消息发送完成：" + result);
        } catch (Exception e) {
            log.error("发送测试订阅消息失败 - userId: {}", userId, e);
            return CommonResult.error("发送测试消息失败：" + e.getMessage());
        }
    }
    /**
     * 根据模板类型获取模板配置
     * 
     * @param templateType 模板类型
     * @return 模板配置信息
     */
    @Operation(summary = "根据模板类型获取模板配置")
    @GetMapping("/mini/subscription/template/type")
    public CommonResult<Map<String, Object>> getTemplateByType(@RequestParam String templateType) {
        if (StrUtil.isBlank(templateType)) {
            log.warn("模板类型参数为空");
            return CommonResult.error("模板类型不能为空");
        }
        
        try {
            log.debug("获取模板配置 - templateType: {}", templateType);
            // 查询模板配置
            WechatSubscriptionTemplate template = templateService.lambdaQuery()
                    .eq(WechatSubscriptionTemplate::getTemplateType, templateType)
                    .eq(WechatSubscriptionTemplate::getStatus, "ENABLE")
                    .one();
            
            if (template == null) {
                return CommonResult.error("未找到类型为 " + templateType + " 的已启用模板");
            }
            
            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("templateId", template.getTemplateId());
            result.put("templateTitle", template.getTemplateTitle());
            result.put("templateType", template.getTemplateType());
            result.put("jumpPage", template.getJumpPage());
            
            // 解析关键词配置
            if (StrUtil.isNotBlank(template.getKeywordConfig())) {
                try {
                    JSONObject keywordConfig = JSONUtil.parseObj(template.getKeywordConfig());
                    result.put("keywordConfig", keywordConfig);
                } catch (Exception e) {
                    log.warn("解析关键词配置失败 - templateType: {}", templateType, e);
                }
            }
            
            return CommonResult.data(result);
        } catch (Exception e) {
            log.error("获取模板配置失败 - templateType: {}", templateType, e);
            return CommonResult.error("获取模板配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存用户订阅信息
     * 
     * @param userId 用户ID
     * @param openId 微信openId
     * @param templateId 模板ID 
     * @param scene 场景
     */
    private void saveUserSubscription(String userId, String openId, String templateId, String scene) {
        try {
            // 检查是否已存在订阅记录
            WechatUserSubscription existingSubscription = userSubscriptionService.lambdaQuery()
                    .eq(WechatUserSubscription::getClientUserId, userId)
                    .eq(WechatUserSubscription::getTemplateId, templateId)
                    .one();
            
            if (existingSubscription != null) {
                // 更新现有记录
                existingSubscription.setSubscriptionStatus("1"); // 已授权
                existingSubscription.setSubscriptionTime(new Date());
                existingSubscription.setOpenid(openId); // 更新openId，以防变化
                existingSubscription.setRemainingTimes(1); // 微信订阅消息为一次性
                existingSubscription.setTotalSent(0); // 重置已发送次数
                existingSubscription.setLastSentTime(null);
                
                // 设置过期时间（订阅消息通常有效期30天）
                Date expireTime = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30天后
                existingSubscription.setExpireTime(expireTime);
                
                userSubscriptionService.updateById(existingSubscription);
                log.info("更新用户订阅记录成功 - userId: {}, templateId: {}, scene: {}, 重新订阅", 
                        userId, templateId, scene);
            } else {
                // 获取模板信息
                WechatSubscriptionTemplate template = templateService.lambdaQuery()
                        .eq(WechatSubscriptionTemplate::getTemplateId, templateId)
                        .one();
                
                // 创建新记录
                WechatUserSubscription subscription = new WechatUserSubscription();
                subscription.setId(IdWorker.getIdStr());
                subscription.setClientUserId(userId);
                subscription.setOpenid(openId);
                subscription.setTemplateId(templateId);
                subscription.setSubscriptionStatus("1"); // 已授权
                subscription.setSubscriptionTime(new Date());
                subscription.setRemainingTimes(1); // 微信订阅消息为一次性
                subscription.setTotalSent(0);
                
                // 设置过期时间（订阅消息通常有效期30天）
                Date expireTime = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30天后
                subscription.setExpireTime(expireTime);
                
                // 设置扩展信息
                if (template != null) {
                    subscription.setExtJson("{\"templateType\":\"" + template.getTemplateType() + 
                                          "\",\"scene\":\"" + scene + "\"}");
                }
                
                userSubscriptionService.save(subscription);
                log.info("创建用户订阅记录成功 - userId: {}, templateId: {}, scene: {}", 
                        userId, templateId, scene);
            }
            
        } catch (Exception e) {
            log.error("保存用户订阅信息失败 - userId: {}, templateId: {}, scene: {}", 
                    userId, templateId, scene, e);
            throw new RuntimeException("保存订阅信息失败: " + e.getMessage());
        }
    }
}