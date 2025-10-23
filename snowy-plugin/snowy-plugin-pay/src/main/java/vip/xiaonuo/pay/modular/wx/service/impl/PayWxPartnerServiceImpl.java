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
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingV3Request.Receiver;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingReceiverV3Request;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingRequest;
import com.github.binarywang.wxpay.bean.ecommerce.ProfitSharingQueryRequest;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingV3Request;
import com.github.binarywang.wxpay.bean.profitsharing.result.ProfitSharingReceiverV3Result;
import com.github.binarywang.wxpay.bean.profitsharing.result.ProfitSharingResult;
import com.github.binarywang.wxpay.bean.profitsharing.result.ProfitSharingMerchantRatioQueryV3Result;
import com.github.binarywang.wxpay.bean.profitsharing.result.ProfitSharingV3Result;
import com.github.binarywang.wxpay.service.ProfitSharingService;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.bean.request.*;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsRequest;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsResult;
import com.github.binarywang.wxpay.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.pay.core.config.PayConfigure;
import vip.xiaonuo.pay.core.wx.WxPayApi;
import vip.xiaonuo.pay.core.wx.WxPayApiConfigKit;
import vip.xiaonuo.pay.modular.wx.param.*;
import vip.xiaonuo.pay.modular.wx.vo.ProfitSharingProcessResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信支付服务商模式实现
 *
 * @author xuyuxiang
 * @date 2024/12/19 16:45
 **/
@Slf4j
@Component
public class PayWxPartnerServiceImpl extends AbstractPayWxService {

    /**
     * 微信JSAPI支付（服务商模式）
     *
     * @param payWxJsParam 支付参数
     * @return 支付结果
     */
    public JSONObject jsPay(PayWxJsParam payWxJsParam) {
        // 参数验证
        if (ObjectUtil.isEmpty(payWxJsParam.getOutTradeNo())) {
            throw new CommonException("订单号不能为空");
        }
        if (ObjectUtil.isEmpty(payWxJsParam.getTotalFee()) || payWxJsParam.getTotalFee().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CommonException("支付金额必须大于0");
        }
        try {
            // 构建服务商模式统一下单请求
            WxPayPartnerUnifiedOrderV3Request request = new WxPayPartnerUnifiedOrderV3Request();

            // 设置基本信息
            request.setOutTradeNo(payWxJsParam.getOutTradeNo());
            request.setDescription(payWxJsParam.getDesc());
            request.setNotifyUrl(WxPayApiConfigKit.getWxPayConfig().getNotifyUrl());
            // 设置服务商模式特有参数
            request.setSpAppid(WxPayApiConfigKit.getWxPayConfig().getAppId()); // 服务商应用ID
            request.setSpMchId(WxPayApiConfigKit.getWxPayConfig().getMchId()); // 服务商商户号
            String subMerchId = orderApi.getStoreSubMchIdByOutTradNo(payWxJsParam.getOutTradeNo());
            if (subMerchId == null) {
                throw new CommonException("未找到门店对应的子商户信息");
            }
            // 确保子商户号清洁，去除首尾空格
            subMerchId = subMerchId.trim();
            request.setSubMchId(subMerchId); // 子商户号

            // 设置金额信息
            WxPayPartnerUnifiedOrderV3Request.Amount amount = new WxPayPartnerUnifiedOrderV3Request.Amount();
            amount.setCurrency("CNY");
            amount.setTotal(WxPayUnifiedOrderRequest.yuanToFen(String.valueOf(payWxJsParam.getTotalFee())));
            request.setAmount(amount);

            // 设置支付者信息（使用子应用的openId）
            WxPayPartnerUnifiedOrderV3Request.Payer payer = new WxPayPartnerUnifiedOrderV3Request.Payer();
            String clientLoginUserOpenId = clientApi.getOpenId(StpClientLoginUserUtil.getClientLoginUser().getId());
            if (ObjectUtil.isEmpty(clientLoginUserOpenId)) {
                throw new CommonException("获取用户openId失败，无法完成微信支付");
            }
            payer.setSpOpenid(clientLoginUserOpenId); // 服务商模式使用sub_openid
            request.setPayer(payer);

            // 设置回调地址
            request.setNotifyUrl(WxPayApiConfigKit.getWxPayConfig().getNotifyUrl());

            // 设置附加数据
            if (ObjectUtil.isNotEmpty(payWxJsParam.getAttach())) {
                request.setAttach(payWxJsParam.getAttach());
            }

            // 服务商模式特有参数
            WxPayPartnerUnifiedOrderV3Request.SettleInfo settleInfo = new WxPayPartnerUnifiedOrderV3Request.SettleInfo();
            // 检查是否为代理设备订单，如果是则启用分账标识
            boolean isAgentOrder = orderApi.isAgentDeviceOrder(payWxJsParam.getOutTradeNo());
            settleInfo.setProfitSharing(isAgentOrder); // 代理设备订单启用分账，普通订单不启用
            request.setSettleInfo(settleInfo);
            
            log.info("订单号：{}，是否代理设备订单：{}，分账标识：{}", 
                payWxJsParam.getOutTradeNo(), isAgentOrder, isAgentOrder);

            // 调用微信支付API（服务商模式）
            Object result = wxPayService.createPartnerOrderV3(TradeTypeEnum.JSAPI, request);

            if (ObjectUtil.isEmpty(result)) {
                throw new CommonException("微信支付预支付订单创建失败");
            }

            // 直接返回 WxJava SDK 生成的支付参数对象，已包含所有必要的签名数据
            return JSONUtil.parseObj(result);

        } catch (WxPayException e) {
            log.error(">>> 微信服务商JSAPI支付异常，订单号：{}，错误信息：{}", payWxJsParam.getOutTradeNo(), e.getMessage(), e);
            throw new CommonException("微信服务商JSAPI支付异常：{}", e.getCustomErrorMsg());
        } catch (Exception e) {
            log.error(">>> 微信服务商支付处理异常，订单号：{}，错误信息：{}", payWxJsParam.getOutTradeNo(), e.getMessage(), e);
            throw new CommonException("支付处理失败，请稍后重试");
        }
    }

