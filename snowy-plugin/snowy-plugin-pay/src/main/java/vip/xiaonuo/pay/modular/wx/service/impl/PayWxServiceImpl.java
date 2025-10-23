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
package vip.xiaonuo.pay.modular.wx.service.impl;

import cn.hutool.json.JSONObject;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.pay.modular.wx.param.*;
import vip.xiaonuo.pay.modular.wx.service.PayWxService;
import vip.xiaonuo.pay.modular.wx.vo.ProfitSharingProcessResult;

import java.math.BigDecimal;

/**
 * 微信支付Service接口实现类
 * 使用工厂模式，根据配置自动选择普通模式或服务商模式的实现
 *
 * @author xuyuxiang
 * @date 2023/3/28 14:13
 **/
@Slf4j
@Service
public class PayWxServiceImpl implements PayWxService {

    @Resource
    private PayWxServiceFactory payWxServiceFactory;

    /**
     * 支付回调通知
     * 回调处理逻辑在两种模式下是相同的，使用基类实现
     *
     * @param notifyData 回调数据
     * @return 处理结果
     */
    @Override
    public String notifyUrl(String notifyData) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        return payWxService.notifyUrl(notifyData);
    }

    /**
     * 退款回调
     * 回调处理逻辑在两种模式下是相同的，使用基类实现
     *
     * @param notifyData 回调数据
     * @return 处理结果
     */
    @Override
    public String refundNotifyUrl(String notifyData) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        return payWxService.refundNotifyUrl(notifyData);
    }

    /**
     * 提现回调
     * 回调处理逻辑在两种模式下是相同的，使用基类实现
     *
     * @param notifyData 回调数据
     * @return 处理结果
     */
    @Override
    public String transferNotifyUrl(String notifyData) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        return payWxService.transferNotifyUrl(notifyData);
    }

    /**
     * 账户余额查询
     * 查询逻辑在两种模式下是相同的，使用基类实现
     *
     * @return 查询结果
     */
    @Override
    public String accountQuery() {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        return payWxService.accountQuery();
    }

    /**
     * 微信JSAPI支付
     * 根据配置自动选择普通模式或服务商模式
     *
     * @param payWxJsParam 支付参数
     * @return 支付结果
     */
    @Override
    public JSONObject jsPay(PayWxJsParam payWxJsParam) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        
        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式进行JSAPI支付，订单号：{}", payWxJsParam.getOutTradeNo());
            return ((PayWxPartnerServiceImpl) payWxService).jsPay(payWxJsParam);
        } else {
            log.info("使用普通模式进行JSAPI支付，订单号：{}", payWxJsParam.getOutTradeNo());
            return ((PayWxNormalServiceImpl) payWxService).jsPay(payWxJsParam);
        }
    }

    /**
     * 服务商分账（单接收方）
     * 只有服务商模式支持分账功能
     *
     * @param profitSharingParam 分账参数
     */
    @Override
    public void profitSharing(ProfitSharingParam profitSharingParam) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        
        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式进行分账，订单号：{}", profitSharingParam.getOutTradeNo());
            ((PayWxPartnerServiceImpl) payWxService).profitSharing(profitSharingParam);
        } else {
            log.info("普通模式不支持分账功能");
            ((PayWxNormalServiceImpl) payWxService).profitSharing(profitSharingParam);
        }
    }

    /**
     * 服务商多接收方分账
     * 只有服务商模式支持分账功能
     *
     * @param multiProfitSharingParam 多接收方分账参数
     */
    @Override
    public ProfitSharingProcessResult multiProfitSharing(MultiProfitSharingParam multiProfitSharingParam) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        
        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式进行多接收方分账，订单号：{}，接收方数量：{}", 
                multiProfitSharingParam.getOutTradeNo(), 
                multiProfitSharingParam.getReceivers().size());
            return ((PayWxPartnerServiceImpl) payWxService).multiProfitSharingWithResult(multiProfitSharingParam);
        } else {
            log.warn("普通模式不支持分账功能，订单号：{}", multiProfitSharingParam.getOutTradeNo());
            throw new CommonException("普通模式不支持分账功能");
        }
    }

    /**
     * 服务商分账查询
     * 只有服务商模式支持分账查询功能
     *
     * @param outOrderNo 商户分账单号
     * @return 分账查询结果
     */
    @Override
    public String profitSharingQuery(String outOrderNo) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        
        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式进行分账查询，分账单号：{}", outOrderNo);
            return ((PayWxPartnerServiceImpl) payWxService).profitSharingQuery(outOrderNo);
        } else {
            log.info("普通模式不支持分账查询功能");
            return ((PayWxNormalServiceImpl) payWxService).profitSharingQuery(outOrderNo);
        }
    }

    /**
     * 获取订单支付结果
     * 查询逻辑在两种模式下是相同的，使用基类实现
     *
     * @param outTradeNo 订单号
     * @return 查询结果
     */
    @Override
    public WxPayOrderQueryV3Result tradeQuery(String outTradeNo) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        return payWxService.tradeQuery(outTradeNo);
    }

    /**
     * 退款查询
     * 根据配置自动选择普通模式或服务商模式
     *
     * @param outTradeNo 订单号
     * @param outRequestNo 退款单号
     * @return 退款查询结果
     */
    @Override
    public WxPayRefundQueryV3Result refundQuery(String outTradeNo, String outRequestNo) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式进行退款查询，订单号：{}，退款单号：{}", outTradeNo, outRequestNo);
            return ((PayWxPartnerServiceImpl) payWxService).refundQuery(outTradeNo, outRequestNo);
        } else {
            log.info("使用普通模式进行退款查询，订单号：{}，退款单号：{}", outTradeNo, outRequestNo);
            return ((PayWxNormalServiceImpl) payWxService).refundQuery(outTradeNo, outRequestNo);
        }
    }

    /**
     * 退款
     * 根据配置自动选择普通模式或服务商模式
     *
     * @param refundParam 退款参数
     * @return 退款结果
     */
    @Override
    public WxPayRefundV3Result doRefund(RefundParam refundParam) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式进行退款，订单号：{}", refundParam.getOutTradeNo());
            return ((PayWxPartnerServiceImpl) payWxService).doRefund(refundParam);
        } else {
            log.info("使用普通模式进行退款，订单号：{}", refundParam.getOutTradeNo());
            return ((PayWxNormalServiceImpl) payWxService).doRefund(refundParam);
        }
    }

    /**
     * 关闭订单
     * 根据配置自动选择普通模式或服务商模式
     *
     * @param payOrder 订单信息
     */
    @Override
    public void doClose(PayOrder payOrder) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        
        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式关闭订单，订单号：{}", payOrder.getOutTradeNo());
            ((PayWxPartnerServiceImpl) payWxService).doClose(payOrder);
        } else {
            log.info("使用普通模式关闭订单，订单号：{}", payOrder.getOutTradeNo());
            ((PayWxNormalServiceImpl) payWxService).doClose(payOrder);
        }
    }

    /**
     * 企业付款到零钱
     * 根据配置自动选择普通模式或服务商模式
     *
     * @param openId 用户openId
     * @param amount 转账金额
     * @param partnerTradeNo 商户转账单号
     * @param desc 转账描述
     * @return 转账结果
     */
    @Override
    public TransferBillsResult withdraw(String openId, BigDecimal amount, String partnerTradeNo, String desc) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        
        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式进行企业付款，转账单号：{}", partnerTradeNo);
            return ((PayWxPartnerServiceImpl) payWxService).withdraw(openId, amount, partnerTradeNo, desc);
        } else {
            log.info("使用普通模式进行企业付款，转账单号：{}", partnerTradeNo);
            return ((PayWxNormalServiceImpl) payWxService).withdraw(openId, amount, partnerTradeNo, desc);
        }
    }

    /**
     * 查询子商户最大分账比例
     * 根据配置自动选择普通模式或服务商模式
     *
     * @param subMchId 子商户号
     * @return 最大分账比例（百分比，如30表示30%）
     */
    @Override
    public BigDecimal queryMaxProfitSharingRatio(String subMchId) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();

        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式查询子商户最大分账比例，子商户号：{}", subMchId);
            return ((PayWxPartnerServiceImpl) payWxService).queryMaxProfitSharingRatio(subMchId);
        } else {
            log.info("普通模式不支持查询子商户分账比例功能");
            return ((PayWxNormalServiceImpl) payWxService).queryMaxProfitSharingRatio(subMchId);
        }
    }

    /**
     * 测试添加分账接收方
     * 只有服务商模式支持此功能
     */
    @Override
    public void testAddProfitSharingReceiver(String account, String subMchId) {
        AbstractPayWxService payWxService = payWxServiceFactory.getPayWxService();
        
        if (payWxService instanceof PayWxPartnerServiceImpl) {
            log.info("使用服务商模式测试添加分账接收方，账号：{}，子商户号：{}", account, subMchId);
            ((PayWxPartnerServiceImpl) payWxService).testAddProfitSharingReceiver(account, subMchId);
        } else {
            log.warn("普通模式不支持添加分账接收方功能");
            throw new CommonException("添加分账接收方功能仅在服务商模式下支持");
        }
    }
}