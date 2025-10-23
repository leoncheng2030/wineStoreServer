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
package vip.xiaonuo.dev.modular.usersubscription.service.impl;

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
import vip.xiaonuo.dev.modular.usersubscription.entity.WechatUserSubscription;
import vip.xiaonuo.dev.modular.usersubscription.mapper.WechatUserSubscriptionMapper;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionAddParam;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionEditParam;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionIdParam;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionPageParam;
import vip.xiaonuo.dev.modular.usersubscription.service.WechatUserSubscriptionService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 用户订阅消息授权表Service接口实现类
 *
 * @author jetox
 * @date  2025/09/29 00:49
 **/
@Slf4j
@Service
public class WechatUserSubscriptionServiceImpl extends ServiceImpl<WechatUserSubscriptionMapper, WechatUserSubscription> implements WechatUserSubscriptionService {

    @Override
    public Page<WechatUserSubscription> page(WechatUserSubscriptionPageParam wechatUserSubscriptionPageParam) {
        QueryWrapper<WechatUserSubscription> queryWrapper = new QueryWrapper<WechatUserSubscription>().checkSqlInjection();
        if(ObjectUtil.isAllNotEmpty(wechatUserSubscriptionPageParam.getSortField(), wechatUserSubscriptionPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wechatUserSubscriptionPageParam.getSortOrder());
            queryWrapper.orderBy(true, wechatUserSubscriptionPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wechatUserSubscriptionPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WechatUserSubscription::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WechatUserSubscriptionAddParam wechatUserSubscriptionAddParam) {
        WechatUserSubscription wechatUserSubscription = BeanUtil.toBean(wechatUserSubscriptionAddParam, WechatUserSubscription.class);
        this.save(wechatUserSubscription);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WechatUserSubscriptionEditParam wechatUserSubscriptionEditParam) {
        WechatUserSubscription wechatUserSubscription = this.queryEntity(wechatUserSubscriptionEditParam.getId());
        BeanUtil.copyProperties(wechatUserSubscriptionEditParam, wechatUserSubscription);
        this.updateById(wechatUserSubscription);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WechatUserSubscriptionIdParam> wechatUserSubscriptionIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wechatUserSubscriptionIdParamList, WechatUserSubscriptionIdParam::getId));
    }

    @Override
    public WechatUserSubscription detail(WechatUserSubscriptionIdParam wechatUserSubscriptionIdParam) {
        return this.queryEntity(wechatUserSubscriptionIdParam.getId());
    }

    @Override
    public WechatUserSubscription queryEntity(String id) {
        WechatUserSubscription wechatUserSubscription = this.getById(id);
        if(ObjectUtil.isEmpty(wechatUserSubscription)) {
            throw new CommonException("用户订阅消息授权表不存在，id值为：{}", id);
        }
        return wechatUserSubscription;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WechatUserSubscriptionEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "用户订阅消息授权表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WechatUserSubscriptionEditParam.class).sheet("用户订阅消息授权表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 用户订阅消息授权表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "用户订阅消息授权表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wechatUserSubscriptionImportTemplate.xlsx"));
            // 读取excel
            List<WechatUserSubscriptionEditParam> wechatUserSubscriptionEditParamList =  EasyExcel.read(tempFile).head(WechatUserSubscriptionEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WechatUserSubscription> allDataList = this.list();
            for (int i = 0; i < wechatUserSubscriptionEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wechatUserSubscriptionEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wechatUserSubscriptionEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 用户订阅消息授权表导入失败：", e);
            throw new CommonException("用户订阅消息授权表导入失败");
        }
    }

    public JSONObject doImport(List<WechatUserSubscription> allDataList, WechatUserSubscriptionEditParam wechatUserSubscriptionEditParam, int i) {
        String id = wechatUserSubscriptionEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WechatUserSubscription::getId).indexOf(wechatUserSubscriptionEditParam.getId());
                WechatUserSubscription wechatUserSubscription;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wechatUserSubscription = new WechatUserSubscription();
                } else {
                    wechatUserSubscription = allDataList.get(index);
                }
                BeanUtil.copyProperties(wechatUserSubscriptionEditParam, wechatUserSubscription);
                if(isAdd) {
                    allDataList.add(wechatUserSubscription);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wechatUserSubscription);
                }
                this.saveOrUpdate(wechatUserSubscription);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WechatUserSubscriptionIdParam> wechatUserSubscriptionIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WechatUserSubscriptionEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wechatUserSubscriptionIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wechatUserSubscriptionIdParamList, WechatUserSubscriptionIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WechatUserSubscriptionEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WechatUserSubscriptionEditParam.class);
         }
         String fileName = "用户订阅消息授权表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WechatUserSubscriptionEditParam.class).sheet("用户订阅消息授权表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 用户订阅消息授权表导出失败：", e);
         CommonResponseUtil.renderError(response, "用户订阅消息授权表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSendRecord(String openid, String templateId, String businessType, String businessId) {
        try {
            // 根据openid和templateId查找用户订阅记录
            LambdaQueryWrapper<WechatUserSubscription> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WechatUserSubscription::getOpenid, openid)
                       .eq(WechatUserSubscription::getTemplateId, templateId)
                       .eq(WechatUserSubscription::getSubscriptionStatus, "1"); // 只更新已授权的订阅
            
            WechatUserSubscription subscription = this.getOne(queryWrapper);
            
            if (subscription != null) {
                // 更新发送记录
                Integer totalSent = subscription.getTotalSent();
                subscription.setTotalSent(totalSent == null ? 1 : totalSent + 1);
                subscription.setLastSentTime(new Date());
                
                // 更新剩余发送次数（如果有限制）
                Integer remainingTimes = subscription.getRemainingTimes();
                if (remainingTimes != null && remainingTimes > 0) {
                    subscription.setRemainingTimes(remainingTimes - 1);
                }
                
                boolean result = this.updateById(subscription);
                
                if (result) {
                    log.info("用户订阅记录更新成功 - openid: {}, templateId: {}, businessType: {}, businessId: {}, totalSent: {}", 
                            openid, templateId, businessType, businessId, subscription.getTotalSent());
                } else {
                    log.warn("用户订阅记录更新失败 - openid: {}, templateId: {}, businessType: {}, businessId: {}", 
                            openid, templateId, businessType, businessId);
                }
                
                return result;
            } else {
                log.warn("未找到用户订阅记录，无法更新发送记录 - openid: {}, templateId: {}, businessType: {}, businessId: {}", 
                        openid, templateId, businessType, businessId);
                return false;
            }
        } catch (Exception e) {
            log.error("更新用户订阅发送记录失败 - openid: {}, templateId: {}, businessType: {}, businessId: {}", 
                    openid, templateId, businessType, businessId, e);
            return false;
        }
    }
}
