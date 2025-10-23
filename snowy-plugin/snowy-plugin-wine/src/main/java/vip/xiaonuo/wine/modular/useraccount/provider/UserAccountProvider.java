package vip.xiaonuo.wine.modular.useraccount.provider;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.wine.api.ClientUserAccountApi;
import vip.xiaonuo.wine.modular.useraccount.param.WineUserAccountAddParam;
import vip.xiaonuo.wine.modular.useraccount.service.WineUserAccountService;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class UserAccountProvider implements ClientUserAccountApi {
    @Resource
    private WineUserAccountService wineUserAccountService;
    @Override
    public void createAccountInfo(String id,String nickName,String phone) {
        WineUserAccountAddParam wineUserAccountAddParam = new WineUserAccountAddParam();
        wineUserAccountAddParam.setUserId(id);
        wineUserAccountAddParam.setUserNickname(nickName);
        wineUserAccountAddParam.setUserPhone(phone);
        wineUserAccountAddParam.setStatus("NORMAL");
        wineUserAccountAddParam.setTotalBalance(BigDecimal.ZERO);
        wineUserAccountAddParam.setAvailableBalance(BigDecimal.ZERO);
        wineUserAccountAddParam.setFrozenBalance(BigDecimal.ZERO);
        wineUserAccountAddParam.setTotalCommission(BigDecimal.ZERO);
        wineUserAccountAddParam.setTotalWithdraw(BigDecimal.ZERO);
        wineUserAccountAddParam.setLastCommissionTime(new Date());
        wineUserAccountAddParam.setLastWithdrawTime(new Date());
        wineUserAccountAddParam.setRemark("");
        wineUserAccountAddParam.setExtJson("{}");
        wineUserAccountService.add(wineUserAccountAddParam);
    }
}
