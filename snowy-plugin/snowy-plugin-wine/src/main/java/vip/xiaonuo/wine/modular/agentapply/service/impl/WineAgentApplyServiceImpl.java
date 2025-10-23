package vip.xiaonuo.wine.modular.agentapply.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.wine.api.WineAgentApi;
import vip.xiaonuo.wine.modular.agent.service.WineAgentService;
import vip.xiaonuo.wine.modular.agentapply.entity.WineAgentApply;
import vip.xiaonuo.wine.modular.agentapply.mapper.WineAgentApplyMapper;
import vip.xiaonuo.wine.modular.agentapply.param.*;
import vip.xiaonuo.wine.modular.agentapply.service.WineAgentApplyService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理商申请服务实现
 *
 * @author jetox
 * @date 2025/09/20
 */
@Slf4j
@Service
public class WineAgentApplyServiceImpl extends ServiceImpl<WineAgentApplyMapper, WineAgentApply> implements WineAgentApplyService {


    @Resource
    private ClientApi clientApi;

    @Resource
    private WineAgentService wineAgentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(WineAgentApplyAddParam wineAgentApplyAddParam) {
        String clientUserId = wineAgentApplyAddParam.getClientUserId();
        
        // 检查用户是否已经是代理商
        if (isUserAgent(clientUserId)) {
            throw new CommonException("用户已经是代理商，无需重复申请");
        }
        
        // 检查是否有待审核的申请
        if (!canApply(clientUserId)) {
            throw new CommonException("您有申请正在审核中，请勿重复申请");
        }
        
        // 创建申请记录
        WineAgentApply wineAgentApply = new WineAgentApply();
        BeanUtil.copyProperties(wineAgentApplyAddParam, wineAgentApply);
        wineAgentApply.setStatus("PENDING"); // 待审核状态
        
        this.save(wineAgentApply);
    }

    @Override
    public WineAgentApply getLatestByUserId(String clientUserId) {
        return this.getOne(
            new LambdaQueryWrapper<WineAgentApply>()
                .eq(WineAgentApply::getClientUserId, clientUserId)
                .orderByDesc(WineAgentApply::getCreateTime)
                .last("LIMIT 1")
        );
    }

    @Override
    public boolean canApply(String clientUserId) {
        // 检查是否有待审核的申请
        WineAgentApply pendingApply = this.getOne(
            new LambdaQueryWrapper<WineAgentApply>()
                .eq(WineAgentApply::getClientUserId, clientUserId)
                .eq(WineAgentApply::getStatus, "PENDING")
        );
        
        return pendingApply == null;
    }

