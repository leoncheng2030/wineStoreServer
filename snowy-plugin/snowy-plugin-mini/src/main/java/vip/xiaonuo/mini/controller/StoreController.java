package vip.xiaonuo.mini.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.mini.param.WineStoreUserRemoveParam;
import vip.xiaonuo.wine.modular.store.entity.WineStore;
import vip.xiaonuo.wine.modular.store.entity.WineStoreUser;
import vip.xiaonuo.wine.modular.store.param.*;
import vip.xiaonuo.wine.modular.store.service.WineStoreService;
import vip.xiaonuo.wine.modular.store.service.WineStoreUserService;

import java.util.List;

/**
 * 门店信息表控制器
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:05
 **/
@Tag(name = "门店信息表控制器")
@RestController
@Validated
public class StoreController {
    @Resource
    private WineStoreService wineStoreService;

    @Resource
    private ClientUserService clientUserService;
    @Resource
    private WineStoreUserService wineStoreUserService;


    @Operation(summary = "根据代理商ID获取门店列表")
    @GetMapping("/mini/store/agent/list")
    public CommonResult<Page<WineStore>> agentList(WineStorePageParam wineStorePageParam) {
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineStorePageParam.setAgentClientUserId(id);
        Page<WineStore> page = wineStoreService.page(wineStorePageParam);
        return CommonResult.data(page);
    }

    @Operation(summary = "门店列表分页")
    @GetMapping("/mini/store/page")
    public CommonResult<Page<WineStore>> page(WineStorePageParam wineStorePageParam) {
        Page<WineStore> page = wineStoreService.page(wineStorePageParam);
        return CommonResult.data(page);
    }
    /**
     * 门店列表
     */
    @Operation(summary = "门店列表")
    @GetMapping("/mini/store/list")
    public CommonResult<List<WineStore>> list() {
        List<WineStore> list = wineStoreService.list();
        return CommonResult.data(list);
    }
    /**
     * 我的门店
     */
    @Operation(summary = "我的门店")
    @GetMapping("/mini/store/my")
    public CommonResult<Page<WineStore>> my(WineStorePageParam wineStorePageParam) {
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineStorePageParam.setManagerId(id);
        Page<WineStore> page = wineStoreService.page(wineStorePageParam);
        return CommonResult.data(page);
    }

    /**
     * 获取附近的门店
     *
     * @author xuyuxiang
     * @date 2022/4/21 18:05
     **/
    @ApiOperationSupport(order = 2)
    @Operation(summary = "获取附近的门店")
    @GetMapping("/mini/store/nearby")
    public CommonResult<Page<WineStore>> nearby(WineStoreNearbyParam wineStoreNearbyParam) {
        return CommonResult.data(wineStoreService.nearby(wineStoreNearbyParam));
    }

    /**
     * 门店详情
     *
     * @author xuyuxiang
     * @date 2022/4/21 18:05
     **/
    @Operation(summary = "门店详情")
    @GetMapping("/mini/store/detail")
    public CommonResult<WineStore> detail(WineStoreIdParam wineStoreIdParam) {
        return CommonResult.data(wineStoreService.queryEntity(wineStoreIdParam.getId()));
    }

    /**
     * 获取门店用户列表
     *
     * @author xuyuxiang
     * @date 2022/4/21 18:05
     **/
    @Operation(summary = "获取门店用户列表")
    @GetMapping("/mini/store/user/list")
    public CommonResult<List<ClientUser>> list(WineStoreIdParam wineStoreIdParam) {
        List<WineStoreUser> list = wineStoreService.list(wineStoreIdParam);
        // 从list中获取所有用户id,组成新的数组
        String[] userIds = list.stream().map(WineStoreUser::getClientUserId).toArray(String[]::new);
        // 使用clientUserService 获取所有用户
        List<ClientUser> userList = clientUserService.listByIds(userIds);
        return CommonResult.data(userList);
    }
    /**
     * 扫描门店二维码成为店员
     *
     * @author xuyuxiang
     * @date 2022/4/21 18:05
     **/
    @Operation(summary = "扫描门店二维码成为店员")
    @GetMapping("/mini/store/becomeClerk")
    public CommonResult<String> becomeClerk(WineStoreIdParam wineStoreIdParam) {
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        wineStoreUserService.becomeClerk(wineStoreIdParam.getId(), id);
        return CommonResult.ok();
    }
    /**
     * 移除店员
     *
     * @author xuyuxiang
     * @date 2022/4/21 18:05
     **/
    @Operation(summary = "移除店员")
    @GetMapping("/mini/store/removeClerk")
    public CommonResult<String> removeClerk(WineStoreUserRemoveParam wineStoreUserRemoveParam) {
        wineStoreUserService.removeClerk(wineStoreUserRemoveParam.getStoreId(), wineStoreUserRemoveParam.getUserId());
        return CommonResult.ok();
    }
    /**
     * 添加门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "添加门店管理表")
    @CommonLog("添加门店管理表")
    @PostMapping("/mini/store/add")
    public CommonResult<String> add(@RequestBody @Valid WineStoreAddParam wineStoreAddParam) {
        wineStoreService.add(wineStoreAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "编辑门店管理表")
    @CommonLog("编辑门店管理表")
    @PostMapping("/mini/store/edit")
    public CommonResult<String> edit(@RequestBody @Valid WineStoreEditParam wineStoreEditParam) {
        wineStoreService.edit(wineStoreEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除门店管理表
     *
     * @author jetox
     * @date  2025/07/24 08:01
     */
    @Operation(summary = "删除门店管理表")
    @CommonLog("删除门店管理表")
    @PostMapping("/mini/store/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                       List<WineStoreIdParam> wineStoreIdParamList) {
        wineStoreService.delete(wineStoreIdParamList);
        return CommonResult.ok();
    }
}
