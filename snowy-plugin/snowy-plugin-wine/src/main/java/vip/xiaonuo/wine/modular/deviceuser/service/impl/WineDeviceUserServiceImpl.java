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
package vip.xiaonuo.wine.modular.deviceuser.service.impl;

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
import vip.xiaonuo.wine.modular.deviceuser.entity.WineDeviceUser;
import vip.xiaonuo.wine.modular.deviceuser.mapper.WineDeviceUserMapper;
import vip.xiaonuo.wine.modular.deviceuser.param.*;
import vip.xiaonuo.wine.modular.deviceuser.service.WineDeviceUserService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.math.BigDecimal;
import java.util.Date;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.client.pojo.ClientUserInfo;
import vip.xiaonuo.wine.modular.deviceuser.vo.WineDeviceUserVO;
import vip.xiaonuo.wine.modular.devicerole.entity.WineDeviceRole;
import vip.xiaonuo.wine.modular.devicerole.service.WineDeviceRoleService;
import lombok.extern.slf4j.Slf4j;

/**
 * 分佣配置Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 08:44
 **/
@Slf4j
@Service
public class WineDeviceUserServiceImpl extends ServiceImpl<WineDeviceUserMapper, WineDeviceUser> implements WineDeviceUserService {

    @Resource
    private WineDeviceRoleService wineDeviceRoleService;

    @Resource
    private ClientApi clientApi;