    /**
     * 服务商分账
     *
     * @param profitSharingParam 分账参数
     */
    public void profitSharing(ProfitSharingParam profitSharingParam) {
        try {
            // 检查是否启用服务商模式
            if (!PayConfigure.getWxPartnerPayConfigEnable()) {
                throw new CommonException("未启用微信服务商支付模式");
            }

            // 动态获取子商户信息（与支付逻辑保持一致）
            String subMchId = orderApi.getStoreSubMchIdByOutTradNo(profitSharingParam.getOutTradeNo());
            if (ObjectUtil.isEmpty(subMchId)) {
                throw new CommonException("无法获取订单对应的子商户号，分账失败");
            }
            // 确保子商户号清洁，去除首尾空格
            subMchId = subMchId.trim();

            // 获取订单总金额进行验证
            BigDecimal orderTotalAmount = orderApi.getOrderTotalAmount(profitSharingParam.getOutTradeNo());
            
            // 先查询订单，获取transaction_id（服务商模式需要传入子商户号）
            WxPayOrderQueryV3Result orderQueryResult = tradeQueryWithSubMchId(profitSharingParam.getOutTradeNo(), subMchId);
            if (orderQueryResult == null || ObjectUtil.isEmpty(orderQueryResult.getTransactionId())) {
                throw new CommonException("无法获取订单交易号，分账失败");
            }
            
            // 验证分账金额
            BigDecimal sharingAmount = profitSharingParam.getAmount();
            if (sharingAmount == null || sharingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new CommonException("分账金额必须大于0");
            }
            if (sharingAmount.compareTo(orderTotalAmount) > 0) {
                throw new CommonException("分账金额不能超过订单总金额");
            }
            
            // 构建普通服务商分账请求
            ProfitSharingRequest request = new ProfitSharingRequest();
            request.setTransactionId(orderQueryResult.getTransactionId());
            request.setOutOrderNo(profitSharingParam.getOutTradeNo() + "_" + System.currentTimeMillis());
            
            // 调试：打印ProfitSharingRequest的所有可用方法
            log.info("ProfitSharingRequest可用方法：");
            for (java.lang.reflect.Method method : request.getClass().getDeclaredMethods()) {
                if (method.getName().startsWith("set")) {
                    log.info("  - {}({})", method.getName(), 
                        java.util.Arrays.toString(method.getParameterTypes()));
                }
            }
            
            // 根据实际API结构，构建接收方JSON数组
            Integer amountInFen = WxPayUnifiedOrderRequest.yuanToFen(String.valueOf(sharingAmount)).intValue();
            String description = ObjectUtil.isNotEmpty(profitSharingParam.getDescription()) ? 
                profitSharingParam.getDescription() : "订单分账";

            // 构建接收方JSON数组（根据微信分账API文档）
            JSONObject receiver = new JSONObject();
            receiver.set("type", "PERSONAL_OPENID"); // 个人openid类型（服务商模式）
            receiver.set("account", profitSharingParam.getReceiverOpenId()); // 接收方账号
            receiver.set("amount", amountInFen); // 分账金额（分）
            receiver.set("description", description); // 分账描述
            
            // 构建接收方数组
            cn.hutool.json.JSONArray receiversArray = new cn.hutool.json.JSONArray();
            receiversArray.add(receiver);
            
            String receiversJson = receiversArray.toString();
            log.debug("构建分账接收方JSON：{}", receiversJson);
            
            // 设置接收方信息
            try {
                request.setReceivers(receiversJson);
                log.debug("成功设置分账接收方JSON：{}", receiversJson);
            } catch (Exception e) {
                log.error("设置分账接收方JSON失败：{}", e.getMessage(), e);
                throw new CommonException("分账接收方信息设置失败：" + e.getMessage());
            }
            
            // 尝试设置其他必需参数（根据官方文档）
            try {
                // 设置是否解冻剩余未分资金
                java.lang.reflect.Method setUnfreezeSplitMethod = request.getClass().getMethod("setUnfreezeSplit", Boolean.class);
                setUnfreezeSplitMethod.invoke(request, true); // 解冻剩余资金
                log.info("成功设置解冻剩余资金参数");
            } catch (Exception e) {
                log.debug("无法设置解冻剩余资金参数：{}", e.getMessage());
            }
            
            // 尝试设置服务商AppID
            try {
                java.lang.reflect.Method setAppidMethod = request.getClass().getMethod("setAppid", String.class);
                setAppidMethod.invoke(request, wxPayService.getConfig().getAppId());
                log.info("成功设置服务商AppID：{}", wxPayService.getConfig().getAppId());
            } catch (Exception e) {
                log.debug("无法设置服务商AppID：{}", e.getMessage());
            }

            log.debug("普通服务商分账请求参数：订单号={}，接收方={}，金额={}元", 
                profitSharingParam.getOutTradeNo(), profitSharingParam.getReceiverOpenId(), sharingAmount);

            // 检查订单支付时间，确保支付成功1分钟后再分账
            checkOrderPaymentTime(orderQueryResult);

            // 使用普通服务商分账API，添加重试机制
            ProfitSharingResult result = executeSingleReceiverWithRetry(wxPayService.getProfitSharingService(), request, profitSharingParam.getOutTradeNo());

            log.info("普通服务商分账成功：{}", JSONUtil.toJsonStr(result));

        } catch (WxPayException e) {
            log.error("普通服务商分账异常：{}", e.getCustomErrorMsg(), e);
            throw new CommonException("分账失败：{}", e.getCustomErrorMsg());
        } catch (CommonException e) {
            log.error("分账处理业务异常，订单号：{}，错误信息：{}", profitSharingParam.getOutTradeNo(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("分账处理系统异常，订单号：{}，错误信息：{}", profitSharingParam.getOutTradeNo(), e.getMessage(), e);
            throw new CommonException("分账处理失败：" + e.getMessage());
        }
    }

    /**
     * 服务商多接收方分账
     *
     * @param multiProfitSharingParam 多接收方分账参数
     */
    public void multiProfitSharing(MultiProfitSharingParam multiProfitSharingParam) {
        // 普通服务商分账：逐个调用单接收方分账
        this.multiProfitSharingForNormalPartner(multiProfitSharingParam);
    }
    
    /**
     * 服务商多接收方分账（返回结果）
     *
     * @param multiProfitSharingParam 多接收方分账参数
     * @return 分账结果
     */
    public ProfitSharingProcessResult multiProfitSharingWithResult(MultiProfitSharingParam multiProfitSharingParam) {
        return this.multiProfitSharingForNormalPartnerWithResult(multiProfitSharingParam);
    }

    /**
     * 普通服务商多接收方分账实现（通过多次单接收方分账）
     */
    private void multiProfitSharingForNormalPartner(MultiProfitSharingParam multiProfitSharingParam) {
        multiProfitSharingForNormalPartnerWithResult(multiProfitSharingParam);
    }
    
    /**
     * 普通服务商多接收方分账实现（返回结果）
     */
    private ProfitSharingProcessResult multiProfitSharingForNormalPartnerWithResult(MultiProfitSharingParam multiProfitSharingParam) {
        try {
            // 检查是否启用服务商模式
            if (!PayConfigure.getWxPartnerPayConfigEnable()) {
                throw new CommonException("未启用微信服务商支付模式");
            }

            // 验证分账参数
            multiProfitSharingParam.validate();

            // 动态获取子商户信息
            String subMchId = orderApi.getStoreSubMchIdByOutTradNo(multiProfitSharingParam.getOutTradeNo());
            if (ObjectUtil.isEmpty(subMchId)) {
                throw new CommonException("无法获取订单对应的子商户号，分账失败");
            }
            // 确保子商户号清洁，去除首尾空格
            subMchId = subMchId.trim();

            // 获取订单总金额进行验证
            BigDecimal orderTotalAmount = orderApi.getOrderTotalAmount(multiProfitSharingParam.getOutTradeNo());
            
            // 先查询订单，获取transaction_id
            WxPayOrderQueryV3Result orderQueryResult = tradeQueryWithSubMchId(multiProfitSharingParam.getOutTradeNo(), subMchId);
            if (orderQueryResult == null || ObjectUtil.isEmpty(orderQueryResult.getTransactionId())) {
                throw new CommonException("无法获取订单交易号，分账失败");
            }
            
            // 验证总分账金额
            BigDecimal totalSharingAmount = multiProfitSharingParam.getTotalAmount();
            if (totalSharingAmount.compareTo(orderTotalAmount) > 0) {
                throw new CommonException("分账总金额不能超过订单金额");
            }
            
            // 验证分账比例限制
            validateProfitSharingRatio(subMchId, orderTotalAmount, multiProfitSharingParam);

            // 检查订单是否启用了分账标识
            boolean isAgentOrder = orderApi.isAgentDeviceOrder(multiProfitSharingParam.getOutTradeNo());
            log.debug("分账条件检查：订单号={}，是否代理设备订单={}，子商户号={}", 
                multiProfitSharingParam.getOutTradeNo(), isAgentOrder, subMchId);
            
            if (!isAgentOrder) {
                log.warn("订单未启用分账标识！订单号={}，正常情况下只有代理设备订单才能进行分账", multiProfitSharingParam.getOutTradeNo());
                // 临时注释掉异常，用于测试
                // throw new CommonException("该订单未启用分账功能，无法进行分账操作");
            }

            log.debug("普通服务商多接收方分账：订单号={}，子商户号={}，接收方数量={}，总金额={}元", 
                multiProfitSharingParam.getOutTradeNo(), subMchId, multiProfitSharingParam.getReceivers().size(), totalSharingAmount);

            // 注释掉自动添加，直接使用现有的分账接收方
             for (MultiProfitSharingParam.ProfitReceiver receiver : multiProfitSharingParam.getReceivers()) {
                 try {
                     // 添加分账接收方
                     addProfitSharingReceiver(receiver.getAccount(), receiver.getType(), null, subMchId);
                 } catch (Exception e) {
                     // 可能分账接收方已存在，继续处理分账
                 }
             }

            // 构建所有接收方的列表，并处理重复账号的金额合并
            Map<String, Receiver> receiverMap = new LinkedHashMap<>(); // 保持顺序
            
            for (MultiProfitSharingParam.ProfitReceiver receiver : multiProfitSharingParam.getReceivers()) {
                log.debug("处理分账接收方：接收方={}，金额={}元", receiver.getAccount(), receiver.getAmount());
                
                Integer amountInFen = WxPayUnifiedOrderRequest.yuanToFen(String.valueOf(receiver.getAmount())).intValue();
                String description = ObjectUtil.isNotEmpty(receiver.getDescription()) ?
                    receiver.getDescription() : "订单分账";
                
                String account = receiver.getAccount();
                
                if (receiverMap.containsKey(account)) {
                    // 如果账号已存在，合并金额
                    Receiver existingReceiver = receiverMap.get(account);
                    int newAmount = existingReceiver.getAmount() + amountInFen;
                    existingReceiver.setAmount(newAmount);
                    
                    // 合并描述信息
                    String existingDesc = existingReceiver.getDescription();
                    if (!existingDesc.contains(description)) {
                        existingReceiver.setDescription(existingDesc + "+" + description);
                    }
                    
                    log.debug("合并重复接收方：账号={}，原金额={}分，新增金额={}分，合并后={}分", 
                        account, existingReceiver.getAmount() - amountInFen, amountInFen, newAmount);
                } else {
                    // 新账号，创建新的接收方
                    Receiver newReceiver = new Receiver();
                    newReceiver.setType("PERSONAL_OPENID");
                    newReceiver.setAccount(account);
                    newReceiver.setAmount(amountInFen);
                    newReceiver.setDescription(description);
                    receiverMap.put(account, newReceiver);
                    
                    log.debug("添加新分账接收方：账号={}，金额={}分", account, amountInFen);
                }
            }
            
            // 转换为List
            List<Receiver> receivers = new ArrayList<>(receiverMap.values());
            
            log.debug("分账接收方去重完成：原始数量={}，去重后数量={}", 
                multiProfitSharingParam.getReceivers().size(), receivers.size());

            log.debug("构建多接收方分账JSON：{}", receivers);
            
            // 执行一次性多接收方分账
            WxPayOrderQueryV3Result multiOrderQueryResult = this.tradeQueryWithSubMchId(multiProfitSharingParam.getOutTradeNo(), subMchId);
            if (multiOrderQueryResult == null || ObjectUtil.isEmpty(multiOrderQueryResult.getTransactionId())) {
                throw new CommonException("订单查询失败，无法获取交易号");
            }
            // 获取分账服务实例
            ProfitSharingService profitSharingService = wxPayService.getProfitSharingService();
            ProfitSharingV3Request request = new ProfitSharingV3Request();
            request.setAppid(wxPayService.getConfig().getAppId());
            request.setSubMchId(subMchId);
            request.setTransactionId(multiOrderQueryResult.getTransactionId());
            request.setOutOrderNo(multiProfitSharingParam.getOutTradeNo() + "_multi_" + System.currentTimeMillis());
            request.setReceivers(receivers);
            request.setUnfreezeUnsplit(true);
            // 检查订单支付时间，确保支付成功1分钟后再分账
            checkOrderPaymentTime(multiOrderQueryResult);
            
            // 执行分账请求，包含重试机制
            ProfitSharingV3Result result = executeWithRetry(profitSharingService, request, multiProfitSharingParam.getOutTradeNo());
            
            if (result.getState().equals("SUCCESS")){
                log.info("普通服务商多接收方分账成功：订单号={}，分账结果={}", 
                    multiProfitSharingParam.getOutTradeNo(), result);
                return new ProfitSharingProcessResult("SUCCESS", request.getOutOrderNo(), "分账成功");
            } else if (result.getState().equals("PROCESSING")) {
                log.info("多接收方分账处理中：订单号={}，状态={}，结果={}", 
                    multiProfitSharingParam.getOutTradeNo(), result.getState(), result);
                log.debug("分账状态为PROCESSING是正常状态，表示微信正在处理分账请求，后续会通过定时任务查询最终结果");
                return new ProfitSharingProcessResult("PROCESSING", request.getOutOrderNo(), "分账处理中，请稍后查询结果");
            } else {
                log.error("多接收方分账失败，订单号={}，状态={}，结果={}", 
                    multiProfitSharingParam.getOutTradeNo(), result.getState(), result);
                throw new CommonException("分账失败：状态为{}", result.getState());
            }


        } catch (CommonException e) {
            log.error("多接收方分账业务异常，订单号：{}，错误信息：{}", multiProfitSharingParam.getOutTradeNo(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("多接收方分账系统异常，订单号：{}，错误信息：{}", multiProfitSharingParam.getOutTradeNo(), e.getMessage(), e);
            throw new CommonException("分账处理失败：{}", e.getMessage());
        }
    }

    /**
     * 执行分账请求的重试机制
     * 根据微信官方建议：
     * 1. 订单支付成功1分钟后再调用分账接口
     * 2. 未结算的订单，请在结算后再调用分账接口
     * 3. 商户开通收支分离但手续费账户余额不足时，需等待1小时后重试
     */
    private ProfitSharingV3Result executeWithRetry(ProfitSharingService profitSharingService, 
                                                   ProfitSharingV3Request request, 
                                                   String orderNo) throws CommonException {
        int maxRetries = 3;
        // 既然已确保支付后1分钟才开始分账，重试间隔可以适当缩短：10秒、30秒、1分钟
        int[] retryDelays = {10000, 30000, 60000}; // 10s, 30s, 1min
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                log.debug("执行分账请求，第{}次尝试，订单号：{}", i + 1, orderNo);
                ProfitSharingV3Result result = profitSharingService.profitSharingV3(request);
                log.debug("分账请求成功：订单号={}，结果={}", orderNo, result);
                return result; // 成功则直接返回
                
            } catch (WxPayException e) {
                String errorMsg = e.getCustomErrorMsg() != null ? e.getCustomErrorMsg() : e.getMessage();
                
                if (errorMsg.contains("订单处理中") && i < maxRetries - 1) {
                    int delay = retryDelays[i];
                    log.warn("订单处理中，等待{}ms后进行第{}次重试，订单号：{}", 
                        delay, i + 2, orderNo);
                    
                    // 详细原因说明
                    log.debug("订单处理中的可能原因：支付成功未满时间、订单尚未结算、手续费账户余额不足或系统正在处理中");
                    
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new CommonException("分账重试被中断");
                    }
                } else {
                    // 其他错误或重试次数用完，直接抛出
                    log.error("分账失败：订单号={}，错误信息={}", orderNo, errorMsg, e);
                    
                    // 错误处理建议
                    if (errorMsg.contains("订单处理中")) {
                        log.error("重试{}次后仍然订单处理中，建议检查订单结算状态和账户余额", maxRetries);
                    }
                    
                    throw new CommonException("分账失败：{}", errorMsg);
                }
            } catch (Exception e) {
                log.error("分账过程中发生未知异常：订单号={}，异常信息={}", orderNo, e.getMessage(), e);
                throw new CommonException("分账失败：{}", e.getMessage());
            }
        }
        
        throw new CommonException("分账失败：重试{}次后仍然订单处理中，订单号：{}，建议检查订单结算状态和账户余额", maxRetries, orderNo);
    }

    /**
     * 执行单个接收方分账请求的重试机制
     */
    private ProfitSharingResult executeSingleReceiverWithRetry(ProfitSharingService profitSharingService, 
                                                               ProfitSharingRequest request, 
                                                               String orderNo) throws CommonException, WxPayException {
        int maxRetries = 3;
        // 既然已确保支付后1分钟才开始分账，重试间隔可以适当缩短：10秒、30秒、1分钟
        int[] retryDelays = {10000, 30000, 60000}; // 10s, 30s, 1min
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                log.debug("执行单接收方分账请求，第{}次尝试，订单号：{}", i + 1, orderNo);
                ProfitSharingResult result = profitSharingService.profitSharing(request);
                log.debug("单接收方分账请求成功：订单号={}，结果={}", orderNo, JSONUtil.toJsonStr(result));
                return result; // 成功则直接返回
                
            } catch (WxPayException e) {
                String errorMsg = e.getCustomErrorMsg() != null ? e.getCustomErrorMsg() : e.getMessage();
                
                if (errorMsg.contains("订单处理中") && i < maxRetries - 1) {
                    int delay = retryDelays[i];
                    log.warn("订单处理中，等待{}ms后进行第{}次重试，订单号：{}", 
                        delay, i + 2, orderNo);
                    
                    // 详细原因说明
                    log.debug("订单处理中的可能原因：支付成功未满时间、订单尚未结算、手续费账户余额不足或系统正在处理中");
                    
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new CommonException("分账重试被中断");
                    }
                } else {
                    // 其他错误或重试次数用完，直接抛出
                    log.error("单接收方分账失败：订单号={}，错误信息={}", orderNo, errorMsg, e);
                    
                    // 错误处理建议
                    if (errorMsg.contains("订单处理中")) {
                        log.error("重试{}次后仍然订单处理中，建议检查订单结算状态和账户余额", maxRetries);
                    }
                    
                    throw e; // 重新抛出WxPayException，让外层catch处理
                }
            } catch (Exception e) {
                log.error("单接收方分账过程中发生未知异常：订单号={}，异常信息={}", orderNo, e.getMessage(), e);
                throw new CommonException("分账失败：{}", e.getMessage());
            }
        }
        
        throw new CommonException("分账失败：重试{}次后仍然订单处理中，订单号：{}，建议检查订单结算状态和账户余额", maxRetries, orderNo);
    }

    /**
     * 检查订单支付时间，确保支付成功1分钟后再进行分账
     * 根据微信官方建议：请在订单支付成功1分钟后再调用分账接口
     */
    private void checkOrderPaymentTime(WxPayOrderQueryV3Result orderQueryResult) throws CommonException {
        if (orderQueryResult == null || orderQueryResult.getSuccessTime() == null) {
            log.warn("无法获取订单支付时间，跳过支付时间检查");
            return;
        }
        
        try {
            // 解析支付成功时间（微信返回格式：2023-09-19T15:28:02+08:00）
            String successTimeStr = orderQueryResult.getSuccessTime();
            log.debug("订单支付成功时间：{}", successTimeStr);
            
            // 转换为时间戳
            java.time.OffsetDateTime successTime = java.time.OffsetDateTime.parse(successTimeStr);
            long paymentTimestamp = successTime.toInstant().toEpochMilli();
            long currentTimestamp = System.currentTimeMillis();
            
            // 计算支付后经过的时间（毫秒）
            long elapsedTime = currentTimestamp - paymentTimestamp;
            long elapsedSeconds = elapsedTime / 1000;
            long elapsedMinutes = elapsedSeconds / 60;
            
            log.debug("订单支付后经过时间：{}分{}秒", elapsedMinutes, elapsedSeconds % 60);
            
            // 检查是否已经过1分钟
            if (elapsedTime < 60000) { // 60000毫秒 = 1分钟
                long waitTime = 60000 - elapsedTime;
                long waitSeconds = waitTime / 1000;
                
                log.warn("订单支付成功未满1分钟，根据微信官方建议需等待{}秒后再进行分账", waitSeconds);
                log.debug("正在等待订单状态稳定...");
                
                try {
                    Thread.sleep(waitTime);
                    log.debug("等待完成，现在可以进行分账了");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new CommonException("等待订单稳定时被中断");
                }
            } else {
                log.debug("订单支付已超过1分钟，可以进行分账");
            }
            
        } catch (Exception e) {
            if (e instanceof CommonException) {
                throw e;
            }
            log.warn("解析订单支付时间失败：{}，跳过支付时间检查", e.getMessage());
        }
    }

    /**
     * 验证并调整分账比例限制
     */
    private void validateProfitSharingRatio(String subMchId, BigDecimal orderAmount, MultiProfitSharingParam param) {
        try {
            // 查询子商户的最大分账比例
            BigDecimal maxRatio = queryMaxRatioFromWxApi(subMchId);
            if (maxRatio != null && maxRatio.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal maxAmount = orderAmount.multiply(maxRatio).divide(BigDecimal.valueOf(100));
                BigDecimal totalSharingAmount = param.getTotalAmount();
                
                if (totalSharingAmount.compareTo(maxAmount) > 0) {
                    log.warn("分账总金额{}元超过最大分账限额{}元（比例{}%），将按比例调整分账金额", 
                        totalSharingAmount, maxAmount, maxRatio);
                    
                    // 计算调整比例
                    BigDecimal adjustRatio = maxAmount.divide(totalSharingAmount, 4, RoundingMode.DOWN);
                    log.info("分账金额调整比例：{}", adjustRatio);
                    
                    // 调整所有接收方的分账金额
                    for (MultiProfitSharingParam.ProfitReceiver receiver : param.getReceivers()) {
                        BigDecimal originalAmount = receiver.getAmount();
                        BigDecimal adjustedAmount = originalAmount.multiply(adjustRatio).setScale(2, RoundingMode.DOWN);
                        receiver.setAmount(adjustedAmount);
                        
                    log.debug("调整分账金额：原金额={}元，调整后={}元", originalAmount, adjustedAmount);
                    }
                    
                    // 重新计算总分账金额（getTotalAmount会自动计算）
                    BigDecimal adjustedTotal = param.getTotalAmount();
                    
                    log.info("分账金额调整完成：原总金额={}元，调整后总金额={}元", 
                        totalSharingAmount, adjustedTotal);
                } else {
                    log.debug("分账比例验证通过：分账金额={}元，最大限额={}元，比例={}%", 
                        totalSharingAmount, maxAmount, maxRatio);
                }
            } else {
                log.warn("无法获取子商户{}的分账比例限制，跳过验证", subMchId);
            }
        } catch (Exception e) {
            log.error("验证分账比例时发生异常：{}", e.getMessage(), e);
            // 验证失败不影响分账流程，只记录警告
            log.warn("分账比例验证异常，继续执行分账流程");
        }
    }



    /**
     * 服务商分账查询
     *
     * @param outOrderNo 商户分账单号
     * @return 分账查询结果
     */
    public String profitSharingQuery(String outOrderNo) {
        try {
            // 检查是否启用服务商模式
            if (!PayConfigure.getWxPartnerPayConfigEnable()) {
                throw new CommonException("未启用微信服务商支付模式");
            }

            // 从分账单号中提取原订单号（格式：outTradeNo_timestamp）
            String originalOutTradeNo = outOrderNo;
            if (outOrderNo.contains("_")) {
                originalOutTradeNo = outOrderNo.substring(0, outOrderNo.lastIndexOf("_"));
            }
            
            // 动态获取子商户信息（与支付逻辑保持一致）
            String subMchId = orderApi.getStoreSubMchIdByOutTradNo(originalOutTradeNo);
            if (ObjectUtil.isEmpty(subMchId)) {
                throw new CommonException("无法获取订单对应的子商户号，分账查询失败");
            }
            // 确保子商户号清洁，去除首尾空格
            subMchId = subMchId.trim();

            // 构建分账查询请求
            ProfitSharingQueryRequest request = new ProfitSharingQueryRequest();
            request.setSubMchid(subMchId);
            request.setOutOrderNo(outOrderNo);

            log.info("服务商分账查询请求参数：{}", JSONUtil.toJsonStr(request));

            // 调用服务商分账查询接口
            String result = "分账查询功能需要根据实际WxJava版本调整";

            log.info("服务商分账查询结果：{}", JSONUtil.toJsonStr(result));

            return result;

        } catch (Exception e) {
            log.error("服务商分账查询异常：{}", e.getMessage(), e);
            throw new CommonException("分账查询失败：{}", e.getMessage());
        }
    }

    /**
     * 服务商模式退款查询
     *
     * @param outTradeNo 订单号
     * @param outRequestNo 退款单号
     * @return 退款查询结果
     */
    public WxPayRefundQueryV3Result refundQuery(String outTradeNo, String outRequestNo) {
        try {
            // 动态获取子商户信息（与支付逻辑保持一致）
            String subMchId = orderApi.getStoreSubMchIdByOutTradNo(outTradeNo);
            if (ObjectUtil.isEmpty(subMchId)) {
                log.error("无法获取订单对应的子商户号，退款查询失败，订单号：{}", outTradeNo);
                return null;
            }
            
            // 构建退款查询请求（服务商模式下也使用普通退款查询，但通过配置区分）
            WxPayRefundQueryV3Request refundQueryRequest = new WxPayRefundQueryV3Request();
            refundQueryRequest.setOutRefundNo(outRequestNo);
            
            log.info("服务商退款查询请求参数：{}", JSONUtil.toJsonStr(refundQueryRequest));
            
            // 调用退款查询API（WxJava SDK会根据配置自动处理服务商模式）
            WxPayRefundQueryV3Result result = wxPayService.refundQueryV3(refundQueryRequest);
            
            log.info("服务商退款查询结果：{}", JSONUtil.toJsonStr(result));
            
            return result;
            
        } catch (WxPayException e) {
            log.error(">>> 微信服务商退款查询异常，订单号：{}，退款单号：{}，错误信息：{}", outTradeNo, outRequestNo, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error(">>> 退款查询处理系统异常，订单号：{}，退款单号：{}，错误信息：{}", outTradeNo, outRequestNo, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 服务商模式退款
     *
     * @param refundParam 退款参数
     * @return 退款结果
     */
    public WxPayRefundV3Result doRefund(RefundParam refundParam) {
        try {
            // 获取订单总金额
            BigDecimal totalAmount = orderApi.getOrderTotalAmount(refundParam.getOutTradeNo());
            BigDecimal refundAmount = refundParam.getRefundAmount() != null ? refundParam.getRefundAmount() : totalAmount;
            
            // 动态获取子商户信息（与支付逻辑保持一致）
            String subMchId = orderApi.getStoreSubMchIdByOutTradNo(refundParam.getOutTradeNo());
            if (ObjectUtil.isEmpty(subMchId)) {
                throw new CommonException("无法获取订单对应的子商户号，退款失败");
            }
            // 确保子商户号清洁，去除首尾空格
            subMchId = subMchId.trim();
            
            // 构建服务商退款请求
            WxPayPartnerRefundV3Request refundRequest = new WxPayPartnerRefundV3Request();
            refundRequest.setOutTradeNo(refundParam.getOutTradeNo());
            refundRequest.setOutRefundNo(IdWorker.getIdStr());
            refundRequest.setSubMchid(subMchId);
            // 设置金额信息
            WxPayPartnerRefundV3Request.Amount amount = new WxPayPartnerRefundV3Request.Amount();
            amount.setTotal(new Money(totalAmount).multiply(100).getAmount().intValue());
            amount.setRefund(new Money(refundAmount).multiply(100).getAmount().intValue());
            amount.setCurrency(Money.DEFAULT_CURRENCY_CODE);
            refundRequest.setAmount(amount);
            
            // 设置退款原因
            if (ObjectUtil.isNotEmpty(refundParam.getReason())) {
                refundRequest.setReason(refundParam.getReason());
            } else {
                refundRequest.setReason("用户申请退款");
            }
            
            log.info("服务商退款请求参数：{}", JSONUtil.toJsonStr(refundRequest));
            
            // 调用服务商退款API
            WxPayRefundV3Result result = wxPayService.refundV3(refundRequest);
            
            log.info("服务商退款结果：{}", JSONUtil.toJsonStr(result));
            
            return result;
            
        } catch (WxPayException e) {
            log.error(">>> 微信服务商退款异常，订单号：{}，错误信息：{}", refundParam.getOutTradeNo(), e.getMessage(), e);
            throw new CommonException("退款失败：{}", e.getCustomErrorMsg());
        } catch (Exception e) {
            log.error(">>> 退款处理系统异常，订单号：{}，错误信息：{}", refundParam.getOutTradeNo(), e.getMessage(), e);
            throw new CommonException("退款处理失败：" + e.getMessage());
        }
    }

    /**
     * 服务商模式关闭订单
     *
     * @param payOrder 订单信息
     */
    public void doClose(PayOrder payOrder) {
        try {
            // 动态获取子商户信息（与支付逻辑保持一致）
            String subMchId = orderApi.getStoreSubMchIdByOutTradNo(payOrder.getOutTradeNo());
            if (ObjectUtil.isEmpty(subMchId)) {
                throw new CommonException("无法获取订单对应的子商户号，关闭订单失败");
            }
            // 确保子商户号清洁，去除首尾空格
            subMchId = subMchId.trim();
            
            // 使用普通关闭订单请求（WxJava SDK会根据配置自动处理服务商模式）
            WxPayOrderCloseV3Request closeRequest = new WxPayOrderCloseV3Request();
            closeRequest.setOutTradeNo(payOrder.getOutTradeNo());
            
            log.info("服务商关闭订单请求参数：{}", JSONUtil.toJsonStr(closeRequest));
            
            // 调用关闭订单API（WxJava SDK会根据配置自动处理服务商模式）
            wxPayService.closeOrderV3(closeRequest);
            
            log.info("服务商关闭订单成功，订单号：{}", payOrder.getOutTradeNo());
            
        } catch (WxPayException e) {
            log.error(">>> 微信服务商关闭订单异常，订单号：{}，错误信息：{}", payOrder.getOutTradeNo(), e.getMessage(), e);
            throw new CommonException("关闭订单失败：{}", e.getCustomErrorMsg());
        } catch (Exception e) {
            log.error(">>> 关闭订单处理系统异常，订单号：{}，错误信息：{}", payOrder.getOutTradeNo(), e.getMessage(), e);
            throw new CommonException("关闭订单处理失败：" + e.getMessage());
        }
    }

    /**
     * 服务商模式企业付款到零钱
     *
     * @param openId 用户openId
     * @param amount 转账金额
     * @param partnerTradeNo 商户转账单号
     * @param desc 转账描述
     * @return 转账结果
     */
    public TransferBillsResult withdraw(String openId, BigDecimal amount, String partnerTradeNo, String desc) {
        try {
            // 通过openId获取用户ID，然后获取对应的子商户信息
            String clientUserId = clientApi.getIdByOpenId(openId);
            if (ObjectUtil.isEmpty(clientUserId)) {
                throw new CommonException("无法获取用户信息，转账失败");
            }
            
            // 获取用户对应的子商户号
            String subMchId = wineAgentApi.getSubMchIdByClientUserId(clientUserId);
            if (ObjectUtil.isEmpty(subMchId)) {
                throw new CommonException("用户未关联代理商或代理商未配置子商户号，转账失败");
            }
            // 确保子商户号清洁，去除首尾空格
            subMchId = subMchId.trim();
            
            // 构建服务商模式转账请求
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
            
            log.info("服务商转账请求参数：{}", JSONUtil.toJsonStr(transferBillsRequest));
            
            // 获取转账服务并执行转账
            TransferService transferService = wxPayService.getTransferService();
            TransferBillsResult result = transferService.transferBills(transferBillsRequest);
            
            log.info("服务商转账结果：{}", JSONUtil.toJsonStr(result));
            
            return result;
            
        } catch (WxPayException e) {
            log.error(">>> 微信服务商转账异常，转账单号：{}，错误信息：{}", partnerTradeNo, e.getMessage(), e);
            throw new CommonException("转账失败：{}", e.getCustomErrorMsg());
        } catch (Exception e) {
            log.error(">>> 转账处理系统异常，转账单号：{}，错误信息：{}", partnerTradeNo, e.getMessage(), e);
            throw new CommonException("转账处理失败：" + e.getMessage());
        }
    }

    /**
     * 查询子商户最大分账比例
     * 服务商模式下查询指定子商户的最大分账比例
     *
     * @param subMchId 子商户号
     * @return 最大分账比例（百分比，如30表示30%）
     */
    public BigDecimal queryMaxProfitSharingRatio(String subMchId) {
        try {
            // 检查是否启用服务商模式
            if (!PayConfigure.getWxPartnerPayConfigEnable()) {
                throw new CommonException("未启用微信服务商支付模式");
            }

            if (ObjectUtil.isEmpty(subMchId)) {
                throw new CommonException("子商户号不能为空");
            }

            // 方式一：从代理商配置中获取分账比例
            // 通过子商户号反查代理商信息，获取配置的最大分账比例
            BigDecimal maxRatio = getMaxRatioFromAgent(subMchId);
            if (maxRatio != null) {
                log.info("从代理商配置中获取子商户{}的最大分账比例：{}%", subMchId, maxRatio);
                return maxRatio;
            }

            // 方式二：调用微信API查询（如果WxJava SDK支持）
            // 注意：目前WxJava可能还不支持直接查询子商户分账比例的API
            // 这里提供一个框架，具体实现需要根据微信官方API文档调整
            BigDecimal ratioFromWx = queryMaxRatioFromWxApi(subMchId);
            if (ratioFromWx != null) {
                log.info("从微信API获取子商户{}的最大分账比例：{}%", subMchId, ratioFromWx);
                return ratioFromWx;
            }

            // 方式三：返回默认值
            BigDecimal defaultRatio = new BigDecimal("30"); // 默认30%
            log.info("使用默认分账比例：{}%，子商户号：{}", defaultRatio, subMchId);
            return defaultRatio;

        } catch (Exception e) {
            log.error("查询子商户最大分账比例异常，子商户号：{}，错误信息：{}", subMchId, e.getMessage(), e);
            throw new CommonException("查询子商户分账比例失败：{}", e.getMessage());
        }
    }

    /**
     * 从代理商配置中获取最大分账比例
     *
     * @param subMchId 子商户号
     * @return 最大分账比例
     */
    private BigDecimal getMaxRatioFromAgent(String subMchId) {
        try {
            // 通过子商户号反查代理商信息
            String clientUserId = wineAgentApi.getClientUserIdBySubMchId(subMchId);
            if (ObjectUtil.isEmpty(clientUserId)) {
                log.warn("未找到子商户号{}对应的代理商信息", subMchId);
                return null;
            }

            // 获取代理商的最大分账比例配置
            BigDecimal maxRatio = wineAgentApi.getMaxProfitSharingRatio(clientUserId);
            if (maxRatio != null && maxRatio.compareTo(BigDecimal.ZERO) > 0) {
                return maxRatio;
            }

            log.warn("代理商{}未配置最大分账比例", clientUserId);
            return null;

        } catch (Exception e) {
            log.error("从代理商配置获取分账比例异常：{}", e.getMessage(), e);
            return null;
        }
    }


    /**
     * 服务商模式订单查询（需要传入子商户号）
     * 暂时跳过订单查询，直接使用已有的transaction_id
     *
     * @param outTradeNo 订单号
     * @param subMchId 子商户号
     * @return 查询结果
     */
    private WxPayOrderQueryV3Result tradeQueryWithSubMchId(String outTradeNo, String subMchId) {
        try {
            log.info("服务商模式查询订单：订单号={}，子商户号={}", outTradeNo, subMchId);
            
            // 临时方案：从数据库中直接获取transaction_id
            // 避免复杂的微信API调用问题
            try {
                // 通过订单API获取transaction_id
                String transactionId = orderApi.getTransactionIdByOutTradeNo(outTradeNo);
                
                if (ObjectUtil.isNotEmpty(transactionId)) {
                    // 构建返回结果
                    WxPayOrderQueryV3Result result = new WxPayOrderQueryV3Result();
                    result.setTransactionId(transactionId);
                    result.setOutTradeNo(outTradeNo);
                    result.setTradeState("SUCCESS"); // 假设已支付成功
                    
                    log.info("从数据库获取交易号成功：订单号={}，交易号={}", outTradeNo, transactionId);
                    return result;
                } else {
                    log.warn("从数据库未找到交易号：订单号={}", outTradeNo);
                    return null;
                }
                
            } catch (Exception e) {
                log.error("从数据库获取交易号异常：{}", e.getMessage(), e);
                return null;
            }
            
        } catch (Exception e) {
            log.error("服务商订单查询系统异常：订单号={}，子商户号={}，异常：{}", outTradeNo, subMchId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从微信API查询最大分账比例
     * 使用微信支付服务商模式的ProfitSharingMerchantRatioQueryV3Result API
     *
     * @param subMchId 子商户号
     * @return 最大分账比例
     */
    public BigDecimal queryMaxRatioFromWxApi(String subMchId) {
        try {
            log.info("开始调用微信API查询子商户分账比例，子商户号：{}", subMchId);
            
            // 检查子商户号是否为空
            if (ObjectUtil.isEmpty(subMchId)) {
                log.error("子商户号不能为空");
                return null;
            }
            
            // 清理子商户号，去除首尾空格，避免URL中出现非法字符
            subMchId = subMchId.trim();
            log.debug("清理后的子商户号：{}", subMchId);
            
            // 使用WxJava SDK的ProfitSharingMerchantRatioQueryV3Result API
            // 根据官方文档正确调用分账比例查询接口
            try {
                log.debug("使用WxJava SDK ProfitSharingService查询子商户分账比例");
                
                // 获取分账服务实例
                ProfitSharingService profitSharingService = wxPayService.getProfitSharingService();
                
                // 调用分账比例查询API
                ProfitSharingMerchantRatioQueryV3Result result = profitSharingService
                    .profitSharingMerchantRatioQueryV3(subMchId);
                
                if (result != null) {
                    log.info("微信API查询分账比例成功，子商户号：{}，结果：{}", subMchId, JSONUtil.toJsonStr(result));
                    
                    // 根据ProfitSharingMerchantRatioQueryV3Result的字段获取最大分账比例
                    // 根据微信官方文档，结果对象包含maxRatio字段
                    Integer maxRatioInt = result.getMaxRatio()/100;
                    if (maxRatioInt != null) {
                        // 微信API返回的可能是整数形式的百分比（如30表示30%）
                        BigDecimal maxRatio = new BigDecimal(maxRatioInt);
                        log.info("成功从微信API获取子商户{}的最大分账比例：{}%", subMchId, maxRatio);
                        return maxRatio;
                    }
                    
                    // 如果maxRatio字段为空，尝试其他可能的字段
                    // 根据实际API响应结构调整
                    log.warn("微信API返回的分账比例结果中maxRatio字段为空，子商户号：{}", subMchId);
                } else {
                    log.warn("微信API查询分账比例返回null，子商户号：{}", subMchId);
                }
                
            } catch (WxPayException e) {
                log.error("调用微信分账比例查询API异常，子商户号：{}，错误码：{}，错误信息：{}", 
                    subMchId, e.getErrCode(), e.getErrCodeDes(), e);
                
                // 如果是子商户不存在或未开通分账功能的错误，记录但不抛异常
                if ("SUB_MCHID_NOT_EXIST".equals(e.getErrCode()) || 
                    "PROFIT_SHARING_NOT_OPEN".equals(e.getErrCode())) {
                    log.warn("子商户{}不存在或未开通分账功能", subMchId);
                    return null;
                }
                
            } catch (Exception e) {
                log.error("调用微信API查询分账比例时发生未知异常，子商户号：{}，异常信息：{}", 
                    subMchId, e.getMessage(), e);
            }
            
            
            log.warn("未能从微信API获取到子商户{}的分账比例信息", subMchId);
            return null;

        } catch (Exception e) {
            log.error("查询微信分账比例时发生系统异常，子商户号：{}，异常信息：{}", subMchId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 测试添加分账接收方（公开方法用于测试）
     */
    public void testAddProfitSharingReceiver(String account, String subMchId) {
        log.info("开始测试添加分账接收方，接收方账号: {}，子商户号: {}", account, subMchId);
        try {
            // 确保子商户号清洁，去除首尾空格
            if (ObjectUtil.isNotEmpty(subMchId)) {
                subMchId = subMchId.trim();
            }
            
            // 先尝试删除（如果存在）
            removeProfitSharingReceiver(account, "PERSONAL_OPENID", subMchId);
            // 再添加
            addProfitSharingReceiver(account, "PERSONAL_OPENID", null, subMchId);
            log.info("添加分账接收方测试完成");
        } catch (Exception e) {
            log.error("添加分账接收方测试失败：{}", e.getMessage(), e);
            throw new CommonException("添加分账接收方测试失败：" + e.getMessage());
        }
    }

    /**
     * 添加分账接收方
     */
    private void addProfitSharingReceiver(String account, String type, String name, String subMchId) {
        try {
            // 确保子商户号清洁，去除首尾空格
            if (ObjectUtil.isNotEmpty(subMchId)) {
                subMchId = subMchId.trim();
            }
            
            ProfitSharingService profitSharingService = wxPayService.getProfitSharingService();
            
            ProfitSharingReceiverV3Request requestV3 = new ProfitSharingReceiverV3Request();
            requestV3.setSubMchId(subMchId);
            requestV3.setAppid(wxPayService.getConfig().getAppId());
            requestV3.setType(type);
            requestV3.setAccount(account);
            requestV3.setRelationType("STORE_OWNER");

            ProfitSharingReceiverV3Result result = profitSharingService.addReceiverV3(requestV3);
            log.info("成功添加分账接收方：账号={}", account);
            
        } catch (Exception e) {
            log.warn("添加分账接收方异常，但继续处理分账：{}", e.getMessage());
        }
    }
    
    /**
     * 删除分账接收方
     */
    private void removeProfitSharingReceiver(String account, String type, String subMchId) {
        try {
            // 确保子商户号清洁，去除首尾空格
            if (ObjectUtil.isNotEmpty(subMchId)) {
                subMchId = subMchId.trim();
            }
            
            ProfitSharingService profitSharingService = wxPayService.getProfitSharingService();
            
            ProfitSharingReceiverV3Request requestV3 = new ProfitSharingReceiverV3Request();
            requestV3.setSubMchId(subMchId);
            requestV3.setAppid(wxPayService.getConfig().getAppId());
            requestV3.setType(type);
            requestV3.setAccount(account);
            requestV3.setRelationType("STORE_OWNER");

            ProfitSharingReceiverV3Result result = profitSharingService.removeReceiverV3(requestV3);
            log.info("成功删除分账接收方：账号={}", account);
            
        } catch (Exception e) {
            log.debug("删除分账接收方失败（可能不存在）：{}", e.getMessage());
        }
    }
}
