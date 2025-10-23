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
package vip.xiaonuo.wine.modular.device.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaQrcodeServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaQrcode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
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
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.poi.hpsf.Decimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import oshi.driver.mac.net.NetStat;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.Date;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.mapper.WineDeviceMapper;
import vip.xiaonuo.wine.modular.device.param.*;
import vip.xiaonuo.wine.modular.device.result.DeviceControlResult;
import vip.xiaonuo.wine.modular.device.service.DeviceControlService;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserAddParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserIdParam;
import vip.xiaonuo.wine.modular.deviceuser.service.WineDeviceUserService;
import vip.xiaonuo.wine.modular.deviceuser.entity.WineDeviceUser;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserBindParam;
import vip.xiaonuo.wine.modular.device.param.WineDeviceBindAgentParam;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.wine.modular.agent.entity.WineAgent;
import vip.xiaonuo.wine.modular.agent.service.WineAgentService;
import vip.xiaonuo.wine.modular.product.entity.WineProduct;
import vip.xiaonuo.wine.modular.product.service.WineProductService;
import vip.xiaonuo.common.websocket.WebSocketMessageService;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.wine.modular.stockrecord.service.WineStockRecordService;
import vip.xiaonuo.wine.modular.stockrecord.enums.WineStockOperationTypeEnum;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 设备信息表Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 07:57
 **/
