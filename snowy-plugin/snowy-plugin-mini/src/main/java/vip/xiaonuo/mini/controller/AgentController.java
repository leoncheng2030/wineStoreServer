package vip.xiaonuo.mini.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.mini.param.AgentApplyParam;
import vip.xiaonuo.wine.api.WineAgentApi;
import vip.xiaonuo.wine.modular.agentapply.entity.WineAgentApply;
import vip.xiaonuo.wine.modular.agentapply.param.WineAgentApplyAddParam;
import vip.xiaonuo.wine.modular.agentapply.service.WineAgentApplyService;
import vip.xiaonuo.wine.modular.commissionrecord.entity.WineCommissionRecord;
import vip.xiaonuo.wine.modular.commissionrecord.service.WineCommissionRecordService;
import vip.xiaonuo.wine.modular.order.entity.WineOrder;
import vip.xiaonuo.wine.modular.order.param.WineOrderAgentPageParam;
import vip.xiaonuo.wine.modular.order.param.WineOrderIdParam;
import vip.xiaonuo.wine.modular.order.service.WineOrderService;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.store.service.WineStoreService;
import vip.xiaonuo.wine.modular.store.entity.WineStore;
import com.fhs.trans.service.impl.TransService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序代理商申请控制器
 *
 * @author jetox
 * @date 2025/09/20
 */
@Slf4j
@Tag(name = "小程序代理商申请控制器")
@RestController
@Validated
public class AgentController {

    @Resource
    private WineAgentApi wineAgentApi;

    @Resource
    private WineAgentApplyService wineAgentApplyService;

    @Resource
    private ClientApi clientApi;
    
    @Resource
    private WineOrderService wineOrderService;

    @Resource
    private WineDeviceService wineDeviceService;
    
    @Resource
    private WineStoreService wineStoreService;
    
    @Resource
    private TransService transService;
    @Resource
    private WineCommissionRecordService wineCommissionRecordService;

    /**
     * 代理商申请
     *
     * @author jetox
     * @date 2025/09/20
     */
    @Operation(summary = "代理商申请")
    @CommonLog("代理商申请")
    @PostMapping("/mini/agent/apply")
    public CommonResult<String> apply(@RequestBody @Valid AgentApplyParam agentApplyParam) {
        try {
            // 获取当前登录用户ID
            String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
            
            // 创建申请参数
            WineAgentApplyAddParam addParam = new WineAgentApplyAddParam();
            BeanUtil.copyProperties(agentApplyParam, addParam);
            addParam.setClientUserId(userId);
            
            // 提交申请
            wineAgentApplyService.add(addParam);
            
            return CommonResult.ok("申请提交成功，请等待审核");
        } catch (Exception e) {
            return CommonResult.error(e.getMessage());
        }
    }

