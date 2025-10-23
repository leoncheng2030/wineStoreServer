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
package vip.xiaonuo.wine.modular.order.service.impl;
import cn.binarywang.wx.miniapp.api.WxMaOrderShippingService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaShopAccountService;
import cn.binarywang.wx.miniapp.api.impl.WxMaOrderShippingServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaShopAccountServiceImpl;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.OrderKeyBean;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.PayerBean;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.ShippingListBean;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.WxMaOrderShippingInfoUploadRequest;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
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
import lombok.val;
import me.chanjar.weixin.common.error.WxErrorException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.auth.core.util.StpClientLoginUserUtil;
import vip.xiaonuo.client.ClientApi;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import vip.xiaonuo.pay.modular.wx.param.RefundParam;
import vip.xiaonuo.pay.modular.wx.service.PayWxService;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.wine.modular.accountflow.entity.WineAccountFlow;
import vip.xiaonuo.wine.modular.accountflow.service.WineAccountFlowService;
import vip.xiaonuo.wine.modular.commissionrecord.entity.WineCommissionRecord;
import vip.xiaonuo.wine.modular.commissionrecord.service.WineCommissionRecordService;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.service.WineDeviceService;
import vip.xiaonuo.wine.modular.order.entity.WineOrder;
import vip.xiaonuo.wine.modular.order.enums.WineOrderProfitSharingStatusEnum;
import vip.xiaonuo.wine.modular.order.enums.WineOrderStatusEnum;
import vip.xiaonuo.wine.modular.order.mapper.WineOrderMapper;
import vip.xiaonuo.wine.modular.order.param.*;
import vip.xiaonuo.wine.modular.order.service.WineOrderService;
import vip.xiaonuo.wine.modular.order.service.WineOrderProfitSharingService;
import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;
import vip.xiaonuo.wine.modular.product.entity.WineProduct;
import vip.xiaonuo.wine.modular.product.service.WineProductService;
import vip.xiaonuo.wine.modular.store.entity.WineStore;
import vip.xiaonuo.wine.modular.store.service.WineStoreService;
import vip.xiaonuo.wine.modular.useraccount.entity.WineUserAccount;
import vip.xiaonuo.wine.modular.useraccount.service.WineUserAccountService;
import java.io.File;
import java.io.IOException;
import java.util.List;
/**
 * 订单表Service接口实现类
 *
 * @author jetox
 * @date  2025/07/24 08:47
 **/
