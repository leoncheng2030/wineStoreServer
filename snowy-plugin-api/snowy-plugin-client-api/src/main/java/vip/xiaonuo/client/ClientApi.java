package vip.xiaonuo.client;

import vip.xiaonuo.client.pojo.ClientUserInfo;

public interface ClientApi {
    String getOpenId(String id);

    String getIdByOpenId(String openId);

    /**
     * 检查用户是否为代理商
     *
     * @param userId 用户ID
     * @return 是否为代理商
     */
    boolean isUserAgent(String userId);

    /**
     * 设置用户为代理商
     *
     * @param userId 用户ID
     */
    void setUserAsAgent(String userId);
    
    /**
     * 根据用户ID获取用户详细信息
     *
     * @param userId 用户ID
     * @return 用户详细信息，查不到则返回null
     */
    ClientUserInfo getUserInfoById(String userId);
}
