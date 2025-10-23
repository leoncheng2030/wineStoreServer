package vip.xiaonuo.pay.modular.wx.provider;

import com.github.binarywang.wxpay.bean.transfer.TransferBillsResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.xiaonuo.pay.api.PayApi;
import vip.xiaonuo.pay.modular.wx.service.PayWxService;
import vip.xiaonuo.pay.modular.wx.service.impl.PayWxPartnerServiceImpl;

import java.math.BigDecimal;

@Slf4j
@Service
public class PayProvider implements PayApi {
    @Resource
    private PayWxService payWxService;
    
    @Resource
    private PayWxPartnerServiceImpl payWxPartnerService;

    @Override
    public String withdraw(String openId, BigDecimal amount, String partnerTradeNo, String desc) {
        TransferBillsResult withdraw = payWxService.withdraw(openId, amount, partnerTradeNo, desc);
        return withdraw.getTransferBillNo();
    }

    @Override
    public BigDecimal queryMaxProfitSharingRatio(String subMchId) {
        // 使用服务商模式的微信API查询最大分账比例
        log.info("定时任务调用服务商模式API查询子商户分账比例，子商户号：{}", subMchId);
        return payWxPartnerService.queryMaxRatioFromWxApi(subMchId);
    }
}
