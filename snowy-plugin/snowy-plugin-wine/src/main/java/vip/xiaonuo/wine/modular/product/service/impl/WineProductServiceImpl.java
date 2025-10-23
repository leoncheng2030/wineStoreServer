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
package vip.xiaonuo.wine.modular.product.service.impl;

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
import vip.xiaonuo.wine.modular.product.entity.WineProduct;
import vip.xiaonuo.wine.modular.product.mapper.WineProductMapper;
import vip.xiaonuo.wine.modular.product.param.*;
import vip.xiaonuo.wine.modular.product.service.WineProductService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 酒品列表Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 08:20
 **/
@Service
public class WineProductServiceImpl extends ServiceImpl<WineProductMapper, WineProduct> implements WineProductService {

    @Override
    public Page<WineProduct> page(WineProductPageParam wineProductPageParam) {
        QueryWrapper<WineProduct> queryWrapper = new QueryWrapper<WineProduct>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineProductPageParam.getProductName())) {
            queryWrapper.lambda().like(WineProduct::getProductName, wineProductPageParam.getProductName());
        }
        if(ObjectUtil.isNotEmpty(wineProductPageParam.getCategoryId())) {
            queryWrapper.lambda().eq(WineProduct::getCategoryId, wineProductPageParam.getCategoryId());
        }
        if(ObjectUtil.isNotEmpty(wineProductPageParam.getStatus())) {
            queryWrapper.lambda().eq(WineProduct::getStatus, wineProductPageParam.getStatus());
        }
        if(ObjectUtil.isAllNotEmpty(wineProductPageParam.getSortField(), wineProductPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineProductPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineProductPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineProductPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineProduct::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<WineProduct> idList(WineProductIdListParam wineProductIdListParam) {
        if (ObjectUtil.isEmpty(wineProductIdListParam.getIdList())) {
            return CollectionUtil.newArrayList();
        }
        LambdaQueryWrapper<WineProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 只查询部分字段
        lambdaQueryWrapper.select(WineProduct::getId, WineProduct::getImageUrl, WineProduct::getProductName,
                        WineProduct::getSortCode)
                .in(WineProduct::getId, wineProductIdListParam.getIdList()).orderByAsc(WineProduct::getSortCode);
        return this.list(lambdaQueryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineProductAddParam wineProductAddParam) {
        WineProduct wineProduct = BeanUtil.toBean(wineProductAddParam, WineProduct.class);
        this.save(wineProduct);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineProductEditParam wineProductEditParam) {
        WineProduct wineProduct = this.queryEntity(wineProductEditParam.getId());
        BeanUtil.copyProperties(wineProductEditParam, wineProduct);
        this.updateById(wineProduct);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineProductIdParam> wineProductIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineProductIdParamList, WineProductIdParam::getId));
    }

    @Override
    public WineProduct detail(WineProductIdParam wineProductIdParam) {
        return this.queryEntity(wineProductIdParam.getId());
    }

    @Override
    public WineProduct queryEntity(String id) {
        WineProduct wineProduct = this.getById(id);
        if(ObjectUtil.isEmpty(wineProduct)) {
            throw new CommonException("酒品列表不存在，id值为：{}", id);
        }
        return wineProduct;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineProductEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "酒品列表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineProductEditParam.class).sheet("酒品列表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 酒品列表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "酒品列表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineProductImportTemplate.xlsx"));
            // 读取excel
            List<WineProductEditParam> wineProductEditParamList =  EasyExcel.read(tempFile).head(WineProductEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineProduct> allDataList = this.list();
            for (int i = 0; i < wineProductEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineProductEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineProductEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 酒品列表导入失败：", e);
            throw new CommonException("酒品列表导入失败");
        }
    }

    public JSONObject doImport(List<WineProduct> allDataList, WineProductEditParam wineProductEditParam, int i) {
        String id = wineProductEditParam.getId();
        String categoryId = wineProductEditParam.getCategoryId();
        String productName = wineProductEditParam.getProductName();
        String productType = wineProductEditParam.getProductType();
        BigDecimal alcoholContent = wineProductEditParam.getAlcoholContent();
        BigDecimal costPrice = wineProductEditParam.getCostPrice();
        BigDecimal unitPrice = wineProductEditParam.getUnitPrice();
        String imageUrl = wineProductEditParam.getImageUrl();
        String status = wineProductEditParam.getStatus();
        if(ObjectUtil.hasEmpty(id, categoryId, productName, productType, alcoholContent, costPrice, unitPrice, imageUrl, status)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineProduct::getId).indexOf(wineProductEditParam.getId());
                WineProduct wineProduct;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineProduct = new WineProduct();
                } else {
                    wineProduct = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineProductEditParam, wineProduct);
                if(isAdd) {
                    allDataList.add(wineProduct);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineProduct);
                }
                this.saveOrUpdate(wineProduct);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineProductIdParam> wineProductIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineProductEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineProductIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineProductIdParamList, WineProductIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineProductEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineProductEditParam.class);
         }
         String fileName = "酒品列表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineProductEditParam.class).sheet("酒品列表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 酒品列表导出失败：", e);
         CommonResponseUtil.renderError(response, "酒品列表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
