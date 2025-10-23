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
package vip.xiaonuo.wine.modular.productcategory.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
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
import vip.xiaonuo.wine.modular.productcategory.entity.WineProductCategory;
import vip.xiaonuo.wine.modular.productcategory.mapper.WineProductCategoryMapper;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryAddParam;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryEditParam;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryIdParam;
import vip.xiaonuo.wine.modular.productcategory.param.WineProductCategoryPageParam;
import vip.xiaonuo.wine.modular.productcategory.service.WineProductCategoryService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 酒品分类表Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 08:11
 **/
@Service
public class WineProductCategoryServiceImpl extends ServiceImpl<WineProductCategoryMapper, WineProductCategory> implements WineProductCategoryService {

    @Override
    public Page<WineProductCategory> page(WineProductCategoryPageParam wineProductCategoryPageParam) {
        QueryWrapper<WineProductCategory> queryWrapper = new QueryWrapper<WineProductCategory>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineProductCategoryPageParam.getCategoryName())) {
            queryWrapper.lambda().like(WineProductCategory::getCategoryName, wineProductCategoryPageParam.getCategoryName());
        }
        if(ObjectUtil.isNotEmpty(wineProductCategoryPageParam.getStatus())) {
            queryWrapper.lambda().eq(WineProductCategory::getStatus, wineProductCategoryPageParam.getStatus());
        }
        if(ObjectUtil.isAllNotEmpty(wineProductCategoryPageParam.getSortField(), wineProductCategoryPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineProductCategoryPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineProductCategoryPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineProductCategoryPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineProductCategory::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<Tree<String>> tree() {
        List<WineProductCategory> wineProductCategoryList = this.list();
        List<TreeNode<String>> treeNodeList = wineProductCategoryList.stream().map(wineProductCategory ->
                        new TreeNode<>(wineProductCategory.getId(), wineProductCategory.getParentId(),
                                wineProductCategory.getCategoryName(), wineProductCategory.getSortCode()).setExtra(JSONUtil.parseObj(wineProductCategory)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineProductCategoryAddParam wineProductCategoryAddParam) {
        WineProductCategory wineProductCategory = BeanUtil.toBean(wineProductCategoryAddParam, WineProductCategory.class);
        this.save(wineProductCategory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineProductCategoryEditParam wineProductCategoryEditParam) {
        WineProductCategory wineProductCategory = this.queryEntity(wineProductCategoryEditParam.getId());
        BeanUtil.copyProperties(wineProductCategoryEditParam, wineProductCategory);
        this.updateById(wineProductCategory);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineProductCategoryIdParam> wineProductCategoryIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineProductCategoryIdParamList, WineProductCategoryIdParam::getId));
    }

    @Override
    public WineProductCategory detail(WineProductCategoryIdParam wineProductCategoryIdParam) {
        return this.queryEntity(wineProductCategoryIdParam.getId());
    }

    @Override
    public WineProductCategory queryEntity(String id) {
        WineProductCategory wineProductCategory = this.getById(id);
        if(ObjectUtil.isEmpty(wineProductCategory)) {
            throw new CommonException("酒品分类表不存在，id值为：{}", id);
        }
        return wineProductCategory;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineProductCategoryEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "酒品分类表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineProductCategoryEditParam.class).sheet("酒品分类表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 酒品分类表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "酒品分类表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineProductCategoryImportTemplate.xlsx"));
            // 读取excel
            List<WineProductCategoryEditParam> wineProductCategoryEditParamList =  EasyExcel.read(tempFile).head(WineProductCategoryEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineProductCategory> allDataList = this.list();
            for (int i = 0; i < wineProductCategoryEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineProductCategoryEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineProductCategoryEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 酒品分类表导入失败：", e);
            throw new CommonException("酒品分类表导入失败");
        }
    }

    public JSONObject doImport(List<WineProductCategory> allDataList, WineProductCategoryEditParam wineProductCategoryEditParam, int i) {
        String id = wineProductCategoryEditParam.getId();
        String categoryName = wineProductCategoryEditParam.getCategoryName();
        String parentId = wineProductCategoryEditParam.getParentId();
        String status = wineProductCategoryEditParam.getStatus();
        if(ObjectUtil.hasEmpty(id, categoryName, parentId, status)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineProductCategory::getId).indexOf(wineProductCategoryEditParam.getId());
                WineProductCategory wineProductCategory;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineProductCategory = new WineProductCategory();
                } else {
                    wineProductCategory = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineProductCategoryEditParam, wineProductCategory);
                if(isAdd) {
                    allDataList.add(wineProductCategory);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineProductCategory);
                }
                this.saveOrUpdate(wineProductCategory);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineProductCategoryIdParam> wineProductCategoryIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineProductCategoryEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineProductCategoryIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineProductCategoryIdParamList, WineProductCategoryIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineProductCategoryEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineProductCategoryEditParam.class);
         }
         String fileName = "酒品分类表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineProductCategoryEditParam.class).sheet("酒品分类表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 酒品分类表导出失败：", e);
         CommonResponseUtil.renderError(response, "酒品分类表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
