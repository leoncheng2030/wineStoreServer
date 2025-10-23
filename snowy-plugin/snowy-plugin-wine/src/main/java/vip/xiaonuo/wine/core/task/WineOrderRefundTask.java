package vip.xiaonuo.wine.core.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.common.timer.CommonTimerTaskRunner;
import vip.xiaonuo.pay.modular.wx.param.RefundParam;
import vip.xiaonuo.pay.modular.wx.service.PayWxService;
import vip.xiaonuo.wine.modular.order.entity.WineOrder;
import vip.xiaonuo.wine.modular.order.enums.WineOrderStatusEnum;
import vip.xiaonuo.wine.modular.order.service.WineOrderService;

import java.util.Date;
import java.util.List;

/**
 * 超时未出酒订单自动退款任务
 * 定时检查5分钟内支付完成但尚未出酒的订单，并执行自动退款
 *
 * @author anonymous
 * @date 2025/08/31
 */
@Slf4j
@Component
public class WineOrderRefundTask implements CommonTimerTaskRunner {

    @Resource
    private WineOrderService wineOrderService;

    @Resource
    private PayWxService payWxService;

    @Override
    public void action(String extJson) {
        log.info("开始执行超时未出酒订单自动退款任务");

        try {
            // 计算5分钟前的时间点
            DateTime fiveMinutesAgo = DateUtil.offsetMinute(new Date(), -5);

            // 查询5分钟内支付完成但尚未出酒的订单
            LambdaQueryWrapper<WineOrder> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WineOrder::getStatus, WineOrderStatusEnum.WAIT_DELIVER.getValue())
                    .le(WineOrder::getPayTime, fiveMinutesAgo);

            List<WineOrder> timeoutOrders = wineOrderService.list(queryWrapper);

            if (ObjectUtil.isEmpty(timeoutOrders)) {
                log.info("没有找到超时未出酒的订单");
                return;
            }

            log.info("找到 {} 个超时未出酒的订单，开始执行退款操作", timeoutOrders.size());

            // 对每个超时订单执行退款操作
            for (WineOrder order : timeoutOrders) {
                try {
                    log.info("正在处理订单 {} 的退款操作", order.getOrderNo());

                    // 更新订单状态为退款中
                    order.setStatus(WineOrderStatusEnum.REFUNDING.getValue());
                    wineOrderService.updateById(order);

                    // 执行退款
                    RefundParam refundParam = new RefundParam();
                    refundParam.setOutTradeNo(order.getOrderNo());
                    refundParam.setRefundAmount(order.getTotalAmount());
                    payWxService.doRefund(refundParam);

                    log.info("订单 {} 退款请求已提交", order.getOrderNo());
                } catch (Exception e) {
                    log.error("处理订单 {} 退款时发生异常", order.getOrderNo(), e);
                    // 恢复订单状态为待取酒，以便下次重试
                    order.setStatus(WineOrderStatusEnum.WAIT_DELIVER.getValue());
                    wineOrderService.updateById(order);
                }
            }
        } catch (Exception e) {
            log.error("执行超时未出酒订单自动退款任务时发生异常", e);
        }

        log.info("超时未出酒订单自动退款任务执行完毕");
    }
}