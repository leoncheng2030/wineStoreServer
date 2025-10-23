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
package vip.xiaonuo.wine.modular.withdrawrecord.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.auth.core.pojo.SaBaseClientLoginUser;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.api.DevMessageApi;
import vip.xiaonuo.pay.api.PayApi;
import vip.xiaonuo.sys.api.SysUserApi;
import vip.xiaonuo.wine.modular.accountflow.entity.WineAccountFlow;
import vip.xiaonuo.wine.modular.accountflow.service.WineAccountFlowService;
import vip.xiaonuo.wine.modular.useraccount.entity.WineUserAccount;
import vip.xiaonuo.wine.modular.useraccount.service.WineUserAccountService;
import vip.xiaonuo.wine.modular.withdrawrecord.entity.WineWithdrawRecord;
import vip.xiaonuo.wine.modular.withdrawrecord.enums.WineWithdrawRecordStatusEnum;
import vip.xiaonuo.wine.modular.withdrawrecord.mapper.WineWithdrawRecordMapper;
import vip.xiaonuo.wine.modular.withdrawrecord.param.*;
import vip.xiaonuo.wine.modular.withdrawrecord.service.WineWithdrawRecordService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 提现记录表Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 08:50
 **/
@Service
public class WineWithdrawRecordServiceImpl extends ServiceImpl<WineWithdrawRecordMapper, WineWithdrawRecord> implements WineWithdrawRecordService {

    @Resource
    private WineUserAccountService wineUserAccountService;

    @Resource
    private WineAccountFlowService wineAccountFlowService;

    @Resource
    private DevConfigApi devConfigApi;
    
    @Resource
    private DevMessageApi devMessageApi;
    
    @Resource
    private SysUserApi sysUserApi;
    
    @Resource
    private PayApi payApi;
    @Resource
    private ClientApi clientApi;

