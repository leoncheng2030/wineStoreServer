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
package vip.xiaonuo.wine.modular.spec.service.impl;

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
import vip.xiaonuo.wine.modular.spec.entity.WineSpec;
import vip.xiaonuo.wine.modular.spec.mapper.WineSpecMapper;
import vip.xiaonuo.wine.modular.spec.param.WineSpecAddParam;
import vip.xiaonuo.wine.modular.spec.param.WineSpecEditParam;
import vip.xiaonuo.wine.modular.spec.param.WineSpecIdParam;
import vip.xiaonuo.wine.modular.spec.param.WineSpecPageParam;
import vip.xiaonuo.wine.modular.spec.service.WineSpecService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 规格Service接口实现类
 *
 * @author jetox
 * @date  2025/09/28 19:06
 **/
@Service
public class WineSpecServiceImpl extends ServiceImpl<WineSpecMapper, WineSpec> implements WineSpecService {

    @Override
    public Page<WineSpec> page(WineSpecPageParam wineSpecPageParam) {
        QueryWrapper<WineSpec> queryWrapper = new QueryWrapper<WineSpec>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineSpecPageParam.getName())) {
            queryWrapper.lambda().like(WineSpec::getName, wineSpecPageParam.getName());
        }
        if(ObjectUtil.isNotEmpty(wineSpecPageParam.getValue())) {
            queryWrapper.lambda().like(WineSpec::getValue, wineSpecPageParam.getValue());
        }
        if(ObjectUtil.isAllNotEmpty(wineSpecPageParam.getSortField(), wineSpecPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineSpecPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineSpecPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineSpecPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineSpec::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineSpecAddParam wineSpecAddParam) {
        WineSpec wineSpec = BeanUtil.toBean(wineSpecAddParam, WineSpec.class);
        this.save(wineSpec);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineSpecEditParam wineSpecEditParam) {
        WineSpec wineSpec = this.queryEntity(wineSpecEditParam.getId());
        BeanUtil.copyProperties(wineSpecEditParam, wineSpec);
        this.updateById(wineSpec);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineSpecIdParam> wineSpecIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineSpecIdParamList, WineSpecIdParam::getId));
    }

    @Override
    public WineSpec detail(WineSpecIdParam wineSpecIdParam) {
        return this.queryEntity(wineSpecIdParam.getId());
    }

    @Override
    public WineSpec queryEntity(String id) {
        WineSpec wineSpec = this.getById(id);
        if(ObjectUtil.isEmpty(wineSpec)) {
            throw new CommonException("规格不存在，id值为：{}", id);
        }
        return wineSpec;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineSpecEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "规格导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineSpecEditParam.class).sheet("规格").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 规格导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "规格导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineSpecImportTemplate.xlsx"));
            // 读取excel
            List<WineSpecEditParam> wineSpecEditParamList =  EasyExcel.read(tempFile).head(WineSpecEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineSpec> allDataList = this.list();
            for (int i = 0; i < wineSpecEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineSpecEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineSpecEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 规格导入失败：", e);
            throw new CommonException("规格导入失败");
        }
    }

    public JSONObject doImport(List<WineSpec> allDataList, WineSpecEditParam wineSpecEditParam, int i) {
        String id = wineSpecEditParam.getId();
        String name = wineSpecEditParam.getName();
        Long value = wineSpecEditParam.getValue();
        String type = wineSpecEditParam.getType();
        if(ObjectUtil.hasEmpty(id, name, value, type)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineSpec::getId).indexOf(wineSpecEditParam.getId());
                WineSpec wineSpec;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineSpec = new WineSpec();
                } else {
                    wineSpec = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineSpecEditParam, wineSpec);
                if(isAdd) {
                    allDataList.add(wineSpec);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineSpec);
                }
                this.saveOrUpdate(wineSpec);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineSpecIdParam> wineSpecIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineSpecEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineSpecIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineSpecIdParamList, WineSpecIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineSpecEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineSpecEditParam.class);
         }
         String fileName = "规格_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineSpecEditParam.class).sheet("规格").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 规格导出失败：", e);
         CommonResponseUtil.renderError(response, "规格导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
