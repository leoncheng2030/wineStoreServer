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
package vip.xiaonuo.pay.modular.wx.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.json.JSONObject;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.pay.modular.wx.param.PayWxJsParam;
import vip.xiaonuo.pay.modular.wx.param.RefundParam;
import vip.xiaonuo.pay.modular.wx.service.PayWxService;

import javax.validation.Valid;
import java.io.IOException;

/**
 * 微信控制器
 *
 * @author xuyuxiang
 * @date 2022/8/16 14:23
 **/
@Tag(name = "微信控制器")
@RestController
@Validated
public class PayWxController {

    @Resource
    private PayWxService payWxService;

    /**
     * 支付回调通知
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "支付回调通知")
    @CommonLog("微信支付回调")
    @PostMapping("/pay/wx/notifyUrl")
    public String notifyUrl(@RequestBody String notifyData) {
        return payWxService.notifyUrl(notifyData);
    }

    /**
     * 退款回调通知
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "退款回调通知")
    @PostMapping("/pay/wx/refundNotifyUrl")
    public String refundNotifyUrl(@RequestBody String notifyData) {
        return payWxService.refundNotifyUrl(notifyData);
    }

    /**
     * 提现回调通知
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "提现回调通知")
    @PostMapping("/pay/wx/transferNotifyUrl")
    public String transferNotifyUrl(@RequestBody String notifyData) {
        return payWxService.transferNotifyUrl(notifyData);
    }


    /**
     * 商家账户余额查询
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "商家账户余额查询")
    @GetMapping("/pay/wx/accountQuery")
    public CommonResult<String> accountQuery() {
        return CommonResult.data(payWxService.accountQuery());
    }


    /**
     * 微信JSAPI支付，返回支付所需参数
     * 根据平台配置自动选择普通支付模式或服务商支付模式
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "微信JSAPI支付（统一接口，自动选择支付模式）")
    @GetMapping("/pay/wx/jsPay")
    public CommonResult<JSONObject> jsPay(@Valid PayWxJsParam payWxJsParam) {
        return CommonResult.data(payWxService.jsPay(payWxJsParam));
    }

    /**
     * 微信退款
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "微信退款")
    @PostMapping("/wechat/refund")
    public CommonResult<WxPayRefundV3Result> refund(@RequestBody RefundParam refundParam) {
        return CommonResult.data(payWxService.doRefund(refundParam));
    }

    /**
     * 查询子商户最大分账比例
     *
     * @param subMchId 子商户号
     * @return 最大分账比例（百分比）
     * @author xuyuxiang
     * @date 2024/12/19 19:00
     */
    @Operation(summary = "查询子商户最大分账比例")
    @GetMapping("/pay/wx/queryMaxProfitSharingRatio")
    public CommonResult<java.math.BigDecimal> queryMaxProfitSharingRatio(String subMchId) {
        return CommonResult.data(payWxService.queryMaxProfitSharingRatio(subMchId));
    }

    /**
     * 测试添加分账接收方
     *
     * @param testParam 测试参数
     * @return 操作结果
     * @author xuyuxiang
     * @date 2025/09/19 13:45
     */
    @Operation(summary = "测试添加分账接收方")
    @PostMapping("/pay/wx/testAddProfitSharingReceiver")
    @CommonLog("测试添加分账接收方")
    public CommonResult<String> testAddProfitSharingReceiver(@RequestBody TestAddReceiverParam testParam) {
        payWxService.testAddProfitSharingReceiver(testParam.getAccount(), testParam.getSubMchId());
        return CommonResult.ok("添加分账接收方测试完成，请查看日志");
    }

    /**
     * 测试添加分账接收方参数类
     */
    public static class TestAddReceiverParam {
        private String account;
        private String subMchId;
        
        public String getAccount() {
            return account;
        }
        
        public void setAccount(String account) {
            this.account = account;
        }
        
        public String getSubMchId() {
            return subMchId;
        }
        
        public void setSubMchId(String subMchId) {
            this.subMchId = subMchId;
        }
    }
}
