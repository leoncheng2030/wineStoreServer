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
package vip.xiaonuo.pay.modular.wx.service;

import cn.hutool.json.JSONObject;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsResult;
import vip.xiaonuo.pay.modular.wx.param.*;
import vip.xiaonuo.pay.modular.wx.vo.ProfitSharingProcessResult;

import java.math.BigDecimal;

/**
 * 微信支付Service接口
 *
 * @author xuyuxiang
 * @date 2023/3/28 14:12
 **/
public interface PayWxService {

    /**
     * 支付回调通知
     *
     * @author xuyuxiang
     * @date 2023/3/27 14:25
     **/
    String notifyUrl(String notifyData);

    /**
     * 退款回调通知
     *
     * @author xuyuxiang
     * @date 2023/3/27 14:25
     **/
    String refundNotifyUrl(String notifyData);
    /**
     * 提现回调通知
     *
     * @author xuyuxiang
     * @date 2023/3/27 14:25
     **/
    String transferNotifyUrl(String notifyData);
    /**
     * 商家账户余额查询
     *
     * @author xuyuxiang
     * @date 2023/3/30 13:18
     **/
    String accountQuery();


    /**
     * 微信JSAPI支付（统一接口，自动选择普通支付或服务商支付模式）
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    JSONObject jsPay(PayWxJsParam payWxJsParam);

//    /**
//     * 微信JSAPI支付（普通模式）
//     *
//     * @author xuyuxiang
//     * @date 2023/3/21 16:17
//     **/
//    JSONObject jsPayNormal(PayWxJsParam payWxJsParam);
//
//    /**
//     * 微信JSAPI支付（服务商模式）
//     *
//     * @author xuyuxiang
//     * @date 2024/12/19 16:17
//     **/
//    JSONObject jsPayPartner(PayWxJsParam payWxJsParam);

    /**
     * 分账接口（单接收方）
     */
    void profitSharing(ProfitSharingParam profitSharingParam);

    /**
     * 多接收方分账接口
     */
    ProfitSharingProcessResult multiProfitSharing(MultiProfitSharingParam multiProfitSharingParam);

    /**
     * 分账结果查询
     *
     * @param outOrderNo 商户分账单号
     * @return 分账查询结果
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    String profitSharingQuery(String outOrderNo);
    /**
     * 交易查询
     *
     * @author xuyuxiang
     * @date 2023/3/28 14:19
     **/
    WxPayOrderQueryV3Result tradeQuery(String outTradeNo);

    /**
     * 退款查询
     *
     * @author xuyuxiang
     * @date 2023/3/28 14:19
     **/
    WxPayRefundQueryV3Result refundQuery(String outTradeNo, String outRequestNo);

    /**
     * 执行退款
     *
     * @author xuyuxiang
     * @date 2023/3/29 21:40
     */
    WxPayRefundV3Result doRefund(RefundParam refundParam);

    /**
     * 执行关闭
     *
     * @author xuyuxiang
     * @date 2023/3/29 21:40
     */
    void doClose(PayOrder payOrder);

    /**
     * 企业付款至用户零钱
     * @return 退款结果
     */
    TransferBillsResult withdraw(String openId, BigDecimal amount, String partnerTradeNo, String desc);

    /**
     * 查询子商户最大分账比例
     * 服务商模式下查询指定子商户的最大分账比例
     *
     * @param subMchId 子商户号
     * @return 最大分账比例（百分比，如30表示30%）
     * @author xuyuxiang
     * @date 2024/12/19 18:00
     **/
    BigDecimal queryMaxProfitSharingRatio(String subMchId);

    /**
     * 测试添加分账接收方
     * 
     * @param account 接收方账号
     * @param subMchId 子商户号
     */
    void testAddProfitSharingReceiver(String account, String subMchId);
}
