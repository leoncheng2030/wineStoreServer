package vip.xiaonuo.pay.modular.wx.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.pay.modular.wx.param.MultiProfitSharingParam;
import vip.xiaonuo.pay.modular.wx.param.ProfitSharingParam;
import vip.xiaonuo.pay.modular.wx.service.PayWxService;
import vip.xiaonuo.pay.modular.wx.vo.ProfitSharingProcessResult;

/**
 * 微信分账控制器
 *
 * @author xuyuxiang
 * @date 2024/12/19
 */
@Tag(name = "微信分账控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 3)
@RestController
@Validated
public class PayWxProfitSharingController {

    @Resource
    private PayWxService payWxService;

    /**
     * 执行单接收方分账
     *
     * @author xuyuxiang
     * @date 2024/12/19
     */
    @ApiOperationSupport(order = 1)
    @Operation(summary = "执行单接收方分账")
    @CommonLog("执行单接收方分账")
    @PostMapping("/pay/wx/profitSharing")
    public CommonResult<String> profitSharing(@RequestBody @Valid ProfitSharingParam profitSharingParam) {
        payWxService.profitSharing(profitSharingParam);
        return CommonResult.ok("分账请求已提交");
    }

    /**
     * 执行多接收方分账
     *
     * @author xuyuxiang
     * @date 2024/12/19
     */
    @ApiOperationSupport(order = 2)
    @Operation(summary = "执行多接收方分账")
    @CommonLog("执行多接收方分账")
    @PostMapping("/pay/wx/multiProfitSharing")
    public CommonResult<ProfitSharingProcessResult> multiProfitSharing(@RequestBody @Valid MultiProfitSharingParam multiProfitSharingParam) {
        ProfitSharingProcessResult result = payWxService.multiProfitSharing(multiProfitSharingParam);
        return CommonResult.data(result);
    }

    /**
     * 查询分账结果
     *
     * @author xuyuxiang
     * @date 2024/12/19
     */
    @ApiOperationSupport(order = 3)
    @Operation(summary = "查询分账结果")
    @CommonLog("查询分账结果")
    @GetMapping("/pay/wx/profitSharingQuery")
    public CommonResult<String> profitSharingQuery(String outOrderNo) {
        String result = payWxService.profitSharingQuery(outOrderNo);
        return CommonResult.data(result);
    }
}
