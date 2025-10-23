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
package vip.xiaonuo.wine.modular.device.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.hpsf.Decimal;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.device.entity.WineDevice;
import vip.xiaonuo.wine.modular.device.param.*;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserAddParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserIdParam;
import vip.xiaonuo.wine.modular.deviceuser.param.WineDeviceUserBindParam;
import vip.xiaonuo.wine.modular.product.entity.WineProduct;

import java.io.IOException;
import java.util.List;

/**
 * 设备信息表Service接口
 *
 * @author jetox
 * @date  2025/07/24 07:57
 **/
public interface WineDeviceService extends IService<WineDevice> {

    /**
     * 获取设备信息表分页
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    Page<WineDevice> page(WineDevicePageParam wineDevicePageParam);
    /**
     * 获取当前登录用户的代理设备
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    /**
     * 获取设备信息表分页
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    Page<WineDevice> agentPage(WineDeviceAgentPage wineDeviceAgentPage);
    /**
     * 添加设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    void add(WineDeviceAddParam wineDeviceAddParam);

    /**
     * 编辑设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    void edit(WineDeviceEditParam wineDeviceEditParam);

    /**
     * 删除设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    void delete(List<WineDeviceIdParam> wineDeviceIdParamList);

    /**
     * 获取设备信息表详情
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    WineDevice detail(WineDeviceIdParam wineDeviceIdParam);

    /**
     * 获取设备信息表详情
     *
     * @author jetox
     * @date  2025/07/24 07:57
     **/
    WineDevice queryEntity(String id);

    /**
     * 下载设备信息表导入模板
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出设备信息表
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    void exportData(List<WineDeviceIdParam> wineDeviceIdParamList, HttpServletResponse response) throws IOException;

    /**
     * 获取产品信息
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    WineProduct getProductInfo(WineDeviceIdParam wineDeviceIdParam);

    /**
     * 更新库存
     *
     * @author jetox
     * @date  2025/07/24 07:57
     */
    void reduceStock(String deviceId, Integer quantity);

    /**
     * 管理员增加设备库存
     *
             * @author jetox
     * @date  2025/07/24 07:57
            */
    WineDevice addStock(WineDeviceStockParam wineDeviceStockParam);

    /**
     * 更新设备脉冲数
     */
    void updatePulseCount(WineDevicePulseRatioParam wineDevicePulseRatioParam);

    /**
     * 根据设备编号获取设备信息
     */
    void unbind(WineDeviceUserIdParam wineDeviceUserIdParam);

    /**
     * 根据设备编号获取设备信息
     */
    WineDevice detailByCode(String deviceCode);
    /**
     * 生成小程序码
     */
    String createQrCode(@Valid WineDeviceIdParam wineDeviceIdParam);
    /**
     * 获取设备控制加密指令
     * 小程序端调用此接口获取加密控制指令，然后通过蓝牙发送给设备
     * @return 控制指令结果
     * @author AI Assistant
     * @date 2025/01/30
     */
    String getDeviceControlCommand(DeviceControlParam deviceControlParam);

    /**
     * 获取设备测试控制加密指令
     * 小程序端调用此接口获取加密控制指令，然后通过蓝牙发送给设备
     * @return 测试控制指令结果
     * @author AI Assistant
     * @date 2025/01/30
     */
    String getDeviceTestControlCommand(DeviceTestControlParam deviceTestControlParam);
    WineDevice getDeviceByDeviceCode(String s);

    void bind(WineDeviceUserAddParam wineDeviceUserAddParam);
    
    /**
     * 绑定设备用户（小程序版）
     */
    void bindDeviceUser(WineDeviceUserBindParam wineDeviceUserBindParam);
    
    /**
     * 绑定设备代理商（小程序版）
     */
    void bindDeviceAgent(WineDeviceBindAgentParam wineDeviceBindAgentParam);
    /**
     * 更新设备信息
     * @param deviceId 设备ID
     * @param productId 产品ID
     * @param storeId 门店ID
     * @param manageId 管理员ID
     * @author AI Assistant
     * @date 2025/01/30
     */
    void updateInfo(String deviceId, String productId, String storeId, String manageId);
    
    /**
     * 批量更新所有设备的脉冲比
     *
     * @param pulseRatio 脉冲比
     * @author AI Assistant
     * @date 2025/01/30
     */
    void updateAllDevicesPulseRatio(Double pulseRatio);
}
