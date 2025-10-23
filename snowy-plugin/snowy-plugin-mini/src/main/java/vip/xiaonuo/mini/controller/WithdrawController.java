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
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.modular.withdrawrecord.entity.WineWithdrawRecord;
import vip.xiaonuo.wine.modular.withdrawrecord.param.WineWithdrawRecordAddParam;
import vip.xiaonuo.wine.modular.withdrawrecord.param.WineWithdrawRecordPageParam;
import vip.xiaonuo.wine.modular.withdrawrecord.service.WineWithdrawRecordService;

@Tag(name = "提现控制器")
@RestController
@Validated
public class WithdrawController {
    @Resource
    private WineWithdrawRecordService wineWithdrawRecordService;
    @ApiOperationSupport(order = 1)
    @Operation(summary = "获取提现列表")
    @GetMapping("/mini/withdraw/page")
    public CommonResult<Page<WineWithdrawRecord>> getMiniWithdrawList(WineWithdrawRecordPageParam wineWithdrawRecordPageParam){
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineWithdrawRecordPageParam.setUserId(id);
        Page<WineWithdrawRecord> page = wineWithdrawRecordService.page(wineWithdrawRecordPageParam);
        return CommonResult.data(page);
    }
    @ApiOperationSupport(order = 2)
    @Operation(summary ="发起提现")
    @PostMapping("/mini/withdraw/handle")
    public CommonResult<String> add(@RequestBody @Validated WineWithdrawRecordAddParam wineWithdrawRecordAddParam) {
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineWithdrawRecordAddParam.setUserId(id);
        wineWithdrawRecordService.add(wineWithdrawRecordAddParam);
        return CommonResult.ok();
    }
    @ApiOperationSupport(order = 3)
    @Operation(summary ="确认收款")
    @PostMapping("/mini/withdraw/confirm")
    public CommonResult<String> confirm() {
        return CommonResult.ok("暂未实现");
    }
}
