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
import cn.hutool.extra.qrcode.QrCodeUtil;
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
import vip.xiaonuo.wine.modular.store.entity.WineStoreUser;
import vip.xiaonuo.wine.modular.store.mapper.WineStoreUserMapper;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserAddParam;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserEditParam;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserIdParam;
import vip.xiaonuo.wine.modular.store.param.WineStoreUserPageParam;
import vip.xiaonuo.wine.modular.store.service.WineStoreUserService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 店员表Service接口实现类
 *
 * @author jetox
 * @date  2025/08/20 19:08
 **/
@Service
public class WineStoreUserServiceImpl extends ServiceImpl<WineStoreUserMapper, WineStoreUser> implements WineStoreUserService {

    @Override
    public Page<WineStoreUser> page(WineStoreUserPageParam wineStoreUserPageParam) {
        QueryWrapper<WineStoreUser> queryWrapper = new QueryWrapper<WineStoreUser>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineStoreUserPageParam.getStoreId())) {
            queryWrapper.lambda().eq(WineStoreUser::getStoreId, wineStoreUserPageParam.getStoreId());
        }
        if(ObjectUtil.isNotEmpty(wineStoreUserPageParam.getClientUserId())) {
            queryWrapper.lambda().eq(WineStoreUser::getClientUserId, wineStoreUserPageParam.getClientUserId());
        }
        if(ObjectUtil.isNotEmpty(wineStoreUserPageParam.getClientUserName())) {
            queryWrapper.lambda().like(WineStoreUser::getClientUserName, wineStoreUserPageParam.getClientUserName());
        }
        if(ObjectUtil.isNotEmpty(wineStoreUserPageParam.getClientUserPhone())) {
            queryWrapper.lambda().like(WineStoreUser::getClientUserPhone, wineStoreUserPageParam.getClientUserPhone());
        }
        if(ObjectUtil.isNotEmpty(wineStoreUserPageParam.getStatus())) {
            queryWrapper.lambda().eq(WineStoreUser::getStatus, wineStoreUserPageParam.getStatus());
        }
        if(ObjectUtil.isAllNotEmpty(wineStoreUserPageParam.getSortField(), wineStoreUserPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineStoreUserPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineStoreUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineStoreUserPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineStoreUser::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineStoreUserAddParam wineStoreUserAddParam) {
        WineStoreUser wineStoreUser = BeanUtil.toBean(wineStoreUserAddParam, WineStoreUser.class);
        this.save(wineStoreUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineStoreUserEditParam wineStoreUserEditParam) {
        WineStoreUser wineStoreUser = this.queryEntity(wineStoreUserEditParam.getId());
        BeanUtil.copyProperties(wineStoreUserEditParam, wineStoreUser);
        this.updateById(wineStoreUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineStoreUserIdParam> wineStoreUserIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineStoreUserIdParamList, WineStoreUserIdParam::getId));
    }

    @Override
    public WineStoreUser detail(WineStoreUserIdParam wineStoreUserIdParam) {
        return this.queryEntity(wineStoreUserIdParam.getId());
    }

    @Override
    public WineStoreUser queryEntity(String id) {
        WineStoreUser wineStoreUser = this.getById(id);
        if(ObjectUtil.isEmpty(wineStoreUser)) {
            throw new CommonException("店员表不存在，id值为：{}", id);
        }
        return wineStoreUser;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineStoreUserEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "店员表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineStoreUserEditParam.class).sheet("店员表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 店员表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "店员表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineStoreUserImportTemplate.xlsx"));
            // 读取excel
            List<WineStoreUserEditParam> wineStoreUserEditParamList =  EasyExcel.read(tempFile).head(WineStoreUserEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineStoreUser> allDataList = this.list();
            for (int i = 0; i < wineStoreUserEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineStoreUserEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineStoreUserEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 店员表导入失败：", e);
            throw new CommonException("店员表导入失败");
        }
    }

    public JSONObject doImport(List<WineStoreUser> allDataList, WineStoreUserEditParam wineStoreUserEditParam, int i) {
        String id = wineStoreUserEditParam.getId();
        String storeId = wineStoreUserEditParam.getStoreId();
        String clientUserId = wineStoreUserEditParam.getClientUserId();
        String clientUserName = wineStoreUserEditParam.getClientUserName();
        String clientUserPhone = wineStoreUserEditParam.getClientUserPhone();
        String status = wineStoreUserEditParam.getStatus();
        if(ObjectUtil.hasEmpty(id, storeId, clientUserId, clientUserName, clientUserPhone, status)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineStoreUser::getId).indexOf(wineStoreUserEditParam.getId());
                WineStoreUser wineStoreUser;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineStoreUser = new WineStoreUser();
                } else {
                    wineStoreUser = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineStoreUserEditParam, wineStoreUser);
                if(isAdd) {
                    allDataList.add(wineStoreUser);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineStoreUser);
                }
                this.saveOrUpdate(wineStoreUser);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineStoreUserIdParam> wineStoreUserIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineStoreUserEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineStoreUserIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineStoreUserIdParamList, WineStoreUserIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineStoreUserEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineStoreUserEditParam.class);
         }
         String fileName = "店员表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineStoreUserEditParam.class).sheet("店员表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 店员表导出失败：", e);
         CommonResponseUtil.renderError(response, "店员表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
    @Override
    public void becomeClerk(String storeId, String clientUserId) {
        WineStoreUser wineStoreUser = new WineStoreUser();
        wineStoreUser.setStoreId(storeId);
        wineStoreUser.setClientUserId(clientUserId);
        this.save(wineStoreUser);
    }
    @Override
    public void removeClerk(String storeId, String clientUserId) {
        this.remove(new QueryWrapper<WineStoreUser>().checkSqlInjection().lambda()
                .eq(WineStoreUser::getStoreId, storeId)
                .eq(WineStoreUser::getClientUserId, clientUserId));
    }
}
