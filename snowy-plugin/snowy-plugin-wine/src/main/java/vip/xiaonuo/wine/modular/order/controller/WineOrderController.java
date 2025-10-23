/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.wine.modular.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.modular.order.entity.WineOrder;
import vip.xiaonuo.wine.modular.order.param.WineOrderAddParam;
import vip.xiaonuo.wine.modular.order.param.WineOrderEditParam;
import vip.xiaonuo.wine.modular.order.param.WineOrderIdParam;
import vip.xiaonuo.wine.modular.order.param.WineOrderPageParam;
import vip.xiaonuo.wine.modular.order.service.WineOrderService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 订单表控制器
 *
 * @author jetox
 * @date  2025/07/24 08:47
 */
@Tag(name = "订单表控制器")
@RestController
@Validated
public class WineOrderController {

    @Resource
    private WineOrderService wineOrderService;

    /**
     * 获取订单表分页
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    @Operation(summary = "获取订单表分页")
    @SaCheckPermission("/wine/order/page")
    @GetMapping("/wine/order/page")
    public CommonResult<Page<WineOrder>> page(WineOrderPageParam wineOrderPageParam) {
        return CommonResult.data(wineOrderService.page(wineOrderPageParam));
    }

    /**
     * 删除订单表
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    @Operation(summary = "删除订单表")
    @CommonLog("删除订单表")
    @SaCheckPermission("/wine/order/delete")
    @PostMapping("/wine/order/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<WineOrderIdParam> wineOrderIdParamList) {
        wineOrderService.delete(wineOrderIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取订单表详情
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    @Operation(summary = "获取订单表详情")
    @SaCheckPermission("/wine/order/detail")
    @GetMapping("/wine/order/detail")
    public CommonResult<WineOrder> detail(@Valid WineOrderIdParam wineOrderIdParam) {
        return CommonResult.data(wineOrderService.detail(wineOrderIdParam));
    }

    /**
     * 手动触发订单分佣分账（管理员功能）
     *
     * @author xuyuxiang
     * @date 2024/12/19
     */
    @Operation(summary = "手动触发订单分佣分账")
    @CommonLog("手动触发订单分佣分账")
    @SaCheckPermission("/wine/order/manualProfitSharing")
    @PostMapping("/wine/order/manualProfitSharing")
    public CommonResult<String> manualProfitSharing(@RequestBody @Valid WineOrderIdParam wineOrderIdParam) {
        WineOrder wineOrder = wineOrderService.queryEntity(wineOrderIdParam.getId());
        wineOrderService.processCommissionAndProfitSharingForOrder(wineOrder);
        return CommonResult.ok("手动分佣分账触发成功");
    }
    
    /**
     * 手动修复退款状态（管理员功能）
     * 用于处理已经退款成功但订单状态未正确更新的情况
     *
     * @author Qoder
     * @date 2025/09/20
     */
    @Operation(summary = "手动修复退款状态")
    @CommonLog("手动修复退款状态")
    @SaCheckPermission("/wine/order/fixRefundStatus")
    @PostMapping("/wine/order/fixRefundStatus")
    public CommonResult<String> fixRefundStatus(@RequestBody @Valid WineOrderIdParam wineOrderIdParam) {
        return CommonResult.data(wineOrderService.fixRefundStatus(wineOrderIdParam));
    }
}
