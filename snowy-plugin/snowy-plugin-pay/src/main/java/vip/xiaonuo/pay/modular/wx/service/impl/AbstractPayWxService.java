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

import cn.hutool.core.util.ObjectUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Response;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.prop.CommonProperties;
import vip.xiaonuo.common.util.CommonServletUtil;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.pay.core.wx.WxPayApi;
import vip.xiaonuo.wine.api.OrderApi;
import vip.xiaonuo.wine.api.WineAgentApi;

/**
 * 微信支付抽象基类
 * 包含通用的回调处理和查询方法
 *
 * @author xuyuxiang
 * @date 2024/12/19 16:00
 **/
@Slf4j
public abstract class AbstractPayWxService {

    /* 微信支付logo */
    protected static final String WX_PAY_LOGO_BASE64 = "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";

    @Resource
    protected CommonProperties commonProperties;

    @Resource
    protected WxPayService wxPayService;

    @Resource
    protected OrderApi orderApi;

    @Resource
    protected ClientApi clientApi;

    @Resource
    protected DevConfigApi devConfigApi;

    @Resource
    protected WineAgentApi wineAgentApi;

    /**
     * 支付回调通知
     *
     * @param notifyData
     * @return
     */
    public String notifyUrl(String notifyData) {
        try {
            if (ObjectUtil.isEmpty(notifyData)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            
            HttpServletRequest request = CommonServletUtil.getRequest();
            String timestamp = request.getHeader("wechatpay-timestamp");
            String nonce = request.getHeader("wechatpay-nonce");
            String signature = request.getHeader("wechatpay-signature");
            String serial = request.getHeader("wechatpay-serial");
            
            if (ObjectUtil.hasEmpty(timestamp, nonce, signature, serial)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            
            SignatureHeader signatureHeader = new SignatureHeader(timestamp, nonce, signature, serial);
            WxPayNotifyV3Result wxPayOrderNotifyV3Result = WxPayApi.me().parseOrderNotifyV3Result(notifyData, signatureHeader);
            
            String outTradeNo = wxPayOrderNotifyV3Result.getResult().getOutTradeNo();
            String attachInfo = wxPayOrderNotifyV3Result.getResult().getAttach();
            String transactionId = wxPayOrderNotifyV3Result.getResult().getTransactionId();
            
            orderApi.notify(outTradeNo, attachInfo, transactionId);
            
            return WxPayNotifyV3Response.success("成功");
        } catch (WxPayException e) {
            log.error(">>> 微信异步通知处理异常：", e);
            return WxPayNotifyV3Response.fail("失败");
        }
    }

    /**
     * 退款回调
     *
     * @param notifyData
     * @return
     */
    public String refundNotifyUrl(String notifyData) {
        try {
            if (ObjectUtil.isEmpty(notifyData)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            HttpServletRequest request = CommonServletUtil.getRequest();
            String timestamp = request.getHeader("wechatpay-timestamp");
            String nonce = request.getHeader("wechatpay-nonce");
            String signature = request.getHeader("wechatpay-signature");
            String serial = request.getHeader("wechatpay-serial");
            if (ObjectUtil.hasEmpty(timestamp, nonce, signature, serial)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            SignatureHeader signatureHeader = new SignatureHeader(timestamp, nonce, signature, serial);
            WxPayRefundNotifyV3Result wxPayRefundNotifyV3Result = WxPayApi.me().parseRefundNotifyV3Result(notifyData, signatureHeader);
            WxPayRefundNotifyV3Result.DecryptNotifyResult decryptNotifyResult = wxPayRefundNotifyV3Result.getResult();
            String status = decryptNotifyResult.getRefundStatus();

            // 记录退款回调详细信息
            log.info("收到微信退款回调：退款单号={}，交易号={}，退款状态={}，订单号={}", 
                decryptNotifyResult.getOutRefundNo(), 
                decryptNotifyResult.getTransactionId(), 
                status,
                decryptNotifyResult.getOutTradeNo());
            orderApi.refundNotify(decryptNotifyResult.getOutRefundNo(), decryptNotifyResult.getTransactionId(), status, String.valueOf(decryptNotifyResult.getAmount()));
            return WxPayNotifyV3Response.success("成功");
        } catch (WxPayException e) {
            log.error(">>> 微信异步通知处理异常：", e);
            return WxPayNotifyV3Response.fail("失败");
        }
    }

    /**
     * 提现回调
     *
     * @param notifyData
     * @return
     */
    public String transferNotifyUrl(String notifyData) {
        try {
            if (ObjectUtil.isEmpty(notifyData)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            HttpServletRequest request = CommonServletUtil.getRequest();
            String timestamp = request.getHeader("wechatpay-timestamp");
            String nonce = request.getHeader("wechatpay-nonce");
            String signature = request.getHeader("wechatpay-signature");
            String serial = request.getHeader("wechatpay-serial");
            if (ObjectUtil.hasEmpty(timestamp, nonce, signature, serial)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            SignatureHeader signatureHeader = new SignatureHeader(timestamp, nonce, signature, serial);
            TransferBillsNotifyResult transferBillsNotifyResult = WxPayApi.me().parseTransferBillsNotifyV3Result(notifyData, signatureHeader);
            TransferBillsNotifyResult.DecryptNotifyResult decryptNotifyResult = transferBillsNotifyResult.getResult();
            orderApi.transferNotify(decryptNotifyResult.getOutBillNo(), decryptNotifyResult.getTransferBillNo(), decryptNotifyResult.getState());
            return WxPayNotifyV3Response.success("成功");
        } catch (WxPayException e) {
            log.error(">>> 微信异步通知处理异常：", e);
            return WxPayNotifyV3Response.fail("失败");
        }
    }

    /**
     * 获取订单支付结果
     *
     * @param outTradeNo
     * @return
     */
    public WxPayOrderQueryV3Result tradeQuery(String outTradeNo) {
        try {
            WxPayOrderQueryV3Request wxPayOrderQueryV3Request = new WxPayOrderQueryV3Request();
            wxPayOrderQueryV3Request.setOutTradeNo(outTradeNo);
            return WxPayApi.me().queryOrderV3(wxPayOrderQueryV3Request);
        } catch (WxPayException e) {
            log.error(">>> 微信交易查询异常：", e);
            return null;
        }
    }

    /**
     * 账户余额查询
     *
     * @return
     */
    public String accountQuery() {
        throw new CommonException("暂不支持微信商家账户余额查询");
    }
}
