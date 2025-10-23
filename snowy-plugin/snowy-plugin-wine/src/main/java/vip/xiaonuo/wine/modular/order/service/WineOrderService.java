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
package vip.xiaonuo.wine.modular.order.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.wine.modular.order.entity.WineOrder;
import vip.xiaonuo.wine.modular.order.param.*;

import java.io.IOException;
import java.util.List;

/**
 * 订单表Service接口
 *
 * @author jetox
 * @date  2025/07/24 08:47
 **/
public interface WineOrderService extends IService<WineOrder> {

    /**
     * 获取订单表分页
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    Page<WineOrder> page(WineOrderPageParam wineOrderPageParam);

    /**
     * 添加订单表
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    void add(WineOrderAddParam wineOrderAddParam);

    /**
     * 小程序端新增订单
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    WineOrder addMini(WineOrderAddParam wineOrderAddParam);

    /**
     * 编辑订单表
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    void edit(WineOrderEditParam wineOrderEditParam);

    /**
     * 删除订单表
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    void delete(List<WineOrderIdParam> wineOrderIdParamList);

    /**
     * 获取订单表详情
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    WineOrder detail(WineOrderIdParam wineOrderIdParam);

    /**
     * 获取订单表详情
     *
     * @author jetox
     * @date  2025/07/24 08:47
     **/
    WineOrder queryEntity(String id);

    /**
     * 下载订单表导入模板
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入订单表
     *
     * @author jetox
     * @date  2025/07/24 08:47
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出订单表
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    void exportData(List<WineOrderIdParam> wineOrderIdParamList, HttpServletResponse response) throws IOException;
    /**
     * 取消订单
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    void cancel(WineOrderCancelParam wineOrderCancelParam);

    /**
     * 开始出酒
     */
    void startDispense(WineOrderIdParam wineOrderIdParam);

    /**
     * 完成订单
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    void endDispense(WineOrderIdParam wineOrderIdParam);


    /**
     * 申请退款
     *
     * @author jetox
     * @date  2025/07/24 08:47
     */
    void applyRefund(WineOrderIdParam wineOrderIdParam);


    /**
     * 为订单处理分佣和分账（统一方法）
     */
    void processCommissionAndProfitSharingForOrder(WineOrder wineOrder);

    /**
     * 获取代理商订单分页
     *
     * @author Qoder
     * @date 2024/09/20
     */
    Page<WineOrder> agentOrderPage(WineOrderAgentPageParam wineOrderAgentPageParam, String agentUserId);
    
    /**
     * 手动修复退款状态
     * 用于处理已经退款成功但订单状态未正确更新的情况
     *
     * @param wineOrderIdParam 订单ID参数
     * @return 操作结果
     * @author Qoder
     * @date 2025/09/20
     */
    String fixRefundStatus(WineOrderIdParam wineOrderIdParam);

    /**
     * 实时上报订单剩余quantity
     *
     * @author Qoder
     * @date 2024/09/20
     */
    void reportOrderRemainQuantity(WineOrderRemainOrderParam wineOrderRemainOrderParam);
}
