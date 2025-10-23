package vip.xiaonuo.pay.api;

import java.math.BigDecimal;

public interface PayApi {
    String withdraw(String openId, BigDecimal amount, String partnerTradeNo, String desc);
    BigDecimal queryMaxProfitSharingRatio(String subMchId);
}
