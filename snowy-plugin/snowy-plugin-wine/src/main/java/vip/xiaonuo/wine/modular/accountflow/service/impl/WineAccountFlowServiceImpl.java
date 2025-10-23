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
package vip.xiaonuo.wine.modular.accountflow.service.impl;

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
import vip.xiaonuo.wine.modular.accountflow.entity.WineAccountFlow;
import vip.xiaonuo.wine.modular.accountflow.mapper.WineAccountFlowMapper;
import vip.xiaonuo.wine.modular.accountflow.param.WineAccountFlowAddParam;
import vip.xiaonuo.wine.modular.accountflow.param.WineAccountFlowEditParam;
import vip.xiaonuo.wine.modular.accountflow.param.WineAccountFlowIdParam;
import vip.xiaonuo.wine.modular.accountflow.param.WineAccountFlowPageParam;
import vip.xiaonuo.wine.modular.accountflow.service.WineAccountFlowService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;
import vip.xiaonuo.wine.modular.device.param.WineDevicePageParam;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 账户流水Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 08:56
 **/
@Service
public class WineAccountFlowServiceImpl extends ServiceImpl<WineAccountFlowMapper, WineAccountFlow> implements WineAccountFlowService {

    @Override
    public Page<WineAccountFlow> page(WineAccountFlowPageParam wineAccountFlowPageParam) {
        QueryWrapper<WineAccountFlow> queryWrapper = new QueryWrapper<WineAccountFlow>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineAccountFlowPageParam.getUserId())) {
            queryWrapper.lambda().like(WineAccountFlow::getUserId, wineAccountFlowPageParam.getUserId());
        }
        if (ObjectUtil.isNotNull(wineAccountFlowPageParam.getUserId())){
            queryWrapper.lambda().like(WineAccountFlow::getUserId, wineAccountFlowPageParam.getUserId());
        }
        if (ObjectUtil.isNotEmpty(wineAccountFlowPageParam.getStatus())){
            if (wineAccountFlowPageParam.getStatus().equals("income")){
                queryWrapper.lambda().gt(WineAccountFlow::getBalanceChange, 0);
            }
            if (wineAccountFlowPageParam.getStatus().equals("expend")){
                queryWrapper.lambda().lt(WineAccountFlow::getBalanceChange, 0);
            }
        }
        if(ObjectUtil.isAllNotEmpty(wineAccountFlowPageParam.getSortField(), wineAccountFlowPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineAccountFlowPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineAccountFlowPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineAccountFlowPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(WineAccountFlow::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineAccountFlowAddParam wineAccountFlowAddParam) {
        WineAccountFlow wineAccountFlow = BeanUtil.toBean(wineAccountFlowAddParam, WineAccountFlow.class);
        this.save(wineAccountFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineAccountFlowEditParam wineAccountFlowEditParam) {
        WineAccountFlow wineAccountFlow = this.queryEntity(wineAccountFlowEditParam.getId());
        BeanUtil.copyProperties(wineAccountFlowEditParam, wineAccountFlow);
        this.updateById(wineAccountFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineAccountFlowIdParam> wineAccountFlowIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineAccountFlowIdParamList, WineAccountFlowIdParam::getId));
    }

    @Override
    public WineAccountFlow detail(WineAccountFlowIdParam wineAccountFlowIdParam) {
        return this.queryEntity(wineAccountFlowIdParam.getId());
    }

    @Override
    public WineAccountFlow queryEntity(String id) {
        WineAccountFlow wineAccountFlow = this.getById(id);
        if(ObjectUtil.isEmpty(wineAccountFlow)) {
            throw new CommonException("账户流水不存在，id值为：{}", id);
        }
        return wineAccountFlow;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineAccountFlowEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "账户流水导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineAccountFlowEditParam.class).sheet("账户流水").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 账户流水导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "账户流水导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineAccountFlowImportTemplate.xlsx"));
            // 读取excel
            List<WineAccountFlowEditParam> wineAccountFlowEditParamList =  EasyExcel.read(tempFile).head(WineAccountFlowEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineAccountFlow> allDataList = this.list();
            for (int i = 0; i < wineAccountFlowEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineAccountFlowEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineAccountFlowEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 账户流水导入失败：", e);
            throw new CommonException("账户流水导入失败");
        }
    }

    public JSONObject doImport(List<WineAccountFlow> allDataList, WineAccountFlowEditParam wineAccountFlowEditParam, int i) {
        String id = wineAccountFlowEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineAccountFlow::getId).indexOf(wineAccountFlowEditParam.getId());
                WineAccountFlow wineAccountFlow;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineAccountFlow = new WineAccountFlow();
                } else {
                    wineAccountFlow = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineAccountFlowEditParam, wineAccountFlow);
                if(isAdd) {
                    allDataList.add(wineAccountFlow);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineAccountFlow);
                }
                this.saveOrUpdate(wineAccountFlow);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineAccountFlowIdParam> wineAccountFlowIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineAccountFlowEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineAccountFlowIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineAccountFlowIdParamList, WineAccountFlowIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineAccountFlowEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineAccountFlowEditParam.class);
         }
         String fileName = "账户流水_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineAccountFlowEditParam.class).sheet("账户流水").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 账户流水导出失败：", e);
         CommonResponseUtil.renderError(response, "账户流水导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
