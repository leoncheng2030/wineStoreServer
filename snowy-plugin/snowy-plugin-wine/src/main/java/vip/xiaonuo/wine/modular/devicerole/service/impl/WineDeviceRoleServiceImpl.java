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
package vip.xiaonuo.wine.modular.devicerole.service.impl;

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
import vip.xiaonuo.wine.modular.devicerole.entity.WineDeviceRole;
import vip.xiaonuo.wine.modular.devicerole.mapper.WineDeviceRoleMapper;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRoleAddParam;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRoleEditParam;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRoleIdParam;
import vip.xiaonuo.wine.modular.devicerole.param.WineDeviceRolePageParam;
import vip.xiaonuo.wine.modular.devicerole.service.WineDeviceRoleService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 设备角色定义表Service接口实现类
 *
 * @author jetox
 * @date  2025/09/21 09:16
 **/
@Service
public class WineDeviceRoleServiceImpl extends ServiceImpl<WineDeviceRoleMapper, WineDeviceRole> implements WineDeviceRoleService {

    @Override
    public Page<WineDeviceRole> page(WineDeviceRolePageParam wineDeviceRolePageParam) {
        QueryWrapper<WineDeviceRole> queryWrapper = new QueryWrapper<WineDeviceRole>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineDeviceRolePageParam.getRoleName())) {
            queryWrapper.lambda().like(WineDeviceRole::getRoleName, wineDeviceRolePageParam.getRoleName());
        }
        if(ObjectUtil.isAllNotEmpty(wineDeviceRolePageParam.getSortField(), wineDeviceRolePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineDeviceRolePageParam.getSortOrder());
            queryWrapper.orderBy(true, wineDeviceRolePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineDeviceRolePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineDeviceRole::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineDeviceRoleAddParam wineDeviceRoleAddParam) {
        WineDeviceRole wineDeviceRole = BeanUtil.toBean(wineDeviceRoleAddParam, WineDeviceRole.class);
        this.save(wineDeviceRole);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineDeviceRoleEditParam wineDeviceRoleEditParam) {
        WineDeviceRole wineDeviceRole = this.queryEntity(wineDeviceRoleEditParam.getId());
        BeanUtil.copyProperties(wineDeviceRoleEditParam, wineDeviceRole);
        this.updateById(wineDeviceRole);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineDeviceRoleIdParam> wineDeviceRoleIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineDeviceRoleIdParamList, WineDeviceRoleIdParam::getId));
    }

    @Override
    public WineDeviceRole detail(WineDeviceRoleIdParam wineDeviceRoleIdParam) {
        return this.queryEntity(wineDeviceRoleIdParam.getId());
    }

    @Override
    public WineDeviceRole queryEntity(String id) {
        WineDeviceRole wineDeviceRole = this.getById(id);
        if(ObjectUtil.isEmpty(wineDeviceRole)) {
            throw new CommonException("设备角色定义表不存在，id值为：{}", id);
        }
        return wineDeviceRole;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineDeviceRoleEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "设备角色定义表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineDeviceRoleEditParam.class).sheet("设备角色定义表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 设备角色定义表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "设备角色定义表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineDeviceRoleImportTemplate.xlsx"));
            // 读取excel
            List<WineDeviceRoleEditParam> wineDeviceRoleEditParamList =  EasyExcel.read(tempFile).head(WineDeviceRoleEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineDeviceRole> allDataList = this.list();
            for (int i = 0; i < wineDeviceRoleEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineDeviceRoleEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineDeviceRoleEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 设备角色定义表导入失败：", e);
            throw new CommonException("设备角色定义表导入失败");
        }
    }

    public JSONObject doImport(List<WineDeviceRole> allDataList, WineDeviceRoleEditParam wineDeviceRoleEditParam, int i) {
        String id = wineDeviceRoleEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineDeviceRole::getId).indexOf(wineDeviceRoleEditParam.getId());
                WineDeviceRole wineDeviceRole;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineDeviceRole = new WineDeviceRole();
                } else {
                    wineDeviceRole = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineDeviceRoleEditParam, wineDeviceRole);
                if(isAdd) {
                    allDataList.add(wineDeviceRole);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineDeviceRole);
                }
                this.saveOrUpdate(wineDeviceRole);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineDeviceRoleIdParam> wineDeviceRoleIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineDeviceRoleEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineDeviceRoleIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineDeviceRoleIdParamList, WineDeviceRoleIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineDeviceRoleEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineDeviceRoleEditParam.class);
         }
         String fileName = "设备角色定义表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineDeviceRoleEditParam.class).sheet("设备角色定义表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 设备角色定义表导出失败：", e);
         CommonResponseUtil.renderError(response, "设备角色定义表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
