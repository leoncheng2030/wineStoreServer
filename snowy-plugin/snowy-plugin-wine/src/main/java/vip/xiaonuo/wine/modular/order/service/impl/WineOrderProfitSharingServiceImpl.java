package vip.xiaonuo.wine.modular.order.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.wine.api.WineAgentApi;
import vip.xiaonuo.pay.modular.wx.param.MultiProfitSharingParam;
import vip.xiaonuo.pay.modular.wx.service.PayWxService;
import vip.xiaonuo.pay.modular.wx.service.impl.PayWxServiceFactory;
import vip.xiaonuo.pay.modular.wx.vo.ProfitSharingProcessResult;
import vip.xiaonuo.wine.modular.accountflow.entity.WineAccountFlow;
import vip.xiaonuo.wine.modular.accountflow.service.WineAccountFlowService;
import vip.xiaonuo.wine.modular.commissionrecord.entity.WineCommissionRecord;
import vip.xiaonuo.wine.modular.commissionrecord.enums.WineCommissionRecordStatusEnum;
import vip.xiaonuo.wine.modular.commissionrecord.service.WineCommissionRecordService;
import vip.xiaonuo.wine.modular.order.enums.WineOrderProfitSharingStatusEnum;
import vip.xiaonuo.wine.modular.order.enums.WineOrderStatusEnum;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;
import vip.xiaonuo.wine.modular.order.entity.WineOrder;
import vip.xiaonuo.wine.modular.order.service.WineOrderProfitSharingService;
import vip.xiaonuo.wine.modular.store.entity.WineStore;
import vip.xiaonuo.wine.modular.store.service.WineStoreService;
import vip.xiaonuo.wine.modular.useraccount.entity.WineUserAccount;
import vip.xiaonuo.wine.modular.useraccount.service.WineUserAccountService;
import vip.xiaonuo.wine.modular.deviceuser.service.WineDeviceUserService;
import vip.xiaonuo.wine.modular.deviceuser.entity.WineDeviceUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 酒品订单分佣分账服务实现
 *
 * @author xuyuxiang
 * @date 2024/12/19
 */
@Slf4j
@Service
public class WineOrderProfitSharingServiceImpl implements WineOrderProfitSharingService {

    @Resource
    private WineDeviceService wineDeviceService;

    @Resource
    private WineStoreService wineStoreService;

    @Resource
    private WineCommissionRecordService wineCommissionRecordService;

    @Resource
    private DevConfigApi devConfigApi;

    @Resource
    private ClientApi clientApi;

    @Resource
    private PayWxService payWxService;
    
    @Resource
    private WineUserAccountService wineUserAccountService;
    
    @Resource
    private WineAccountFlowService wineAccountFlowService;
    
    @Resource
    private WineAgentApi wineAgentApi;
    
    @Resource
    private WineDeviceUserService wineDeviceUserService;
    
