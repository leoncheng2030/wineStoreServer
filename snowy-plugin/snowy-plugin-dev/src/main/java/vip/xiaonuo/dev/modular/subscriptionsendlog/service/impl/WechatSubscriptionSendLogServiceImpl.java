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
package vip.xiaonuo.dev.modular.subscriptionsendlog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
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
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.Date;
import vip.xiaonuo.dev.modular.subscriptionsendlog.entity.WechatSubscriptionSendLog;
import vip.xiaonuo.dev.modular.subscriptionsendlog.mapper.WechatSubscriptionSendLogMapper;
import vip.xiaonuo.dev.modular.subscriptionsendlog.param.WechatSubscriptionSendLogAddParam;
import vip.xiaonuo.dev.modular.subscriptionsendlog.param.WechatSubscriptionSendLogEditParam;
import vip.xiaonuo.dev.modular.subscriptionsendlog.param.WechatSubscriptionSendLogIdParam;
import vip.xiaonuo.dev.modular.subscriptionsendlog.param.WechatSubscriptionSendLogPageParam;
import vip.xiaonuo.dev.modular.subscriptionsendlog.service.WechatSubscriptionSendLogService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 订阅消息发送记录表Service接口实现类
 *
 * @author jetox
 * @date  2025/09/29 00:50
 **/
@Slf4j
@Service
public class WechatSubscriptionSendLogServiceImpl extends ServiceImpl<WechatSubscriptionSendLogMapper, WechatSubscriptionSendLog> implements WechatSubscriptionSendLogService {

    @Override
    public Page<WechatSubscriptionSendLog> page(WechatSubscriptionSendLogPageParam wechatSubscriptionSendLogPageParam) {
        QueryWrapper<WechatSubscriptionSendLog> queryWrapper = new QueryWrapper<WechatSubscriptionSendLog>().checkSqlInjection();
        if(ObjectUtil.isAllNotEmpty(wechatSubscriptionSendLogPageParam.getSortField(), wechatSubscriptionSendLogPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wechatSubscriptionSendLogPageParam.getSortOrder());
            queryWrapper.orderBy(true, wechatSubscriptionSendLogPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wechatSubscriptionSendLogPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WechatSubscriptionSendLog::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WechatSubscriptionSendLogAddParam wechatSubscriptionSendLogAddParam) {
        WechatSubscriptionSendLog wechatSubscriptionSendLog = BeanUtil.toBean(wechatSubscriptionSendLogAddParam, WechatSubscriptionSendLog.class);
        this.save(wechatSubscriptionSendLog);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WechatSubscriptionSendLogEditParam wechatSubscriptionSendLogEditParam) {
        WechatSubscriptionSendLog wechatSubscriptionSendLog = this.queryEntity(wechatSubscriptionSendLogEditParam.getId());
        BeanUtil.copyProperties(wechatSubscriptionSendLogEditParam, wechatSubscriptionSendLog);
        this.updateById(wechatSubscriptionSendLog);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WechatSubscriptionSendLogIdParam> wechatSubscriptionSendLogIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wechatSubscriptionSendLogIdParamList, WechatSubscriptionSendLogIdParam::getId));
    }

    @Override
    public WechatSubscriptionSendLog detail(WechatSubscriptionSendLogIdParam wechatSubscriptionSendLogIdParam) {
        return this.queryEntity(wechatSubscriptionSendLogIdParam.getId());
    }

    @Override
    public WechatSubscriptionSendLog queryEntity(String id) {
        WechatSubscriptionSendLog wechatSubscriptionSendLog = this.getById(id);
        if(ObjectUtil.isEmpty(wechatSubscriptionSendLog)) {
            throw new CommonException("订阅消息发送记录表不存在，id值为：{}", id);
        }
        return wechatSubscriptionSendLog;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WechatSubscriptionSendLogEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "订阅消息发送记录表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WechatSubscriptionSendLogEditParam.class).sheet("订阅消息发送记录表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 订阅消息发送记录表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "订阅消息发送记录表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wechatSubscriptionSendLogImportTemplate.xlsx"));
            // 读取excel
            List<WechatSubscriptionSendLogEditParam> wechatSubscriptionSendLogEditParamList =  EasyExcel.read(tempFile).head(WechatSubscriptionSendLogEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WechatSubscriptionSendLog> allDataList = this.list();
            for (int i = 0; i < wechatSubscriptionSendLogEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wechatSubscriptionSendLogEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wechatSubscriptionSendLogEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 订阅消息发送记录表导入失败：", e);
            throw new CommonException("订阅消息发送记录表导入失败");
        }
    }

    public JSONObject doImport(List<WechatSubscriptionSendLog> allDataList, WechatSubscriptionSendLogEditParam wechatSubscriptionSendLogEditParam, int i) {
        String id = wechatSubscriptionSendLogEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WechatSubscriptionSendLog::getId).indexOf(wechatSubscriptionSendLogEditParam.getId());
                WechatSubscriptionSendLog wechatSubscriptionSendLog;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wechatSubscriptionSendLog = new WechatSubscriptionSendLog();
                } else {
                    wechatSubscriptionSendLog = allDataList.get(index);
                }
                BeanUtil.copyProperties(wechatSubscriptionSendLogEditParam, wechatSubscriptionSendLog);
                if(isAdd) {
                    allDataList.add(wechatSubscriptionSendLog);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wechatSubscriptionSendLog);
                }
                this.saveOrUpdate(wechatSubscriptionSendLog);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WechatSubscriptionSendLogIdParam> wechatSubscriptionSendLogIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WechatSubscriptionSendLogEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wechatSubscriptionSendLogIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wechatSubscriptionSendLogIdParamList, WechatSubscriptionSendLogIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WechatSubscriptionSendLogEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WechatSubscriptionSendLogEditParam.class);
         }
         String fileName = "订阅消息发送记录表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WechatSubscriptionSendLogEditParam.class).sheet("订阅消息发送记录表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 订阅消息发送记录表导出失败：", e);
         CommonResponseUtil.renderError(response, "订阅消息发送记录表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    @Override
    public boolean hasSentSuccessToday(String openid, String templateId, String businessType, String businessId) {
        // 获取今日开始和结束时间
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        // 转换为Date类型
        Date startOfDayDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endOfDayDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        
        // 构建查询条件
        LambdaQueryWrapper<WechatSubscriptionSendLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WechatSubscriptionSendLog::getOpenid, openid)
                   .eq(WechatSubscriptionSendLog::getTemplateId, templateId)
                   .eq(WechatSubscriptionSendLog::getBusinessType, businessType)
                   .eq(WechatSubscriptionSendLog::getBusinessId, businessId)
                   .eq(WechatSubscriptionSendLog::getSendStatus, "1") // 发送成功状态
                   .between(WechatSubscriptionSendLog::getSendTime, startOfDayDate, endOfDayDate);
        
        // 查询是否存在符合条件的记录
        long count = this.count(queryWrapper);
        
        if (count > 0) {
            log.info("用户今日已发送过成功的订阅消息 - openid: {}, templateId: {}, businessType: {}, businessId: {}, count: {}", 
                    openid, templateId, businessType, businessId, count);
            return true;
        }
        
        return false;
    }
}