    /**
     * 检查代理商状态
     *
     * @author jetox
     * @date 2025/09/20
     */
    @Operation(summary = "检查代理商状态")
    @GetMapping("/mini/agent/checkStatus")
    public CommonResult<Map<String, Object>> checkStatus() {
        String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 通过ClientApi获取用户信息
            boolean isAgent = clientApi.isUserAgent(userId);
            
            result.put("isAgent", isAgent);
            
            if (!isAgent) {
                // 获取最新申请记录
                WineAgentApply latestApply = wineAgentApplyService.getLatestByUserId(userId);
                if (latestApply != null) {
                    result.put("hasApply", true);
                    result.put("applyStatus", latestApply.getStatus());
                    result.put("applyTime", latestApply.getCreateTime());
                    result.put("auditRemark", latestApply.getAuditRemark());
                } else {
                    result.put("hasApply", false);
                }
                
                // 检查是否可以申请
                result.put("canApply", wineAgentApplyService.canApply(userId));
            }
            
            return CommonResult.data(result);
        } catch (Exception e) {
            log.error("检查代理商状态失败", e);
            result.put("isAgent", false);
            result.put("hasApply", false);
            result.put("canApply", true);
            return CommonResult.data(result);
        }
    }

    /**
     * 获取代理商申请详情
     *
     * @author jetox
     * @date 2025/09/20
     */
    @Operation(summary = "获取代理商申请详情")
    @GetMapping("/mini/agent/getApplyDetail")
    public CommonResult<WineAgentApply> getApplyDetail() {
        String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
        
        try {
            WineAgentApply latestApply = wineAgentApplyService.getLatestByUserId(userId);
            return CommonResult.data(latestApply);
        } catch (Exception e) {
            return CommonResult.error("获取申请详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取代理商订单分页
     *
     * @author Qoder
     * @date 2024/09/20
     */
    @Operation(summary = "获取代理商订单分页")
    @CommonLog("获取代理商订单分页")
    @GetMapping("/mini/agent/orderPage")
    public CommonResult<Page<WineOrder>> getAgentOrderPage(WineOrderAgentPageParam wineOrderAgentPageParam) {
        try {
            String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
            
            // 检查用户是否为代理商
            boolean isAgent = clientApi.isUserAgent(userId);
            if (!isAgent) {
                return CommonResult.error("您不是代理商，无法查看订单");
            }
            
            Page<WineOrder> orderPage = wineOrderService.agentOrderPage(wineOrderAgentPageParam, userId);
            return CommonResult.data(orderPage);
        } catch (Exception e) {
            log.error("获取代理商订单失败", e);
            return CommonResult.error("获取订单列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取代理商订单详情
     *
     * @author Qoder
     * @date 2024/09/20
     */
    @Operation(summary = "获取代理商订单详情")
    @CommonLog("获取代理商订单详情")
    @GetMapping("/mini/agent/orderDetail")
    public CommonResult<Map<String, Object>> getAgentOrderDetail(@Valid WineOrderIdParam wineOrderIdParam) {
        try {
            String userId = StpClientLoginUserUtil.getClientLoginUser().getId();
            
            // 检查用户是否为代理商
            boolean isAgent = clientApi.isUserAgent(userId);
            if (!isAgent) {
                return CommonResult.error("您不是代理商，无法查看订单");
            }
            
            WineOrder order = wineOrderService.detail(wineOrderIdParam);
            if (order == null) {
                return CommonResult.error("订单不存在");
            }
            
            // 验证订单是否属于该代理商的设备
            String deviceId = order.getDeviceId();
            if (deviceId != null) {
                WineDevice device = wineDeviceService.queryEntity(deviceId);
                if (device == null || !userId.equals(device.getAgentUserId())) {
                    return CommonResult.error("您无权查看该订单");
                }
                
                // 手动设置设备相关信息
                order.setDeviceCode(device.getDeviceCode());
                order.setDeviceName(device.getDeviceName());
                
                // 手动获取门店名称和地址
                if (device.getStoreId() != null) {
                    WineStore store = wineStoreService.queryEntity(device.getStoreId());
                    if (store != null) {
                        order.setStoreName(store.getStoreName());
                        // 设置设备位置（使用门店地址）
                        String deviceLocation = "";
                        if (store.getProvince() != null) deviceLocation += store.getProvince();
                        if (store.getCity() != null) deviceLocation += store.getCity();
                        if (store.getDistrict() != null) deviceLocation += store.getDistrict();
                        if (store.getDetailAddress() != null) deviceLocation += store.getDetailAddress();
                        order.setDeviceLocation(deviceLocation);
                    }
                }
            }
            
            // 进行数据转换，填充关联信息
            transService.transOne(order);
            
            // 获取订单的佣金记录
            List<WineCommissionRecord> commissionRecords = wineCommissionRecordService.list(
                new LambdaQueryWrapper<WineCommissionRecord>()
                    .eq(WineCommissionRecord::getOrderId, order.getId())
            );
            
            // 对佣金记录进行数据转换
            if (commissionRecords != null && !commissionRecords.isEmpty()) {
                transService.transMore(commissionRecords);
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("orderDetail", order);
            result.put("commissionInfo", commissionRecords);
            
            return CommonResult.data(result);
        } catch (Exception e) {
            log.error("获取代理商订单详情失败", e);
            return CommonResult.error("获取订单详情失败：" + e.getMessage());
        }
    }
}