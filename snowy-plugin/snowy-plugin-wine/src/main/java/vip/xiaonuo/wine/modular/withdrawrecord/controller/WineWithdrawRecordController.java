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
package vip.xiaonuo.wine.modular.withdrawrecord.controller;

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
import vip.xiaonuo.wine.modular.withdrawrecord.entity.WineWithdrawRecord;
import vip.xiaonuo.wine.modular.withdrawrecord.param.*;
import vip.xiaonuo.wine.modular.withdrawrecord.service.WineWithdrawRecordService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 提现记录表控制器
 *
 * @author jetox
 * @date  2025/07/24 08:50
 */
@Tag(name = "提现记录表控制器")
@RestController
@Validated
public class WineWithdrawRecordController {

    @Resource
    private WineWithdrawRecordService wineWithdrawRecordService;

    /**
     * 获取提现记录表分页
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    @Operation(summary = "获取提现记录表分页")
    @SaCheckPermission("/wine/withdrawrecord/page")
    @GetMapping("/wine/withdrawrecord/page")
    public CommonResult<Page<WineWithdrawRecord>> page(WineWithdrawRecordPageParam wineWithdrawRecordPageParam) {
        return CommonResult.data(wineWithdrawRecordService.page(wineWithdrawRecordPageParam));
    }

    /**
     * 获取提现记录表详情
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    @Operation(summary = "获取提现记录表详情")
    @SaCheckPermission("/wine/withdrawrecord/detail")
    @GetMapping("/wine/withdrawrecord/detail")
    public CommonResult<WineWithdrawRecord> detail(@Valid WineWithdrawRecordIdParam wineWithdrawRecordIdParam) {
        return CommonResult.data(wineWithdrawRecordService.detail(wineWithdrawRecordIdParam));
    }

    /**
     * 导出提现记录表
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    @Operation(summary = "导出提现记录表")
    @SaCheckPermission("/wine/withdrawrecord/exportData")
    @PostMapping(value = "/wine/withdrawrecord/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<WineWithdrawRecordIdParam> wineWithdrawRecordIdParamList, HttpServletResponse response) throws IOException {
        wineWithdrawRecordService.exportData(wineWithdrawRecordIdParamList, response);
    }

    /**
     * 提现审核
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    @Operation(summary = "提现审核")
    @SaCheckPermission("/wine/withdrawrecord/approveWithdraw")
    @CommonLog("提现审核")
    @PostMapping("/wine/withdrawrecord/approveWithdraw")
    public CommonResult<String> approveWithdraw(@Validated @RequestBody WineWithdrawRecordIdParam wineWithdrawRecordIdParam) {
        wineWithdrawRecordService.approveWithdraw(wineWithdrawRecordIdParam);
        return CommonResult.ok();
    }
    /**
     * 提现拒绝
     *
     * @author jetox
     * @date  2025/07/24 08:50
     */
    @Operation(summary = "提现拒绝")
    @SaCheckPermission("/wine/withdrawrecord/rejectWithdraw")
    @CommonLog("提现拒绝")
    @PostMapping("/wine/withdrawrecord/rejectWithdraw")
    public CommonResult<String> rejectWithdraw(@Validated @RequestBody WineWithdrawRecordRejectWithdrawParam wineWithdrawRecordRejectWithdrawParam) {
        wineWithdrawRecordService.rejectWithdraw(wineWithdrawRecordRejectWithdrawParam);
        return CommonResult.ok();
    }
}
