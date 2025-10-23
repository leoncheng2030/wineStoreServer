package vip.xiaonuo.mini.controller;

import cn.dev33.satoken.annotation.SaCheckDisable;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.core.pojo.SaBaseClientLoginUser;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.mini.param.DeviceUpdateInfoParam;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.param.*;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserAddParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserCommissionRateParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserIdParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserUnbindParam;
import vip.xiaonuo.wine.modular.deviceuser.service.WineDeviceUserService;
import vip.xiaonuo.wine.modular.deviceuser.entity.WineDeviceUser;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserBindParam;
import vip.xiaonuo.wine.modular.device.param.WineDeviceBindAgentParam;
import vip.xiaonuo.wine.modular.deviceuser.vo.WineDeviceUserVO;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserBindRoleParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserEditRoleParam;
import vip.xiaonuo.wine.modular.product.entity.WineProduct;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "设备控制器")
@RestController
@Validated
@Slf4j
public class DeviceController {
    @Resource
    private WineDeviceService wineDeviceService;
    @Resource
    private WineDeviceUserService wineDeviceUserService;

    // ==================== 设备基本信息管理 ====================
    
    @Operation(summary = "获取设备列表")
    @GetMapping("/mini/device/page")
    @CommonLog("获取设备列表")
    @ApiOperationSupport(order = 1)
    public CommonResult<Page<WineDevice>> getMiniDeviceList(WineDevicePageParam wineDevicePageParam){
        return CommonResult.data(wineDeviceService.page(wineDevicePageParam));
    }

    @Operation(summary = "获取设备详情")
    @GetMapping("/mini/device/detail")
    @CommonLog("获取设备详情")
    @ApiOperationSupport(order = 2)
    public CommonResult<WineDevice> getMiniDeviceDetail(WineDeviceIdParam wineDeviceIdParam){
        return CommonResult.data(wineDeviceService.queryEntity(wineDeviceIdParam.getId()));
    }

    @Operation(summary = "根据设备码获取设备详情")
    @GetMapping("/mini/device/detailByCode")
    @CommonLog("根据设备码获取设备详情")
    @ApiOperationSupport(order = 3)
    public CommonResult<WineDevice> getMiniDeviceByCode(String deviceCode){
        return CommonResult.data(wineDeviceService.detailByCode(deviceCode));
    }
    
    @Operation(summary = "获取我的设备列表")
    @GetMapping("/mini/device/my-devices")
    @CommonLog("获取我的设备列表")
    @ApiOperationSupport(order = 4)
    public CommonResult<Page<WineDevice>> getMyDevices(WineDevicePageParam wineDevicePageParam){
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineDevicePageParam.setUserId(id);
        return CommonResult.data(wineDeviceService.page(wineDevicePageParam));
    }
    
    @Operation(summary = "获取设备列表-代理商版本")
    @GetMapping("/mini/device/agentPage")
    @CommonLog("获取设备列表-代理商版本")
    @ApiOperationSupport(order = 5)
    public CommonResult<Page<WineDevice>> getMiniDeviceListForAgent(WineDeviceAgentPage wineDeviceAgentPage){
        return CommonResult.data(wineDeviceService.agentPage(wineDeviceAgentPage));
    }
    
    @Operation(summary = "更新设备信息")
    @PostMapping("/mini/device/updateInfo")
    @CommonLog("更新设备信息")
    @ApiOperationSupport(order = 6)
    public CommonResult<String> updateInfo(@RequestBody @Validated DeviceUpdateInfoParam deviceUpdateInfoParam){
        wineDeviceService.updateInfo(deviceUpdateInfoParam.getDeviceId(),deviceUpdateInfoParam.getProductId(),deviceUpdateInfoParam.getStoreId(),deviceUpdateInfoParam.getManageId());
        return CommonResult.ok();
    }
    
    @Operation(summary = "根据设备ID获取对应的产品信息")
    @GetMapping("/mini/device/getProductInfoByDeviceId")
    @CommonLog("根据设备ID获取对应的产品信息")
    @ApiOperationSupport(order = 7)
    public CommonResult<WineProduct> getProductInfo(WineDeviceIdParam wineDeviceIdParam){
        return CommonResult.data(wineDeviceService.getProductInfo(wineDeviceIdParam));
    }


    // ==================== 设备绑定管理 ====================
    
    @Operation(summary = "绑定设备代理商")
    @PostMapping("/mini/device/bindAgent")
    @CommonLog("绑定设备代理商")
    @ApiOperationSupport(order = 10)
    public CommonResult<String> bindAgent(@RequestBody @Validated WineDeviceBindAgentParam wineDeviceBindAgentParam){
        // 自动获取当前登录用户的ID作为代理商ID
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineDeviceBindAgentParam.setAgentUserId(currentUserId);
        
        wineDeviceService.bindDeviceAgent(wineDeviceBindAgentParam);
        return CommonResult.ok();
    }
    
    @Operation(summary = "绑定设备用户角色")
    @PostMapping("/mini/device/bindUserRole")
    @CommonLog("绑定设备用户角色")
    @ApiOperationSupport(order = 11)
    public CommonResult<String> bindUserRole(@RequestBody WineDeviceUserBindRoleParam wineDeviceUserBindRoleParam){
        // 自动获取当前登录用户的ID，在验证之前设置
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineDeviceUserBindRoleParam.setUserId(currentUserId);
        
        // 检查用户资料完整性
        SaBaseClientLoginUser currentUser = StpClientLoginUserUtil.getClientLoginUser();
        if (!isUserProfileComplete(currentUser)) {
            log.warn("用户{}资料不完整，需要先完善资料", currentUserId);
            return CommonResult.get(1001, "请先完善个人资料再进行角色绑定", null);
        }
        wineDeviceUserService.bindUserRole(wineDeviceUserBindRoleParam);
        return CommonResult.ok();
    }
    
