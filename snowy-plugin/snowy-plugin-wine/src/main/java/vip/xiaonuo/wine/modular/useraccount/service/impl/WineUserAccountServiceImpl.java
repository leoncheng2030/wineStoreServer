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
package vip.xiaonuo.wine.modular.useraccount.service.impl;

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
import vip.xiaonuo.wine.modular.useraccount.entity.WineUserAccount;
import vip.xiaonuo.wine.modular.useraccount.mapper.WineUserAccountMapper;
import vip.xiaonuo.wine.modular.useraccount.param.WineUserAccountAddParam;
import vip.xiaonuo.wine.modular.useraccount.param.WineUserAccountEditParam;
import vip.xiaonuo.wine.modular.useraccount.param.WineUserAccountIdParam;
import vip.xiaonuo.wine.modular.useraccount.param.WineUserAccountPageParam;
import vip.xiaonuo.wine.modular.useraccount.service.WineUserAccountService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 账户列表Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 08:55
 **/
@Service
public class WineUserAccountServiceImpl extends ServiceImpl<WineUserAccountMapper, WineUserAccount> implements WineUserAccountService {

    @Override
    public Page<WineUserAccount> page(WineUserAccountPageParam wineUserAccountPageParam) {
        QueryWrapper<WineUserAccount> queryWrapper = new QueryWrapper<WineUserAccount>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineUserAccountPageParam.getUserId())) {
            queryWrapper.lambda().eq(WineUserAccount::getUserId, wineUserAccountPageParam.getUserId());
        }
        if (ObjectUtil.isNotEmpty(wineUserAccountPageParam.getUserPhone())){
            queryWrapper.lambda().like(WineUserAccount::getUserPhone, wineUserAccountPageParam.getUserPhone());
        }
        if(ObjectUtil.isAllNotEmpty(wineUserAccountPageParam.getSortField(), wineUserAccountPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineUserAccountPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineUserAccountPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineUserAccountPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineUserAccount::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineUserAccountAddParam wineUserAccountAddParam) {
        WineUserAccount wineUserAccount = BeanUtil.toBean(wineUserAccountAddParam, WineUserAccount.class);
        this.save(wineUserAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineUserAccountEditParam wineUserAccountEditParam) {
        WineUserAccount wineUserAccount = this.queryEntity(wineUserAccountEditParam.getId());
        BeanUtil.copyProperties(wineUserAccountEditParam, wineUserAccount);
        this.updateById(wineUserAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineUserAccountIdParam> wineUserAccountIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineUserAccountIdParamList, WineUserAccountIdParam::getId));
    }

    @Override
    public WineUserAccount detail(WineUserAccountIdParam wineUserAccountIdParam) {
        return this.queryEntity(wineUserAccountIdParam.getId());
    }

    @Override
    public WineUserAccount getUserInfo(String userId) {
        QueryWrapper<WineUserAccount> wineUserAccountQueryWrapper = new QueryWrapper<WineUserAccount>().checkSqlInjection();
        wineUserAccountQueryWrapper.lambda().eq(WineUserAccount::getUserId, userId);
        return this.getOne(wineUserAccountQueryWrapper);
    }

    @Override
    public WineUserAccount queryEntity(String id) {
        WineUserAccount wineUserAccount = this.getById(id);
        if(ObjectUtil.isEmpty(wineUserAccount)) {
            throw new CommonException("账户列表不存在，id值为：{}", id);
        }
        return wineUserAccount;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineUserAccountEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "账户列表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineUserAccountEditParam.class).sheet("账户列表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 账户列表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "账户列表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineUserAccountImportTemplate.xlsx"));
            // 读取excel
            List<WineUserAccountEditParam> wineUserAccountEditParamList =  EasyExcel.read(tempFile).head(WineUserAccountEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineUserAccount> allDataList = this.list();
            for (int i = 0; i < wineUserAccountEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineUserAccountEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineUserAccountEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 账户列表导入失败：", e);
            throw new CommonException("账户列表导入失败");
        }
    }

    public JSONObject doImport(List<WineUserAccount> allDataList, WineUserAccountEditParam wineUserAccountEditParam, int i) {
        String id = wineUserAccountEditParam.getId();
        String userId = wineUserAccountEditParam.getUserId();
        if(ObjectUtil.hasEmpty(id, userId)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineUserAccount::getId).indexOf(wineUserAccountEditParam.getId());
                WineUserAccount wineUserAccount;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineUserAccount = new WineUserAccount();
                } else {
                    wineUserAccount = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineUserAccountEditParam, wineUserAccount);
                if(isAdd) {
                    allDataList.add(wineUserAccount);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineUserAccount);
                }
                this.saveOrUpdate(wineUserAccount);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineUserAccountIdParam> wineUserAccountIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineUserAccountEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineUserAccountIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineUserAccountIdParamList, WineUserAccountIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineUserAccountEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineUserAccountEditParam.class);
         }
         String fileName = "账户列表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineUserAccountEditParam.class).sheet("账户列表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 账户列表导出失败：", e);
         CommonResponseUtil.renderError(response, "账户列表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    @Override
    public WineUserAccount getAccountInfo(String id) {
        QueryWrapper<WineUserAccount> wineUserAccountQueryWrapper = new QueryWrapper<WineUserAccount>().checkSqlInjection();
        wineUserAccountQueryWrapper.lambda().eq(WineUserAccount::getUserId,id);
        return this.getOne(wineUserAccountQueryWrapper);
    }
}