    @Override
    public Page<WineAgentApply> page(WineAgentApplyPageParam wineAgentApplyPageParam) {
        QueryWrapper<WineAgentApply> queryWrapper = new QueryWrapper<WineAgentApply>();
        
        // 根据查询条件构建查询
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getSearchKey())) {
            queryWrapper.and(q -> q.like("REAL_NAME", wineAgentApplyPageParam.getSearchKey())
                    .or().like("PHONE", wineAgentApplyPageParam.getSearchKey())
                    .or().like("ID_CARD", wineAgentApplyPageParam.getSearchKey()));
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getClientUserId())) {
            queryWrapper.lambda().eq(WineAgentApply::getClientUserId, wineAgentApplyPageParam.getClientUserId());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getRealName())) {
            queryWrapper.lambda().like(WineAgentApply::getRealName, wineAgentApplyPageParam.getRealName());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getPhone())) {
            queryWrapper.lambda().like(WineAgentApply::getPhone, wineAgentApplyPageParam.getPhone());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getStatus())) {
            queryWrapper.lambda().eq(WineAgentApply::getStatus, wineAgentApplyPageParam.getStatus());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getStartTime())) {
            queryWrapper.lambda().ge(WineAgentApply::getCreateTime, wineAgentApplyPageParam.getStartTime());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getEndTime())) {
            queryWrapper.lambda().le(WineAgentApply::getCreateTime, wineAgentApplyPageParam.getEndTime());
        }
        
        // 排序
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getSortField())) {
            CommonSortOrderEnum.validate(wineAgentApplyPageParam.getSortOrder());
            queryWrapper.orderBy(true, CommonSortOrderEnum.ASC.getValue().equals(wineAgentApplyPageParam.getSortOrder()),
                    StrUtil.toUnderlineCase(wineAgentApplyPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(WineAgentApply::getCreateTime);
        }
        
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public WineAgentApply detail(WineAgentApplyIdParam wineAgentApplyIdParam) {
        return this.queryEntity(wineAgentApplyIdParam.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<WineAgentApplyIdParam> wineAgentApplyIdParamList) {
        List<String> wineAgentApplyIdList = wineAgentApplyIdParamList.stream()
                .map(WineAgentApplyIdParam::getId).toList();
        this.removeByIds(wineAgentApplyIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditBatch(WineAgentApplyAuditParam wineAgentApplyAuditParam) {
        String auditUser = StpLoginUserUtil.getLoginUser().getName();
        audit(wineAgentApplyAuditParam.getId(), wineAgentApplyAuditParam.getSubMerId(), wineAgentApplyAuditParam.getStatus(),
              wineAgentApplyAuditParam.getAuditRemark(), auditUser);
    }

    /**
     * 批量审核申请
     */
    private void audit(String applyId,String subMerId, String status, String auditRemark, String auditUser) {
        if (StrUtil.isEmpty(applyId)) {
            throw new CommonException("申请ID不能为空");
        }
        
        if (!StrUtil.equals(status, "APPROVED") && !StrUtil.equals(status, "REJECTED")) {
            throw new CommonException("审核状态不正确");
        }
        
        WineAgentApply wineAgentApply = this.getById(applyId);
        if (wineAgentApply == null) {
            throw new CommonException("申请记录不存在");
        }
        
        if (!StrUtil.equals(wineAgentApply.getStatus(), "PENDING")) {
            throw new CommonException("该申请已经审核过了");
        }
        
        // 更新审核信息
        wineAgentApply.setStatus(status);
        wineAgentApply.setAuditRemark(auditRemark);
        wineAgentApply.setAuditTime(new Date());
        wineAgentApply.setAuditUser(auditUser);
        this.updateById(wineAgentApply);
        
        // 如果审核通过，设置用户为代理商
        if (StrUtil.equals(status, "APPROVED")) {
            clientApi.setUserAsAgent(wineAgentApply.getClientUserId());
            wineAgentService.setSubMerId(wineAgentApply.getClientUserId(), subMerId);
        }

    }

    @Override
    public List<WineAgentApply> list(WineAgentApplyPageParam wineAgentApplyPageParam) {
        QueryWrapper<WineAgentApply> queryWrapper = new QueryWrapper<WineAgentApply>();
        
        // 根据查询条件构建查询
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getSearchKey())) {
            queryWrapper.and(q -> q.like("REAL_NAME", wineAgentApplyPageParam.getSearchKey())
                    .or().like("PHONE", wineAgentApplyPageParam.getSearchKey())
                    .or().like("ID_CARD", wineAgentApplyPageParam.getSearchKey()));
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getClientUserId())) {
            queryWrapper.lambda().eq(WineAgentApply::getClientUserId, wineAgentApplyPageParam.getClientUserId());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getRealName())) {
            queryWrapper.lambda().like(WineAgentApply::getRealName, wineAgentApplyPageParam.getRealName());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getPhone())) {
            queryWrapper.lambda().like(WineAgentApply::getPhone, wineAgentApplyPageParam.getPhone());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getStatus())) {
            queryWrapper.lambda().eq(WineAgentApply::getStatus, wineAgentApplyPageParam.getStatus());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getStartTime())) {
            queryWrapper.lambda().ge(WineAgentApply::getCreateTime, wineAgentApplyPageParam.getStartTime());
        }
        
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getEndTime())) {
            queryWrapper.lambda().le(WineAgentApply::getCreateTime, wineAgentApplyPageParam.getEndTime());
        }
        
        // 排序
        if (ObjectUtil.isNotEmpty(wineAgentApplyPageParam.getSortField())) {
            CommonSortOrderEnum.validate(wineAgentApplyPageParam.getSortOrder());
            queryWrapper.orderBy(true, CommonSortOrderEnum.ASC.getValue().equals(wineAgentApplyPageParam.getSortOrder()),
                    StrUtil.toUnderlineCase(wineAgentApplyPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(WineAgentApply::getCreateTime);
        }
        
        return this.list(queryWrapper);
    }

    @Override
    public Object getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计各状态数量
        statistics.put("totalCount", this.count());
        statistics.put("pendingCount", this.count(new LambdaQueryWrapper<WineAgentApply>()
                .eq(WineAgentApply::getStatus, "PENDING")));
        statistics.put("approvedCount", this.count(new LambdaQueryWrapper<WineAgentApply>()
                .eq(WineAgentApply::getStatus, "APPROVED")));
        statistics.put("rejectedCount", this.count(new LambdaQueryWrapper<WineAgentApply>()
                .eq(WineAgentApply::getStatus, "REJECTED")));
        
        return statistics;
    }

    /**
     * 检查用户是否已经是代理商
     *
     * @param clientUserId 用户ID
     * @return 是否为代理商
     */
    private boolean isUserAgent(String clientUserId) {
        return clientApi.isUserAgent(clientUserId);
    }

    /**
     * 查询实体
     *
     * @param id ID
     * @return 实体
     */
    public WineAgentApply queryEntity(String id) {
        WineAgentApply wineAgentApply = this.getById(id);
        if (ObjectUtil.isEmpty(wineAgentApply)) {
            throw new CommonException("代理商申请记录不存在，id值为：{}", id);
        }
        return wineAgentApply;
    }
}