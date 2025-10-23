package vip.xiaonuo.wine.api;

import java.util.List;
import java.util.Map;

/**
 * 设备管理API
 *
 * @author yourname
 * @date 2025/7/28
 */
public interface WineDeviceApi {

    /**
     * 根据管理员用户ID获取设备列表
     *
     * @param managerUserId 管理员用户ID
     * @return 设备ID列表
     */
    List<String> getDeviceIdsByManagerUserId(String managerUserId);
    
    /**
     * 根据设备名称获取设备信息
     *
     * @param deviceName 设备名称
     * @return 设备信息 Map，包含 deviceId, deviceCode, deviceName, storeId 等字段
     */
    Map<String, Object> getDeviceInfoByDeviceName(String deviceName);
    
    /**
     * 根据设备ID获取设备信息
     *
     * @param deviceId 设备ID
     * @return 设备信息 Map，包含 deviceId, deviceCode, deviceName, storeId 等字段
     */
    Map<String, Object> getDeviceInfoById(String deviceId);
}