    @Resource
    private PayWxServiceFactory payWxServiceFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processCommissionAndProfitSharing(WineOrder wineOrder) {
        try {
            log.info("开始处理订单分佣，订单号：{}，订单金额：{}元", wineOrder.getOrderNo(), wineOrder.getTotalAmount());

            // 1. 计算并保存分佣记录
            List<WineCommissionRecord> commissionRecords = calculateAndSaveCommission(wineOrder);
            log.info("分佣计算完成，订单号：{}，生成佣金记录数：{}", wineOrder.getOrderNo(), commissionRecords.size());

            // 2. 判断是否需要执行微信分账
            boolean shouldProfitSharing = shouldExecuteProfitSharing(wineOrder);
            log.info("分账条件检查，订单号：{}，是否需要分账：{}", wineOrder.getOrderNo(), shouldProfitSharing);
            
            // 3. 更新账户余额（佣金立即生效）
            updateAccountBalances(commissionRecords);

            // 4. 获取分佣配置，根据配置设置分账状态
            String commissionDistribution = devConfigApi.getValueByKey("COMMISSION_DISTRIBUTION");
            log.info("分佣配置 COMMISSION_DISTRIBUTION：{}", commissionDistribution);
            
            // 5. 设置订单分账状态
            if (shouldProfitSharing) {
                if ("1".equals(commissionDistribution)) {
                    // 支付成功立即分佣：设置为待分账状态，由定时任务处理
                    wineOrder.setProfitSharingStatus(WineOrderProfitSharingStatusEnum.PENDING.getValue());
                    log.info("支付成功立即分佣：订单{}设置为待分账状态，将由定时任务处理分账", wineOrder.getOrderNo());
                    // 将需要分账的佣金记录状态设置为待分账
                    updateCommissionRecordsStatusForProfitSharing(commissionRecords);
                } else if ("2".equals(commissionDistribution)) {
                    // 出酒完成后分佣：需要根据当前订单状态判断是否设置为待分账
                    if (WineOrderStatusEnum.TRADE_SUCCESS.getValue().equals(wineOrder.getStatus())) {
                        // 出酒完成时：设置为待分账状态，由定时任务处理
                        wineOrder.setProfitSharingStatus(WineOrderProfitSharingStatusEnum.PENDING.getValue());
                        log.info("出酒完成后分佣：订单{}出酒完成，设置为待分账状态", wineOrder.getOrderNo());
                        
                        // 关键修复：对于出酒完成后分佣模式，需要重新查询所有该订单的佣金记录并更新状态
                        List<WineCommissionRecord> allOrderCommissionRecords = wineCommissionRecordService.list(
                            new LambdaQueryWrapper<WineCommissionRecord>().eq(WineCommissionRecord::getOrderNo, wineOrder.getOrderNo())
                        );
                        
                        // 将需要分账的佣金记录状态设置为待分账
                        updateCommissionRecordsStatusForProfitSharing(allOrderCommissionRecords);
                        log.info("出酒完成后分佣：更新{}条佣金记录状态为待分账", allOrderCommissionRecords.size());
                    } else {
                        // 支付成功时：不设置分账状态，等待出酒完成时再处理
                        wineOrder.setProfitSharingStatus(WineOrderProfitSharingStatusEnum.NO_NEED.getValue());
                        log.info("出酒完成后分佣：订单{}在支付成功时不设置分账状态，等待出酒完成时处理", wineOrder.getOrderNo());
                        // 出酒完成后分佣模式下，佣金记录状态保持为 SETTLED，不设置为待分账
                    }
                } else {
                    // 默认情况，设置为待分账
                    wineOrder.setProfitSharingStatus(WineOrderProfitSharingStatusEnum.PENDING.getValue());
                    updateCommissionRecordsStatusForProfitSharing(commissionRecords);
                    log.info("默认分佣模式：订单{}设置为待分账状态", wineOrder.getOrderNo());
                }
            } else {
                // 不需要分账的订单设置为无需分账
                wineOrder.setProfitSharingStatus(WineOrderProfitSharingStatusEnum.NO_NEED.getValue());
                log.info("订单{}不满足分账条件，标记为无需分账", wineOrder.getOrderNo());
            }

            log.info("订单分佣处理完成，订单号：{}，分账状态：{}", wineOrder.getOrderNo(), wineOrder.getProfitSharingStatus());

        } catch (Exception e) {
            log.error("处理订单分佣异常，订单号：{}，异常信息：{}", wineOrder.getOrderNo(), e.getMessage(), e);
            
            // 设置订单分账状态为异常
            wineOrder.setProfitSharingStatus(WineOrderProfitSharingStatusEnum.ERROR.getValue());
            
            throw new CommonException("订单分佣处理失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processCommissionOnly(WineOrder wineOrder) {
        try {
            log.info("开始处理订单分佣（不执行分账），订单号：{}", wineOrder.getOrderNo());

            // 计算并保存分佣记录
            List<WineCommissionRecord> commissionRecords = calculateAndSaveCommission(wineOrder);

            // 更新账户余额
            updateAccountBalances(commissionRecords);

            log.info("订单分佣处理完成，订单号：{}", wineOrder.getOrderNo());

        } catch (Exception e) {
            log.error("处理订单分佣异常，订单号：{}，异常信息：{}", wineOrder.getOrderNo(), e.getMessage(), e);
            throw new CommonException("订单分佣处理失败：" + e.getMessage());
        }
    }

    @Override
    public void retryProfitSharing(String orderNo) {
        throw new CommonException("分账重试功能尚未实现");
    }

    @Override
    public String queryProfitSharingStatus(String orderNo) {
        throw new CommonException("分账状态查询功能尚未实现");
    }

    /**
     * 计算并保存佣金记录
     */
    private List<WineCommissionRecord> calculateAndSaveCommission(WineOrder wineOrder) {
        // 1. 检查是否已经存在该订单的佣金记录
        List<WineCommissionRecord> existingRecords = wineCommissionRecordService.list(
            new LambdaQueryWrapper<WineCommissionRecord>().eq(WineCommissionRecord::getOrderId, wineOrder.getId())
        );
        
        if (!existingRecords.isEmpty()) {
            log.info("订单{}已存在佣金记录，数量：{}，跳过重复计算", wineOrder.getOrderNo(), existingRecords.size());
            return existingRecords;
        }
        
        List<WineCommissionRecord> commissionRecords = new ArrayList<>();

        // 2. 获取设备信息
        WineDevice wineDevice = wineDeviceService.queryEntity(wineOrder.getDeviceId());
        
        // 3. 获取设备关联的所有用户及其佣金比例
        List<WineDeviceUser> deviceUsers = wineDeviceUserService.getDeviceUsers(wineOrder.getDeviceId());
        if (deviceUsers.isEmpty()) {
            log.warn("设备{}未配置任何用户佣金，跳过分佣计算", wineDevice.getDeviceName());
            return commissionRecords;
        }
        
        // 4. 判断是否为代理商设备
        boolean isAgentDevice = "OPERATION_TYPE_AGENT".equals(wineDevice.getOperationType())
                && wineDevice.getAgentUserId() != null
                && !wineDevice.getAgentUserId().isEmpty();

        // 5. 计算可分佣的总金额
        BigDecimal totalCommissionAmount;
        if (isAgentDevice) {
            // 代理商设备：按最大分账比例限制计算
            BigDecimal maxSharingRatio = wineAgentApi.getMaxProfitSharingRatio(wineDevice.getAgentUserId());
            if (maxSharingRatio == null) {
                maxSharingRatio = new BigDecimal("30"); // 默认值30%
                log.warn("代理商未配置最大分账比例，使用默认值{}%，代理商ID：{}", maxSharingRatio, wineDevice.getAgentUserId());
            } else {
                log.info("从代理商配置获取最大分账比例：{}%，代理商ID：{}", maxSharingRatio, wineDevice.getAgentUserId());
            }
            totalCommissionAmount = wineOrder.getTotalAmount().multiply(maxSharingRatio).divide(new BigDecimal(100));
        } else {
            // 非代理商设备：按设备配置的佣金比例总和计算
            BigDecimal totalRate = deviceUsers.stream()
                .map(WineDeviceUser::getCommissionRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalCommissionAmount = wineOrder.getTotalAmount().multiply(totalRate).divide(new BigDecimal(100));
        }
        
        // 6. 计算所有用户佣金比例的总和
        BigDecimal totalUserRate = deviceUsers.stream()
            .map(WineDeviceUser::getCommissionRate)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        if (totalUserRate.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("设备{}关联用户的佣金比例总和为0，跳过分佣计算", wineDevice.getDeviceName());
            return commissionRecords;
        }
        
        log.info("设备{}分佣计算：可分佣总金额={}元，关联用户数={}，用户佣金比例总和={}%", 
            wineDevice.getDeviceName(), totalCommissionAmount, deviceUsers.size(), totalUserRate);
        
        // 7. 为每个用户创建佣金记录
        for (WineDeviceUser deviceUser : deviceUsers) {
            // 计算用户佣金：对于代理商设备，用户佣金 = 订单金额 × 最大分账比例 × 用户佣金比例 / 100
            // 对于非代理商设备，用户佣金 = 订单金额 × 用户佣金比例 / 100
            BigDecimal userCommissionAmount;
            if (isAgentDevice) {
                // 代理商设备：订单金额 × 最大分账比例 × 用户佣金比例 / 10000
                BigDecimal maxSharingRatio = wineAgentApi.getMaxProfitSharingRatio(wineDevice.getAgentUserId());
                if (maxSharingRatio == null) {
                    maxSharingRatio = new BigDecimal("30");
                }
                userCommissionAmount = wineOrder.getTotalAmount()
                    .multiply(maxSharingRatio)
                    .multiply(deviceUser.getCommissionRate())
                    .divide(new BigDecimal(10000), 2, RoundingMode.DOWN);
            } else {
                // 非代理商设备：按比例分配总佣金
                userCommissionAmount = totalCommissionAmount
                    .multiply(deviceUser.getCommissionRate())
                    .divide(totalUserRate, 2, RoundingMode.DOWN);
            }
                
            if (userCommissionAmount.compareTo(BigDecimal.ZERO) > 0) {
                WineCommissionRecord userRecord = createCommissionRecord(wineOrder, deviceUser.getUserId(), 
                    "USER", deviceUser.getCommissionRate(), userCommissionAmount, "SETTLED");
                commissionRecords.add(userRecord);
                
                log.debug("用户{}佣金记录：比例={}%，金额={}元", 
                    deviceUser.getUserId(), deviceUser.getCommissionRate(), userCommissionAmount);
            }
        }

        // 8. 批量保存佣金记录
        wineCommissionRecordService.saveBatch(commissionRecords);

        log.info("订单佣金计算完成，订单号：{}，佣金记录数：{}", wineOrder.getOrderNo(), commissionRecords.size());
        return commissionRecords;
    }

    /**
     * 创建佣金记录
     */
    private WineCommissionRecord createCommissionRecord(WineOrder wineOrder, String userId, String commissionType, 
                                                       BigDecimal rate, BigDecimal amount, String status) {
        WineCommissionRecord record = new WineCommissionRecord();
        record.setOrderId(wineOrder.getId());
        record.setOrderNo(wineOrder.getOrderNo());
        record.setUserId(userId);
        record.setDeviceId(wineOrder.getDeviceId());
        record.setWineId(wineOrder.getProductId());
        record.setWineName(wineOrder.getProductName());
        record.setOrderAmount(wineOrder.getTotalAmount());
        record.setCommissionType(commissionType);
        record.setCommissionRate(rate);
        record.setCommissionAmount(amount);
        record.setStatus(status);
        record.setCalculateTime(new Date());
        
        if ("SETTLED".equals(status)) {
            record.setSettleTime(new Date());
        }
        
        return record;
    }

    /**
     * 判断是否需要执行微信分账
     */
    private boolean shouldExecuteProfitSharing(WineOrder wineOrder) {
        try {
            // 获取设备信息
            WineDevice wineDevice = wineDeviceService.queryEntity(wineOrder.getDeviceId());
            
            // 只有代理商设备才需要执行微信分账
            boolean isAgentDevice = "OPERATION_TYPE_AGENT".equals(wineDevice.getOperationType())
                    && wineDevice.getAgentUserId() != null
                    && !wineDevice.getAgentUserId().isEmpty();
            
            log.info("订单分账条件检查，订单号：{}，是否代理设备：{}", wineOrder.getOrderNo(), isAgentDevice);
            return isAgentDevice;
            
        } catch (Exception e) {
            log.error("检查分账条件异常：{}", e.getMessage(), e);
            return false;
        }
    }



    /**
     * 判断佣金记录是否需要参与微信分账
     */
    private boolean shouldIncludeInProfitSharing(WineCommissionRecord record) {
        // 现在所有用户佣金都需要分账
        return true;
    }

    /**
     * 获取佣金描述
     */
    private String getCommissionDescription(String commissionType) {
        switch (commissionType) {
            case "USER":
                return "用户佣金";
            default:
                return "订单佣金";
        }
    }

    /**
     * 更新佣金记录状态
     */
    private void updateCommissionRecordsStatus(List<WineCommissionRecord> records, String status) {
        for (WineCommissionRecord record : records) {
            WineCommissionRecord updateRecord = new WineCommissionRecord();
            updateRecord.setId(record.getId());
            updateRecord.setStatus(status);
            
            // 如果是分账成功，更新发放时间
            if (WineCommissionRecordStatusEnum.PROFIT_SHARED.getValue().equals(status)) {
                updateRecord.setSettleTime(new Date());
            }
            
            wineCommissionRecordService.updateById(updateRecord);
        }
    }

    /**
     * 更新需要分账的佣金记录状态为待分账
     */
    private void updateCommissionRecordsStatusForProfitSharing(List<WineCommissionRecord> records) {
        for (WineCommissionRecord record : records) {
            // 只有需要参与微信分账的佣金记录才设置为待分账状态
            if (shouldIncludeInProfitSharing(record)) {
                WineCommissionRecord updateRecord = new WineCommissionRecord();
                updateRecord.setId(record.getId());
                updateRecord.setStatus(WineCommissionRecordStatusEnum.WAIT_PROFIT_SHARING.getValue());
                wineCommissionRecordService.updateById(updateRecord);
                
                log.debug("佣金记录{}设置为待分账状态，用户ID：{}，类型：{}，金额：{}元", 
                    record.getId(), record.getUserId(), record.getCommissionType(), record.getCommissionAmount());
            } else {
                // 不需要分账的佣金记录直接设置为已发放
                WineCommissionRecord updateRecord = new WineCommissionRecord();
                updateRecord.setId(record.getId());
                updateRecord.setStatus(WineCommissionRecordStatusEnum.SETTLED.getValue());
                updateRecord.setSettleTime(new Date());
                wineCommissionRecordService.updateById(updateRecord);
                
                log.debug("佣金记录{}不需要分账，直接设置为已发放，用户ID：{}，类型：{}，金额：{}元", 
                    record.getId(), record.getUserId(), record.getCommissionType(), record.getCommissionAmount());
            }
        }
    }

    /**
     * 更新账户余额
     */
    private void updateAccountBalances(List<WineCommissionRecord> commissionRecords) {
        // 检查是否为服务商模式
        boolean isPartnerMode = payWxServiceFactory.isPartnerMode();
        
        if (isPartnerMode) {
            log.info("服务商模式下不更新用户账户余额和创建流水记录");
            return;
        }
        
        for (WineCommissionRecord record : commissionRecords) {
            if ("SETTLED".equals(record.getStatus()) && !"SYSTEM_PLATFORM".equals(record.getUserId())) {
                try {
                    updateSingleAccountBalance(record.getUserId(), record.getCommissionAmount(), record);
                    log.debug("更新账户余额：用户ID={}，金额={}元", record.getUserId(), record.getCommissionAmount());
                } catch (Exception e) {
                    log.error("更新账户余额异常，用户ID：{}，异常：{}", record.getUserId(), e.getMessage());
                    throw e; // 重新抛出异常以确保事务回滚
                }
            }
        }
    }
    
    /**
     * 更新单个用户账户余额
     *
     * @param userId           用户ID
     * @param commissionAmount 佣金金额
     * @param record          佣金记录
     */
    private void updateSingleAccountBalance(String userId, BigDecimal commissionAmount, WineCommissionRecord record) {
        // 检查是否为服务商模式
        boolean isPartnerMode = payWxServiceFactory.isPartnerMode();
        
        if (isPartnerMode) {
            log.info("服务商模式下不更新用户账户余额和创建流水记录，用户ID: {}，订单ID: {}，佣金金额: {}", 
                userId, record.getOrderId(), commissionAmount);
            return;
        }
        
        // 查询用户账户信息
        WineUserAccount userAccount = wineUserAccountService.getOne(
            new LambdaQueryWrapper<WineUserAccount>().eq(WineUserAccount::getUserId, userId)
        );
        
        if (userAccount != null) {
            BigDecimal oldAvailableBalance = userAccount.getAvailableBalance() != null ? 
                userAccount.getAvailableBalance() : BigDecimal.ZERO;
            BigDecimal oldTotalBalance = userAccount.getTotalBalance() != null ? 
                userAccount.getTotalBalance() : BigDecimal.ZERO;
            BigDecimal oldTotalCommission = userAccount.getTotalCommission() != null ? 
                userAccount.getTotalCommission() : BigDecimal.ZERO;

            // 更新账户余额
            userAccount.setAvailableBalance(oldAvailableBalance.add(commissionAmount));
            userAccount.setTotalBalance(oldTotalBalance.add(commissionAmount));
            userAccount.setTotalCommission(oldTotalCommission.add(commissionAmount));
            userAccount.setLastCommissionTime(new Date());
            wineUserAccountService.updateById(userAccount);

            try {
                // 检查是否已经存在相同的账户流水记录（幂等性检查）
                LambdaQueryWrapper<WineAccountFlow> flowQueryWrapper = new LambdaQueryWrapper<>();
                flowQueryWrapper.eq(WineAccountFlow::getUserId, userId)
                        .eq(WineAccountFlow::getFlowType, "COMMISSION")
                        .eq(WineAccountFlow::getRelatedId, record.getOrderId())
                        .eq(WineAccountFlow::getRelatedType, "ORDER")
                        .eq(WineAccountFlow::getAmount, commissionAmount);
                
                WineAccountFlow existingFlow = wineAccountFlowService.getOne(flowQueryWrapper);
                
                if (existingFlow == null) {
                    // 只有不存在相同流水记录时才创建新记录
                    WineAccountFlow accountFlow = new WineAccountFlow();
                    // 使用雪花算法生成唯一流水号，避免时间戳重复
                    accountFlow.setFlowNo(IdUtil.getSnowflake(1, 1).nextIdStr());
                    accountFlow.setUserId(userId);
                    accountFlow.setFlowType("COMMISSION"); // 佣金收入
                    accountFlow.setAmount(commissionAmount);
                    accountFlow.setBalanceChange(commissionAmount);
                    accountFlow.setBeforeBalance(oldAvailableBalance);
                    accountFlow.setAfterBalance(oldAvailableBalance.add(commissionAmount));
                    accountFlow.setRelatedId(record.getOrderId());
                    accountFlow.setRelatedType("ORDER");
                    accountFlow.setRelatedNo(record.getOrderNo());
                    accountFlow.setDescription("订单佣金收入");
                    accountFlow.setStatus("SUCCESS");
                    accountFlow.setTransactionTime(new Date());
                    wineAccountFlowService.save(accountFlow);
                    
                    log.debug("创建账户流水记录成功，用户ID: {}，订单ID: {}，流水号: {}", 
                        userId, record.getOrderId(), accountFlow.getFlowNo());
                } else {
                    log.debug("账户流水记录已存在，跳过创建，用户ID: {}，订单ID: {}，流水号: {}", 
                        userId, record.getOrderId(), existingFlow.getFlowNo());
                }
            } catch (Exception e) {
                log.error("创建账户流水记录失败，用户ID: {}，订单ID: {}，佣金金额: {}", 
                    userId, record.getOrderId(), commissionAmount);
                // 抛出异常以确保事务回滚
                throw new CommonException("创建账户流水记录失败: " + e.getMessage());
            }
        } else {
            log.warn("用户账户不存在，无法更新余额和创建流水记录，用户ID: {}，订单ID: {}，佣金金额: {}", 
                userId, record.getOrderId(), commissionAmount);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processCommissionAndProfitSharingByCommissionRecords(String orderNo, List<WineCommissionRecord> commissionRecords) {
        try {
            log.info("基于佣金记录处理分账，订单号：{}，佣金记录数：{}", orderNo, commissionRecords.size());

            // 1. 构建多接收方分账参数（基于已有的佣金记录）
            MultiProfitSharingParam profitSharingParam = buildMultiProfitSharingParamFromCommissionRecords(orderNo, commissionRecords);
            
            if (profitSharingParam.getReceivers().isEmpty()) {
                log.info("订单{}无有效分账接收方，跳过分账", orderNo);
                return;
            }

            log.info("构建分账参数完成，订单号：{}，接收方数量：{}", orderNo, profitSharingParam.getReceivers().size());

            // 2. 执行微信分账
            log.info("开始执行微信分账，订单号：{}", orderNo);
            ProfitSharingProcessResult profitSharingResult = payWxService.multiProfitSharing(profitSharingParam);
            
            if ("SUCCESS".equals(profitSharingResult.getState())) {
                log.info("订单分账成功，订单号：{}，分账接收方数量：{}", orderNo, profitSharingParam.getReceivers().size());
            } else if ("PROCESSING".equals(profitSharingResult.getState())) {
                log.info("订单分账处理中，订单号：{}，状态：{}，说明：{}", 
                    orderNo, profitSharingResult.getState(), profitSharingResult.getDescription());
                log.info("分账状态为PROCESSING是正常状态，表示微信正在处理分账请求，后续会通过定时任务查询最终结果");
            }

        } catch (Exception e) {
            log.error("基于佣金记录处理分账异常，订单号：{}，异常信息：{}", orderNo, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 基于佣金记录构建多接收方分账参数
     */
    private MultiProfitSharingParam buildMultiProfitSharingParamFromCommissionRecords(String orderNo, List<WineCommissionRecord> commissionRecords) {
        MultiProfitSharingParam profitSharingParam = new MultiProfitSharingParam();
        profitSharingParam.setOutTradeNo(orderNo);
        
        ArrayList<MultiProfitSharingParam.ProfitReceiver> receivers = new ArrayList<>();
        
        for (WineCommissionRecord record : commissionRecords) {
            // 只处理有效的佣金记录
            if (record.getCommissionAmount() == null || record.getCommissionAmount().compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("跳过无效佣金记录：记录ID={}，金额={}", record.getId(), record.getCommissionAmount());
                continue;
            }
            
            // 获取用户的openid
            String openid = clientApi.getOpenId(record.getUserId());
            if (StrUtil.isBlank(openid)) {
                log.warn("跳过无openid的佣金记录：记录ID={}，用户ID={}", record.getId(), record.getUserId());
                continue;
            }
            
            MultiProfitSharingParam.ProfitReceiver receiver = new MultiProfitSharingParam.ProfitReceiver();
            receiver.setType("PERSONAL_OPENID");
            receiver.setAccount(openid);
            receiver.setAmount(record.getCommissionAmount());
            receiver.setDescription("用户佣金");
            receiver.setUserId(record.getUserId());
            receiver.setCommissionType(record.getCommissionType());
            receiver.setCommissionRecordId(record.getId());
            
            receivers.add(receiver);
            
            log.debug("添加分账接收方：用户ID={}，openid={}，金额={}元，类型={}", 
                record.getUserId(), openid, record.getCommissionAmount(), record.getCommissionType());
        }
        
        profitSharingParam.setReceivers(receivers);
        return profitSharingParam;
    }
}
