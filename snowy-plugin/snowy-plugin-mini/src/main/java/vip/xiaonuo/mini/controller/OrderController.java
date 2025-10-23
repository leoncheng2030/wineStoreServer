package vip.xiaonuo.mini.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.modular.order.entity.WineOrder;
import vip.xiaonuo.wine.modular.order.param.*;
import vip.xiaonuo.wine.modular.order.service.WineOrderService;

@RestController
@Tag(name = "订单控制器")
@Validated
public class OrderController {
    @Resource
    private WineOrderService wineOrderService;
    /**
     * 获取订单列表
     */
    @Operation(summary = "获取订单列表")
    @GetMapping("/mini/order/page")
    @CommonLog("获取订单列表")
    @ApiOperationSupport(order = 1)
    public CommonResult<Page<WineOrder>> page(WineOrderPageParam wineOrderPageParam){
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineOrderPageParam.setUserId(id);
        Page<WineOrder> page = wineOrderService.page(wineOrderPageParam);
        return CommonResult.data(page);
    }
    /**
     * 获取订单详情
     */

    @GetMapping("/mini/order/detail")
    @Operation(summary = "获取订单详情")
    @ApiOperationSupport(order = 2)
    public CommonResult<WineOrder> add(WineOrderIdParam wineOrderIdParam){
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        return CommonResult.data(wineOrderService.detail(wineOrderIdParam));
    }
    /**
     * 创建订单
     */
    @PostMapping("/mini/order/create")
    @Operation(summary = "创建订单")
    @CommonLog("创建订单")
    @ApiOperationSupport(order = 3)
    public CommonResult<WineOrder> create(WineOrderAddParam wineOrderAddParam){
        return CommonResult.data(wineOrderService.addMini(wineOrderAddParam));
    }


    /**
     * 取消订单
     */
    @Operation(summary = "取消订单")
    @PostMapping("/mini/order/cancel")
    @CommonLog("取消订单")
    @ApiOperationSupport(order = 4)
    public CommonResult<String> cancel(WineOrderCancelParam wineOrderCancelParam){
        wineOrderService.cancel(wineOrderCancelParam);
        return CommonResult.ok();
    }

    /**
     * 申请退款
     * @param wineOrderIdParam
     * @return
     */
    @Operation(summary = "申请退款")
    @ApiOperationSupport(order = 5)
    @PostMapping("/mini/order/applyRefund")
    @CommonLog("申请退款")
    public CommonResult<String> applyRefund(@RequestBody @Validated WineOrderIdParam wineOrderIdParam){
        wineOrderService.applyRefund(wineOrderIdParam);
        return CommonResult.ok();
    }

    @PostMapping("/mini/order/startDispense")
    @Operation(summary = "开始出酒")
    @CommonLog("开始出酒")
    @ApiOperationSupport(order = 6)
    public CommonResult<String> startDispense(@RequestBody @Validated WineOrderIdParam wineOrderIdParam){
        wineOrderService.startDispense(wineOrderIdParam);
        return CommonResult.ok();
    }

    /**
     * 出酒完成
     */
    @PostMapping("/mini/order/endDispense")
    @Operation(summary = "结束出酒")
    @CommonLog("结束出酒")
    @ApiOperationSupport(order = 7)
    public CommonResult<String> endDispense(@RequestBody @Validated WineOrderIdParam wineOrderIdParam){
        wineOrderService.endDispense(wineOrderIdParam);
        return CommonResult.ok();
    }

    /**
     * 实时上报剩余脉冲数
     */
    @PostMapping("/mini/order/reportOrderRemainQuantity")
    @Operation(summary = "上报剩余脉冲数")
    @CommonLog("上报剩余脉冲数")
    @ApiOperationSupport(order = 8)
    public CommonResult<String> reportOrderRemainQuantity(@RequestBody @Validated WineOrderRemainOrderParam wineOrderRemainOrderParam){
        wineOrderService.reportOrderRemainQuantity(wineOrderRemainOrderParam);
        return CommonResult.ok();
    }
}
