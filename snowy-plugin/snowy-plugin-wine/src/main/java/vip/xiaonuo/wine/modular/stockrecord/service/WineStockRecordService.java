package vip.xiaonuo.wine.modular.stockrecord.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.wine.modular.stockrecord.entity.WineStockRecord;
import vip.xiaonuo.wine.modular.stockrecord.param.WineStockRecordAddParam;
import vip.xiaonuo.wine.modular.stockrecord.param.WineStockRecordPageParam;

import java.util.List;

/**
 * 库存记录表Service接口
 *
 * @author system
 * @date 2025/09/21
 **/
public interface WineStockRecordService extends IService<WineStockRecord> {

    /**
     * 获取库存记录分页
     *
     * @param wineStockRecordPageParam 查询参数
     * @return 分页结果
     */
    Page<WineStockRecord> page(WineStockRecordPageParam wineStockRecordPageParam);

    /**
     * 添加库存记录
     *
     * @param wineStockRecordAddParam 添加参数
     */
    void add(WineStockRecordAddParam wineStockRecordAddParam);

    /**
     * 获取设备的库存记录
     *
     * @param deviceId 设备ID
     * @param limit 限制数量
     * @return 库存记录列表
     */
    List<WineStockRecord> getDeviceStockRecords(String deviceId, Integer limit);

    /**
     * 获取设备管理员的操作记录
     *
     * @param operatorId 操作人员ID
     * @param limit 限制数量
     * @return 库存记录列表
     */
    List<WineStockRecord> getManagerOperationRecords(String operatorId, Integer limit);

    /**
     * 记录库存变更
     *
     * @param deviceId 设备ID
     * @param operationType 操作类型
     * @param beforeStock 变更前库存
     * @param afterStock 变更后库存
     * @param operatorId 操作人员ID
     * @param operatorType 操作人员类型
     * @param remark 备注
     */
    void recordStockChange(String deviceId, String operationType, Integer beforeStock, 
                          Integer afterStock, String operatorId, String operatorType, String remark);

    /**
     * 获取今日补货次数统计
     *
     * @param operatorId 操作人员ID
     * @param deviceIds 设备ID列表
     * @return 今日补货次数
     */
    int getTodayRestockCount(String operatorId, List<String> deviceIds);

    /**
     * 获取补货记录总数统计
     *
     * @param operatorId 操作人员ID
     * @param deviceIds 设备ID列表
     * @return 补货记录总数
     */
    int getTotalRestockCount(String operatorId, List<String> deviceIds);

}