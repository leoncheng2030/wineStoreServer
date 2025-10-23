package vip.xiaonuo.mini.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.modular.spec.entity.WineSpec;
import vip.xiaonuo.wine.modular.spec.service.WineSpecService;

import java.util.List;

@Slf4j
@Tag(name = "小程序规格获取")
@RestController
@Validated
public class SpecController {
    @Resource
    private WineSpecService wineSpecService;

    @Operation(summary = "获取规格列表")
    @CommonLog("代理商申请")
    @GetMapping("/mini/spec/list")
    public CommonResult<List<WineSpec>> list() {
        List<WineSpec> list = wineSpecService.list();
        return CommonResult.data(list);
    }
}
