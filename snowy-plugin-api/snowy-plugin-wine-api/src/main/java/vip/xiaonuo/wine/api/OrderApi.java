package vip.xiaonuo.wine.api;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public interface OrderApi {
    void notify(String tradeNo, String outTradeNo, String attachInfo);

    void refundNotify(String outRefundNo, String transactionId, String status, String Amount);

    void transferNotify(String outBillNo, String transferBillNo, String state);

    BigDecimal getOrderTotalAmount(@NotBlank(message = "outTradeNo不能为空") String outTradeNo);
    // 根据outTradeNo获取门店对应的子商户号和应用ID
    String getStoreSubMchIdByOutTradNo(@NotBlank(message = "outTradeNo不能为空") String outTradeNo);
    
    /**
     * 检查订单是否来自代理设备
     * @param outTradeNo 订单号
     * @return true-代理设备订单，false-非代理设备订单
     */
    boolean isAgentDeviceOrder(@NotBlank(message = "outTradeNo不能为空") String outTradeNo);
    
    /**
     * 根据订单号获取微信交易号
     * @param outTradeNo 订单号
     * @return 微信交易号(transaction_id)
     */
    String getTransactionIdByOutTradeNo(@NotBlank(message = "outTradeNo不能为空") String outTradeNo);
}
