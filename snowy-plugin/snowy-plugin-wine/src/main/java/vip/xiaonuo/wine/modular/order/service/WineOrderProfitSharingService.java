package vip.xiaonuo.wine.modular.order.service;

import vip.xiaonuo.wine.modular.order.entity.WineOrder;

/**
 * 酒品订单分佣分账服务接口
 *
 * @author xuyuxiang
 * @date 2024/12/19
 */
public interface WineOrderProfitSharingService {

    /**
     * 处理订单的分佣和分账
     * 统一处理分佣计算、微信分账、状态更新等操作
     *
     * @param wineOrder 订单信息
     */
    void processCommissionAndProfitSharing(WineOrder wineOrder);

    /**
     * 仅处理分佣（不执行微信分账）
     * 用于特殊情况下只需要计算佣金的场景
     *
     * @param wineOrder 订单信息
     */
    void processCommissionOnly(WineOrder wineOrder);

    /**
     * 重新执行分账
     * 用于分账失败后的重试
     *
     * @param orderNo 订单号
     */
    void retryProfitSharing(String orderNo);

    /**
     * 查询订单分账状态
     *
     * @param orderNo 订单号
     * @return 分账状态信息
     */
    String queryProfitSharingStatus(String orderNo);

    /**
     * 基于佣金记录处理分账
     * 专门用于定时任务，从已有的佣金记录中获取分账数据
     *
     * @param orderNo 订单号
     * @param commissionRecords 佣金记录列表
     */
    void processCommissionAndProfitSharingByCommissionRecords(String orderNo, java.util.List<vip.xiaonuo.wine.modular.commissionrecord.entity.WineCommissionRecord> commissionRecords);
}
