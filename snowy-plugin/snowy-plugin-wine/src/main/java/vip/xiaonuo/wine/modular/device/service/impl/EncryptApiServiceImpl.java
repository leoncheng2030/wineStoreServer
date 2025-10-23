package vip.xiaonuo.wine.modular.device.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vip.xiaonuo.wine.modular.device.param.DeviceControlParam;
import vip.xiaonuo.wine.modular.device.service.EncryptApiService;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方加密接口服务实现类
 *
 * @author AI Assistant
 * @date 2025/01/30
 */
@Slf4j
@Service
public class EncryptApiServiceImpl implements EncryptApiService {

    @Value("${wqs.device.encrypt.api.url:https://gateway.biandianxia.com/encrypt/sharedevice}")
    private String encryptApiUrl;

    @Value("${wqs.device.encrypt.api.timeout:10000}")
    private int timeout;

    @Override
    public String getEncryptCommand(DeviceControlParam param) {
        try {
            log.info("调用第三方加密接口，设备UUID：{}，订单ID：{}", param.getUuid(), param.getChargeId());

            // 1. 构建请求参数
            Map<String, Object> requestData = buildRequestData(param);

            // 2. 调用第三方接口
            String response = HttpUtil.post(encryptApiUrl, requestData, timeout);

            if (StrUtil.isBlank(response)) {
                log.error("第三方接口返回空响应");
                return null;
            }

            // 3. 解析响应
            JSONObject responseJson = JSONUtil.parseObj(response);
            String cmd = responseJson.getStr("cmd");

            if (StrUtil.isBlank(cmd)) {
                log.error("第三方接口未返回CMD指令，响应：{}", response);
                return null;
            }

            log.info("第三方加密接口调用成功，指令长度：{}", cmd.length());
            return cmd;

        } catch (Exception e) {
            log.error("调用第三方加密接口异常，设备UUID：{}，订单ID：{}", param.getUuid(), param.getChargeId(), e);
            return null;
        }
    }



    /**
     * 构建请求参数
     */
    private Map<String, Object> buildRequestData(DeviceControlParam param) {
        Map<String, Object> data = new HashMap<>();
        data.put("UUID", param.getUuid());
        data.put("device_id", param.getDeviceCode());
        data.put("charge_id", param.getChargeId());
        data.put("quantity", param.getQuantity() != null ? param.getQuantity() : 0);
        data.put("minute", param.getMinute() != null ? param.getMinute() : 0);
        data.put("second", param.getSecond() != null ? param.getSecond() : 0);

        log.debug("构建第三方接口请求参数：{}", JSONUtil.toJsonStr(data));
        return data;
    }
}
