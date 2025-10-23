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
package vip.xiaonuo.wine.core.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.common.timer.CommonTimerTaskRunner;
import vip.xiaonuo.wine.modular.commissionrecord.entity.WineCommissionRecord;
import vip.xiaonuo.wine.modular.commissionrecord.enums.WineCommissionRecordStatusEnum;
import vip.xiaonuo.wine.modular.commissionrecord.service.WineCommissionRecordService;
import vip.xiaonuo.wine.modular.order.entity.WineOrder;
import vip.xiaonuo.wine.modular.order.enums.WineOrderProfitSharingStatusEnum;
import vip.xiaonuo.wine.modular.order.enums.WineOrderStatusEnum;
import vip.xiaonuo.wine.modular.order.service.WineOrderService;
import vip.xiaonuo.wine.modular.order.service.WineOrderProfitSharingService;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 定时分账任务
 * 
 * 业务逻辑：
 * 1. 扫描待分账的订单
 * 2. 检查订单支付时间是否超过1分钟
 * 3. 从佣金记录表获取分账金额
 * 4. 调用微信分账接口
 * 5. 更新分账状态
 *
 * @author jetox
 * @date 2025/09/19
 */
@Slf4j
@Component
public class ProfitSharingScheduledTask implements CommonTimerTaskRunner {

    @Resource
    private WineOrderService wineOrderService;

    @Resource
    private WineCommissionRecordService wineCommissionRecordService;

    @Resource
    private WineOrderProfitSharingService wineOrderProfitSharingService;

    @Resource
    private vip.xiaonuo.pay.modular.wx.service.impl.PayWxServiceFactory payWxServiceFactory;

