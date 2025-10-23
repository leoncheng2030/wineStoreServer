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
package vip.xiaonuo.dev.modular.district.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
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
import vip.xiaonuo.dev.modular.district.entity.DevDistrict;
import vip.xiaonuo.dev.modular.district.mapper.DevDistrictMapper;
import vip.xiaonuo.dev.modular.district.param.DevDistrictAddParam;
import vip.xiaonuo.dev.modular.district.param.DevDistrictEditParam;
import vip.xiaonuo.dev.modular.district.param.DevDistrictIdParam;
import vip.xiaonuo.dev.modular.district.param.DevDistrictPageParam;
import vip.xiaonuo.dev.modular.district.service.DevDistrictService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地区表Service接口实现类
 *
 * @author jetox
 * @date  2025/08/12 07:53
 **/
@Service
public class DevDistrictServiceImpl extends ServiceImpl<DevDistrictMapper, DevDistrict> implements DevDistrictService {

    @Override
    public Page<DevDistrict> page(DevDistrictPageParam devDistrictPageParam) {
        QueryWrapper<DevDistrict> queryWrapper = new QueryWrapper<DevDistrict>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(devDistrictPageParam.getParentId())) {
            queryWrapper.lambda().eq(DevDistrict::getParentId, devDistrictPageParam.getParentId());
        }
        if(ObjectUtil.isNotEmpty(devDistrictPageParam.getLevel())) {
            queryWrapper.lambda().eq(DevDistrict::getLevel, devDistrictPageParam.getLevel());
        }
        if(ObjectUtil.isNotEmpty(devDistrictPageParam.getName())) {
            queryWrapper.lambda().like(DevDistrict::getName, devDistrictPageParam.getName());
        }
        if(ObjectUtil.isNotEmpty(devDistrictPageParam.getShortName())) {
            queryWrapper.lambda().like(DevDistrict::getShortName, devDistrictPageParam.getShortName());
        }
        if(ObjectUtil.isAllNotEmpty(devDistrictPageParam.getSortField(), devDistrictPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(devDistrictPageParam.getSortOrder());
            queryWrapper.orderBy(true, devDistrictPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(devDistrictPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(DevDistrict::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<Tree<String>> tree() {
        List<DevDistrict> devDistricts = this.list();
        List<TreeNode<String>> treeNodeList = devDistricts.stream().map(devDistrict ->
                        new TreeNode<String>(devDistrict.getId(), String.valueOf(devDistrict.getParentId()),
                                devDistrict.getName(), devDistrict.getSortCode()).setExtra(JSONUtil.parseObj(devDistrict)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(DevDistrictAddParam devDistrictAddParam) {
        DevDistrict devDistrict = BeanUtil.toBean(devDistrictAddParam, DevDistrict.class);
        this.save(devDistrict);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(DevDistrictEditParam devDistrictEditParam) {
        DevDistrict devDistrict = this.queryEntity(devDistrictEditParam.getId());
        BeanUtil.copyProperties(devDistrictEditParam, devDistrict);
        this.updateById(devDistrict);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<DevDistrictIdParam> devDistrictIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(devDistrictIdParamList, DevDistrictIdParam::getId));
    }

    @Override
    public DevDistrict detail(DevDistrictIdParam devDistrictIdParam) {
        return this.queryEntity(devDistrictIdParam.getId());
    }

    @Override
    public DevDistrict queryEntity(String id) {
        DevDistrict devDistrict = this.getById(id);
        if(ObjectUtil.isEmpty(devDistrict)) {
            throw new CommonException("地区表不存在，id值为：{}", id);
        }
        return devDistrict;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<DevDistrictEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "地区表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), DevDistrictEditParam.class).sheet("地区表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 地区表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "地区表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "devDistrictImportTemplate.xlsx"));
            // 读取excel
            List<DevDistrictEditParam> devDistrictEditParamList =  EasyExcel.read(tempFile).head(DevDistrictEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<DevDistrict> allDataList = this.list();
            for (int i = 0; i < devDistrictEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, devDistrictEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", devDistrictEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 地区表导入失败：", e);
            throw new CommonException("地区表导入失败");
        }
    }

    public JSONObject doImport(List<DevDistrict> allDataList, DevDistrictEditParam devDistrictEditParam, int i) {
        String id = devDistrictEditParam.getId();
        String parentId = devDistrictEditParam.getParentId();
        Boolean level = devDistrictEditParam.getLevel();
        String name = devDistrictEditParam.getName();
        String shortName = devDistrictEditParam.getShortName();
        String cityCode = devDistrictEditParam.getCityCode();
        if(ObjectUtil.hasEmpty(id, parentId, level, name, shortName, cityCode)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, DevDistrict::getId).indexOf(devDistrictEditParam.getId());
                DevDistrict devDistrict;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    devDistrict = new DevDistrict();
                } else {
                    devDistrict = allDataList.get(index);
                }
                BeanUtil.copyProperties(devDistrictEditParam, devDistrict);
                if(isAdd) {
                    allDataList.add(devDistrict);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, devDistrict);
                }
                this.saveOrUpdate(devDistrict);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<DevDistrictIdParam> devDistrictIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<DevDistrictEditParam> dataList;
         if(ObjectUtil.isNotEmpty(devDistrictIdParamList)) {
            List<String> idList = CollStreamUtil.toList(devDistrictIdParamList, DevDistrictIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), DevDistrictEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), DevDistrictEditParam.class);
         }
         String fileName = "地区表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), DevDistrictEditParam.class).sheet("地区表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 地区表导出失败：", e);
         CommonResponseUtil.renderError(response, "地区表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
