package vip.xiaonuo.mini.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.api.DevConfigApi;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "小程序配置控制器")
@RestController
@Validated
public class BaseConfigController {



    @Resource
    private DevConfigApi devConfigApi;

    @Operation(summary = "小程序基础配置")
    @GetMapping("/mini/base/config")
    @CommonLog("小程序基础配置")
    public CommonResult<Map<String, String>> getMiniBaseConfig() {
        Map<String, String> configResult = new HashMap<>();
        configResult.put("MINI_PROGRAM_NAME", devConfigApi.getValueByKey("MINI_PROGRAM_NAME"));
        configResult.put("MINI_PROGRAM_DESC", devConfigApi.getValueByKey("MINI_PROGRAM_DESC"));
        configResult.put("MINI_PROGRAM_LOGO", devConfigApi.getValueByKey("MINI_PROGRAM_LOGO"));
        configResult.put("MIN_WITHDRAW", devConfigApi.getValueByKey("MIN_WITHDRAW"));
        configResult.put("WITHDRAW_SERVICE_RATIO", devConfigApi.getValueByKey("WITHDRAW_SERVICE_RATIO"));
        configResult.put("STOCK_WARNING", devConfigApi.getValueByKey("STOCK_WARNING"));
        
        // 添加服务商支付模式状态，用于前端判断是否显示提现功能
        String partnerPayEnabled = devConfigApi.getValueByKey("SNOWY_PAY_WX_PARTNER_ENABLED");
        configResult.put("WX_PARTNER_PAY_ENABLED", partnerPayEnabled != null ? partnerPayEnabled : "false");
        
        return CommonResult.data(configResult);
    }

}
