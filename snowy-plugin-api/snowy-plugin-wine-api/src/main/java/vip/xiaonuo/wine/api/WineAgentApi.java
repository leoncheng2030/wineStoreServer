package vip.xiaonuo.wine.api;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public interface WineAgentApi {
    String getSubMchIdByOutTradeNo(@NotBlank(message = "outTradeNo不能为空") String outTradeNo);
    String getSubMchIdByClientUserId(@NotBlank(message = "outTradeNo不能为空") String clientUserId);
    String getSubMchIdByOpenId(@NotBlank(message = "outTradeNo不能为空") String openId);
    String getClientUserIdBySubMchId(String subMchId);
    BigDecimal getMaxProfitSharingRatio(String clientUserId);
    
    /**
     * 创建代理商记录
     * @param clientUserId 用户ID
     */
    void createAgentRecord(@NotBlank(message = "clientUserId不能为空") String clientUserId);
    
    /**
     * 删除代理商记录
     * @param clientUserId 用户ID
     */
    void deleteAgentRecord(@NotBlank(message = "clientUserId不能为空") String clientUserId);
    
}
