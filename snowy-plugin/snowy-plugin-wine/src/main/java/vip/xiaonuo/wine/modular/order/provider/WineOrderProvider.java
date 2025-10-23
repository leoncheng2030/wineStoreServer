package vip.xiaonuo.wine.modular.order.provider;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.wine.api.OrderApi;
import vip.xiaonuo.wine.modular.agent.entity.WineAgent;
import vip.xiaonuo.wine.modular.agent.service.WineAgentService;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;
import vip.xiaonuo.wine.modular.order.entity.WineOrder;
import vip.xiaonuo.wine.modular.order.enums.WineOrderProfitSharingStatusEnum;
import vip.xiaonuo.wine.modular.order.enums.WineOrderStatusEnum;
import vip.xiaonuo.wine.modular.order.service.WineOrderService;
import vip.xiaonuo.wine.modular.store.entity.WineStore;
import vip.xiaonuo.wine.modular.store.service.WineStoreService;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class WineOrderProvider implements OrderApi {
    @Resource
    private WineOrderService wineOrderService;
    @Resource
    private DevConfigApi devConfigApi;

    @Resource
    private WineDeviceService wineDeviceService;
    @Resource
    private WineStoreService wineStoreService;
    @Autowired
    private WineAgentService wineAgentService;

    @Override
    public void notify(String outTradeNo, String attachInfo, String transactionId) {
        log.info("收到支付回调通知：订单号={}，交易号={}，附加信息={}", outTradeNo, transactionId, attachInfo);
        
        // 根据订单号查询订单
        WineOrder wineOrder = wineOrderService.getOne(new LambdaQueryWrapper<WineOrder>().eq(WineOrder::getOrderNo, outTradeNo));
        if (ObjectUtil.isNotEmpty(wineOrder)) {
            log.info("找到订单：订单ID={}，订单号={}，当前状态={}", wineOrder.getId(), wineOrder.getOrderNo(), wineOrder.getStatus());
            
            // 订单存在，处理订单
            wineOrder.setTransactionId(transactionId);
            // 设置支付时间和订单状态
            wineOrder.setPayTime(new Date());
            wineOrder.setStatus(WineOrderStatusEnum.WAIT_DELIVER.getValue());
            // 更新订单
            wineOrderService.updateById(wineOrder);
            log.info("订单状态已更新为：{}", wineOrder.getStatus());
            
            // 检查分佣配置，完整判断所有配置情况
            String commissionDistribution = devConfigApi.getValueByKey("COMMISSION_DISTRIBUTION");
            log.info("支付成功处理，订单号：{}，分佣配置：{}", outTradeNo, commissionDistribution);
            
            if ("1".equals(commissionDistribution)) {
                // 支付成功立即分佣分账
                log.info("支付成功立即分佣模式：触发分佣分账，订单号：{}", outTradeNo);
                try {
                    wineOrderService.processCommissionAndProfitSharingForOrder(wineOrder);
                    log.info("支付成功分佣分账处理成功，订单号：{}", outTradeNo);
                } catch (Exception e) {
                    log.error("支付成功分佣分账处理失败，订单号：{}，错误：{}", outTradeNo, e.getMessage(), e);
                    // 不抛出异常，避免影响支付成功状态
                }
            } else if ("2".equals(commissionDistribution)) {
                // 出酒完成后分佣：支付成功时只创建佣金记录，不执行分账
                log.info("出酒完成后分佣模式：支付成功时创建佣金记录，订单号：{}", outTradeNo);
                try {
                    wineOrderService.processCommissionAndProfitSharingForOrder(wineOrder);
                    log.info("支付成功佣金记录创建成功，订单号：{}", outTradeNo);
                } catch (Exception e) {
                    log.error("支付成功佣金记录创建失败，订单号：{}，错误：{}", outTradeNo, e.getMessage(), e);
                    // 不抛出异常，避免影响支付成功状态
                }
            } else {
                // 未配置或不支持的分佣配置：记录日志但不影响支付流程
                log.warn("未配置或不支持的分佣配置：{}，订单号：{}，跳过分佣处理", commissionDistribution, outTradeNo);
            }
        } else {
            log.warn("未找到订单：{}", outTradeNo);
        }
    }

    @Override
    public void refundNotify(String refundNo, String transactionId, String status, String Amount) {
        log.info("收到退款回调通知：退款单号={}，交易号={}，状态={}", refundNo, transactionId, status);
        
        // 根据微信交易号查询订单（根据历史经验教训，应使用transactionId而非退款单号）
        WineOrder wineOrder = wineOrderService.getOne(new LambdaQueryWrapper<WineOrder>().eq(WineOrder::getTransactionId, transactionId));
        if (ObjectUtil.isNotEmpty(wineOrder)) {
            log.info("找到订单：订单ID={}，订单号={}，当前状态={}", wineOrder.getId(), wineOrder.getOrderNo(), wineOrder.getStatus());

            // 设置退款时间
            wineOrder.setRefundTime(new Date());
            
            // 设置退款金额（如果尚未设置）
            if (wineOrder.getRefundAmount() == null) {
                wineOrder.setRefundAmount(wineOrder.getTotalAmount());
            }
            
            // 根据退款状态更新订单状态
            if ("SUCCESS".equals(status)) {
                // 退款成功，订单状态更新为交易关闭
                if (wineOrder.getTotalAmount().compareTo(new BigDecimal(Amount)) == 0) {
                    wineOrder.setStatus(WineOrderStatusEnum.TRADE_CLOSED.getValue());
                }else{
                    wineOrder.setStatus(WineOrderStatusEnum.TRADE_SUCCESS.getValue());
                }
                log.info("退款成功，订单状态更新为交易关闭，订单号：{}", wineOrder.getOrderNo());
            } else if ("PROCESSING".equals(status)) {
                // 退款处理中，保持退款中状态
                wineOrder.setStatus(WineOrderStatusEnum.REFUNDING.getValue());
                log.info("退款处理中，保持退款中状态，订单号：{}", wineOrder.getOrderNo());
            } else if ("CLOSED".equals(status)) {
                // 退款关闭，恢复到原状态或设置为退款失败状态
                wineOrder.setStatus(WineOrderStatusEnum.WAIT_DELIVER.getValue());
                log.info("退款关闭，订单状态恢复到待取酒，订单号：{}", wineOrder.getOrderNo());
            } else if ("ABNORMAL".equals(status)) {
                // 退款异常，需要人工处理
                log.warn("退款异常，需要人工处理，订单号：{}", wineOrder.getOrderNo());
                // 可以添加相应的异常处理逻辑，比如发送通知等
            } else {
                log.warn("未知的退款状态：{}，订单号：{}", status, wineOrder.getOrderNo());
            }

            // 更新订单
            boolean updateResult = wineOrderService.updateById(wineOrder);
            if (updateResult) {
                log.info("订单退款状态已更新，订单号：{}，新状态：{}", wineOrder.getOrderNo(), wineOrder.getStatus());
            } else {
                log.error("订单退款状态更新失败，订单号：{}", wineOrder.getOrderNo());
            }
        } else {
            log.warn("未找到对应的订单：交易号={}，退款单号={}", transactionId, refundNo);
        }
    }

    @Override
    public void transferNotify(String outBillNo, String transferBillNo, String state) {
        // todo: 转账成功
    }

    @Override
    public BigDecimal getOrderTotalAmount(String outTradeNo) {
        QueryWrapper<WineOrder> wineOrderQueryWrapper = new QueryWrapper<WineOrder>().checkSqlInjection();
        wineOrderQueryWrapper.lambda().eq(WineOrder::getOrderNo, outTradeNo);
        WineOrder wineOrder = wineOrderService.getOne(wineOrderQueryWrapper);
        if (ObjectUtil.isEmpty(wineOrder)) {
            throw new RuntimeException("订单不存在，订单号：" + outTradeNo);
        }
        return wineOrder.getTotalAmount();
    }

    @Override
    public String getStoreSubMchIdByOutTradNo(String outTradeNo) {
        QueryWrapper<WineOrder> wineOrderQueryWrapper = new QueryWrapper<WineOrder>().checkSqlInjection();
        wineOrderQueryWrapper.lambda().eq(WineOrder::getOrderNo, outTradeNo);
        WineOrder wineOrder = wineOrderService.getOne(wineOrderQueryWrapper);
        if (ObjectUtil.isEmpty(wineOrder)) {
            throw new RuntimeException("订单不存在，订单号：" + outTradeNo);
        }
        WineDevice one = wineDeviceService.getOne(new LambdaQueryWrapper<WineDevice>().eq(WineDevice::getId, wineOrder.getDeviceId()));
        if (ObjectUtil.isEmpty(one)) {
            throw new RuntimeException("设备不存在，设备ID：" + wineOrder.getDeviceId());
        }
        String agentUserId = one.getAgentUserId();
        if (ObjectUtil.isEmpty(agentUserId)) {
            throw new RuntimeException("设备未关联代理商，设备ID：" + wineOrder.getDeviceId());
        }
        WineAgent wineAgent = wineAgentService.getOne(new LambdaQueryWrapper<WineAgent>().eq(WineAgent::getClientUserId, agentUserId));
        if (ObjectUtil.isEmpty(wineAgent)) {
            throw new RuntimeException("代理商信息不存在，用户ID：" + agentUserId);
        }
        if (ObjectUtil.isEmpty(wineAgent.getSubMerId())) {
            throw new RuntimeException("代理商未配置子商户号，代理商ID：" + wineAgent.getId());
        }
        // 返回时清理子商户号，去除首尾空格，避免URL中出现非法字符
        return wineAgent.getSubMerId().trim();
    }

    @Override
    public boolean isAgentDeviceOrder(String outTradeNo) {
        try {
            // 根据订单号查询订单
            WineOrder wineOrder = wineOrderService.getOne(
                new LambdaQueryWrapper<WineOrder>().eq(WineOrder::getOrderNo, outTradeNo)
            );
            if (ObjectUtil.isEmpty(wineOrder)) {
                return false;
            }
            
            // 根据订单获取设备信息
            WineDevice wineDevice = wineDeviceService.getById(wineOrder.getDeviceId());
            if (ObjectUtil.isEmpty(wineDevice)) {
                return false;
            }
            
            // 判断是否为代理商设备（与WineOrderServiceImpl中的逻辑保持一致）
            return "OPERATION_TYPE_AGENT".equals(wineDevice.getOperationType())
                    && wineDevice.getAgentUserId() != null
                    && !wineDevice.getAgentUserId().isEmpty();
                    
        } catch (Exception e) {
            // 出现异常时返回false，避免影响支付流程
            return false;
        }
    }

    @Override
    public String getTransactionIdByOutTradeNo(String outTradeNo) {
        try {
            // 根据订单号查询订单
            WineOrder wineOrder = wineOrderService.getOne(
                new LambdaQueryWrapper<WineOrder>().eq(WineOrder::getOrderNo, outTradeNo)
            );
            if (ObjectUtil.isNotEmpty(wineOrder)) {
                return wineOrder.getTransactionId();
            }
        } catch (Exception e) {
            log.error("根据订单号获取交易号异常：订单号={}，异常：{}", outTradeNo, e.getMessage(), e);
        }
        return null;
    }

}
