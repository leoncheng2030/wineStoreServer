package vip.xiaonuo.wine.modular.device.service;

import vip.xiaonuo.wine.modular.device.param.DeviceControlParam;
import vip.xiaonuo.wine.modular.device.result.DeviceControlResult;

/**
 * 设备控制业务服务接口
 * 仅负责获取设备控制的加密指令，不包含实际控制功能
 *
 * @author AI Assistant
 * @date 2025/01/30
 */
public interface DeviceControlService {

    /**
     * 获取设备控制加密指令
     * 调用第三方接口获取加密控制指令
     *
     * @param param 控制参数
     * @return 控制结果，包含加密指令
     */
    DeviceControlResult getControlCommand(DeviceControlParam param);
}
