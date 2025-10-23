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
package vip.xiaonuo.wine.modular.commissionrecord.service.impl;

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
import vip.xiaonuo.wine.modular.commissionrecord.entity.WineCommissionRecord;
import vip.xiaonuo.wine.modular.commissionrecord.mapper.WineCommissionRecordMapper;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordAddParam;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordEditParam;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordIdParam;
import vip.xiaonuo.wine.modular.commissionrecord.param.WineCommissionRecordPageParam;
import vip.xiaonuo.wine.modular.commissionrecord.service.WineCommissionRecordService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 佣金记录表Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 08:50
 **/
@Service
public class WineCommissionRecordServiceImpl extends ServiceImpl<WineCommissionRecordMapper, WineCommissionRecord> implements WineCommissionRecordService {

    @Override
    public Page<WineCommissionRecord> page(WineCommissionRecordPageParam wineCommissionRecordPageParam) {
        QueryWrapper<WineCommissionRecord> queryWrapper = new QueryWrapper<WineCommissionRecord>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineCommissionRecordPageParam.getOrderId())) {
            queryWrapper.lambda().eq(WineCommissionRecord::getOrderId, wineCommissionRecordPageParam.getOrderId());
        }
        if(ObjectUtil.isAllNotEmpty(wineCommissionRecordPageParam.getSortField(), wineCommissionRecordPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineCommissionRecordPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineCommissionRecordPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineCommissionRecordPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineCommissionRecord::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineCommissionRecordAddParam wineCommissionRecordAddParam) {
        WineCommissionRecord wineCommissionRecord = BeanUtil.toBean(wineCommissionRecordAddParam, WineCommissionRecord.class);
        this.save(wineCommissionRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineCommissionRecordEditParam wineCommissionRecordEditParam) {
        WineCommissionRecord wineCommissionRecord = this.queryEntity(wineCommissionRecordEditParam.getId());
        BeanUtil.copyProperties(wineCommissionRecordEditParam, wineCommissionRecord);
        this.updateById(wineCommissionRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineCommissionRecordIdParam> wineCommissionRecordIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineCommissionRecordIdParamList, WineCommissionRecordIdParam::getId));
    }

    @Override
    public WineCommissionRecord detail(WineCommissionRecordIdParam wineCommissionRecordIdParam) {
        return this.queryEntity(wineCommissionRecordIdParam.getId());
    }

    @Override
    public WineCommissionRecord queryEntity(String id) {
        WineCommissionRecord wineCommissionRecord = this.getById(id);
        if(ObjectUtil.isEmpty(wineCommissionRecord)) {
            throw new CommonException("佣金记录表不存在，id值为：{}", id);
        }
        return wineCommissionRecord;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineCommissionRecordEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "佣金记录表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineCommissionRecordEditParam.class).sheet("佣金记录表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 佣金记录表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "佣金记录表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineCommissionRecordImportTemplate.xlsx"));
            // 读取excel
            List<WineCommissionRecordEditParam> wineCommissionRecordEditParamList =  EasyExcel.read(tempFile).head(WineCommissionRecordEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineCommissionRecord> allDataList = this.list();
            for (int i = 0; i < wineCommissionRecordEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineCommissionRecordEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineCommissionRecordEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 佣金记录表导入失败：", e);
            throw new CommonException("佣金记录表导入失败");
        }
    }

    public JSONObject doImport(List<WineCommissionRecord> allDataList, WineCommissionRecordEditParam wineCommissionRecordEditParam, int i) {
        String id = wineCommissionRecordEditParam.getId();
        String orderId = wineCommissionRecordEditParam.getOrderId();
        if(ObjectUtil.hasEmpty(id, orderId)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineCommissionRecord::getId).indexOf(wineCommissionRecordEditParam.getId());
                WineCommissionRecord wineCommissionRecord;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineCommissionRecord = new WineCommissionRecord();
                } else {
                    wineCommissionRecord = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineCommissionRecordEditParam, wineCommissionRecord);
                if(isAdd) {
                    allDataList.add(wineCommissionRecord);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineCommissionRecord);
                }
                this.saveOrUpdate(wineCommissionRecord);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineCommissionRecordIdParam> wineCommissionRecordIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineCommissionRecordEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineCommissionRecordIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineCommissionRecordIdParamList, WineCommissionRecordIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineCommissionRecordEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineCommissionRecordEditParam.class);
         }
         String fileName = "佣金记录表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineCommissionRecordEditParam.class).sheet("佣金记录表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 佣金记录表导出失败：", e);
         CommonResponseUtil.renderError(response, "佣金记录表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
