package vip.xiaonuo.mini.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.api.DevSlideshowApi;
import vip.xiaonuo.wine.modular.product.entity.WineProduct;
import vip.xiaonuo.wine.modular.product.param.WineProductIdParam;
import vip.xiaonuo.wine.modular.product.param.WineProductPageParam;
import vip.xiaonuo.wine.modular.product.service.WineProductService;

import java.util.List;
import java.util.Objects;

@Tag(name = "产品控制器")
@RestController
@Validated
public class ProductController {

    @Resource
    private WineProductService wineProductService;
    /**
     * 首页获取产品列表
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    @Operation(summary = "首页获取产品列表")
    @GetMapping("/mini/product/page")
    public CommonResult<Page<WineProduct>> getMiniSlider(WineProductPageParam wineProductPageParam) {
        Page<WineProduct> page = wineProductService.page(wineProductPageParam);
        return CommonResult.data(page);
    }

    /**
     * 获取小程序端产品详情
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    @Operation(summary = "获取小程序端产品详情")
    @GetMapping("/mini/product/detail")
    public CommonResult<WineProduct> getMiniProductDetail(WineProductIdParam wineProductIdParam) {
        WineProduct wineProduct = wineProductService.getById(wineProductIdParam.getId());
        return CommonResult.data(wineProduct);
    }

    /**
     * 获取酒品列表-不分页
     * @author jetox
     * @date  2025/07/24 08:56
     */
    @Operation(summary = "获取小程序端酒品列表-不分页")
    @GetMapping("/mini/product/list")
    public CommonResult<List<WineProduct>> getMiniProductList() {
        List<WineProduct> wineProductList = wineProductService.list();
        return CommonResult.data(wineProductList);
    }

}
