package vip.xiaonuo.wine.modular.stockrecord.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.wine.modular.stockrecord.entity.WineStockRecord;
import vip.xiaonuo.wine.modular.stockrecord.mapper.WineStockRecordMapper;
import vip.xiaonuo.wine.modular.stockrecord.param.WineStockRecordAddParam;
import vip.xiaonuo.wine.modular.stockrecord.param.WineStockRecordPageParam;
import vip.xiaonuo.wine.modular.stockrecord.service.WineStockRecordService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 库存记录表Service接口实现类
 *
 * @author system
 * @date 2025/09/21
 **/
@Slf4j
@Service
public class WineStockRecordServiceImpl extends ServiceImpl<WineStockRecordMapper, WineStockRecord> implements WineStockRecordService {

    @Override
    public Page<WineStockRecord> page(WineStockRecordPageParam wineStockRecordPageParam) {
        QueryWrapper<WineStockRecord> queryWrapper = new QueryWrapper<WineStockRecord>().checkSqlInjection();
        
        if (ObjectUtil.isNotEmpty(wineStockRecordPageParam.getDeviceId())) {
            queryWrapper.lambda().eq(WineStockRecord::getDeviceId, wineStockRecordPageParam.getDeviceId());
        }
        if (ObjectUtil.isNotEmpty(wineStockRecordPageParam.getOperationType())) {
            queryWrapper.lambda().eq(WineStockRecord::getOperationType, wineStockRecordPageParam.getOperationType());
        }
        if (ObjectUtil.isNotEmpty(wineStockRecordPageParam.getOperatorId())) {
            queryWrapper.lambda().eq(WineStockRecord::getOperatorId, wineStockRecordPageParam.getOperatorId());
        }
        if (ObjectUtil.isNotEmpty(wineStockRecordPageParam.getOperatorType())) {
            queryWrapper.lambda().eq(WineStockRecord::getOperatorType, wineStockRecordPageParam.getOperatorType());
        }

        // 按创建时间倒序
        queryWrapper.lambda().orderByDesc(WineStockRecord::getCreateTime);
        
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public void add(WineStockRecordAddParam wineStockRecordAddParam) {
        WineStockRecord wineStockRecord = BeanUtil.toBean(wineStockRecordAddParam, WineStockRecord.class);
        this.save(wineStockRecord);
    }

    @Override
    public List<WineStockRecord> getDeviceStockRecords(String deviceId, Integer limit) {
        QueryWrapper<WineStockRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(WineStockRecord::getDeviceId, deviceId)
                .orderByDesc(WineStockRecord::getCreateTime);
        
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        
        return this.list(queryWrapper);
    }

    @Override
    public List<WineStockRecord> getManagerOperationRecords(String operatorId, Integer limit) {
        QueryWrapper<WineStockRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(WineStockRecord::getOperatorId, operatorId)
                .eq(WineStockRecord::getOperatorType, "DEVICE_MANAGER")
                .orderByDesc(WineStockRecord::getCreateTime);
        
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        
        return this.list(queryWrapper);
    }

    @Override
    public void recordStockChange(String deviceId, String operationType, Integer beforeStock, 
                                 Integer afterStock, String operatorId, String operatorType, String remark) {
        WineStockRecord stockRecord = new WineStockRecord();
        stockRecord.setDeviceId(deviceId);
        stockRecord.setOperationType(operationType);
        stockRecord.setBeforeStock(beforeStock);
        stockRecord.setAfterStock(afterStock);
        stockRecord.setChangeQuantity(afterStock - beforeStock);
        stockRecord.setOperatorId(operatorId);
        stockRecord.setOperatorType(operatorType);
        stockRecord.setRemark(remark);
        
        this.save(stockRecord);
        log.info("库存变更记录已保存：设备ID={}, 操作类型={}, 变更前={}, 变更后={}, 操作人={}", 
                deviceId, operationType, beforeStock, afterStock, operatorId);
    }

    @Override
    public int getTodayRestockCount(String operatorId, List<String> deviceIds) {
        if (deviceIds.isEmpty()) {
            return 0;
        }
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        
        LambdaQueryWrapper<WineStockRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineStockRecord::getOperatorId, operatorId)
                   .eq(WineStockRecord::getOperationType, "RESTOCK")
                   .in(WineStockRecord::getDeviceId, deviceIds)
                   .between(WineStockRecord::getCreateTime, todayStart, todayEnd);
        
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public int getTotalRestockCount(String operatorId, List<String> deviceIds) {
        if (deviceIds.isEmpty()) {
            return 0;
        }
        
        LambdaQueryWrapper<WineStockRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineStockRecord::getOperatorId, operatorId)
                   .eq(WineStockRecord::getOperationType, "RESTOCK")
                   .in(WineStockRecord::getDeviceId, deviceIds);
        
        return Math.toIntExact(this.count(queryWrapper));
    }

}