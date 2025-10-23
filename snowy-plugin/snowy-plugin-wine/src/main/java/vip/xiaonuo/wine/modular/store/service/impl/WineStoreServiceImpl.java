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
package vip.xiaonuo.wine.modular.store.service.impl;

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
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import vip.xiaonuo.wine.modular.store.entity.WineStore;
import vip.xiaonuo.wine.modular.store.entity.WineStoreUser;
import vip.xiaonuo.wine.modular.store.mapper.WineStoreMapper;
import vip.xiaonuo.wine.modular.store.param.*;
import vip.xiaonuo.wine.modular.store.service.WineStoreService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;
import vip.xiaonuo.wine.modular.store.service.WineStoreUserService;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 门店管理表Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 08:01
 **/
@Service
public class WineStoreServiceImpl extends ServiceImpl<WineStoreMapper, WineStore> implements WineStoreService {

    @Resource
    private WineStoreUserService wineStoreUserService;

    @Override
    public Page<WineStore> page(WineStorePageParam wineStorePageParam) {
        QueryWrapper<WineStore> queryWrapper = new QueryWrapper<WineStore>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineStorePageParam.getStoreName())) {
            queryWrapper.lambda().like(WineStore::getStoreName, wineStorePageParam.getStoreName());
        }
        if(ObjectUtil.isNotEmpty(wineStorePageParam.getAgentClientUserId())) {
            queryWrapper.lambda().like(WineStore::getAgentClientUserId, wineStorePageParam.getAgentClientUserId());
        }
        if (ObjectUtil.isNotEmpty(wineStorePageParam.getManagerId())){
            queryWrapper.lambda().eq(WineStore::getStoreManagerId, wineStorePageParam.getManagerId());
        }
        if(ObjectUtil.isAllNotEmpty(wineStorePageParam.getSortField(), wineStorePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineStorePageParam.getSortOrder());
            queryWrapper.orderBy(true, wineStorePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineStorePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineStore::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<WineStore> idList(WineStoreIdListParam wineStoreIdListParam) {
        if (ObjectUtil.isEmpty(wineStoreIdListParam.getIdList())) {
            return CollectionUtil.newArrayList();
        }
        LambdaQueryWrapper<WineStore> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 只查询部分字段
        lambdaQueryWrapper.select(WineStore::getId, WineStore::getStoreName,
                        WineStore::getSortCode)
                .in(WineStore::getId, wineStoreIdListParam.getIdList()).orderByAsc(WineStore::getSortCode);
        return this.list(lambdaQueryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineStoreAddParam wineStoreAddParam) {
        WineStore wineStore = BeanUtil.toBean(wineStoreAddParam, WineStore.class);
        this.save(wineStore);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineStoreEditParam wineStoreEditParam) {
        WineStore wineStore = this.queryEntity(wineStoreEditParam.getId());
        BeanUtil.copyProperties(wineStoreEditParam, wineStore);
        this.updateById(wineStore);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineStoreIdParam> wineStoreIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineStoreIdParamList, WineStoreIdParam::getId));
    }

    @Override
    public WineStore detail(WineStoreIdParam wineStoreIdParam) {
        return this.queryEntity(wineStoreIdParam.getId());
    }

    @Override
    public WineStore queryEntity(String id) {
        WineStore wineStore = this.getById(id);
        if(ObjectUtil.isEmpty(wineStore)) {
            throw new CommonException("门店管理表不存在，id值为：{}", id);
        }
        return wineStore;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineStoreEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "门店管理表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineStoreEditParam.class).sheet("门店管理表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 门店管理表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "门店管理表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineStoreImportTemplate.xlsx"));
            // 读取excel
            List<WineStoreEditParam> wineStoreEditParamList =  EasyExcel.read(tempFile).head(WineStoreEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineStore> allDataList = this.list();
            for (int i = 0; i < wineStoreEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineStoreEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineStoreEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 门店管理表导入失败：", e);
            throw new CommonException("门店管理表导入失败");
        }
    }

    public JSONObject doImport(List<WineStore> allDataList, WineStoreEditParam wineStoreEditParam, int i) {
        String id = wineStoreEditParam.getId();
        String storeName = wineStoreEditParam.getStoreName();
        if(ObjectUtil.hasEmpty(id, storeName)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineStore::getId).indexOf(wineStoreEditParam.getId());
                WineStore wineStore;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineStore = new WineStore();
                } else {
                    wineStore = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineStoreEditParam, wineStore);
                if(isAdd) {
                    allDataList.add(wineStore);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineStore);
                }
                this.saveOrUpdate(wineStore);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineStoreIdParam> wineStoreIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineStoreEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineStoreIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineStoreIdParamList, WineStoreIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineStoreEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineStoreEditParam.class);
         }
         String fileName = "门店管理表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineStoreEditParam.class).sheet("门店管理表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 门店管理表导出失败：", e);
         CommonResponseUtil.renderError(response, "门店管理表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
    @Override
    public Page<WineStore> nearby(WineStoreNearbyParam wineStoreNearbyParam) {
        QueryWrapper<WineStore> queryWrapper = new QueryWrapper<WineStore>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineStoreNearbyParam.getStoreName())) {
            queryWrapper.lambda().like(WineStore::getStoreName, wineStoreNearbyParam.getStoreName());
        }
        // 如果传入了经纬度，则查询附近的门店
        if (wineStoreNearbyParam.getLongitude() != null && wineStoreNearbyParam.getLatitude() != null) {
            try {
                // 将字符串转换为BigDecimal
                BigDecimal longitude = new BigDecimal(wineStoreNearbyParam.getLongitude());
                BigDecimal latitude = new BigDecimal(wineStoreNearbyParam.getLatitude());

                // 使用MySQL的空间函数筛选5公里范围内的门店，并按距离排序
                queryWrapper.apply("ST_Distance_Sphere(POINT({0},{1}), POINT(longitude, latitude)) <= 5000", longitude, latitude)
                        .isNotNull("longitude")
                        .isNotNull("latitude")
                        .orderByAsc("ST_Distance_Sphere(POINT(" + longitude + "," + latitude + "), POINT(longitude, latitude))");
            } catch (NumberFormatException e) {
                // 如果经纬度格式不正确，则按默认方式查询
                queryWrapper.lambda().eq(WineStore::getStatus, "ENABLE");
            }
        } else {
            // 如果没有传入经纬度，则按默认方式查询
            queryWrapper.lambda().eq(WineStore::getStatus, "ENABLE");
        }

        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<WineStoreUser> list(WineStoreIdParam wineStoreIdParam) {
        QueryWrapper<WineStoreUser> wineStoreUserQueryWrapper = new QueryWrapper<WineStoreUser>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineStoreIdParam.getId())) {
            wineStoreUserQueryWrapper.lambda().eq(WineStoreUser::getStoreId, wineStoreIdParam.getId());
        }
        return wineStoreUserService.list(wineStoreUserQueryWrapper);
    }

}
