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
package vip.xiaonuo.wine.modular.commissionrecord.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.modular.commissionrecord.entity.WineCommissionRecord;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordAddParam;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordEditParam;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordIdParam;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordPageParam;
import vip.xiaonuo.wine.modular.commissionrecord.service.WineCommissionRecordService;
import vip.xiaonuo.pay.modular.wx.service.impl.PayWxServiceFactory;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 佣金记录表控制器
 *
 * @author jetox
 * @date  2025/07/24 08:50
 */
@Tag(name = "佣金记录表控制器")
@RestController
@Validated
public class WineCommissionRecordController {

    @Resource
    private WineCommissionRecordService wineCommissionRecordService;
    
    @Resource
    private PayWxServiceFactory payWxServiceFactory;

    /**
     * 获取支付模式配置
     *
     * @author jetox
     * @date  2025/09/30
     */
    @Operation(summary = "获取支付模式配置")
    @GetMapping("/wine/commissionrecord/paymentMode")
    public CommonResult<Map<String, Object>> getPaymentMode() {
        Map<String, Object> result = new HashMap<>();
        result.put("isPartnerMode", payWxServiceFactory.isPartnerMode());
        return CommonResult.data(result);
    }

    /**
     * 获取佣金记录表分页
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    @Operation(summary = "获取佣金记录表分页")
    @SaCheckPermission("/wine/commissionrecord/page")
    @GetMapping("/wine/commissionrecord/page")
    public CommonResult<Page<WineCommissionRecord>> page(WineCommissionRecordPageParam wineCommissionRecordPageParam) {
        return CommonResult.data(wineCommissionRecordService.page(wineCommissionRecordPageParam));
    }

    /**
     * 获取佣金记录表详情
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    @Operation(summary = "获取佣金记录表详情")
    @SaCheckPermission("/wine/commissionrecord/detail")
    @GetMapping("/wine/commissionrecord/detail")
    public CommonResult<WineCommissionRecord> detail(@Valid WineCommissionRecordIdParam wineCommissionRecordIdParam) {
        return CommonResult.data(wineCommissionRecordService.detail(wineCommissionRecordIdParam));
    }

    /**
     * 导出佣金记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    @Operation(summary = "导出佣金记录表")
    @SaCheckPermission("/wine/commissionrecord/exportData")
    @PostMapping(value = "/wine/commissionrecord/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineCommissionRecordIdParam> wineCommissionRecordIdParamList, HttpServletResponse response) throws IOException {
        wineCommissionRecordService.exportData(wineCommissionRecordIdParamList, response);
    }
}
