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
package vip.xiaonuo.dbs.modular.database.service.impl;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import vip.xiaonuo.dbs.modular.database.entity.ExtDatabase;
import vip.xiaonuo.dbs.modular.database.mapper.ExtDatabaseMapper;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabaseAddParam;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabaseEditParam;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabaseIdParam;
import vip.xiaonuo.dbs.modular.database.param.ExtDatabasePageParam;
import vip.xiaonuo.dbs.modular.database.service.ExtDatabaseService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 数据源Service接口实现类
 *
 * @author jetox
 * @date  2025/07/28 00:35
 **/
@Service
public class ExtDatabaseServiceImpl extends ServiceImpl<ExtDatabaseMapper, ExtDatabase> implements ExtDatabaseService {

    @Override
    public Page<ExtDatabase> page(ExtDatabasePageParam extDatabasePageParam) {
        QueryWrapper<ExtDatabase> queryWrapper = new QueryWrapper<ExtDatabase>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(extDatabasePageParam.getPoolName())) {
            queryWrapper.lambda().like(ExtDatabase::getPoolName, extDatabasePageParam.getPoolName());
        }
        if(ObjectUtil.isNotEmpty(extDatabasePageParam.getDriverName())) {
            queryWrapper.lambda().eq(ExtDatabase::getDriverName, extDatabasePageParam.getDriverName());
        }
        if(ObjectUtil.isAllNotEmpty(extDatabasePageParam.getSortField(), extDatabasePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(extDatabasePageParam.getSortOrder());
            queryWrapper.orderBy(true, extDatabasePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(extDatabasePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ExtDatabase::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ExtDatabaseAddParam extDatabaseAddParam) {
        ExtDatabase extDatabase = BeanUtil.toBean(extDatabaseAddParam, ExtDatabase.class);
        if(this.count(new LambdaQueryWrapper<ExtDatabase>().eq(ExtDatabase::getPoolName, extDatabase.getPoolName())) > 0) {
            throw new CommonException("存在重复的名称，值为：{}", extDatabase.getPoolName());
        }
        if(this.count(new LambdaQueryWrapper<ExtDatabase>().eq(ExtDatabase::getDriverName, extDatabase.getDriverName())) > 0) {
            throw new CommonException("存在重复的驱动名称，值为：{}", extDatabase.getDriverName());
        }
        // 保存前对密码进行加密
        extDatabase.setEncryptedPassword(extDatabase.getPassword());
        // 测试数据库连接（使用解密后的密码）
        if (!testConnection(extDatabase)) {
            throw new CommonException("数据库连接测试失败，请检查连接配置");
        }
        this.save(extDatabase);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ExtDatabaseEditParam extDatabaseEditParam) {
        ExtDatabase extDatabase = this.queryEntity(extDatabaseEditParam.getId());
        BeanUtil.copyProperties(extDatabaseEditParam, extDatabase);
        if(this.count(new LambdaQueryWrapper<ExtDatabase>().ne(ExtDatabase::getId, extDatabase.getId()).eq(ExtDatabase::getPoolName, extDatabase.getPoolName())) > 0) {
            throw new CommonException("存在重复的名称，值为：{}", extDatabase.getPoolName());
        }
        if(this.count(new LambdaQueryWrapper<ExtDatabase>().ne(ExtDatabase::getId, extDatabase.getId()).eq(ExtDatabase::getDriverName, extDatabase.getDriverName())) > 0) {
            throw new CommonException("存在重复的驱动名称，值为：{}", extDatabase.getDriverName());
        }
        // 如果密码有更新，则进行加密
        if (extDatabaseEditParam.getPassword() != null) {
            extDatabase.setEncryptedPassword(extDatabaseEditParam.getPassword());
        }
        // 测试数据库连接（使用解密后的密码）
        if (!testConnection(extDatabase)) {
            throw new CommonException("数据库连接测试失败，请检查连接配置");
        }
        this.updateById(extDatabase);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ExtDatabaseIdParam> extDatabaseIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(extDatabaseIdParamList, ExtDatabaseIdParam::getId));
    }

    @Override
    public ExtDatabase detail(ExtDatabaseIdParam extDatabaseIdParam) {
        return this.queryEntity(extDatabaseIdParam.getId());
    }

    @Override
    public ExtDatabase queryEntity(String id) {
        ExtDatabase extDatabase = this.getById(id);
        if(ObjectUtil.isEmpty(extDatabase)) {
            throw new CommonException("数据源不存在，id值为：{}", id);
        }
        return extDatabase;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<ExtDatabaseEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "数据源导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), ExtDatabaseEditParam.class).sheet("数据源").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 数据源导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "数据源导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "extDatabaseImportTemplate.xlsx"));
            // 读取excel
            List<ExtDatabaseEditParam> extDatabaseEditParamList =  EasyExcel.read(tempFile).head(ExtDatabaseEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<ExtDatabase> allDataList = this.list();
            for (int i = 0; i < extDatabaseEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, extDatabaseEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", extDatabaseEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 数据源导入失败：", e);
            throw new CommonException("数据源导入失败");
        }
    }
    public JSONObject doImport(List<ExtDatabase> allDataList, ExtDatabaseEditParam extDatabaseEditParam, int i) {
        String id = extDatabaseEditParam.getId();
        String poolName = extDatabaseEditParam.getPoolName();
        String url = extDatabaseEditParam.getUrl();
        String username = extDatabaseEditParam.getUsername();
        String password = extDatabaseEditParam.getPassword();
        String driverName = extDatabaseEditParam.getDriverName();
        if(ObjectUtil.hasEmpty(id, poolName, url, username, password, driverName)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, ExtDatabase::getId).indexOf(extDatabaseEditParam.getId());
                ExtDatabase extDatabase;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    extDatabase = new ExtDatabase();
                } else {
                    extDatabase = allDataList.get(index);
                }
                if(isAdd) {
                    boolean repeatPoolName = allDataList.stream().anyMatch(tempData -> ObjectUtil
                            .isNotEmpty(tempData.getPoolName()) && tempData.getPoolName().equals(extDatabaseEditParam.getPoolName()));
                    if(repeatPoolName) {
                        return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "新增数据时字段【名称（poolName）】与数据库中数据重复，值为：" + extDatabaseEditParam.getPoolName());
                    }
                    boolean repeatDriverName = allDataList.stream().anyMatch(tempData -> ObjectUtil
                            .isNotEmpty(tempData.getDriverName()) && tempData.getDriverName().equals(extDatabaseEditParam.getDriverName()));
                    if(repeatDriverName) {
                        return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "新增数据时字段【驱动名称（driverName）】与数据库中数据重复，值为：" + extDatabaseEditParam.getDriverName());
                    }
                } else {
                    boolean repeatPoolName = allDataList.stream().anyMatch(tempData -> ObjectUtil
                            .isNotEmpty(tempData.getPoolName()) && tempData.getPoolName()
                            .equals(extDatabaseEditParam.getPoolName()) && !tempData.getId().equals(extDatabase.getId()));
                    if(repeatPoolName) {
                        return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "更新数据时字段【名称（poolName）】与数据库中数据重复，值为：" + extDatabaseEditParam.getPoolName());
                    }
                    boolean repeatDriverName = allDataList.stream().anyMatch(tempData -> ObjectUtil
                            .isNotEmpty(tempData.getDriverName()) && tempData.getDriverName()
                            .equals(extDatabaseEditParam.getDriverName()) && !tempData.getId().equals(extDatabase.getId()));
                    if(repeatDriverName) {
                        return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "更新数据时字段【驱动名称（driverName）】与数据库中数据重复，值为：" + extDatabaseEditParam.getDriverName());
                    }
                }
                BeanUtil.copyProperties(extDatabaseEditParam, extDatabase);
                // 对导入的密码进行加密
                extDatabase.setEncryptedPassword(extDatabase.getPassword());
                if(isAdd) {
                    allDataList.add(extDatabase);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, extDatabase);
                }
                this.saveOrUpdate(extDatabase);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
                log.error(">>> 数据导入异常：", e);
                return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }


    @Override
    public void exportData(List<ExtDatabaseIdParam> extDatabaseIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<ExtDatabaseEditParam> dataList;
         if(ObjectUtil.isNotEmpty(extDatabaseIdParamList)) {
            List<String> idList = CollStreamUtil.toList(extDatabaseIdParamList, ExtDatabaseIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), ExtDatabaseEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), ExtDatabaseEditParam.class);
         }
         String fileName = "数据源_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), ExtDatabaseEditParam.class).sheet("数据源").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 数据源导出失败：", e);
         CommonResponseUtil.renderError(response, "数据源导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
    /**
     * 测试数据库连接
     *
     * @param extDatabase 数据源实体
     * @return 连接是否成功
     */
    private boolean testConnection(ExtDatabase extDatabase) {
        try {
            // 获取数据库连接URL、用户名和密码
            String url = extDatabase.getUrl();
            String username = extDatabase.getUsername();
            // 使用解密后的密码进行连接测试
            String password = extDatabase.getDecryptedPassword();
            String driverName = extDatabase.getDriverName();

            // 加载数据库驱动
            Class.forName(driverName);

            // 建立数据库连接
            Connection connection = DriverManager.getConnection(url, username, password);

            // 如果连接成功，关闭连接并返回true
            if (connection != null && !connection.isClosed()) {
                connection.close();
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            // 如果发生异常，记录日志并返回false
            log.error(">>> 数据库连接测试失败：", e);
        }
        return false;
    }
}
