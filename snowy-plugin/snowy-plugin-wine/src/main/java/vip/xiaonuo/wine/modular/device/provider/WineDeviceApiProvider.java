package vip.xiaonuo.wine.modular.device.provider;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.wine.api.WineDeviceApi;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;
import vip.xiaonuo.wine.modular.deviceuser.service.WineDeviceUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备管理API实现
 *
 * @author yourname
 * @date 2025/7/28
 */
@Service
public class WineDeviceApiProvider implements WineDeviceApi {
    @Resource
    private WineDeviceUserService wineDeviceUserService;

    @Resource
    private WineDeviceService wineDeviceService;

    @Override
    public List<String> getDeviceIdsByManagerUserId(String managerUserId) {
        // 使用新的角色系统获取设备列表
        return wineDeviceUserService.getDeviceIdsByUserRole(managerUserId, "DEVICE_MANAGER");
    }

    @Override
    public Map<String, Object> getDeviceInfoByDeviceName(String deviceName) {
        Map<String, Object> result = new HashMap<>();

        if (StrUtil.isBlank(deviceName)) {
            return result;
        }

        try {
            // 根据设备名称查询设备信息
            WineDevice device = wineDeviceService.lambdaQuery()
                    .eq(WineDevice::getDeviceName, deviceName)
                    .one();

            if (device != null) {
                result.put("deviceId", device.getId());
                result.put("deviceCode", device.getDeviceCode());
                result.put("deviceName", device.getDeviceName());
                result.put("storeId", device.getStoreId());
                result.put("stock", device.getStock());
                result.put("status", device.getStatus());
            }
        } catch (Exception e) {
            // 日志记录错误，但不抛出异常，返回空结果
            // 这里可以添加日志记录逻辑
        }
        return result;
    }

    @Override
    public Map<String, Object> getDeviceInfoById(String deviceId) {
        Map<String, Object> result = new HashMap<>();

        if (StrUtil.isBlank(deviceId)) {
            return result;
        }

        try {
            // 根据设备ID查询设备信息
            WineDevice device = wineDeviceService.getById(deviceId);

            if (device != null) {
                result.put("deviceId", device.getId());
                result.put("deviceCode", device.getDeviceCode());
                result.put("deviceName", device.getDeviceName());
                result.put("storeId", device.getStoreId());
                result.put("stock", device.getStock());
                result.put("status", device.getStatus());
            }
        } catch (Exception e) {
            // 日志记录错误，但不抛出异常，返回空结果
            // 这里可以添加日志记录逻辑
        }

        return result;
    }
}
