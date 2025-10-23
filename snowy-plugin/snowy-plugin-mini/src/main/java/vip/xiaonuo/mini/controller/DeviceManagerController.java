package vip.xiaonuo.mini.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.param.WineDeviceStockParam;
import vip.xiaonuo.wine.modular.device.param.WineDevicePulseRatioParam;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;
import vip.xiaonuo.wine.modular.deviceuser.service.WineDeviceUserService;
import vip.xiaonuo.wine.modular.stockrecord.entity.WineStockRecord;
import vip.xiaonuo.wine.modular.stockrecord.param.WineStockRecordPageParam;
import vip.xiaonuo.wine.modular.stockrecord.service.WineStockRecordService;

import java.util.List;
import java.util.Map;

/**
 * 设备管理员控制器
 *
 * @author system
 * @date 2025/09/21
 */
@Tag(name = "设备管理员控制器")
@RestController
@Validated
@Slf4j
public class DeviceManagerController {

    @Resource
    private WineDeviceService wineDeviceService;
    
    @Resource
    private WineDeviceUserService wineDeviceUserService;
    
    @Resource
    private WineStockRecordService wineStockRecordService;

    /**
     * 获取设备管理员管理的设备列表
     */
    @Operation(summary = "获取设备管理员管理的设备列表")
    @GetMapping("/device-manager/devices")
    public CommonResult<List<WineDevice>> getManagerDevices() {
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        List<String> deviceIds = wineDeviceUserService.getDeviceIdsByUserRole(currentUserId, "DEVICE_MANAGER");
        
        if (deviceIds.isEmpty()) {
            return CommonResult.data(List.of());
        }
        
        List<WineDevice> devices = wineDeviceService.listByIds(deviceIds);
        return CommonResult.data(devices);
    }

    /**
     * 获取设备管理员统计信息
     */
    @Operation(summary = "获取设备管理员统计信息")
    @GetMapping("/device-manager/statistics")
    public CommonResult<Object> getStatistics() {
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        
        // 获取管理的设备ID列表
        List<String> deviceIds = wineDeviceUserService.getDeviceIdsByUserRole(currentUserId, "DEVICE_MANAGER");
        
        if (deviceIds.isEmpty()) {
            return CommonResult.data(Map.of(
                "managedDeviceCount", 0,
                "lowStockCount", 0,
                "todayRestockCount", 0,
                "totalRestockCount", 0
            ));
        }
        
        // 获取设备列表计算统计
        List<WineDevice> devices = wineDeviceService.listByIds(deviceIds);
        long lowStockCount = devices.stream().filter(device -> device.getStock() < 100).count();
        
        // 获取今日补货次数
        int todayRestockCount = wineStockRecordService.getTodayRestockCount(currentUserId, deviceIds);
        log.info("今日补货次数: {}", todayRestockCount);
        
        // 获取补货记录总数 - 使用临时方案
        int totalRestockCount;
        try {
            totalRestockCount = wineStockRecordService.getTotalRestockCount(currentUserId, deviceIds);
            log.info("补货记录总数(方法1): {}", totalRestockCount);
        } catch (Exception e) {
            log.error("调用getTotalRestockCount失败: {}", e.getMessage());
            // 使用备用方案：直接查询
            totalRestockCount = Math.toIntExact(wineStockRecordService.lambdaQuery()
                .eq(WineStockRecord::getOperatorId, currentUserId)
                .eq(WineStockRecord::getOperationType, "RESTOCK")
                .in(WineStockRecord::getDeviceId, deviceIds)
                .count());
            log.info("补货记录总数(备用方案): {}", totalRestockCount);
        }
        
        Map<String, Object> statistics = Map.of(
            "managedDeviceCount", devices.size(),
            "lowStockCount", lowStockCount,
            "todayRestockCount", todayRestockCount,
            "totalRestockCount", totalRestockCount
        );
        
        log.info("返回统计数据: {}", statistics);
        
        return CommonResult.data(statistics);
    }

    /**
     * 获取设备详细信息（包含库存）
     */
    @Operation(summary = "获取设备详细信息")
    @GetMapping("/device-manager/device/{deviceId}")
    public CommonResult<WineDevice> getDeviceDetail(@PathVariable String deviceId) {
        // 检查当前用户是否有权限管理该设备
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        List<String> managedDeviceIds = wineDeviceUserService.getDeviceIdsByUserRole(currentUserId, "DEVICE_MANAGER");
        
        if (!managedDeviceIds.contains(deviceId)) {
            return CommonResult.error("无权限访问该设备信息");
        }
        
        WineDevice device = wineDeviceService.getById(deviceId);
        return CommonResult.data(device);
    }