    @Override
    public Page<WineDeviceUser> page(WineDeviceUserPageParam wineDeviceUserPageParam) {
        QueryWrapper<WineDeviceUser> queryWrapper = new QueryWrapper<WineDeviceUser>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(wineDeviceUserPageParam.getUserId())) {
            queryWrapper.lambda().like(WineDeviceUser::getUserId, wineDeviceUserPageParam.getUserId());
        }
        if(ObjectUtil.isNotEmpty(wineDeviceUserPageParam.getDeviceId())) {
            queryWrapper.lambda().like(WineDeviceUser::getDeviceId, wineDeviceUserPageParam.getDeviceId());
        }
        if(ObjectUtil.isAllNotEmpty(wineDeviceUserPageParam.getSortField(), wineDeviceUserPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineDeviceUserPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineDeviceUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineDeviceUserPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineDeviceUser::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineDeviceUserAddParam wineDeviceUserAddParam) {
        // 检查设备总佣金比例是否超过100%
        validateTotalCommissionRate(wineDeviceUserAddParam.getDeviceId(), null, wineDeviceUserAddParam.getCommissionRate());
        
        WineDeviceUser wineDeviceUser = BeanUtil.toBean(wineDeviceUserAddParam, WineDeviceUser.class);
        this.save(wineDeviceUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineDeviceUserEditParam wineDeviceUserEditParam) {
        WineDeviceUser wineDeviceUser = this.queryEntity(wineDeviceUserEditParam.getId());
        BeanUtil.copyProperties(wineDeviceUserEditParam, wineDeviceUser);
        this.updateById(wineDeviceUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineDeviceUserIdParam> wineDeviceUserIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineDeviceUserIdParamList, WineDeviceUserIdParam::getId));
    }

    @Override
    public WineDeviceUser detail(WineDeviceUserIdParam wineDeviceUserIdParam) {
        return this.queryEntity(wineDeviceUserIdParam.getId());
    }

    @Override
    public WineDeviceUser queryEntity(String id) {
        WineDeviceUser wineDeviceUser = this.getById(id);
        if(ObjectUtil.isEmpty(wineDeviceUser)) {
            throw new CommonException("分佣配置不存在，id值为：{}", id);
        }
        return wineDeviceUser;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineDeviceUserEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "分佣配置导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineDeviceUserEditParam.class).sheet("分佣配置").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 分佣配置导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "分佣配置导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineDeviceUserImportTemplate.xlsx"));
            // 读取excel
            List<WineDeviceUserEditParam> wineDeviceUserEditParamList =  EasyExcel.read(tempFile).head(WineDeviceUserEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineDeviceUser> allDataList = this.list();
            for (int i = 0; i < wineDeviceUserEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineDeviceUserEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineDeviceUserEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 分佣配置导入失败：", e);
            throw new CommonException("分佣配置导入失败");
        }
    }

    public JSONObject doImport(List<WineDeviceUser> allDataList, WineDeviceUserEditParam wineDeviceUserEditParam, int i) {
        String id = wineDeviceUserEditParam.getId();
        String userId = wineDeviceUserEditParam.getUserId();
        String deviceId = wineDeviceUserEditParam.getDeviceId();
        BigDecimal commissionRate = wineDeviceUserEditParam.getCommissionRate();
        if(ObjectUtil.hasEmpty(id, userId, deviceId, commissionRate)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineDeviceUser::getId).indexOf(wineDeviceUserEditParam.getId());
                WineDeviceUser wineDeviceUser;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineDeviceUser = new WineDeviceUser();
                } else {
                    wineDeviceUser = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineDeviceUserEditParam, wineDeviceUser);
                if(isAdd) {
                    allDataList.add(wineDeviceUser);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineDeviceUser);
                }
                this.saveOrUpdate(wineDeviceUser);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineDeviceUserIdParam> wineDeviceUserIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineDeviceUserEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineDeviceUserIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineDeviceUserIdParamList, WineDeviceUserIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineDeviceUserEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineDeviceUserEditParam.class);
         }
         String fileName = "分佣配置_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineDeviceUserEditParam.class).sheet("分佣配置").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 分佣配置导出失败：", e);
         CommonResponseUtil.renderError(response, "分佣配置导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    @Override
    public void setCommission(WineDeviceUserCommissionRateParam wineDeviceUserCommissionRateParam) {
        WineDeviceUser wineDeviceUser = this.queryEntity(wineDeviceUserCommissionRateParam.getId());
        
        // 检查设备总佣金比例是否超过100%
        BigDecimal newCommissionRate = wineDeviceUserCommissionRateParam.getCommissionRate();
        validateTotalCommissionRate(wineDeviceUser.getDeviceId(), wineDeviceUser.getId(), newCommissionRate);
        
        wineDeviceUser.setCommissionRate(newCommissionRate);
        this.updateById(wineDeviceUser);
    }
    
    @Override
    public BigDecimal getCommissionRate(String userId, String deviceId) {
        LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineDeviceUser::getUserId, userId)
                   .eq(WineDeviceUser::getDeviceId, deviceId);
        WineDeviceUser wineDeviceUser = this.getOne(queryWrapper);
        return wineDeviceUser != null ? wineDeviceUser.getCommissionRate() : null;
    }
    
    @Override
    public List<WineDeviceUser> getDeviceUsers(String deviceId) {
        LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineDeviceUser::getDeviceId, deviceId)
                   .eq(WineDeviceUser::getStatus, "ACTIVE") // 只查询激活状态的用户
                   .orderByDesc(WineDeviceUser::getCreateTime); // 按创建时间降序
        return this.list(queryWrapper);
    }
    
    @Override
    public List<WineDeviceUserVO> getDeviceUsersWithUserInfo(String deviceId) {
        // 先获取设备用户关系列表
        List<WineDeviceUser> deviceUsers = getDeviceUsers(deviceId);
        
        if (deviceUsers.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<WineDeviceUserVO> result = new ArrayList<>();
        for (WineDeviceUser deviceUser : deviceUsers) {
            WineDeviceUserVO vo = new WineDeviceUserVO();
            // 复制基础信息
            vo.setId(deviceUser.getId());
            vo.setUserId(deviceUser.getUserId());
            vo.setDeviceId(deviceUser.getDeviceId());
            vo.setCommissionRate(deviceUser.getCommissionRate());
            vo.setRemark(deviceUser.getRemark());
            vo.setCreateTime(deviceUser.getCreateTime());
            
            // 设置角色信息
            vo.setRoleCode(deviceUser.getRoleCode());
            vo.setRoleName(getRoleNameByCode(deviceUser.getRoleCode()));
            
            // 查询用户详细信息
            try {
                ClientUserInfo clientUserInfo = clientApi.getUserInfoById(deviceUser.getUserId());
                if (clientUserInfo != null) {
                    vo.setUserName(clientUserInfo.getName());
                    vo.setNickName(clientUserInfo.getNickname());
                    vo.setPhone(clientUserInfo.getPhone());
                    vo.setAvatar(clientUserInfo.getAvatar());
                    vo.setLastActiveTime(clientUserInfo.getLastLoginTime());
                } else {
                    // 如果获取用户信息失败，设置默认值
                    vo.setUserName("未知用户");
                    vo.setNickName("未知用户");
                    vo.setPhone("未知");
                }
            } catch (Exception e) {
                // 如果获取用户信息失败，设置默认值
                vo.setUserName("未知用户");
                vo.setNickName("未知用户");
                vo.setPhone("未知");
            }
            
            result.add(vo);
        }
        
        return result;
    }
    
    @Override
    public void unbindUser(String deviceId, String userId) {
        LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineDeviceUser::getDeviceId, deviceId)
                   .eq(WineDeviceUser::getUserId, userId);
        
        WineDeviceUser deviceUser = this.getOne(queryWrapper);
        if (deviceUser == null) {
            throw new CommonException("用户与设备的绑定关系不存在");
        }
        
        this.removeById(deviceUser.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindUserRole(WineDeviceUserBindRoleParam wineDeviceUserBindRoleParam) {
        // 检查用户是否已经在该设备上有绑定关系
        LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineDeviceUser::getDeviceId, wineDeviceUserBindRoleParam.getDeviceId())
                   .eq(WineDeviceUser::getUserId, wineDeviceUserBindRoleParam.getUserId());
        
        WineDeviceUser existingUser = this.getOne(queryWrapper);
        
        if (existingUser != null) {
            // 更新现有绑定关系的角色
            existingUser.setRoleCode(wineDeviceUserBindRoleParam.getRoleCode());
            existingUser.setCommissionRate(wineDeviceUserBindRoleParam.getCommissionRate());
            existingUser.setEffectiveTime(wineDeviceUserBindRoleParam.getEffectiveTime());
            existingUser.setExpireTime(wineDeviceUserBindRoleParam.getExpireTime());
            existingUser.setRemark(wineDeviceUserBindRoleParam.getRemark());
            existingUser.setStatus("ACTIVE");
            this.updateById(existingUser);
        } else {
            // 创建新的绑定关系
            WineDeviceUser newUser = new WineDeviceUser();
            newUser.setDeviceId(wineDeviceUserBindRoleParam.getDeviceId());
            newUser.setUserId(wineDeviceUserBindRoleParam.getUserId());
            newUser.setRoleCode(wineDeviceUserBindRoleParam.getRoleCode());
            newUser.setCommissionRate(wineDeviceUserBindRoleParam.getCommissionRate());
            newUser.setEffectiveTime(wineDeviceUserBindRoleParam.getEffectiveTime());
            newUser.setExpireTime(wineDeviceUserBindRoleParam.getExpireTime());
            newUser.setRemark(wineDeviceUserBindRoleParam.getRemark());
            newUser.setStatus("ACTIVE");
            this.save(newUser);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editUserRole(WineDeviceUserEditRoleParam wineDeviceUserEditRoleParam) {
        // 通过关系ID查询设备用户关系
        WineDeviceUser deviceUser = this.getById(wineDeviceUserEditRoleParam.getId());
        if (ObjectUtil.isNull(deviceUser)) {
            throw new CommonException("设备用户关系不存在");
        }
        
        // 验证佣金比例不超过100%（排除当前用户的原有佣金）
        BigDecimal totalCommission = this.list().stream()
            .filter(user -> !user.getId().equals(wineDeviceUserEditRoleParam.getId()) && 
                           user.getDeviceId().equals(deviceUser.getDeviceId()))
            .map(WineDeviceUser::getCommissionRate)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalCommission.add(wineDeviceUserEditRoleParam.getCommissionRate()).compareTo(new BigDecimal("100")) > 0) {
            throw new CommonException("佣金比例超过100%，当前剩余可分配比例：" + 
                new BigDecimal("100").subtract(totalCommission) + "%");
        }
        
        // 更新角色和佣金比例
        deviceUser.setRoleCode(wineDeviceUserEditRoleParam.getRoleCode());
        deviceUser.setCommissionRate(wineDeviceUserEditRoleParam.getCommissionRate());
        if (ObjectUtil.isNotEmpty(wineDeviceUserEditRoleParam.getRemark())) {
            deviceUser.setRemark(wineDeviceUserEditRoleParam.getRemark());
        }
        
        this.updateById(deviceUser);
        
        log.info("成功编辑设备用户角色，关系ID：{}，角色：{}，佣金比例：{}%", 
            wineDeviceUserEditRoleParam.getId(), wineDeviceUserEditRoleParam.getRoleCode(), 
            wineDeviceUserEditRoleParam.getCommissionRate());
    }

    @Override
    public List<WineDeviceUser> getUsersByDeviceAndRole(String deviceId, String roleCode) {
        LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineDeviceUser::getDeviceId, deviceId)
                   .eq(WineDeviceUser::getRoleCode, roleCode)
                   .eq(WineDeviceUser::getStatus, "ACTIVE");
        return this.list(queryWrapper);
    }

    @Override
    public List<String> getUserRolesForDevice(String userId, String deviceId) {
        LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineDeviceUser::getUserId, userId)
                   .eq(WineDeviceUser::getDeviceId, deviceId)
                   .eq(WineDeviceUser::getStatus, "ACTIVE")
                   .isNotNull(WineDeviceUser::getRoleCode);
        
        List<WineDeviceUser> deviceUsers = this.list(queryWrapper);
        return deviceUsers.stream()
                .map(WineDeviceUser::getRoleCode)
                .filter(ObjectUtil::isNotEmpty)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean hasRole(String userId, String deviceId, String roleCode) {
        LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineDeviceUser::getUserId, userId)
                   .eq(WineDeviceUser::getDeviceId, deviceId)
                   .eq(WineDeviceUser::getRoleCode, roleCode)
                   .eq(WineDeviceUser::getStatus, "ACTIVE");
        
        return this.count(queryWrapper) > 0;
    }

    /**
     * 验证设备总佣金比例是否超过100%
     * 
     * @param deviceId 设备ID
     * @param excludeId 排除的用户关系ID（用于编辑时排除当前编辑的记录）
     * @param newCommissionRate 新的佣金比例
     */
    private void validateTotalCommissionRate(String deviceId, String excludeId, BigDecimal newCommissionRate) {
        // 获取设备当前所有用户的佣金比例
        LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WineDeviceUser::getDeviceId, deviceId);
        
        // 如果是编辑操作，排除当前编辑的记录
        if (excludeId != null) {
            queryWrapper.ne(WineDeviceUser::getId, excludeId);
        }
        
        List<WineDeviceUser> existingUsers = this.list(queryWrapper);
        
        // 计算当前总佣金比例
        BigDecimal totalCommission = existingUsers.stream()
                .map(WineDeviceUser::getCommissionRate)
                .filter(rate -> rate != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 加上新的佣金比例
        if (newCommissionRate != null) {
            totalCommission = totalCommission.add(newCommissionRate);
        }
        
        // 检查是否超过100%
        if (totalCommission.compareTo(new BigDecimal("100")) > 0) {
            throw new CommonException("设备总佣金比例不能超过100%，当前总比例将达到{}%", totalCommission);
        }
    }
    
    /**
     * 根据角色编码获取角色名称
     * 
     * @param roleCode 角色编码
     * @return 角色名称
     */
    private String getRoleNameByCode(String roleCode) {
        if (roleCode == null || roleCode.isEmpty()) {
            return null;
        }
        
        try {
            // 优先从数据库中查询角色信息
            LambdaQueryWrapper<WineDeviceRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WineDeviceRole::getRoleCode, roleCode);
            queryWrapper.eq(WineDeviceRole::getStatus, "ENABLE"); // 只查询启用状态的角色
            
            WineDeviceRole deviceRole = wineDeviceRoleService.getOne(queryWrapper);
            if (deviceRole != null) {
                return deviceRole.getRoleName();
            }
        } catch (Exception e) {
            log.error("查询角色信息失败，roleCode: {}", roleCode, e);
        }
        
        // 如果数据库中没有找到角色信息，记录警告并返回未知角色
        log.warn("未在数据库中找到角色编码对应的角色信息: {}", roleCode);
        return "未知角色";
    }
    
    @Override
    public List<String> getDeviceIdsByUserRole(String userId, String roleCode) {
        try {
            LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WineDeviceUser::getUserId, userId);
            queryWrapper.eq(WineDeviceUser::getRoleCode, roleCode);
            queryWrapper.eq(WineDeviceUser::getStatus, "ACTIVE");
            
            List<WineDeviceUser> deviceUsers = this.list(queryWrapper);
            return deviceUsers.stream()
                    .map(WineDeviceUser::getDeviceId)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("根据用户角色获取设备列表失败，userId: {}, roleCode: {}", userId, roleCode, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<String> getUserIdsByDeviceRole(String deviceId, String roleCode) {
        try {
            LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WineDeviceUser::getDeviceId, deviceId);
            queryWrapper.eq(WineDeviceUser::getRoleCode, roleCode);
            queryWrapper.eq(WineDeviceUser::getStatus, "ACTIVE");
            
            List<WineDeviceUser> deviceUsers = this.list(queryWrapper);
            return deviceUsers.stream()
                    .map(WineDeviceUser::getUserId)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("根据设备角色获取用户列表失败，deviceId: {}, roleCode: {}", deviceId, roleCode, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean hasUserRole(String userId, String deviceId, String roleCode) {
        try {
            LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WineDeviceUser::getUserId, userId);
            queryWrapper.eq(WineDeviceUser::getDeviceId, deviceId);
            queryWrapper.eq(WineDeviceUser::getRoleCode, roleCode);
            queryWrapper.eq(WineDeviceUser::getStatus, "ACTIVE");
            
            return this.count(queryWrapper) > 0;
        } catch (Exception e) {
            log.error("检查用户角色权限失败，userId: {}, deviceId: {}, roleCode: {}", userId, deviceId, roleCode, e);
            return false;
        }
    }
    
    @Override
    public boolean hasUserWithRole(String deviceId, String roleCode) {
        try {
            LambdaQueryWrapper<WineDeviceUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WineDeviceUser::getDeviceId, deviceId);
            queryWrapper.eq(WineDeviceUser::getRoleCode, roleCode);
            queryWrapper.eq(WineDeviceUser::getStatus, "ACTIVE");
            
            return this.count(queryWrapper) > 0;
        } catch (Exception e) {
            log.error("检查设备角色用户失败，deviceId: {}, roleCode: {}", deviceId, roleCode, e);
            return false;
        }
    }
}
