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

import cn.hutool.core.math.Money;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.binarywang.wxpay.bean.request.WxPayOrderCloseV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayRefundQueryV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsRequest;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.pay.core.wx.WxPayApi;
import vip.xiaonuo.pay.core.wx.WxPayApiConfigKit;
import vip.xiaonuo.pay.modular.wx.param.PayOrder;
import vip.xiaonuo.pay.modular.wx.param.PayWxJsParam;
import vip.xiaonuo.pay.modular.wx.param.ProfitSharingParam;
import vip.xiaonuo.pay.modular.wx.param.RefundParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信支付普通模式实现
 *
 * @author xuyuxiang
 * @date 2024/12/19 16:30
 **/
@Slf4j
@Component
public class PayWxNormalServiceImpl extends AbstractPayWxService {

    /**
     * 微信JSAPI支付（普通模式）
     *
     * @param payWxJsParam 支付参数
     * @return 支付结果
     */
    public JSONObject jsPay(PayWxJsParam payWxJsParam) {
        BigDecimal orderTotalAmount = orderApi.getOrderTotalAmount(payWxJsParam.getOutTradeNo());
        if (orderTotalAmount.compareTo(payWxJsParam.getTotalFee()) != 0) {
            throw new CommonException("订单金额不一致");
        }
        String clientLoginUserOpenId = clientApi.getOpenId(StpClientLoginUserUtil.getClientLoginUser().getId());
        if (ObjectUtil.isEmpty(clientLoginUserOpenId)) {
            throw new CommonException("获取用户openId失败，无法完成微信支付");
        }
        try {
            WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request();
            wxPayUnifiedOrderV3Request.setDescription(payWxJsParam.getDesc());
            wxPayUnifiedOrderV3Request.setOutTradeNo(payWxJsParam.getOutTradeNo());
            wxPayUnifiedOrderV3Request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest.yuanToFen(String.valueOf(payWxJsParam.getTotalFee()))));
            wxPayUnifiedOrderV3Request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(clientLoginUserOpenId));
            wxPayUnifiedOrderV3Request.setNotifyUrl(WxPayApiConfigKit.getWxPayConfig().getNotifyUrl());

            // 设置附加数据
            if (ObjectUtil.isNotEmpty(payWxJsParam.getAttach())) {
                wxPayUnifiedOrderV3Request.setAttach(payWxJsParam.getAttach());
            }

            // 使用WxJava SDK的createOrderV3方法来生成完整的小程序支付参数（包含package、timeStamp、nonceStr、paySign等）
            Object result = WxPayApi.me().createOrderV3(TradeTypeEnum.JSAPI, wxPayUnifiedOrderV3Request);
            
            if (ObjectUtil.isEmpty(result)) {
                throw new CommonException("微信支付预支付订单创建失败");
            }
            
            // 直接返回 WxJava SDK 生成的支付参数对象，已包含所有必要的签名数据
            return JSONUtil.parseObj(result);
        } catch (WxPayException e) {
            throw new CommonException("微信JSAPI支付异常：{}", e.getCustomErrorMsg());
        }
    }

    /**
     * 普通模式不支持分账
     *
     * @param profitSharingParam 分账参数
     */
    public void profitSharing(ProfitSharingParam profitSharingParam) {
        throw new CommonException("普通模式不支持分账功能，请使用服务商模式");
    }

    /**
     * 普通模式不支持分账查询
     *
     * @param outOrderNo 分账单号
     * @return 分账查询结果
     */
    public String profitSharingQuery(String outOrderNo) {
        throw new CommonException("普通模式不支持分账查询功能，请使用服务商模式");
    }

    /**
     * 普通模式退款查询
     *
     * @param outTradeNo 订单号
     * @param outRequestNo 退款单号
     * @return 退款查询结果
     */
    public WxPayRefundQueryV3Result refundQuery(String outTradeNo, String outRequestNo) {
        try {
            WxPayRefundQueryV3Request wxPayRefundQueryV3Request = new WxPayRefundQueryV3Request();
            wxPayRefundQueryV3Request.setOutRefundNo(outRequestNo);
            return WxPayApi.me().refundQueryV3(wxPayRefundQueryV3Request);
        } catch (WxPayException e) {
            log.error(">>> 微信退款查询异常：", e);
            return null;
        }
    }

    /**
     * 普通模式退款
     *
     * @param refundParam 退款参数
     * @return 退款结果
     */
    public WxPayRefundV3Result doRefund(RefundParam refundParam) {
        BigDecimal totalAmount = orderApi.getOrderTotalAmount(refundParam.getOutTradeNo());
        BigDecimal refundAmount = refundParam.getRefundAmount() != null ? refundParam.getRefundAmount() : totalAmount;
        WxPayRefundV3Result wxPayRefundV3Result;
        String refundNo = IdWorker.getIdStr();
        try {
            WxPayRefundV3Request wxPayRefundV3Request = new WxPayRefundV3Request();
            wxPayRefundV3Request.setOutTradeNo(refundParam.getOutTradeNo());
            wxPayRefundV3Request.setOutRefundNo(refundNo);
            wxPayRefundV3Request.setAmount(new WxPayRefundV3Request.Amount()
                    .setTotal(new Money(totalAmount).multiply(100).getAmount().intValue())
                    .setCurrency(Money.DEFAULT_CURRENCY_CODE)
                    .setRefund(new Money(refundAmount).multiply(100).getAmount().intValue()));
            wxPayRefundV3Request.setNotifyUrl(WxPayApi.REFUND_NOTIFY_URL);
            wxPayRefundV3Result = WxPayApi.me().refundV3(wxPayRefundV3Request);
        } catch (WxPayException e) {
            throw new CommonException(e.getCustomErrorMsg());
        }
        return wxPayRefundV3Result;
    }

    /**
     * 普通模式关闭订单
     *
     * @param payOrder 订单信息
     */
    public void doClose(PayOrder payOrder) {
        try {
            WxPayOrderCloseV3Request wxPayOrderCloseV3Request = new WxPayOrderCloseV3Request();
            wxPayOrderCloseV3Request.setOutTradeNo(payOrder.getOutTradeNo());
            WxPayApi.me().closeOrderV3(wxPayOrderCloseV3Request);
        } catch (WxPayException e) {
            throw new CommonException(e.getCustomErrorMsg());
        }
    }

    /**
     * 普通模式企业付款到零钱
     *
     * @param openId 用户openId
     * @param amount 转账金额
     * @param partnerTradeNo 商户转账单号
     * @param desc 转账描述
     * @return 转账结果
     */
    public TransferBillsResult withdraw(String openId, BigDecimal amount, String partnerTradeNo, String desc) {
        List<TransferBillsRequest.TransferSceneReportInfo> transferSceneReportInfos = new ArrayList<>();
        transferSceneReportInfos.add(new TransferBillsRequest.TransferSceneReportInfo("采购商品名称", "赊店白酒"));
        TransferBillsRequest transferBillsRequest = new TransferBillsRequest();
        transferBillsRequest.setAppid(WxPayApiConfigKit.getWxPayConfig().getAppId());
        transferBillsRequest.setOutBillNo(partnerTradeNo);
        transferBillsRequest.setTransferSceneId("1009");
        transferBillsRequest.setOpenid(openId);
        transferBillsRequest.setTransferAmount(amount.multiply(new BigDecimal("100")).intValue());
        transferBillsRequest.setTransferRemark(desc);
        transferBillsRequest.setTransferSceneReportInfos(transferSceneReportInfos);
        transferBillsRequest.setNotifyUrl(WxPayApi.TRANSFER_NOTIFY_URL);
        TransferService transferService = WxPayApi.me().getTransferService();
        try {
            return transferService.transferBills(transferBillsRequest);
        } catch (WxPayException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 普通模式不支持查询子商户最大分账比例
     *
     * @param subMchId 子商户号
     * @return 不支持，抛出异常
     */
    public BigDecimal queryMaxProfitSharingRatio(String subMchId) {
        throw new CommonException("普通模式不支持查询子商户分账比例功能，请使用服务商模式");
    }

    /**
     * 普通模式不支持从微信API查询子商户最大分账比例
     *
     * @param subMchId 子商户号
     * @return 不支持，抛出异常
     */
    public BigDecimal queryMaxRatioFromWxApi(String subMchId) {
        throw new CommonException("普通模式不支持从微信API查询子商户分账比例功能，请使用服务商模式");
    }
}
