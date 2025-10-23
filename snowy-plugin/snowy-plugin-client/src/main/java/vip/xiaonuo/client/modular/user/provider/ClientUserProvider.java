package vip.xiaonuo.client.modular.user.provider;

import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.result.ClientLoginUser;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.client.pojo.ClientUserInfo;

@Service
public class ClientUserProvider implements ClientApi {
    @Resource
    private ClientUserService clientUserService;
    
    @Override
    public String getOpenId(String id) {
        ClientUser userById = clientUserService.queryEntity(id);
        return userById.getOpenid();
    }

    @Override
    public String getIdByOpenId(String openId) {
        ClientLoginUser clientLoginUser = clientUserService.getByOpenid(openId);
        return clientLoginUser.getId();
    }

    @Override
    public boolean isUserAgent(String userId) {
        try {
            ClientUser clientUser = clientUserService.queryEntity(userId);
            return "YES".equals(clientUser.getIsAgent());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setUserAsAgent(String userId) {
        clientUserService.setUserAsAgent(userId);
    }
    
    @Override
    public ClientUserInfo getUserInfoById(String userId) {
        try {
            ClientUser clientUser = clientUserService.queryEntity(userId);
            ClientUserInfo userInfo = new ClientUserInfo();
            userInfo.setId(clientUser.getId());
            userInfo.setName(clientUser.getName());
            userInfo.setNickname(clientUser.getNickname());
            userInfo.setPhone(clientUser.getPhone());
            userInfo.setAvatar(clientUser.getAvatar());
            userInfo.setLastLoginTime(clientUser.getLatestLoginTime());
            return userInfo;
        } catch (Exception e) {
            return null;
        }
    }
}