    @Override
    public Page<WineWithdrawRecord> page(WineWithdrawRecordPageParam wineWithdrawRecordPageParam) {
        QueryWrapper<WineWithdrawRecord> queryWrapper = new QueryWrapper<WineWithdrawRecord>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineWithdrawRecordPageParam.getWithdrawNo())) {
            queryWrapper.lambda().like(WineWithdrawRecord::getWithdrawNo, wineWithdrawRecordPageParam.getWithdrawNo());
        }
        if(ObjectUtil.isNotEmpty(wineWithdrawRecordPageParam.getUserId())) {
            queryWrapper.lambda().eq(WineWithdrawRecord::getUserId, wineWithdrawRecordPageParam.getUserId());
        }
        if(ObjectUtil.isAllNotEmpty(wineWithdrawRecordPageParam.getSortField(), wineWithdrawRecordPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineWithdrawRecordPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineWithdrawRecordPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineWithdrawRecordPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(WineWithdrawRecord::getCreateTime);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineWithdrawRecordAddParam wineWithdrawRecordAddParam) {
        // 检查是否开启了服务商支付模式
        if (isWxPartnerPayEnabled()) {
            throw new CommonException("当前已开启服务商支付模式，不支持提现功能，请联系管理员处理");
        }
        
        String valueByKey = devConfigApi.getValueByKey("WITHDRAW_SERVICE_RATIO");
        BigDecimal withdrawServiceRatio = new BigDecimal(valueByKey);
        SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
        WineWithdrawRecord wineWithdrawRecord = BeanUtil.toBean(wineWithdrawRecordAddParam, WineWithdrawRecord.class);
        wineWithdrawRecord.setUserId(clientLoginUser.getId());
        wineWithdrawRecord.setUserNickname(clientLoginUser.getName());
        wineWithdrawRecord.setServiceFee(wineWithdrawRecordAddParam.getWithdrawAmount().multiply(withdrawServiceRatio));
        wineWithdrawRecord.setWithdrawNo(IdUtil.getSnowflake(1, 1).nextIdStr());
        wineWithdrawRecord.setStatus(WineWithdrawRecordStatusEnum.WAIT.getValue());
        wineWithdrawRecord.setActualAmount(wineWithdrawRecord.getWithdrawAmount().multiply(new BigDecimal(1).subtract(withdrawServiceRatio)));
        this.save(wineWithdrawRecord);
        // 冻结用户账户中的提现金额
        WineUserAccount userAccount = wineUserAccountService.getOne(new LambdaQueryWrapper<WineUserAccount>()
                .eq(WineUserAccount::getUserId, clientLoginUser.getId()));
        if (userAccount != null) {
            BigDecimal availableBalance = userAccount.getAvailableBalance() != null ? userAccount.getAvailableBalance() : BigDecimal.ZERO;
            BigDecimal frozenBalance = userAccount.getFrozenBalance() != null ? userAccount.getFrozenBalance() : BigDecimal.ZERO;
            BigDecimal withdrawAmount = wineWithdrawRecord.getWithdrawAmount() != null ? wineWithdrawRecord.getWithdrawAmount() : BigDecimal.ZERO;

            // 检查可用余额是否充足
            if (availableBalance.compareTo(withdrawAmount) < 0) {
                throw new CommonException("可用余额不足，无法发起提现");
            }

            // 更新账户余额：减少可用余额，增加冻结余额
            userAccount.setAvailableBalance(availableBalance.subtract(withdrawAmount));
            userAccount.setFrozenBalance(frozenBalance.add(withdrawAmount));
            userAccount.setLastWithdrawTime(new Date());
            wineUserAccountService.updateById(userAccount);

            // 添加账户流水记录
            WineAccountFlow accountFlow = new WineAccountFlow();
            accountFlow.setFlowNo(IdUtil.getSnowflake(1, 1).nextIdStr());
            accountFlow.setUserId(clientLoginUser.getId());
            accountFlow.setUserNickname(clientLoginUser.getName());
            accountFlow.setFlowType("WITHDRAW"); // 提现支出
            accountFlow.setAmount(withdrawAmount);
            accountFlow.setBalanceChange(withdrawAmount.negate()); // 负数表示减少
            accountFlow.setBeforeBalance(availableBalance);
            accountFlow.setAfterBalance(availableBalance.subtract(withdrawAmount));
            accountFlow.setRelatedId(wineWithdrawRecord.getId());
            accountFlow.setRelatedType("WITHDRAW");
            accountFlow.setRelatedNo(wineWithdrawRecord.getWithdrawNo());
            accountFlow.setDescription("提现申请冻结资金");
            accountFlow.setStatus("SUCCESS");
            accountFlow.setTransactionTime(new Date());
            wineAccountFlowService.save(accountFlow);
        }
        
        // 发送站内信给系统管理员
        try {
            Collection<Object> adminUsers = sysUserApi.listAdminUsers();
            if (adminUsers != null && !adminUsers.isEmpty()) {
                List<String> adminUserIds = adminUsers.stream()
                        .map(user -> ((JSONObject) user).getStr("id"))
                        .filter(ObjectUtil::isNotEmpty)
                        .collect(Collectors.toList());
                
                if (!adminUserIds.isEmpty()) {
                    String subject = "收到新的提现申请";
                    String content = String.format("用户 %s 提交了一笔 %.2f 元的提现申请，请及时处理。", 
                            clientLoginUser.getName(), 
                            wineWithdrawRecord.getWithdrawAmount());
                    devMessageApi.sendMessageWithContent(adminUserIds, "WITHDRAW_APPLY", subject, content);
                }
            }
        } catch (Exception e) {
            log.error("发送提现申请站内信通知失败", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineWithdrawRecordEditParam wineWithdrawRecordEditParam) {
        WineWithdrawRecord wineWithdrawRecord = this.queryEntity(wineWithdrawRecordEditParam.getId());
        BeanUtil.copyProperties(wineWithdrawRecordEditParam, wineWithdrawRecord);
        this.updateById(wineWithdrawRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineWithdrawRecordIdParam> wineWithdrawRecordIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineWithdrawRecordIdParamList, WineWithdrawRecordIdParam::getId));
    }

    @Override
    public WineWithdrawRecord detail(WineWithdrawRecordIdParam wineWithdrawRecordIdParam) {
        return this.queryEntity(wineWithdrawRecordIdParam.getId());
    }

    @Override
    public WineWithdrawRecord queryEntity(String id) {
        WineWithdrawRecord wineWithdrawRecord = this.getById(id);
        if(ObjectUtil.isEmpty(wineWithdrawRecord)) {
            throw new CommonException("提现记录表不存在，id值为：{}", id);
        }
        return wineWithdrawRecord;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineWithdrawRecordEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "提现记录表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineWithdrawRecordEditParam.class).sheet("提现记录表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 提现记录表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "提现记录表导入模板下载失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject importData(MultipartFile file) {
        try {
            int successCount = 0;
            int errorCount = 0;
            JSONArray errorDetail = JSONUtil.createArray();
            // 创建临时文件
            File tempFile = FileUtil.writeBytes(file.getBytes(), FileUtil.file(FileUtil.getTmpDir() +
                    FileUtil.FILE_SEPARATOR + "wineWithdrawRecordImportTemplate.xlsx"));
            // 读取excel
            List<WineWithdrawRecordEditParam> wineWithdrawRecordEditParamList =  EasyExcel.read(tempFile).head(WineWithdrawRecordEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineWithdrawRecord> allDataList = this.list();
            for (int i = 0; i < wineWithdrawRecordEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineWithdrawRecordEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineWithdrawRecordEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 提现记录表导入失败：", e);
            throw new CommonException("提现记录表导入失败");
        }
    }

    public JSONObject doImport(List<WineWithdrawRecord> allDataList, WineWithdrawRecordEditParam wineWithdrawRecordEditParam, int i) {
        String id = wineWithdrawRecordEditParam.getId();
        String withdrawNo = wineWithdrawRecordEditParam.getWithdrawNo();
        String userId = wineWithdrawRecordEditParam.getUserId();
        String userNickname = wineWithdrawRecordEditParam.getUserNickname();
        BigDecimal withdrawAmount = wineWithdrawRecordEditParam.getWithdrawAmount();
        BigDecimal serviceFee = wineWithdrawRecordEditParam.getServiceFee();
        String withdrawType = wineWithdrawRecordEditParam.getWithdrawType();
        if(ObjectUtil.hasEmpty(id, withdrawNo, userId, userNickname, withdrawAmount, serviceFee, withdrawType)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineWithdrawRecord::getId).indexOf(wineWithdrawRecordEditParam.getId());
                WineWithdrawRecord wineWithdrawRecord;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineWithdrawRecord = new WineWithdrawRecord();
                } else {
                    wineWithdrawRecord = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineWithdrawRecordEditParam, wineWithdrawRecord);
                if(isAdd) {
                    allDataList.add(wineWithdrawRecord);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineWithdrawRecord);
                }
                this.saveOrUpdate(wineWithdrawRecord);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineWithdrawRecordIdParam> wineWithdrawRecordIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineWithdrawRecordEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineWithdrawRecordIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineWithdrawRecordIdParamList, WineWithdrawRecordIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineWithdrawRecordEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineWithdrawRecordEditParam.class);
         }
         String fileName = "提现记录表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineWithdrawRecordEditParam.class).sheet("提现记录表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 提现记录表导出失败：", e);
         CommonResponseUtil.renderError(response, "提现记录表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
    /**
     * 审核通过提现申请
     * @param wineWithdrawRecordIdParam 提现记录参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveWithdraw(WineWithdrawRecordIdParam wineWithdrawRecordIdParam) {
        WineWithdrawRecord wineWithdrawRecord = this.queryEntity(wineWithdrawRecordIdParam.getId());

        // 检查状态是否为待处理
        if (!WineWithdrawRecordStatusEnum.WAIT.getValue().equals(wineWithdrawRecord.getStatus())) {
            throw new CommonException("只有待处理状态的提现申请才能审核通过");
        }

        // 解冻并扣除用户账户中的冻结金额
        WineUserAccount userAccount = wineUserAccountService.getOne(new LambdaQueryWrapper<WineUserAccount>()
                .eq(WineUserAccount::getUserId, wineWithdrawRecord.getUserId()));
        if (userAccount != null) {
            BigDecimal frozenBalance = userAccount.getFrozenBalance() != null ? userAccount.getFrozenBalance() : BigDecimal.ZERO;
            BigDecimal totalWithdraw = userAccount.getTotalWithdraw() != null ? userAccount.getTotalWithdraw() : BigDecimal.ZERO;
            BigDecimal withdrawAmount = wineWithdrawRecord.getWithdrawAmount() != null ? wineWithdrawRecord.getWithdrawAmount() : BigDecimal.ZERO;

            // 检查冻结余额是否充足
            if (frozenBalance.compareTo(withdrawAmount) < 0) {
                throw new CommonException("冻结余额不足，无法完成提现");
            }

            // 通过微信支付的接口进行提现
            try {
                String desc = String.format("提现申请: %s", wineWithdrawRecord.getWithdrawNo());

                // 获取体现用户的openId
                String openId = clientApi.getOpenId(wineWithdrawRecord.getUserId());
                String withdraw = payApi.withdraw(
                        openId,
                        wineWithdrawRecord.getActualAmount(),
                        wineWithdrawRecord.getWithdrawNo(),
                        desc);
                
                // 微信支付成功后，更新提现记录状态为成功
                wineWithdrawRecord.setStatus(WineWithdrawRecordStatusEnum.SUCCESS.getValue());
                if (ObjectUtil.isNotEmpty(withdraw)){
                    wineWithdrawRecord.setTransactionId(withdraw);
                }
                this.updateById(wineWithdrawRecord);

                // 更新账户余额：减少冻结余额，增加累计提现
                userAccount.setFrozenBalance(frozenBalance.subtract(withdrawAmount));
                userAccount.setTotalWithdraw(totalWithdraw.add(withdrawAmount));
                userAccount.setLastWithdrawTime(new Date());
                wineUserAccountService.updateById(userAccount);

                // 添加账户流水记录
                WineAccountFlow accountFlow = new WineAccountFlow();
                accountFlow.setFlowNo(IdUtil.getSnowflake(1, 1).nextIdStr());
                accountFlow.setUserId(wineWithdrawRecord.getUserId());
                accountFlow.setUserNickname(wineWithdrawRecord.getUserNickname());
                accountFlow.setFlowType("WITHDRAW"); // 提现支出
                accountFlow.setAmount(withdrawAmount);
                accountFlow.setBalanceChange(withdrawAmount.negate()); // 负数表示减少
                accountFlow.setBeforeBalance(frozenBalance);
                accountFlow.setAfterBalance(frozenBalance.subtract(withdrawAmount));
                accountFlow.setRelatedId(wineWithdrawRecord.getId());
                accountFlow.setRelatedType("WITHDRAW");
                accountFlow.setRelatedNo(wineWithdrawRecord.getWithdrawNo());
                accountFlow.setDescription("提现审核通过，解冻并扣除资金");
                accountFlow.setStatus("SUCCESS");
                accountFlow.setTransactionTime(new Date());
                wineAccountFlowService.save(accountFlow);
            } catch (Exception e) {
                log.error("微信企业付款失败", e);
                throw new CommonException("微信企业付款失败: " + e.getMessage());
            }
        }
    }

    /**
     * 拒绝提现申请
     * @param wineWithdrawRecordRejectWithdrawParam 提现记录参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectWithdraw(WineWithdrawRecordRejectWithdrawParam wineWithdrawRecordRejectWithdrawParam) {
        WineWithdrawRecord wineWithdrawRecord = this.queryEntity(wineWithdrawRecordRejectWithdrawParam.getId());

        // 检查状态是否为待处理
        if (!WineWithdrawRecordStatusEnum.WAIT.getValue().equals(wineWithdrawRecord.getStatus())) {
            throw new CommonException("只有待处理状态的提现申请才能拒绝");
        }

        // 更新提现记录状态为失败
        wineWithdrawRecord.setStatus(WineWithdrawRecordStatusEnum.FAIL.getValue());
        wineWithdrawRecord.setFailReason(wineWithdrawRecordRejectWithdrawParam.getFailReason());
        this.updateById(wineWithdrawRecord);

        // 解冻用户账户中的冻结金额，恢复到可用余额
        WineUserAccount userAccount = wineUserAccountService.getOne(new LambdaQueryWrapper<WineUserAccount>()
                .eq(WineUserAccount::getUserId, wineWithdrawRecord.getUserId()));
        if (userAccount != null) {
            BigDecimal availableBalance = userAccount.getAvailableBalance() != null ? userAccount.getAvailableBalance() : BigDecimal.ZERO;
            BigDecimal frozenBalance = userAccount.getFrozenBalance() != null ? userAccount.getFrozenBalance() : BigDecimal.ZERO;
            BigDecimal withdrawAmount = wineWithdrawRecord.getWithdrawAmount() != null ? wineWithdrawRecord.getWithdrawAmount() : BigDecimal.ZERO;

            // 检查冻结余额是否充足
            if (frozenBalance.compareTo(withdrawAmount) < 0) {
                throw new CommonException("冻结余额不足，无法完成操作");
            }

            // 更新账户余额：减少冻结余额，增加可用余额
            userAccount.setAvailableBalance(availableBalance.add(withdrawAmount));
            userAccount.setFrozenBalance(frozenBalance.subtract(withdrawAmount));
            userAccount.setLastWithdrawTime(new Date());
            wineUserAccountService.updateById(userAccount);

            // 添加账户流水记录
            WineAccountFlow accountFlow = new WineAccountFlow();
            accountFlow.setFlowNo(IdUtil.getSnowflake(1, 1).nextIdStr());
            accountFlow.setUserId(wineWithdrawRecord.getUserId());
            accountFlow.setUserNickname(wineWithdrawRecord.getUserNickname());
            accountFlow.setFlowType("WITHDRAW_REFUND"); // 提现拒绝资金返还
            accountFlow.setAmount(withdrawAmount);
            accountFlow.setBalanceChange(withdrawAmount); // 正数表示余额增加
            accountFlow.setBeforeBalance(availableBalance);
            accountFlow.setAfterBalance(availableBalance.add(withdrawAmount));
            accountFlow.setRelatedId(wineWithdrawRecord.getId());
            accountFlow.setRelatedType("WITHDRAW");
            accountFlow.setRelatedNo(wineWithdrawRecord.getWithdrawNo());
            accountFlow.setDescription(wineWithdrawRecordRejectWithdrawParam.getFailReason());
            accountFlow.setStatus("FAILED");
            accountFlow.setTransactionTime(new Date());
            wineAccountFlowService.save(accountFlow);
        }
    }

    /**
     * 检查微信服务商支付是否启用
     *
     * @return boolean
     */
    private boolean isWxPartnerPayEnabled() {
        try {
            String enabled = devConfigApi.getValueByKey("SNOWY_PAY_WX_PARTNER_ENABLED");
            return "true".equals(enabled);
        } catch (Exception e) {
            // 如果获取配置失败，默认为未启用
            return false;
        }
    }
}
