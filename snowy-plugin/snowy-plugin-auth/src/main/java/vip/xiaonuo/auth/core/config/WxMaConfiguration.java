package vip.xiaonuo.auth.core.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.xiaonuo.dev.api.DevApi;
import vip.xiaonuo.dev.api.DevConfigApi;

@Slf4j
@Configuration
public class WxMaConfiguration {

    @Resource
    private DevConfigApi devConfigApi;
    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(devConfigApi.getValueByKey("APP_ID"));
        config.setSecret(devConfigApi.getValueByKey("APP_SECRET"));
        config.setMsgDataFormat("JSON");

        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}
