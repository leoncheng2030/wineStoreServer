package vip.xiaonuo.wine.modular.agentapply.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.wine.modular.agentapply.entity.WineAgentApply;
import vip.xiaonuo.wine.modular.agentapply.param.WineAgentApplyAddParam;
import vip.xiaonuo.wine.modular.agentapply.param.WineAgentApplyAuditParam;
import vip.xiaonuo.wine.modular.agentapply.param.WineAgentApplyIdParam;
import vip.xiaonuo.wine.modular.agentapply.param.WineAgentApplyPageParam;

import java.util.List;

/**
 * 代理商申请服务接口
 *
 * @author jetox
 * @date 2025/09/20
 */
public interface WineAgentApplyService extends IService<WineAgentApply> {

    /**
     * 添加代理商申请
     *
     * @param wineAgentApplyAddParam 申请参数
     */
    void add(WineAgentApplyAddParam wineAgentApplyAddParam);

    /**
     * 根据用户ID获取最新的申请记录
     *
     * @param clientUserId 用户ID
     * @return 申请记录
     */
    WineAgentApply getLatestByUserId(String clientUserId);

    /**
     * 检查用户是否可以申请（没有待审核的申请）
     *
     * @param clientUserId 用户ID
     * @return 是否可以申请
     */
    boolean canApply(String clientUserId);

    /**
     * 获取代理商申请分页
     *
     * @param wineAgentApplyPageParam 分页参数
     * @return 分页结果
     */
    Page<WineAgentApply> page(WineAgentApplyPageParam wineAgentApplyPageParam);

    /**
     * 获取代理商申请详情
     *
     * @param wineAgentApplyIdParam 申请ID参数
     * @return 申请详情
     */
    WineAgentApply detail(WineAgentApplyIdParam wineAgentApplyIdParam);

    /**
     * 删除代理商申请记录
     *
     * @param wineAgentApplyIdParamList ID列表
     */
    void delete(List<WineAgentApplyIdParam> wineAgentApplyIdParamList);

    /**
     * 批量审核申请
     *
     * @param wineAgentApplyAuditParam 审核参数
     */
    void auditBatch(WineAgentApplyAuditParam wineAgentApplyAuditParam);

    /**
     * 获取代理商申请列表
     *
     * @param wineAgentApplyPageParam 查询参数
     * @return 申请列表
     */
    List<WineAgentApply> list(WineAgentApplyPageParam wineAgentApplyPageParam);

    /**
     * 获取申请统计信息
     *
     * @return 统计信息
     */
    Object getStatistics();
}