    /**
     * 补货操作
     */
    @Operation(summary = "设备补货")
    @CommonLog("设备补货")
    @PostMapping("/device-manager/restock")
    public CommonResult<WineDevice> restock(@RequestBody @Valid WineDeviceStockParam wineDeviceStockParam) {
        try {
            // 检查当前用户是否有权限管理该设备
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            List<String> managedDeviceIds = wineDeviceUserService.getDeviceIdsByUserRole(currentUserId, "DEVICE_MANAGER");
            
            if (!managedDeviceIds.contains(wineDeviceStockParam.getDeviceId())) {
                return CommonResult.error("无权限操作该设备");
            }
            
            WineDevice updatedDevice = wineDeviceService.addStock(wineDeviceStockParam);
            log.info("设备管理员{}成功为设备{}补货{}，当前库存：{}", 
                    currentUserId, wineDeviceStockParam.getDeviceId(), 
                    wineDeviceStockParam.getStockNum(), updatedDevice.getStock());
            
            return CommonResult.data(updatedDevice);
        } catch (Exception e) {
            log.error("设备补货失败", e);
            return CommonResult.error("补货操作失败：" + e.getMessage());
        }
    }

    /**
     * 获取设备库存记录
     */
    @Operation(summary = "获取设备库存记录")
    @GetMapping("/device-manager/stock-records/{deviceId}")
    public CommonResult<List<WineStockRecord>> getDeviceStockRecords(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        // 检查当前用户是否有权限管理该设备
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        List<String> managedDeviceIds = wineDeviceUserService.getDeviceIdsByUserRole(currentUserId, "DEVICE_MANAGER");
        
        if (!managedDeviceIds.contains(deviceId)) {
            return CommonResult.error("无权限访问该设备信息");
        }
        
        List<WineStockRecord> records = wineStockRecordService.getDeviceStockRecords(deviceId, limit);
        return CommonResult.data(records);
    }

    /**
     * 获取管理员操作记录
     */
    @Operation(summary = "获取管理员操作记录")
    @GetMapping("/device-manager/my-operation-records")
    public CommonResult<List<WineStockRecord>> getMyOperationRecords(
            @RequestParam(defaultValue = "20") Integer limit) {
        
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        List<WineStockRecord> records = wineStockRecordService.getManagerOperationRecords(currentUserId, limit);
        return CommonResult.data(records);
    }

    /**
     * 获取库存记录分页
     */
    @Operation(summary = "获取库存记录分页")
    @GetMapping("/device-manager/stock-records/page")
    public CommonResult<Page<WineStockRecord>> getStockRecordsPage(WineStockRecordPageParam pageParam) {
        // 限制只能查看自己管理的设备
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        List<String> managedDeviceIds = wineDeviceUserService.getDeviceIdsByUserRole(currentUserId, "DEVICE_MANAGER");
        
        if (managedDeviceIds.isEmpty()) {
            return CommonResult.data(new Page<>());
        }
        
        // 如果指定了设备ID，验证权限
        if (pageParam.getDeviceId() != null && !managedDeviceIds.contains(pageParam.getDeviceId())) {
            return CommonResult.error("无权限访问该设备信息");
        }
        
        Page<WineStockRecord> page = wineStockRecordService.page(pageParam);
        return CommonResult.data(page);
    }

    /**
     * 更新设备脉冲比
     */
    @Operation(summary = "更新设备脉冲比")
    @CommonLog("更新设备脉冲比")
    @PostMapping("/device-manager/pulse-ratio")
    public CommonResult<String> updatePulseRatio(@RequestBody @Valid WineDevicePulseRatioParam wineDevicePulseRatioParam) {
        try {
            // 检查当前用户是否有权限管理该设备
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            List<String> managedDeviceIds = wineDeviceUserService.getDeviceIdsByUserRole(currentUserId, "DEVICE_MANAGER");
            
            if (!managedDeviceIds.contains(wineDevicePulseRatioParam.getId())) {
                return CommonResult.error("无权限操作该设备");
            }
            
            wineDeviceService.updatePulseCount(wineDevicePulseRatioParam);
            log.info("设备管理员{}成功更新设备{}的脉冲比为{}", 
                    currentUserId, wineDevicePulseRatioParam.getId(), 
                    wineDevicePulseRatioParam.getPulseRatio());
            
            return CommonResult.ok();
        } catch (Exception e) {
            log.error("更新设备脉冲比失败", e);
            return CommonResult.error("更新脉冲比失败：" + e.getMessage());
        }
    }

}