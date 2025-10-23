package vip.xiaonuo.mini.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.api.DevSlideshowApi;
import vip.xiaonuo.dev.modular.slideshow.service.DevSlideshowService;

import java.util.List;

@Tag(name = "轮播图控制器啊")
@RestController
@Validated
public class SliderController {
    @Resource
    private DevSlideshowService devSlideshowService;
    /**
     * 获取小程序端轮播图
     *
     * @author jetox
     * @date  2025/07/24 08:56
     */
    @Operation(summary = "获取小程序端轮播图")
    @GetMapping("/mini/slideshow/listByPlace")
    public CommonResult<List<JSONObject>> getMiniSlider() {
        List<JSONObject> mini = devSlideshowService.getListByPlace("MINI_HOME");
        return CommonResult.data(mini);
    }
}
