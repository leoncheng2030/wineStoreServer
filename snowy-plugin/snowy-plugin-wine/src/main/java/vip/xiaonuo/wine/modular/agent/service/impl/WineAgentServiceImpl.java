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
package vip.xiaonuo.wine.modular.agent.service.impl;

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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.wine.modular.agentapply.entity.WineAgentApply;
import vip.xiaonuo.wine.modular.agentapply.service.WineAgentApplyService;
import java.math.BigDecimal;
import java.util.Date;
import vip.xiaonuo.wine.modular.agent.dto.WineAgentDetailDto;
import vip.xiaonuo.wine.modular.agent.entity.WineAgent;
import vip.xiaonuo.wine.modular.agent.mapper.WineAgentMapper;
import vip.xiaonuo.wine.modular.agent.param.WineAgentAddParam;
import vip.xiaonuo.wine.modular.agent.param.WineAgentEditParam;
import vip.xiaonuo.wine.modular.agent.param.WineAgentIdParam;
import vip.xiaonuo.wine.modular.agent.param.WineAgentPageParam;
import vip.xiaonuo.wine.modular.agent.service.WineAgentService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * wine_agentService接口实现类
 *
 * @author jetox
 * @date  2025/09/19 07:13
 **/
@Service
public class WineAgentServiceImpl extends ServiceImpl<WineAgentMapper, WineAgent> implements WineAgentService {

    @jakarta.annotation.Resource
    private WineAgentApplyService wineAgentApplyService;

    @Override
    public Page<WineAgent> page(WineAgentPageParam wineAgentPageParam) {
        QueryWrapper<WineAgent> queryWrapper = new QueryWrapper<WineAgent>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineAgentPageParam.getClientUserId())) {
            queryWrapper.lambda().like(WineAgent::getClientUserId, wineAgentPageParam.getClientUserId());
        }
        if(ObjectUtil.isNotEmpty(wineAgentPageParam.getAgentCode())) {
            queryWrapper.lambda().like(WineAgent::getAgentCode, wineAgentPageParam.getAgentCode());
        }
        if(ObjectUtil.isNotEmpty(wineAgentPageParam.getSubMerId())) {
            queryWrapper.lambda().like(WineAgent::getSubMerId, wineAgentPageParam.getSubMerId());
        }
        if(ObjectUtil.isAllNotEmpty(wineAgentPageParam.getSortField(), wineAgentPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineAgentPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineAgentPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineAgentPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineAgent::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineAgentAddParam wineAgentAddParam) {
        WineAgent wineAgent = BeanUtil.toBean(wineAgentAddParam, WineAgent.class);
        this.save(wineAgent);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineAgentEditParam wineAgentEditParam) {
        WineAgent wineAgent = this.queryEntity(wineAgentEditParam.getId());
        BeanUtil.copyProperties(wineAgentEditParam, wineAgent);
        this.updateById(wineAgent);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineAgentIdParam> wineAgentIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineAgentIdParamList, WineAgentIdParam::getId));
    }

    @Override
    public WineAgent detail(WineAgentIdParam wineAgentIdParam) {
        return this.queryEntity(wineAgentIdParam.getId());
    }

    @Override
    public WineAgent queryEntity(String id) {
        WineAgent wineAgent = this.getById(id);
        if(ObjectUtil.isEmpty(wineAgent)) {
            throw new CommonException("wine_agent不存在，id值为：{}", id);
        }
        return wineAgent;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineAgentEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "wine_agent导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineAgentEditParam.class).sheet("wine_agent").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> wine_agent导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "wine_agent导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineAgentImportTemplate.xlsx"));
            // 读取excel
            List<WineAgentEditParam> wineAgentEditParamList =  EasyExcel.read(tempFile).head(WineAgentEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineAgent> allDataList = this.list();
            for (int i = 0; i < wineAgentEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineAgentEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineAgentEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> wine_agent导入失败：", e);
            throw new CommonException("wine_agent导入失败");
        }
    }

    public JSONObject doImport(List<WineAgent> allDataList, WineAgentEditParam wineAgentEditParam, int i) {
        String id = wineAgentEditParam.getId();
        String clientUserId = wineAgentEditParam.getClientUserId();
        String agentCode = wineAgentEditParam.getAgentCode();
        if(ObjectUtil.hasEmpty(id, clientUserId, agentCode)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineAgent::getId).indexOf(wineAgentEditParam.getId());
                WineAgent wineAgent;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineAgent = new WineAgent();
                } else {
                    wineAgent = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineAgentEditParam, wineAgent);
                if(isAdd) {
                    allDataList.add(wineAgent);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineAgent);
                }
                this.saveOrUpdate(wineAgent);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineAgentIdParam> wineAgentIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineAgentEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineAgentIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineAgentIdParamList, WineAgentIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineAgentEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineAgentEditParam.class);
         }
         String fileName = "wine_agent_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineAgentEditParam.class).sheet("wine_agent").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> wine_agent导出失败：", e);
         CommonResponseUtil.renderError(response, "wine_agent导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
    @Override
    public BigDecimal queryMaxProfitSharingRatio(String subMchId) {
        QueryWrapper<WineAgent> wineAgentQueryWrapper = new QueryWrapper<WineAgent>().checkSqlInjection();
        wineAgentQueryWrapper.lambda().eq(WineAgent::getSubMerId, subMchId);
        WineAgent wineAgent = this.getOne(wineAgentQueryWrapper);
        return wineAgent.getProfitSharingMaxRate();
    }

    @Override
    public WineAgent detailByUserId(String userId) {
        QueryWrapper<WineAgent> queryWrapper = new QueryWrapper<WineAgent>().checkSqlInjection();
        queryWrapper.lambda().eq(WineAgent::getClientUserId, userId);
        WineAgent wineAgent = this.getOne(queryWrapper);
        if(ObjectUtil.isEmpty(wineAgent)) {
            throw new CommonException("代理商不存在，用户ID值为：{}", userId);
        }
        return wineAgent;
    }

    @Override
    public WineAgentDetailDto getAgentDetailWithApplyInfo(String userId) {
        WineAgentDetailDto detailDto = new WineAgentDetailDto();
        
        // 获取代理商信息
        QueryWrapper<WineAgent> agentQueryWrapper = new QueryWrapper<WineAgent>().checkSqlInjection();
        agentQueryWrapper.lambda().eq(WineAgent::getClientUserId, userId);
        WineAgent wineAgent = this.getOne(agentQueryWrapper);
        if(ObjectUtil.isEmpty(wineAgent)) {
            throw new CommonException("代理商不存在，用户ID值为：{}", userId);
        }
        detailDto.setAgentInfo(wineAgent);
        
        // 获取代理商申请信息
        QueryWrapper<WineAgentApply> applyQueryWrapper = new QueryWrapper<WineAgentApply>().checkSqlInjection();
        applyQueryWrapper.lambda().eq(WineAgentApply::getClientUserId, userId);
        applyQueryWrapper.lambda().orderByDesc(WineAgentApply::getCreateTime);
        WineAgentApply wineAgentApply = wineAgentApplyService.getOne(applyQueryWrapper, false);
        detailDto.setApplyInfo(wineAgentApply);
        
        return detailDto;
    }

    @Override
    public void setSubMerId(String clientUserId, String subMerId) {
        QueryWrapper<WineAgent> queryWrapper = new QueryWrapper<WineAgent>().checkSqlInjection();
        queryWrapper.lambda().eq(WineAgent::getClientUserId, clientUserId);
        WineAgent wineAgent = this.getOne(queryWrapper);
        wineAgent.setSubMerId(subMerId);
        this.updateById(wineAgent);
    }
}
