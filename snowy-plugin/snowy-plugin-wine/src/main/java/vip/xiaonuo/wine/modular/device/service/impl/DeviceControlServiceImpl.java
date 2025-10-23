package vip.xiaonuo.wine.modular.device.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.param.DeviceControlParam;
import vip.xiaonuo.wine.modular.device.result.DeviceControlResult;
import vip.xiaonuo.wine.modular.device.service.DeviceControlService;
import vip.xiaonuo.wine.modular.device.service.EncryptApiService;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;

@Slf4j
@Service
public class DeviceControlServiceImpl implements DeviceControlService {

    @Resource
    private EncryptApiService encryptApiService;

    @Resource
    private WineDeviceService wineDeviceService;

    @Resource
    private DevConfigApi devConfigApi;

    @Override
    public DeviceControlResult getControlCommand(DeviceControlParam param) {
        try {
            log.info("获取设备控制加密指令，设备ID：{}，订单ID：{}", param.getDeviceCode(), param.getChargeId());
            // 1. 获取设备信息
            WineDevice device = wineDeviceService.getDeviceByDeviceCode(String.valueOf(param.getDeviceCode()));
            if (device == null) {
                return DeviceControlResult.failure("设备不存在");
            }
            param.setUuid(device.getUuid());
            // 只使用设备的脉冲比进行计算
            Double devicePulseRatio = device.getPulseRatio();

            if (devicePulseRatio != null && param.getQuantity() > 0) {
                // 计算脉冲数并进行边界检查
                double calculatedQuantity = devicePulseRatio * param.getQuantity();
                if (calculatedQuantity < 0 || calculatedQuantity > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("Calculated quantity is out of valid range: " + calculatedQuantity);
                }

                // 使用更安全的类型转换
                int roundedQuantity = (int) Math.round(calculatedQuantity);
                param.setQuantity(roundedQuantity);
            }
            // 4. 调用第三方接口获取加密指令
            String encryptCommand = encryptApiService.getEncryptCommand(param);
            if (StrUtil.isBlank(encryptCommand)) {
                return DeviceControlResult.failure("获取加密指令失败");
            }
            log.info("获取设备控制加密指令成功，最终结果：{}", encryptCommand);
            log.info("设备控制加密指令获取成功，设备ID：{}，订单ID：{}", param.getDeviceCode(), param.getChargeId());
            return DeviceControlResult.success(encryptCommand);

        } catch (Exception e) {
            log.error("获取设备控制加密指令异常，设备ID：{}，订单ID：{}", param.getDeviceCode(), param.getChargeId(), e);
            return DeviceControlResult.failure("系统异常：" + e.getMessage());
        }
    }
}