@Slf4j
@Service
public class WineOrderServiceImpl extends ServiceImpl<WineOrderMapper, WineOrder> implements WineOrderService {
    @Resource
    private WineProductService wineProductService;
    /**
     * 酒类设备服务
     */
    @Resource
    private WineDeviceService wineDeviceService;
    /**
     * 微信支付服务
     */
    @Resource
    private PayWxService payWxService;
    /**
     * 微信小程序服务
     */
    @Resource
    private WineCommissionRecordService wineCommissionRecordService;
    @Resource
    private WineStoreService wineStoreService;
    @Resource
    private DevConfigApi devConfigApi;
    @Resource
    private ClientApi clientApi;
    @Override
    public Page<WineOrder> page(WineOrderPageParam wineOrderPageParam) {
        QueryWrapper<WineOrder> queryWrapper = new QueryWrapper<WineOrder>().checkSqlInjection();
        if (ObjectUtil.isNotEmpty(wineOrderPageParam.getOrderNo())) {
            queryWrapper.lambda().eq(WineOrder::getOrderNo, wineOrderPageParam.getOrderNo());
        }
        if (ObjectUtil.isNotEmpty(wineOrderPageParam.getUserId())) {
            queryWrapper.lambda().eq(WineOrder::getUserId, wineOrderPageParam.getUserId());
        }
        if (ObjectUtil.isNotEmpty(wineOrderPageParam.getStatus())) {
            if (!wineOrderPageParam.getStatus().equals("ALL")) {
                queryWrapper.lambda().eq(WineOrder::getStatus, wineOrderPageParam.getStatus());
            }
        }
        if (ObjectUtil.isAllNotEmpty(wineOrderPageParam.getSortField(), wineOrderPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineOrderPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineOrderPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineOrderPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(WineOrder::getCreateTime);
        }
        val page = this.page(CommonPageRequest.defaultPage(), queryWrapper);
        page.getRecords().forEach(wineOrder -> {
            WineDevice wineDevice = wineDeviceService.queryEntity(wineOrder.getDeviceId());
            WineStore wineStore  = wineStoreService.queryEntity(wineDevice.getStoreId());
            wineOrder.setDeviceCode(wineDevice.getDeviceCode());
            wineOrder.setStoreName(wineStore.getStoreName());
        });
        return page;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WineOrderAddParam wineOrderAddParam) {
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        WineOrder wineOrder = BeanUtil.toBean(wineOrderAddParam, WineOrder.class);
        wineOrder.setUserId(id);
        WineProduct wineProduct = wineProductService.queryEntity(wineOrderAddParam.getProductId());
        wineOrder.setProductName(wineProduct.getProductName());
        wineOrder.setUnitPrice(wineProduct.getUnitPrice());
        wineOrder.setOrderNo(System.currentTimeMillis() / 1000 + "");
        wineOrder.setQuantity(wineOrderAddParam.getQuantity());
        wineOrder.setTotalAmount(new BigDecimal(wineOrder.getQuantity()).multiply(wineOrder.getUnitPrice()));
        wineOrder.setStatus(WineOrderStatusEnum.WAIT_PAY.getValue());
        if (this.count(new LambdaQueryWrapper<WineOrder>().eq(WineOrder::getOrderNo, wineOrder.getOrderNo())) > 0) {
            throw new CommonException("存在重复的订单号，值为：{}", wineOrder.getOrderNo());
        }
        this.save(wineOrder);
    }
    @Override
    public WineOrder addMini(WineOrderAddParam wineOrderAddParam) {
        String id = StpClientLoginUserUtil.getClientLoginUser().getId();
        WineOrder wineOrder = BeanUtil.toBean(wineOrderAddParam, WineOrder.class);
        wineOrder.setUserId(id);
        WineProduct wineProduct = wineProductService.queryEntity(wineOrderAddParam.getProductId());
        wineOrder.setProductName(wineProduct.getProductName());
        wineOrder.setUnitPrice(wineProduct.getUnitPrice());
        wineOrder.setOrderNo(String.valueOf(System.currentTimeMillis() / 1000));
        wineOrder.setQuantity(wineOrderAddParam.getQuantity());
        wineOrder.setTotalAmount(new BigDecimal(wineOrder.getQuantity()).multiply(wineOrder.getUnitPrice()));
        wineOrder.setStatus(WineOrderStatusEnum.WAIT_PAY.getValue());
        if (this.count(new LambdaQueryWrapper<WineOrder>().eq(WineOrder::getOrderNo, wineOrder.getOrderNo())) > 0) {
            throw new CommonException("存在重复的订单号，值为：{}", wineOrder.getOrderNo());
        }
        // 计算脉冲数
        Integer pulseCount = calculatePulseCount(wineOrderAddParam.getDeviceId(), wineOrderAddParam.getQuantity());
        wineOrder.setPulse(pulseCount);
        wineOrder.setLeftQuantity(wineOrderAddParam.getQuantity());
        this.save(wineOrder);
        return wineOrder;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(WineOrderEditParam wineOrderEditParam) {
        WineOrder wineOrder = this.queryEntity(wineOrderEditParam.getId());
        BeanUtil.copyProperties(wineOrderEditParam, wineOrder);
        if (this.count(new LambdaQueryWrapper<WineOrder>().ne(WineOrder::getId, wineOrder.getId()).eq(WineOrder::getOrderNo, wineOrder.getOrderNo())) > 0) {
            throw new CommonException("存在重复的订单号，值为：{}", wineOrder.getOrderNo());
        }
        this.updateById(wineOrder);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<WineOrderIdParam> wineOrderIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(wineOrderIdParamList, WineOrderIdParam::getId));
    }
    @Override
    public WineOrder detail(WineOrderIdParam wineOrderIdParam) {
        return this.queryEntity(wineOrderIdParam.getId());
    }
    @Override
    public WineOrder queryEntity(String id) {
        WineOrder wineOrder = this.getById(id);
        if (ObjectUtil.isEmpty(wineOrder)) {
            throw new CommonException("订单表不存在，id值为：{}", id);
        }
        
        // 手动设置设备编码
        if (ObjectUtil.isNotEmpty(wineOrder.getDeviceId())) {
            WineDevice wineDevice = wineDeviceService.queryEntity(wineOrder.getDeviceId());
            if (ObjectUtil.isNotEmpty(wineDevice)) {
                wineOrder.setDeviceCode(wineDevice.getDeviceCode());
                log.info(">>> 订单查询成功设置deviceCode，订单ID: {}, 设备ID: {}, 设备编码: {}", 
                    wineOrder.getId(), wineOrder.getDeviceId(), wineOrder.getDeviceCode());
            } else {
                log.warn(">>> 订单查询未找到设备信息，订单ID: {}, 设备ID: {}", 
                    wineOrder.getId(), wineOrder.getDeviceId());
            }
        } else {
            log.warn(">>> 订单查询设备ID为空，订单ID: {}", wineOrder.getId());
        }
        
        return wineOrder;
    }
    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        File tempFile = null;
        try {
            List<WineOrderEditParam> dataList = CollectionUtil.newArrayList();
            String fileName = "订单表导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
            tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
            EasyExcel.write(tempFile.getPath(), WineOrderEditParam.class).sheet("订单表").doWrite(dataList);
            CommonDownloadUtil.download(tempFile, response);
        } catch (Exception e) {
            log.error(">>> 订单表导入模板下载失败：", e);
            CommonResponseUtil.renderError(response, "订单表导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "wineOrderImportTemplate.xlsx"));
            // 读取excel
            List<WineOrderEditParam> wineOrderEditParamList = EasyExcel.read(tempFile).head(WineOrderEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<WineOrder> allDataList = this.list();
            for (int i = 0; i < wineOrderEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, wineOrderEditParamList.get(i), i);
                if (jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", wineOrderEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 订单表导入失败：", e);
            throw new CommonException("订单表导入失败");
        }
    }
    public JSONObject doImport(List<WineOrder> allDataList, WineOrderEditParam wineOrderEditParam, int i) {
        String id = wineOrderEditParam.getId();
        String orderNo = wineOrderEditParam.getOrderNo();
        String userId = wineOrderEditParam.getUserId();
        String deviceId = wineOrderEditParam.getDeviceId();
        String productId = wineOrderEditParam.getProductId();
        String productName = wineOrderEditParam.getProductName();
        Integer quantity = wineOrderEditParam.getQuantity();
        BigDecimal unitPrice = wineOrderEditParam.getUnitPrice();
        BigDecimal totalAmount = wineOrderEditParam.getTotalAmount();
        String status = wineOrderEditParam.getStatus();
        if (ObjectUtil.hasEmpty(id, orderNo, userId, deviceId, productId, productName, quantity, unitPrice, totalAmount, status)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, WineOrder::getId).indexOf(wineOrderEditParam.getId());
                WineOrder wineOrder;
                boolean isAdd = false;
                if (index == -1) {
                    isAdd = true;
                    wineOrder = new WineOrder();
                } else {
                    wineOrder = allDataList.get(index);
                }
                if (isAdd) {
                    boolean repeatOrderNo = allDataList.stream().anyMatch(tempData -> ObjectUtil
                            .isNotEmpty(tempData.getOrderNo()) && tempData.getOrderNo().equals(wineOrderEditParam.getOrderNo()));
                    if (repeatOrderNo) {
                        return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "新增数据时字段【订单号（orderNo）】与数据库中数据重复，值为：" + wineOrderEditParam.getOrderNo());
                    }
                } else {
                    boolean repeatOrderNo = allDataList.stream().anyMatch(tempData -> ObjectUtil
                            .isNotEmpty(tempData.getOrderNo()) && tempData.getOrderNo()
                            .equals(wineOrderEditParam.getOrderNo()) && !tempData.getId().equals(wineOrder.getId()));
                    if (repeatOrderNo) {
                        return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "更新数据时字段【订单号（orderNo）】与数据库中数据重复，值为：" + wineOrderEditParam.getOrderNo());
                    }
                }
                BeanUtil.copyProperties(wineOrderEditParam, wineOrder);
                if (isAdd) {
                    allDataList.add(wineOrder);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, wineOrder);
                }
                this.saveOrUpdate(wineOrder);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
                log.error(">>> 数据导入异常：", e);
                return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }
    @Override
    public void exportData(List<WineOrderIdParam> wineOrderIdParamList, HttpServletResponse response) throws IOException {
        File tempFile = null;
        try {
            List<WineOrderEditParam> dataList;
            if (ObjectUtil.isNotEmpty(wineOrderIdParamList)) {
                List<String> idList = CollStreamUtil.toList(wineOrderIdParamList, WineOrderIdParam::getId);
                dataList = BeanUtil.copyToList(this.listByIds(idList), WineOrderEditParam.class);
            } else {
                dataList = BeanUtil.copyToList(this.list(), WineOrderEditParam.class);
            }
            String fileName = "订单表_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
            tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
            EasyExcel.write(tempFile.getPath(), WineOrderEditParam.class).sheet("订单表").doWrite(dataList);
            CommonDownloadUtil.download(tempFile, response);
        } catch (Exception e) {
            log.error(">>> 订单表导出失败：", e);
            CommonResponseUtil.renderError(response, "订单表导出失败");
        } finally {
            FileUtil.del(tempFile);
        }
    }
    @Override
    public void cancel(WineOrderCancelParam wineOrderCancelParam) {
        WineOrder wineOrder = this.queryEntity(wineOrderCancelParam.getId());
        if (WineOrderStatusEnum.WAIT_PAY.getValue().equals(wineOrder.getStatus())) {
            wineOrder.setCancelReason(wineOrderCancelParam.getCancelReason());
            wineOrder.setCancelTime(new Date());
            wineOrder.setStatus(WineOrderStatusEnum.TRADE_CLOSED.getValue());
            this.updateById(wineOrder);
        } else {
            throw new CommonException("当前状态,不允许操作");
        }
    }
    @Override
    public void startDispense(WineOrderIdParam wineOrderIdParam) {
        WineOrder wineOrder = this.queryEntity(wineOrderIdParam.getId());
        if (!WineOrderStatusEnum.WAIT_DELIVER.getValue().equals(wineOrder.getStatus())) {
            throw new CommonException("订单已开始取酒,不允许操作");
        }
        wineOrder.setDispenseStartTime(new Date());
        wineOrder.setStatus(WineOrderStatusEnum.DELIVERING.getValue());
        this.updateById(wineOrder);
    }
    @Override
    public void endDispense(WineOrderIdParam wineOrderIdParam) {
        WineOrder wineOrder = this.queryEntity(wineOrderIdParam.getId());
        if (!WineOrderStatusEnum.DELIVERING.getValue().equals(wineOrder.getStatus())) {
            throw new CommonException("订单已结束取酒,不允许操作");
        }
        wineOrder.setDispenseEndTime(new Date());
        wineOrder.setStatus(WineOrderStatusEnum.TRADE_SUCCESS.getValue());
        wineDeviceService.reduceStock(wineOrder.getDeviceId(), wineOrder.getQuantity());
        // 处理分佣和分账逻辑
        String valueByKey = devConfigApi.getValueByKey("COMMISSION_DISTRIBUTION");
        if (valueByKey != null && valueByKey.equals("2")) {
            this.processCommissionAndProfitSharingForOrder(wineOrder);  // 使用新的统一方法
        }
        this.updateById(wineOrder);
    }
    @Resource
    private WineOrderProfitSharingService wineOrderProfitSharingService;
    @Resource
    private vip.xiaonuo.pay.modular.wx.service.impl.PayWxServiceFactory payWxServiceFactory;
    /**
     * 为订单处理分佣和分账（新的统一方法）
     *
     * @param wineOrder 订单信息
     */
    @Override
    public void processCommissionAndProfitSharingForOrder(WineOrder wineOrder) {
        try {
            log.info("开始为订单处理分佣分账，订单号：{}", wineOrder.getOrderNo());

            // 判断当前支付模式
            boolean isPartnerMode = payWxServiceFactory.isPartnerMode();
            log.info("订单{}当前支付模式：{}模式", wineOrder.getOrderNo(), isPartnerMode ? "服务商" : "普通");

            if (isPartnerMode) {
                // 服务商模式：支持分账，使用完整的分佣分账流程
                log.info("服务商模式：执行分佣分账流程，订单号：{}", wineOrder.getOrderNo());
                wineOrderProfitSharingService.processCommissionAndProfitSharing(wineOrder);
            } else {
                // 普通模式：不支持分账，只进行本地分佣处理
                log.info("普通模式：只执行分佣流程（不进行微信分账），订单号：{}", wineOrder.getOrderNo());
                wineOrderProfitSharingService.processCommissionOnly(wineOrder);

                // 普通模式下设置分账状态为无需分账
                wineOrder.setProfitSharingStatus(WineOrderProfitSharingStatusEnum.NO_NEED.getValue());
                wineOrder.setProfitSharingFailureReason(null);
            }
            // 分账成功时清空失败原因
            if (WineOrderProfitSharingStatusEnum.SUCCESS.getValue().equals(wineOrder.getProfitSharingStatus())) {
                wineOrder.setProfitSharingFailureReason(null);
            }
            // 更新订单的分账状态字段
            this.updateById(wineOrder);
            log.info("订单分佣分账处理完成，订单号：{}，分账状态：{}", wineOrder.getOrderNo(), wineOrder.getProfitSharingStatus());
        } catch (Exception e) {
            log.error("订单分佣分账处理异常，订单号：{}，异常信息：{}", wineOrder.getOrderNo(), e.getMessage(), e);
            // 设置分账失败状态和失败原因
            wineOrder.setProfitSharingStatus(WineOrderProfitSharingStatusEnum.FAILED.getValue());
            String failureReason = "手动分账失败：" + e.getMessage();
            // 截取前255个字符，避免数据库字段超长
            wineOrder.setProfitSharingFailureReason(failureReason.length() > 255 ? failureReason.substring(0, 255) : failureReason);
            // 确保异常情况下也更新订单状态
            this.updateById(wineOrder);
            throw e;
        }
    }
    @Override
    public void applyRefund(WineOrderIdParam wineOrderIdParam) {
        WineOrder wineOrder = this.queryEntity(wineOrderIdParam.getId());
        if (!WineOrderStatusEnum.WAIT_DELIVER.getValue().equals(wineOrder.getStatus())) {
            throw new CommonException("当前状态不允许申请退款");
        }
        // 更新订单状态为退款中
        wineOrder.setStatus(WineOrderStatusEnum.REFUNDING.getValue());
        this.updateById(wineOrder);
        // 调用支付模块执行退款
        RefundParam refundParam = new RefundParam();
        refundParam.setOutTradeNo(wineOrder.getOrderNo());
        refundParam.setRefundAmount(wineOrder.getTotalAmount());
        payWxService.doRefund(refundParam);
    }
    /**
     * 计算脉冲数
     *
     * @param deviceId 设备ID
     * @param quantity 商品数量
     * @return 脉冲数
     */
    private Integer calculatePulseCount(String deviceId, Integer quantity) {
        // 获取设备信息
        WineDevice wineDevice = wineDeviceService.queryEntity(deviceId);
        // 获取设备脉冲比
        Double devicePulseRatio = wineDevice.getPulseRatio();
        // 只使用设备的脉冲比，如果设备没有配置脉冲比则使用默认值1.0
        double pulseRatio = devicePulseRatio != null ? devicePulseRatio : 1.0;
        // 对于小容量酒品，使用截断而不是四舍五入来避免输出过量
        double calculatedPulseCount = quantity * pulseRatio;
        if (quantity <= 50) {
            // 对于小于等于50ml的酒品，向下取整以避免输出过量
            return (int) Math.floor(calculatedPulseCount);
        } else {
            // 对于大于50ml的酒品，使用四舍五入保持精度
            return (int) Math.round(calculatedPulseCount);
        }
    }
    /**
     * 脉冲数转为出酒量
     *
     */
    private Integer pulseCountToQuantity(String deviceId, Integer quantity) {
        // 脉冲数除以脉冲比就等于出酒量，参考calculatePulseCount，但是要反过来计算
        // 获取设备信息
        WineDevice wineDevice = wineDeviceService.queryEntity(deviceId);
        // 获取设备脉冲比
        Double devicePulseRatio = wineDevice.getPulseRatio();
        // 只使用设备的脉冲比，如果设备没有配置脉冲比则使用默认值1.0
        double pulseRatio = devicePulseRatio != null ? devicePulseRatio : 1.0;
        return (int) Math.round(quantity / pulseRatio);
    }
    @Override
    public Page<WineOrder> agentOrderPage(WineOrderAgentPageParam wineOrderAgentPageParam, String agentUserId) {
        QueryWrapper<WineOrder> queryWrapper = new QueryWrapper<WineOrder>().checkSqlInjection();
        // 只查询该代理商的设备产生的订单
        queryWrapper.exists(
                "SELECT 1 FROM wine_device wd WHERE wd.id = wine_order.device_id " +
                        "AND wd.operation_type = 'OPERATION_TYPE_AGENT' " +
                        "AND wd.agent_user_id = {0}", agentUserId
        );
        // 搜索条件：订单号或设备编号
        if (ObjectUtil.isNotEmpty(wineOrderAgentPageParam.getSearchKey())) {
            String searchKey = wineOrderAgentPageParam.getSearchKey();
            queryWrapper.and(wrapper ->
                    wrapper.lambda().like(WineOrder::getOrderNo, searchKey)
                            .or()
                            .exists("SELECT 1 FROM wine_device wd WHERE wd.id = wine_order.device_id AND wd.device_code LIKE '" + searchKey + "%'")
            );
        }
        // 订单状态筛选
        if (ObjectUtil.isNotEmpty(wineOrderAgentPageParam.getStatus())) {
            queryWrapper.lambda().eq(WineOrder::getStatus, wineOrderAgentPageParam.getStatus());
        }
        // 时间范围筛选
        if (ObjectUtil.isNotEmpty(wineOrderAgentPageParam.getStartTime())) {
            queryWrapper.lambda().ge(WineOrder::getCreateTime, wineOrderAgentPageParam.getStartTime());
        }
        if (ObjectUtil.isNotEmpty(wineOrderAgentPageParam.getEndTime())) {
            queryWrapper.lambda().le(WineOrder::getCreateTime, wineOrderAgentPageParam.getEndTime());
        }
        // 排序
        if (ObjectUtil.isAllNotEmpty(wineOrderAgentPageParam.getSortField(), wineOrderAgentPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(wineOrderAgentPageParam.getSortOrder());
            queryWrapper.orderBy(true, wineOrderAgentPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(wineOrderAgentPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(WineOrder::getCreateTime);
        }
        Page<WineOrder> orderPage = this.page(CommonPageRequest.defaultPage(), queryWrapper);

        // 为每个订单添加佣金信息
        if (orderPage.getRecords() != null && !orderPage.getRecords().isEmpty()) {
            for (WineOrder order : orderPage.getRecords()) {
                // 获取订单的佣金信息
                BigDecimal commissionAmount = wineCommissionRecordService.list(
                                new LambdaQueryWrapper<WineCommissionRecord>()
                                        .eq(WineCommissionRecord::getOrderId, order.getId())
                        ).stream()
                        .map(WineCommissionRecord::getCommissionAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 设置佣金金额（用于前端显示）
                order.setExtJson(commissionAmount.toString());
            }
        }

        return orderPage;
    }

    @Override
    public String fixRefundStatus(WineOrderIdParam wineOrderIdParam) {
        WineOrder wineOrder = this.queryEntity(wineOrderIdParam.getId());

        // 只处理退款中状态的订单
        if (!WineOrderStatusEnum.REFUNDING.getValue().equals(wineOrder.getStatus())) {
            return "订单当前状态不是退款中，无法修复";
        }

        try {
            // 检查微信退款状态
            if (wineOrder.getTransactionId() != null) {
                // 使用订单号查询退款状态（这里需要根据实际情况调整）
                log.info("开始手动修复退款状态，订单号：{}，交易号：{}", wineOrder.getOrderNo(), wineOrder.getTransactionId());

                // 直接更新为交易关闭状态（假设退款已成功）
                wineOrder.setStatus(WineOrderStatusEnum.TRADE_CLOSED.getValue());
                wineOrder.setRefundTime(new Date());

                // 如果退款金额为空，设置为订单总金额
                if (wineOrder.getRefundAmount() == null) {
                    wineOrder.setRefundAmount(wineOrder.getTotalAmount());
                }

                boolean updateResult = this.updateById(wineOrder);
                if (updateResult) {
                    log.info("手动修复退款状态成功，订单号：{}，新状态：{}", wineOrder.getOrderNo(), wineOrder.getStatus());
                    return "退款状态修复成功，订单已更新为交易关闭状态";
                } else {
                    log.error("手动修复退款状态失败，数据库更新失败，订单号：{}", wineOrder.getOrderNo());
                    return "退款状态修复失败，数据库更新失败";
                }
            } else {
                return "订单缺少微信交易号，无法修复";
            }
        } catch (Exception e) {
            log.error("手动修复退款状态异常，订单号：{}，异常信息：{}", wineOrder.getOrderNo(), e.getMessage(), e);
            return "退款状态修复失败：" + e.getMessage();
        }
    }

    @Override
    public void reportOrderRemainQuantity(WineOrderRemainOrderParam wineOrderRemainOrderParam) {
        try {
            log.info(">>> 实时上报开始，订单ID: {}, 剩余脉冲数: {}", 
                wineOrderRemainOrderParam.getId(), wineOrderRemainOrderParam.getQuantity());
            
            WineOrder wineOrder = this.queryEntity(wineOrderRemainOrderParam.getId());
            
            log.info(">>> 实时上报获取订单信息，订单ID: {}, 设备ID: {}", 
                wineOrderRemainOrderParam.getId(), wineOrder.getDeviceId());
            
            //这里接收的参数是剩余的脉冲数，需要吧脉冲数转为剩余的出酒量
            Integer i = pulseCountToQuantity(wineOrder.getDeviceId(), wineOrderRemainOrderParam.getQuantity());
            
            log.info(">>> 实时上报脉冲数转换，订单ID: {}, 脉冲数: {}, 转换后出酒量: {}", 
                wineOrderRemainOrderParam.getId(), wineOrderRemainOrderParam.getQuantity(), i);
            
            wineOrder.setLeftQuantity(i);
            this.updateById(wineOrder);
            
            log.info(">>> 实时上报成功，订单ID: {}, 剩余出酒量: {}", 
                wineOrderRemainOrderParam.getId(), i);
        } catch (Exception e) {
            log.error(">>> 实时上报失败，订单ID: {}, 剩余脉冲数: {}, 异常信息: {}", 
                wineOrderRemainOrderParam.getId(), wineOrderRemainOrderParam.getQuantity(), e.getMessage(), e);
            throw new CommonException("实时上报失败：" + e.getMessage());
        }
    }
}