package vip.xiaonuo.wine.modular.device.service;

import vip.xiaonuo.wine.modular.device.param.DeviceControlParam;

public interface EncryptApiService {

    /**
     * 调用第三方接口获取加密控制指令
     *
     * @param param 控制参数
     * @return 加密指令
     */
    String getEncryptCommand(DeviceControlParam param);
}
