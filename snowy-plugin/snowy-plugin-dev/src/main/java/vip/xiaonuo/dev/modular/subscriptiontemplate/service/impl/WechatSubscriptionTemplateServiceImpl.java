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
package vip.xiaonuo.dev.modular.subscriptiontemplate.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.Date;
import vip.xiaonuo.dev.modular.subscriptiontemplate.entity.WechatSubscriptionTemplate;
import vip.xiaonuo.dev.modular.subscriptiontemplate.mapper.WechatSubscriptionTemplateMapper;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplateAddParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplateEditParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplateIdParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplatePageParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.service.WechatSubscriptionTemplateService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 微信订阅消息模板表Service接口实现类
 *
 * @author jetox
 * @date  2025/09/29 00:49
 **/
@Service
public class WechatSubscriptionTemplateServiceImpl extends ServiceImpl<WechatSubscriptionTemplateMapper, WechatSubscriptionTemplate> implements WechatSubscriptionTemplateService {

    @Override
    public Page<WechatSubscriptionTemplate> page(WechatSubscriptionTemplatePageParam wechatSubscriptionTemplatePageParam) {
        QueryWrapper<WechatSubscriptionTemplate> queryWrapper = new QueryWrapper<WechatSubscriptionTemplate>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wechatSubscriptionTemplatePageParam.getTemplateTitle())) {
            queryWrapper.lambda().like(WechatSubscriptionTemplate::getTemplateTitle, wechatSubscriptionTemplatePageParam.getTemplateTitle());
        }
        if(ObjectUtil.isAllNotEmpty(wechatSubscriptionTemplatePageParam.getSortField(), wechatSubscriptionTemplatePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wechatSubscriptionTemplatePageParam.getSortOrder());
            queryWrapper.orderBy(true, wechatSubscriptionTemplatePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wechatSubscriptionTemplatePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WechatSubscriptionTemplate::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WechatSubscriptionTemplateAddParam wechatSubscriptionTemplateAddParam) {
        WechatSubscriptionTemplate wechatSubscriptionTemplate = BeanUtil.toBean(wechatSubscriptionTemplateAddParam, WechatSubscriptionTemplate.class);
        this.save(wechatSubscriptionTemplate);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WechatSubscriptionTemplateEditParam wechatSubscriptionTemplateEditParam) {
        WechatSubscriptionTemplate wechatSubscriptionTemplate = this.queryEntity(wechatSubscriptionTemplateEditParam.getId());
        BeanUtil.copyProperties(wechatSubscriptionTemplateEditParam, wechatSubscriptionTemplate);
        this.updateById(wechatSubscriptionTemplate);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WechatSubscriptionTemplateIdParam> wechatSubscriptionTemplateIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wechatSubscriptionTemplateIdParamList, WechatSubscriptionTemplateIdParam::getId));
    }

    @Override
    public WechatSubscriptionTemplate detail(WechatSubscriptionTemplateIdParam wechatSubscriptionTemplateIdParam) {
        return this.queryEntity(wechatSubscriptionTemplateIdParam.getId());
    }

    @Override
    public WechatSubscriptionTemplate queryEntity(String id) {
        WechatSubscriptionTemplate wechatSubscriptionTemplate = this.getById(id);
        if(ObjectUtil.isEmpty(wechatSubscriptionTemplate)) {
            throw new CommonException("微信订阅消息模板表不存在，id值为：{}", id);
        }
        return wechatSubscriptionTemplate;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WechatSubscriptionTemplateEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "微信订阅消息模板表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WechatSubscriptionTemplateEditParam.class).sheet("微信订阅消息模板表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 微信订阅消息模板表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "微信订阅消息模板表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wechatSubscriptionTemplateImportTemplate.xlsx"));
            // 读取excel
            List<WechatSubscriptionTemplateEditParam> wechatSubscriptionTemplateEditParamList =  EasyExcel.read(tempFile).head(WechatSubscriptionTemplateEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WechatSubscriptionTemplate> allDataList = this.list();
            for (int i = 0; i < wechatSubscriptionTemplateEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wechatSubscriptionTemplateEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wechatSubscriptionTemplateEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 微信订阅消息模板表导入失败：", e);
            throw new CommonException("微信订阅消息模板表导入失败");
        }
    }

    public JSONObject doImport(List<WechatSubscriptionTemplate> allDataList, WechatSubscriptionTemplateEditParam wechatSubscriptionTemplateEditParam, int i) {
        String id = wechatSubscriptionTemplateEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WechatSubscriptionTemplate::getId).indexOf(wechatSubscriptionTemplateEditParam.getId());
                WechatSubscriptionTemplate wechatSubscriptionTemplate;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wechatSubscriptionTemplate = new WechatSubscriptionTemplate();
                } else {
                    wechatSubscriptionTemplate = allDataList.get(index);
                }
                BeanUtil.copyProperties(wechatSubscriptionTemplateEditParam, wechatSubscriptionTemplate);
                if(isAdd) {
                    allDataList.add(wechatSubscriptionTemplate);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wechatSubscriptionTemplate);
                }
                this.saveOrUpdate(wechatSubscriptionTemplate);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WechatSubscriptionTemplateIdParam> wechatSubscriptionTemplateIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WechatSubscriptionTemplateEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wechatSubscriptionTemplateIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wechatSubscriptionTemplateIdParamList, WechatSubscriptionTemplateIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WechatSubscriptionTemplateEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WechatSubscriptionTemplateEditParam.class);
         }
         String fileName = "微信订阅消息模板表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WechatSubscriptionTemplateEditParam.class).sheet("微信订阅消息模板表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 微信订阅消息模板表导出失败：", e);
         CommonResponseUtil.renderError(response, "微信订阅消息模板表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