    @Override
    public void action(String extJson) {
        log.info(">>> 定时分账任务执行开始 <<<");

        try {
            // 0. 检查支付模式，只有服务商模式才需要执行分账
            boolean isPartnerMode = payWxServiceFactory.isPartnerMode();
            if (!isPartnerMode) {
                log.info("当前为普通支付模式，无需执行分账任务");

                // 处理普通模式下可能存在的历史待分账记录
                handleHistoricalRecordsInNormalMode();
                return;
            }

            log.info("当前为服务商支付模式，开始执行分账任务");

            // 1. 查询待分账的订单
            List<WineOrder> pendingOrders = getPendingProfitSharingOrders();
            if (CollUtil.isEmpty(pendingOrders)) {
                log.info("暂无待分账订单");
                return;
            }

            log.info("发现{}个待分账订单", pendingOrders.size());

            // 2. 逐个处理分账订单
            for (WineOrder order : pendingOrders) {
                try {
                    processSingleOrderProfitSharing(order);
                } catch (Exception e) {
                    log.error("处理订单分账异常，订单号：{}，错误信息：{}", order.getOrderNo(), e.getMessage(), e);
                    // 更新订单分账状态为异常，并保存失败原因
                    updateOrderProfitSharingStatusWithReason(order.getId(), WineOrderProfitSharingStatusEnum.ERROR.getValue(), "分账异常：" + e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("定时分账任务执行异常", e);
        }

        log.info(">>> 定时分账任务执行完成 <<<");
    }

    /**
     * 获取待分账的订单
     */
    private List<WineOrder> getPendingProfitSharingOrders() {
        LambdaQueryWrapper<WineOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineOrder::getProfitSharingStatus, WineOrderProfitSharingStatusEnum.PENDING.getValue()) // 待分账状态
                .isNotNull(WineOrder::getPayTime) // 有支付时间
                .and(wrapper -> wrapper
                        .eq(WineOrder::getStatus, WineOrderStatusEnum.WAIT_DELIVER.getValue()) // 支付成功立即分佣的订单（待取酒状态）
                        .or()
                        .eq(WineOrder::getStatus, WineOrderStatusEnum.TRADE_SUCCESS.getValue()) // 出酒完成后分佣的订单（交易成功状态）
                )
                .orderByAsc(WineOrder::getPayTime); // 按支付时间升序

        return wineOrderService.list(queryWrapper);
    }

    /**
     * 处理单个订单的分账
     */
    private void processSingleOrderProfitSharing(WineOrder order) {
        String orderNo = order.getOrderNo();
        log.info("开始处理订单分账：{}", orderNo);

        // 1. 检查支付时间是否满足分账条件（支付成功1分钟后）
        if (!checkPaymentTimeEligible(order)) {
            log.info("订单{}支付时间未满1分钟，跳过分账", orderNo);
            return;
        }

        // 2. 获取该订单的佣金记录
        List<WineCommissionRecord> commissionRecords = getOrderCommissionRecords(order.getOrderNo());
        if (CollUtil.isEmpty(commissionRecords)) {
            log.warn("订单{}无佣金记录，标记为无需分账", orderNo);
            updateOrderProfitSharingStatus(order.getId(), WineOrderProfitSharingStatusEnum.NO_NEED.getValue());
            return;
        }

        // 3. 更新订单状态为分账中
        updateOrderProfitSharingStatus(order.getId(), WineOrderProfitSharingStatusEnum.PROCESSING.getValue());

        // 4. 更新佣金记录状态为分账中
        updateCommissionRecordsStatus(commissionRecords, WineCommissionRecordStatusEnum.PROFIT_SHARING.getValue());

        try {
            // 5. 执行分账
            wineOrderProfitSharingService.processCommissionAndProfitSharingByCommissionRecords(orderNo, commissionRecords);

            // 6. 分账成功，更新状态
            updateOrderProfitSharingStatus(order.getId(), WineOrderProfitSharingStatusEnum.SUCCESS.getValue());
            updateCommissionRecordsStatus(commissionRecords, WineCommissionRecordStatusEnum.PROFIT_SHARED.getValue());

            log.info("订单{}分账成功", orderNo);

        } catch (Exception e) {
            log.error("订单{}分账失败：{}", orderNo, e.getMessage(), e);

            // 分账失败，更新状态并保存失败原因
            updateOrderProfitSharingStatusWithReason(order.getId(), WineOrderProfitSharingStatusEnum.FAILED.getValue(), "分账失败：" + e.getMessage());
            updateCommissionRecordsStatus(commissionRecords, WineCommissionRecordStatusEnum.PROFIT_SHARING_FAILED.getValue());
        }
    }

    /**
     * 检查支付时间是否满足分账条件（支付成功1分钟后）
     */
    private boolean checkPaymentTimeEligible(WineOrder order) {
        if (order.getPayTime() == null) {
            return false;
        }

        // 计算支付后经过的时间
        long paymentTime = order.getPayTime().getTime();
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - paymentTime;

        // 支付成功1分钟后才能分账
        return elapsedTime >= 60000; // 60000毫秒 = 1分钟
    }

    /**
     * 获取订单的佣金记录
     */
    private List<WineCommissionRecord> getOrderCommissionRecords(String orderNo) {
        LambdaQueryWrapper<WineCommissionRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineCommissionRecord::getOrderNo, orderNo)
                .eq(WineCommissionRecord::getStatus, WineCommissionRecordStatusEnum.WAIT_PROFIT_SHARING.getValue()); // 待分账状态

        return wineCommissionRecordService.list(queryWrapper);
    }

    /**
     * 更新订单分账状态
     */
    private void updateOrderProfitSharingStatus(String orderId, String status) {
        updateOrderProfitSharingStatusWithReason(orderId, status, null);
    }

    /**
     * 更新订单分账状态并保存失败原因
     */
    private void updateOrderProfitSharingStatusWithReason(String orderId, String status, String failureReason) {
        WineOrder updateOrder = new WineOrder();
        updateOrder.setId(orderId);
        updateOrder.setProfitSharingStatus(status);

        // 如果是失败或异常状态，保存失败原因
        if ((WineOrderProfitSharingStatusEnum.FAILED.getValue().equals(status) ||
                WineOrderProfitSharingStatusEnum.ERROR.getValue().equals(status)) &&
                ObjectUtil.isNotEmpty(failureReason)) {
            // 截取前255个字符，避免数据库字段超长
            String truncatedReason = failureReason.length() > 255 ? failureReason.substring(0, 255) : failureReason;
            updateOrder.setProfitSharingFailureReason(truncatedReason);
        } else if (WineOrderProfitSharingStatusEnum.SUCCESS.getValue().equals(status)) {
            // 分账成功时清空失败原因
            updateOrder.setProfitSharingFailureReason(null);
        }

        wineOrderService.updateById(updateOrder);
    }

    /**
     * 更新佣金记录状态
     */
    private void updateCommissionRecordsStatus(List<WineCommissionRecord> records, String status) {
        for (WineCommissionRecord record : records) {
            WineCommissionRecord updateRecord = new WineCommissionRecord();
            updateRecord.setId(record.getId());

            // 根据支付模式和业务逻辑确定最终状态
            String finalStatus = determineFinalCommissionStatus(status);
            updateRecord.setStatus(finalStatus);

            // 如果是分账成功或已发放，需要设置发放时间
            if (WineCommissionRecordStatusEnum.PROFIT_SHARED.getValue().equals(finalStatus) ||
                    WineCommissionRecordStatusEnum.SETTLED.getValue().equals(finalStatus)) {
                updateRecord.setSettleTime(new Date());
            }

            wineCommissionRecordService.updateById(updateRecord);
        }
    }

    /**
     * 根据支付模式确定佣金记录的最终状态
     *
     * @param targetStatus 目标状态
     * @return 最终状态
     */
    private String determineFinalCommissionStatus(String targetStatus) {
        // 检查当前支付模式
        boolean isPartnerMode = payWxServiceFactory.isPartnerMode();

        if (WineCommissionRecordStatusEnum.PROFIT_SHARED.getValue().equals(targetStatus)) {
            if (isPartnerMode) {
                // 服务商模式：真正的分账成功
                return WineCommissionRecordStatusEnum.PROFIT_SHARED.getValue();
            } else {
                // 普通模式：不支持分账，应该显示为已发放
                log.debug("普通支付模式下，将分账成功状态转换为已发放状态");
                return WineCommissionRecordStatusEnum.SETTLED.getValue();
            }
        }

        // 其他状态保持不变
        return targetStatus;
    }

    /**
     * 手动触发分账任务（用于管理员手动触发）
     */
    public void manualExecuteProfitSharing() {
        log.info("手动触发定时分账任务");
        action(null);
    }

    /**
     * 处理普通模式下可能存在的历史待分账记录
     * 如果从服务商模式切换到普通模式，可能会有一些历史记录处于待分账状态
     */
    private void handleHistoricalRecordsInNormalMode() {
        try {
            log.info("检查普通模式下的历史待分账记录...");

            // 查询所有待分账状态的佣金记录
            LambdaQueryWrapper<WineCommissionRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WineCommissionRecord::getStatus, WineCommissionRecordStatusEnum.WAIT_PROFIT_SHARING.getValue());

            List<WineCommissionRecord> waitingRecords = wineCommissionRecordService.list(queryWrapper);

            if (CollUtil.isNotEmpty(waitingRecords)) {
                log.info("发现{}条历史待分账记录，在普通模式下将其更新为已发放状态", waitingRecords.size());

                for (WineCommissionRecord record : waitingRecords) {
                    WineCommissionRecord updateRecord = new WineCommissionRecord();
                    updateRecord.setId(record.getId());
                    updateRecord.setStatus(WineCommissionRecordStatusEnum.SETTLED.getValue());
                    updateRecord.setSettleTime(new Date());

                    wineCommissionRecordService.updateById(updateRecord);

                    log.debug("更新佣金记录{}状态为已发放，订单号：{}", record.getId(), record.getOrderNo());
                }

                log.info("已将{}条历史待分账记录更新为已发放状态", waitingRecords.size());
            } else {
                log.debug("普通模式下无历史待分账记录");
            }

        } catch (Exception e) {
            log.error("处理普通模式下历史记录异常：{}", e.getMessage(), e);
        }
    }
}
