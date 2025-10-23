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
package vip.xiaonuo.wine.modular.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.core.task.ProfitSharingScheduledTask;

import jakarta.annotation.Resource;

/**
 * 分账任务控制器
 *
 * @author jetox
 * @date 2025/09/19
 */
@Tag(name = "分账任务控制器")
@Slf4j
@RestController
@Validated
public class ProfitSharingJobController {

    @Resource
    private ProfitSharingScheduledTask profitSharingScheduledTask;

    /**
     * 手动触发分账任务
     */
    @Operation(summary = "手动触发分账任务")
    @PostMapping("/wine/order/triggerProfitSharingJob")
    @CommonLog("手动触发分账任务")
    public CommonResult<String> triggerProfitSharingJob() {
        try {
            profitSharingScheduledTask.manualExecuteProfitSharing();
            return CommonResult.ok("分账任务执行完成，请查看日志");
        } catch (Exception e) {
            log.error("手动触发分账任务异常", e);
            return CommonResult.error("分账任务执行失败：" + e.getMessage());
        }
    }
}
