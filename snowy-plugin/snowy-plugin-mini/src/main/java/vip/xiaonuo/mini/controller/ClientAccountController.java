package vip.xiaonuo.mini.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.checkerframework.checker.units.qual.C;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.client.modular.user.param.ClientUserUpdateInfoParam;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.wine.modular.accountflow.entity.WineAccountFlow;
import vip.xiaonuo.wine.modular.accountflow.param.WineAccountFlowPageParam;
import vip.xiaonuo.wine.modular.accountflow.service.WineAccountFlowService;
import vip.xiaonuo.wine.modular.useraccount.entity.WineUserAccount;
import vip.xiaonuo.wine.modular.useraccount.service.WineUserAccountService;

import javax.validation.Valid;

@Tag(name = "用户账户控制器")
@RestController
@Validated
public class ClientAccountController {
    @Resource
    private WineUserAccountService wineUserAccountService;
    @Resource
    private WineAccountFlowService wineAccountFlowService;

    @Resource
    private ClientUserService clientUserService;
    @Operation(summary = "获取用户账户信息")
    @GetMapping("/mini/clientAccount/detail")
    @CommonLog("获取用户账户信息")
    public CommonResult<WineUserAccount> getMiniClientAccountList(){
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        return CommonResult.data(wineUserAccountService.getAccountInfo(id));
    }

    @Operation(summary = "获取用户账户流水")
    @GetMapping("/mini/clientAccount/flow")
    @CommonLog("获取用户账户流水")
    public CommonResult<Page<WineAccountFlow>> getMiniClientAccountFlow(WineAccountFlowPageParam wineAccountFlowPageParam){
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineAccountFlowPageParam.setUserId(id);
        return CommonResult.data(wineAccountFlowService.page(wineAccountFlowPageParam));
    }
    /**
     * 修改用户头像
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 17)
    @Operation(summary = "修改用户头像")
    @CommonLog("修改用户头像")
    @PostMapping("/client/clientAccount/updateAvatar")
    public CommonResult<String> updateAvatar(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(clientUserService.updateAvatar(file));
    }
    /**
     * 编辑个人信息
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 19)
    @Operation(summary = "编辑个人信息")
    @CommonLog("编辑个人信息")
    @PostMapping("/client/clientAccount/updateUserInfo")
    public CommonResult<String> updateUserInfo(@RequestBody @Valid ClientUserUpdateInfoParam clientUserUpdateInfoParam) {
        clientUserService.updateUserInfo(clientUserUpdateInfoParam);
        return CommonResult.ok();
    }

}