@Slf4j
@Service
public class WineDeviceServiceImpl extends ServiceImpl<WineDeviceMapper, WineDevice> implements WineDeviceService {
    @Resource
    private WineProductService wineProductService;
    @Resource
    private DeviceControlService deviceControlService;
    @Resource
    private WineDeviceUserService wineDeviceUserService;
    @Resource
    private ClientApi clientApi;
    @Resource
    private WineAgentService wineAgentService;
    @Resource
    private WebSocketMessageService webSocketMessageService;
    @Resource
    private DevConfigApi devConfigApi;
    @Resource
    private WineStockRecordService wineStockRecordService;
    @Override
    public Page<WineDevice> page(WineDevicePageParam wineDevicePageParam) {
        QueryWrapper<WineDevice> queryWrapper = new QueryWrapper<WineDevice>().checkSqlInjection();
        // 注意：移除了基于managerUserId的查询，现在通过角色系统管理
        // if (ObjectUtil.isNotNull(wineDevicePageParam.getUserId())){
        //     queryWrapper.lambda().eq(WineDevice::getManagerUserId, wineDevicePageParam.getUserId());
        // }
        if(ObjectUtil.isNotEmpty(wineDevicePageParam.getStoreId())) {
            queryWrapper.lambda().like(WineDevice::getStoreId, wineDevicePageParam.getStoreId());
        }
        if(ObjectUtil.isNotEmpty(wineDevicePageParam.getCurrentProductId())) {
            queryWrapper.lambda().like(WineDevice::getCurrentProductId, wineDevicePageParam.getCurrentProductId());
        }
        if(ObjectUtil.isNotEmpty(wineDevicePageParam.getDeviceCode())) {
            queryWrapper.lambda().like(WineDevice::getDeviceCode, wineDevicePageParam.getDeviceCode());
        }
        if(ObjectUtil.isNotEmpty(wineDevicePageParam.getAgentAccount())) {
            // 通过代理商编号查找对应的用户ID
            try {
                LambdaQueryWrapper<WineAgent> agentQueryWrapper = new QueryWrapper<WineAgent>()
                        .checkSqlInjection()
                        .lambda()
                        .eq(WineAgent::getAgentCode, wineDevicePageParam.getAgentAccount());
                WineAgent wineAgent = wineAgentService.getOne(agentQueryWrapper);
                if(wineAgent != null) {
                    queryWrapper.lambda().eq(WineDevice::getAgentUserId, wineAgent.getClientUserId());
                } else {
                    // 如果找不到对应的代理商，返回空结果
                    queryWrapper.lambda().eq(WineDevice::getAgentUserId, "-1");
                }
            } catch (Exception e) {
                // 如果查询出错，返回空结果
                queryWrapper.lambda().eq(WineDevice::getAgentUserId, "-1");
            }
        }
        if(ObjectUtil.isAllNotEmpty(wineDevicePageParam.getSortField(), wineDevicePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineDevicePageParam.getSortOrder());
            queryWrapper.orderBy(true, wineDevicePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineDevicePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(WineDevice::getDeviceCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public Page<WineDevice> agentPage(WineDeviceAgentPage wineDeviceAgentPage) {
        String agentId = StpClientLoginUserUtil.getClientLoginUser().getId();
        QueryWrapper<WineDevice> queryWrapper = new QueryWrapper<WineDevice>().checkSqlInjection();
        if (ObjectUtil.isNotNull(agentId)){
            queryWrapper.lambda().eq(WineDevice::getAgentUserId, agentId);
        }
        queryWrapper.lambda().orderByAsc(WineDevice::getDeviceCode);
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineDeviceAddParam wineDeviceAddParam) {
        WineDevice wineDevice = BeanUtil.toBean(wineDeviceAddParam, WineDevice.class);
        this.save(wineDevice);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineDeviceEditParam wineDeviceEditParam) {
        WineDevice wineDevice = this.queryEntity(wineDeviceEditParam.getId());
        BeanUtil.copyProperties(wineDeviceEditParam, wineDevice);
        this.updateById(wineDevice);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineDeviceIdParam> wineDeviceIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineDeviceIdParamList, WineDeviceIdParam::getId));
    }

    @Override
    public WineDevice detail(WineDeviceIdParam wineDeviceIdParam) {
        return this.queryEntity(wineDeviceIdParam.getId());
    }

    @Override
    public WineDevice queryEntity(String id) {
        WineDevice wineDevice = this.getById(id);
        if(ObjectUtil.isEmpty(wineDevice)) {
            throw new CommonException("设备信息表不存在，id值为：{}", id);
        }
        return wineDevice;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineDeviceEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "设备信息表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineDeviceEditParam.class).sheet("设备信息表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 设备信息表导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "设备信息表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineDeviceImportTemplate.xlsx"));
            // 读取excel
            List<WineDeviceEditParam> wineDeviceEditParamList =  EasyExcel.read(tempFile).head(WineDeviceEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineDevice> allDataList = this.list();
            for (int i = 0; i < wineDeviceEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineDeviceEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineDeviceEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 设备信息表导入失败：", e);
            throw new CommonException("设备信息表导入失败");
        }
    }

    public JSONObject doImport(List<WineDevice> allDataList, WineDeviceEditParam wineDeviceEditParam, int i) {
        String id = wineDeviceEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineDevice::getId).indexOf(wineDeviceEditParam.getId());
                WineDevice wineDevice;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    wineDevice = new WineDevice();
                } else {
                    wineDevice = allDataList.get(index);
                }
                BeanUtil.copyProperties(wineDeviceEditParam, wineDevice);
                if(isAdd) {
                    allDataList.add(wineDevice);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineDevice);
                }
                this.saveOrUpdate(wineDevice);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<WineDeviceIdParam> wineDeviceIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<WineDeviceEditParam> dataList;
         if(ObjectUtil.isNotEmpty(wineDeviceIdParamList)) {
            List<String> idList = CollStreamUtil.toList(wineDeviceIdParamList, WineDeviceIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), WineDeviceEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), WineDeviceEditParam.class);
         }
         String fileName = "设备信息表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), WineDeviceEditParam.class).sheet("设备信息表").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 设备信息表导出失败：", e);
         CommonResponseUtil.renderError(response, "设备信息表导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    @Override
    public WineProduct getProductInfo(WineDeviceIdParam wineDeviceIdParam) {
        WineDevice wineDevice = this.queryEntity(wineDeviceIdParam.getId());
        return wineProductService.getById(wineDevice.getCurrentProductId());
    }

    @Override
    public void reduceStock(String deviceId, Integer quantity) {
        WineDevice wineDevice = this.queryEntity(deviceId);
        Integer oldStock = wineDevice.getStock(); // 记录原库存
        wineDevice.setStock(wineDevice.getStock() - quantity);
        this.updateById(wineDevice);
        
        // 记录库存变更
        try {
            wineStockRecordService.recordStockChange(
                wineDevice.getId(),
                WineStockOperationTypeEnum.CONSUME.getValue(),
                oldStock,
                wineDevice.getStock(),
                null, // 系统自动消费，操作人为空
                "SYSTEM",
                "设备自动消费"
            );
        } catch (Exception e) {
            log.error("记录库存变更失败", e);
        }
        
        // 发送库存更新WebSocket推送
        sendStockUpdateWebSocket(wineDevice, oldStock, wineDevice.getStock(), "consumption");
        
        // 检查是否需要库存预警
        checkAndSendStockWarningWebSocket(wineDevice);
    }

    @Override
    public WineDevice addStock(WineDeviceStockParam wineDeviceStockParam) {
        WineDevice wineDevice = this.queryEntity(wineDeviceStockParam.getDeviceId());
        
        // 检查当前用户是否有设备管理权限
        if (!hasDeviceManagePermission(wineDevice.getId())) {
            throw new CommonException("当前登录的终端用户没有设备管理权限");
        }
        
        Integer oldStock = wineDevice.getStock(); // 记录原库存
        wineDevice.setStock(wineDevice.getStock() + wineDeviceStockParam.getStockNum());
        this.updateById(wineDevice);
        
        // 记录库存变更
        try {
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            
            // 判断操作人员类型
            String operatorType;
            String operatorRemark;
            
            // 检查是否是设备管理员
            boolean isDeviceManager = wineDeviceUserService.hasUserRole(currentUserId, wineDevice.getId(), "DEVICE_MANAGER");
            if (isDeviceManager) {
                operatorType = "DEVICE_MANAGER";
                operatorRemark = "设备管理员补货操作";
            } else if (currentUserId.equals(wineDevice.getAgentUserId())) {
                operatorType = "AGENT";
                operatorRemark = "代理商补货操作";
            } else {
                operatorType = "OTHER";
                operatorRemark = "其他用户补货操作";
            }
            
            log.info("记录库存变更: 设备ID={}, 操作人员ID={}, 操作人员类型={}, 库存变化: {} -> {}", 
                    wineDevice.getId(), currentUserId, operatorType, oldStock, wineDevice.getStock());
            
            wineStockRecordService.recordStockChange(
                wineDevice.getId(),
                WineStockOperationTypeEnum.RESTOCK.getValue(),
                oldStock,
                wineDevice.getStock(),
                currentUserId,
                operatorType,
                operatorRemark
            );
        } catch (Exception e) {
            log.error("记录库存变更失败", e);
        }
        
        // 发送库存更新WebSocket推送
        sendStockUpdateWebSocket(wineDevice, oldStock, wineDevice.getStock(), "restock");
        
        return wineDevice;
    }

    @Override
    public void updatePulseCount(WineDevicePulseRatioParam wineDevicePulseRatioParam) {
        WineDevice wineDevice = this.queryEntity(wineDevicePulseRatioParam.getId());
        // 检查当前用户是否有设备管理权限（通过角色权限验证）
        if (!hasDeviceManagePermission(wineDevice.getId())) {
            throw new CommonException("当前登录的终端用户没有设备管理权限");
        }
        wineDevice.setPulseRatio(wineDevicePulseRatioParam.getPulseRatio());
        this.updateById(wineDevice);
    }

    @Override
    public void unbind(WineDeviceUserIdParam wineDeviceUserIdParam) {
        // 通过关系ID查询设备用户关系，获取userId和deviceId
        WineDeviceUser deviceUser = wineDeviceUserService.getById(wineDeviceUserIdParam.getId());
        if (ObjectUtil.isNull(deviceUser)) {
            throw new CommonException("设备用户关系不存在");
        }
        
        // 查询设备信息用于权限检查
        WineDevice wineDevice = this.queryEntity(deviceUser.getDeviceId());
        // 检查当前用户是否有设备管理权限（通过角色权限验证）
        if (!hasDeviceManagePermission(wineDevice.getId())) {
            throw new CommonException("当前登录的终端用户没有设备管理权限");
        }
        
        // 解绑用户的角色关系
        wineDeviceUserService.unbindUser(deviceUser.getDeviceId(), deviceUser.getUserId());
        log.info("成功解绑用户{}与设备{}的关联关系", deviceUser.getUserId(), deviceUser.getDeviceId());
    }

    @Override
    public WineDevice detailByCode(String deviceCode) {
        return this.getOne(new QueryWrapper<WineDevice>().lambda().eq(WineDevice::getDeviceCode, deviceCode));
    }

    @Override
    public String createQrCode(WineDeviceIdParam wineDeviceIdParam) {
        WineDevice wineDevice = this.queryEntity(wineDeviceIdParam.getId());
        WxMaService wxMaService = SpringUtil.getBean(WxMaService.class);
        WxMaQrcodeServiceImpl wxMaQrcodeService = new WxMaQrcodeServiceImpl(wxMaService);
        try {
            return wxMaQrcodeService.createWxaCodeUnlimit("deviceCode="+wineDevice.getDeviceCode(), "pages/device/detail").getAbsolutePath();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDeviceControlCommand(DeviceControlParam deviceControlParam) {
        try {
            log.info("获取设备控制加密指令，设备ID：{}，订单号：{}", deviceControlParam.getDeviceCode(), deviceControlParam.getChargeId());

            // 1. 参数验证
            if (ObjectUtil.isNull(deviceControlParam.getDeviceCode())){
                throw new CommonException("设备Code不能为空");
            }
            if (ObjectUtil.isNull(deviceControlParam.getChargeId())){
                throw new CommonException("订单号不能为空");
            }

            // 3. 获取加密指令
            DeviceControlResult result = deviceControlService.getControlCommand(deviceControlParam);
            if (result == null || !result.isSuccess()) {
                log.warn("获取设备控制指令失败：{}", result != null ? result.getMessage() : "返回结果为空");
                return null;
            }

            log.info("设备控制加密指令获取成功，设备ID：{}，订单号：{}", deviceControlParam.getDeviceCode(), deviceControlParam.getChargeId());
            return result.getCmd();

        } catch (Exception e) {
            log.error("获取设备控制指令异常，设备ID：{}，订单号：{}", deviceControlParam.getDeviceCode(), deviceControlParam.getChargeId(), e);
            return null;
        }
    }

    @Override
    public String getDeviceTestControlCommand(DeviceTestControlParam deviceTestControlParam) {
        DeviceControlParam deviceControlParam = BeanUtil.copyProperties(deviceTestControlParam, DeviceControlParam.class);
        try {
            // 1. 参数验证
            if (ObjectUtil.isNull(deviceTestControlParam.getDeviceCode())){
                throw new CommonException("设备Code不能为空");
            }
            String s = String.valueOf(System.currentTimeMillis() / 1000);
            // 2. 获取订编号
            deviceControlParam.setChargeId(s);
            // 3. 获取加密指令
            DeviceControlResult result = deviceControlService.getControlCommand(deviceControlParam);
            if (result == null || !result.isSuccess()) {
                return null;
            }
            return result.getCmd();

        } catch (Exception e) {
            log.error("获取设备控制指令异常，设备ID：{}，订单号：{}", deviceTestControlParam.getDeviceCode(), deviceControlParam.getChargeId(), e);
            return null;
        }
    }

    @Override
    public WineDevice getDeviceByDeviceCode(String s) {
        return this.getOne(new QueryWrapper<WineDevice>().lambda().eq(WineDevice::getDeviceCode, s));
    }

    @Override
    public void bind(WineDeviceUserAddParam wineDeviceUserAddParam) {
        String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
        WineDevice deviceByDeviceCode = this.getDeviceByDeviceCode(wineDeviceUserAddParam.getDeviceCode());
        if (ObjectUtil.isNull(deviceByDeviceCode)) {
            throw new CommonException("设备不存在");
        }
        
        // 检查设备是否已经绑定了管理员角色
        boolean hasDeviceManager = wineDeviceUserService.hasUserWithRole(deviceByDeviceCode.getId(), "DEVICE_MANAGER");
        if (hasDeviceManager) {
            throw new CommonException("该设备已经绑定了设备管理员");
        }
        
        // 通过设备用户服务绑定用户为设备管理员
        WineDeviceUserAddParam addParam = new WineDeviceUserAddParam();
        addParam.setUserId(currentUserId);
        addParam.setDeviceId(deviceByDeviceCode.getId());
        addParam.setRoleCode("DEVICE_MANAGER");
        addParam.setCommissionRate(BigDecimal.ZERO); // 默认佣金为0
        
        wineDeviceUserService.add(addParam);
    }

    @Override
    public void updateInfo(String deviceId, String productId, String storeId, String manageId) {
        WineDevice device = this.queryEntity(deviceId);
        if (ObjectUtil.isNull(device)){
            throw new CommonException("设备不存在");
        }
        if (ObjectUtil.isNotEmpty(productId)){
            device.setCurrentProductId(productId);
        }
        if (ObjectUtil.isNotEmpty(storeId)){
            device.setStoreId(storeId);
        }
        // 通过角色系统管理设备管理员
        if (ObjectUtil.isNotEmpty(manageId)){
            // 检查该用户是否已经是设备管理员
            boolean isAlreadyManager = wineDeviceUserService.hasUserRole(manageId, deviceId, "DEVICE_MANAGER");
            if (!isAlreadyManager) {
                // 添加管理员角色绑定
                WineDeviceUserAddParam addParam = new WineDeviceUserAddParam();
                addParam.setUserId(manageId);
                addParam.setDeviceId(deviceId);
                addParam.setRoleCode("DEVICE_MANAGER");
                addParam.setCommissionRate(BigDecimal.ZERO); // 默认佣金为0
                wineDeviceUserService.add(addParam);
                log.info("成功为设备{}添加管理员{}", deviceId, manageId);
            }
        }
        this.updateById(device);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindDeviceUser(WineDeviceUserBindParam wineDeviceUserBindParam) {
        // 检查设备是否存在
        WineDevice device = this.queryEntity(wineDeviceUserBindParam.getDeviceId());
        if (ObjectUtil.isNull(device)) {
            throw new CommonException("设备不存在");
        }
        
        // 检查用户是否已经绑定该设备
        BigDecimal existingRate = wineDeviceUserService.getCommissionRate(
            wineDeviceUserBindParam.getUserId(), wineDeviceUserBindParam.getDeviceId());
        if (existingRate != null) {
            throw new CommonException("用户已经绑定该设备");
        }
        
        // 检查设备总佣金比例是否超过100%（使用驱委模式，调用设备用户服务的验证方法）
        WineDeviceUserAddParam addParam = new WineDeviceUserAddParam();
        addParam.setUserId(wineDeviceUserBindParam.getUserId());
        addParam.setDeviceId(wineDeviceUserBindParam.getDeviceId());
        addParam.setCommissionRate(wineDeviceUserBindParam.getCommissionRate());
        addParam.setRemark(wineDeviceUserBindParam.getRemark());
        
        // 调用设备用户服务的add方法，该方法已经包含了总佣金比例验证
        wineDeviceUserService.add(addParam);
        
        log.info("用户{}成功绑定设备{}，佣金比例：{}%", 
            wineDeviceUserBindParam.getUserId(), wineDeviceUserBindParam.getDeviceId(), wineDeviceUserBindParam.getCommissionRate());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindDeviceAgent(WineDeviceBindAgentParam wineDeviceBindAgentParam) {
        // 通过设备编码查找设备
        WineDevice device = this.detailByCode(wineDeviceBindAgentParam.getDeviceCode());
        if (ObjectUtil.isNull(device)) {
            throw new CommonException("设备不存在，设备编码：" + wineDeviceBindAgentParam.getDeviceCode());
        }
        
        // 检查设备是否已经有代理商
        if (StrUtil.isNotEmpty(device.getAgentUserId())) {
            throw new CommonException("设备已经绑定代理商，无法重复绑定");
        }
        
        // 检查用户是否为代理商
        boolean isAgent = clientApi.isUserAgent(wineDeviceBindAgentParam.getAgentUserId());
        if (!isAgent) {
            throw new CommonException("用户不是代理商，无法绑定设备");
        }
        
        // 更新设备的代理商ID和运营方式
        device.setAgentUserId(wineDeviceBindAgentParam.getAgentUserId());
        device.setOperationType("OPERATION_TYPE_AGENT"); // 设置为代理商运营模式
        if (StrUtil.isNotEmpty(wineDeviceBindAgentParam.getRemark())) {
            device.setRemark(wineDeviceBindAgentParam.getRemark());
        }
        
        this.updateById(device);
        
        log.info("代理商{}成功绑定设备{}，设备编码：{}", 
            wineDeviceBindAgentParam.getAgentUserId(), device.getId(), wineDeviceBindAgentParam.getDeviceCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAllDevicesPulseRatio(Double pulseRatio) {
        if (pulseRatio == null || pulseRatio <= 0) {
            throw new CommonException("脉冲比必须大于0");
        }
        
        // 获取所有设备
        List<WineDevice> allDevices = this.list();
        
        if (CollectionUtil.isNotEmpty(allDevices)) {
            // 批量更新脉冲比
            allDevices.forEach(device -> {
                device.setPulseRatio(pulseRatio);
            });
            
            // 批量保存
            this.updateBatchById(allDevices);
            
            log.info("成功更新{}个设备的脉冲比为：{}", allDevices.size(), pulseRatio);
        } else {
            log.warn("没有找到任何设备，无法更新脉冲比");
        }
    }
    
    /**
     * 发送库存更新WebSocket推送
     * 
     * @param device 设备信息
     * @param oldStock 原库存
     * @param newStock 新库存
     * @param updateReason 更新原因
     */
    private void sendStockUpdateWebSocket(WineDevice device, Integer oldStock, Integer newStock, String updateReason) {
        try {
            // 构造WebSocket消息
            Map<String, Object> websocketMessage = new HashMap<>();
            websocketMessage.put("type", "DEVICE_STOCK_UPDATE");
            websocketMessage.put("deviceId", device.getId());
            websocketMessage.put("deviceName", device.getDeviceName());
            websocketMessage.put("oldStock", oldStock);
            websocketMessage.put("newStock", newStock);
            websocketMessage.put("updateReason", updateReason);
            websocketMessage.put("storeId", device.getStoreId());
            websocketMessage.put("timestamp", System.currentTimeMillis());
            
            String messageJson = JSONUtil.toJsonStr(websocketMessage);
            
            // 现在通过角色系统获取设备管理员列表
            List<String> deviceManagerIds = wineDeviceUserService.getUserIdsByDeviceRole(device.getId(), "DEVICE_MANAGER");
            for (String managerId : deviceManagerIds) {
                String clientId = webSocketMessageService.getClientIdByLoginId(managerId);
                if (ObjectUtil.isNotEmpty(clientId)) {
                    webSocketMessageService.sendMessageToClient(clientId, messageJson);
                    log.info("已向设备管理员发送库存更新WebSocket消息，设备ID：{}，管理员ID：{}", device.getId(), managerId);
                }
            }
            
            // 推送给代理商
            if (ObjectUtil.isNotEmpty(device.getAgentUserId())) {
                String agentClientId = webSocketMessageService.getClientIdByLoginId(device.getAgentUserId());
                if (ObjectUtil.isNotEmpty(agentClientId)) {
                    webSocketMessageService.sendMessageToClient(agentClientId, messageJson);
                    log.info("已向代理商发送库存更新WebSocket消息，设备ID：{}，代理商ID：{}", device.getId(), device.getAgentUserId());
                }
            }
            
        } catch (Exception e) {
            log.error("发送库存更新WebSocket推送失败", e);
        }
    }

    /**
     * 检查并发送库存预警WebSocket推送
     * 
     * @param device 设备信息
     */
    private void checkAndSendStockWarningWebSocket(WineDevice device) {
        try {
            // 获取库存顤警阈值
            String stockWarningThresholdStr = devConfigApi.getValueByKey("STOCK_WARNING");
            int stockWarningThreshold = 100; // 默认阈值
            if (ObjectUtil.isNotEmpty(stockWarningThresholdStr)) {
                try {
                    stockWarningThreshold = Integer.parseInt(stockWarningThresholdStr);
                } catch (NumberFormatException e) {
                    log.warn("库存预警阈值配置不是有效数字，使用默认值100");
                }
            }
            
            // 检查是否需要预警
            if (device.getStock() < stockWarningThreshold) {
                sendStockWarningWebSocket(device, stockWarningThreshold);
            }
            
        } catch (Exception e) {
            log.error("检查库存预警失败", e);
        }
    }

    /**
     * 发送库存预警WebSocket推送
     * 
     * @param device 设备信息
     * @param warningThreshold 顤警阈值
     */
    private void sendStockWarningWebSocket(WineDevice device, Integer warningThreshold) {
        try {
            // 构造WebSocket消息
            Map<String, Object> websocketMessage = new HashMap<>();
            websocketMessage.put("type", "DEVICE_STOCK_WARNING");
            websocketMessage.put("deviceId", device.getId());
            websocketMessage.put("deviceName", device.getDeviceName());
            websocketMessage.put("currentStock", device.getStock());
            websocketMessage.put("warningThreshold", warningThreshold);
            websocketMessage.put("storeId", device.getStoreId());
            // 不再需要managerUserId字段，已通过角色系统管理
            websocketMessage.put("timestamp", System.currentTimeMillis());
            
            String messageJson = JSONUtil.toJsonStr(websocketMessage);
            
            // 现在通过角色系统获取设备管理员列表
            List<String> deviceManagerIds = wineDeviceUserService.getUserIdsByDeviceRole(device.getId(), "DEVICE_MANAGER");
            for (String managerId : deviceManagerIds) {
                String clientId = webSocketMessageService.getClientIdByLoginId(managerId);
                if (ObjectUtil.isNotEmpty(clientId)) {
                    webSocketMessageService.sendMessageToClient(clientId, messageJson);
                    log.info("已向设备管理员发送库存预警WebSocket消息，设备ID：{}，管理员ID：{}", device.getId(), managerId);
                }
            }
            
            // 推送给代理商
            if (ObjectUtil.isNotEmpty(device.getAgentUserId())) {
                String agentClientId = webSocketMessageService.getClientIdByLoginId(device.getAgentUserId());
                if (ObjectUtil.isNotEmpty(agentClientId)) {
                    webSocketMessageService.sendMessageToClient(agentClientId, messageJson);
                    log.info("已向代理商发送库存预警WebSocket消息，设备ID：{}，代理商ID：{}", device.getId(), device.getAgentUserId());
                }
            }
            
        } catch (Exception e) {
            log.error("发送库存预警WebSocket推送失败", e);
        }
    }
    
    /**
     * 检查当前用户是否有设备管理权限
     * @param deviceId 设备ID
     * @return 是否有权限
     */
    private boolean hasDeviceManagePermission(String deviceId) {
        try {
            String currentUserId = StpClientLoginUserUtil.getClientLoginUser().getId();
            
            // 1. 检查用户是否有设备管理员角色
            boolean isDeviceManager = wineDeviceUserService.hasUserRole(currentUserId, deviceId, "DEVICE_MANAGER");
            if (isDeviceManager) {
                log.debug("用户{}具有设备{}的DEVICE_MANAGER角色权限", currentUserId, deviceId);
                return true;
            }
            
            // 2. 检查用户是否是该设备的代理商
            WineDevice device = this.getById(deviceId);
            if (device != null && currentUserId.equals(device.getAgentUserId())) {
                log.debug("用户{}是设备{}的代理商，具有管理权限", currentUserId, deviceId);
                return true;
            }
            
            log.debug("用户{}没有设备{}的管理权限", currentUserId, deviceId);
            return false;
        } catch (Exception e) {
            log.error("检查设备管理权限失败", e);
            return false;
        }
    }
}