    @Operation(summary = "编辑设备用户角色")
    @PostMapping("/mini/device/editUserRole")
    @CommonLog("编辑设备用户角色")
    @ApiOperationSupport(order = 12)
    public CommonResult<String> editUserRole(@RequestBody WineDeviceUserEditRoleParam wineDeviceUserEditRoleParam){
        wineDeviceUserService.editUserRole(wineDeviceUserEditRoleParam);
        return CommonResult.ok();
    }
    
    @Operation(summary = "解绑设备用户")
    @PostMapping("/mini/device/unbindUser")
    @CommonLog("解绑设备用户")
    @ApiOperationSupport(order = 13)
    public CommonResult<String> unbindUser(@RequestBody WineDeviceUserUnbindParam unbindParam){
        wineDeviceUserService.unbindUser(unbindParam.getDeviceId(), unbindParam.getUserId());
        return CommonResult.ok();
    }

    // ==================== 用户权限管理 ====================
    
    @Operation(summary = "设置佣金")
    @PostMapping("/mini/device/setCommission")
    @CommonLog("设置佣金")
    @ApiOperationSupport(order = 20)
    public CommonResult<String> setCommission(@RequestBody @Valid WineDeviceUserCommissionRateParam wineDeviceUserCommissionRateParam){
        wineDeviceUserService.setCommission(wineDeviceUserCommissionRateParam);
        return CommonResult.ok();
    }
    
    @Operation(summary = "获取设备关联的用户列表")
    @GetMapping("/mini/device/users")
    @CommonLog("获取设备关联的用户列表")
    @ApiOperationSupport(order = 21)
    public CommonResult<List<WineDeviceUserVO>> getDeviceUsers(String deviceId){
        return CommonResult.data(wineDeviceUserService.getDeviceUsersWithUserInfo(deviceId));
    }
    
    @Operation(summary = "根据设备和角色获取用户列表")
    @GetMapping("/mini/device/usersByRole")
    @CommonLog("根据设备和角色获取用户列表")
    @ApiOperationSupport(order = 22)
    public CommonResult<List<WineDeviceUser>> getUsersByDeviceAndRole(String deviceId, String roleCode){
        return CommonResult.data(wineDeviceUserService.getUsersByDeviceAndRole(deviceId, roleCode));
    }
    
    @Operation(summary = "获取用户在设备上的角色列表")
    @GetMapping("/mini/device/userRoles")
    @CommonLog("获取用户在设备上的角色列表")
    @ApiOperationSupport(order = 23)
    public CommonResult<List<String>> getUserRolesForDevice(String deviceId){
        String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
        return CommonResult.data(wineDeviceUserService.getUserRolesForDevice(userId, deviceId));
    }
    
    @Operation(summary = "检查用户是否有指定角色")
    @GetMapping("/mini/device/hasRole")
    @CommonLog("检查用户是否有指定角色")
    @ApiOperationSupport(order = 24)
    public CommonResult<Boolean> hasRole(String deviceId, String roleCode){
        String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
        return CommonResult.data(wineDeviceUserService.hasRole(userId, deviceId, roleCode));
    }

    
    // ==================== 设备操作管理 ====================
    
    @Operation(summary = "更新设备的脉冲比")
    @PostMapping("/mini/device/updatePulseCount")
    @CommonLog("更新设备的脉冲比")
    @ApiOperationSupport(order = 30)
    public CommonResult<String> updatePulseCount(WineDevicePulseRatioParam wineDevicePulseRatioParam){
        wineDeviceService.updatePulseCount(wineDevicePulseRatioParam);
        return CommonResult.ok();
    }
    
    @Operation(summary = "增加库存")
    @PostMapping("/mini/device/addStock")
    @CommonLog("增加库存")
    @ApiOperationSupport(order = 31)
    public CommonResult<WineDevice> addStock(@RequestBody @Validated WineDeviceStockParam wineDeviceStockParam){
        return CommonResult.data(wineDeviceService.addStock(wineDeviceStockParam));
    }
    
    @Operation(summary = "获取加密指令")
    @PostMapping("/mini/device/getDeviceControlCommand")
    @CommonLog("获取加密指令")
    @ApiOperationSupport(order = 32)
    public CommonResult<String> getDeviceControlCommand(@RequestBody DeviceControlParam deviceControlParam){
        return CommonResult.data(wineDeviceService.getDeviceControlCommand(deviceControlParam));
    }
    
    @Operation(summary = "获取加密指令-测试")
    @PostMapping("/mini/device/getDeviceTestControlCommand")
    @CommonLog("获取加密指令-测试")
    @ApiOperationSupport(order = 33)
    public CommonResult<String> getDeviceTestControlCommand(@RequestBody DeviceTestControlParam deviceTestControlParam){
        return CommonResult.data(wineDeviceService.getDeviceTestControlCommand(deviceTestControlParam));
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * 检查用户资料是否完整
     * @param user 用户信息
     * @return 是否完整
     */
    private boolean isUserProfileComplete(SaBaseClientLoginUser user) {
        // 检查必要字段：昵称和头像
        boolean hasNickname = user.getNickname() != null && 
                             !user.getNickname().trim().isEmpty() && 
                             !user.getNickname().startsWith("微信用户"); // 排除默认昵称
        
        boolean hasAvatar = user.getAvatar() != null && 
                           !user.getAvatar().trim().isEmpty() && 
                           !user.getAvatar().contains("default-avatar"); // 排除默认头像
        return hasNickname && hasAvatar;
    }
}
