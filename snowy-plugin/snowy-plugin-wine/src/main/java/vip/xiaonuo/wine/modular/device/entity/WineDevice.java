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
package vip.xiaonuo.wine.modular.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fhs.core.trans.anno.Trans;
import com.fhs.core.trans.constant.TransType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;
import vip.xiaonuo.wine.modular.product.entity.WineProduct;
import vip.xiaonuo.wine.modular.store.entity.WineStore;

import java.util.Date;

/**
 * 设备信息表实体
 *
 * @author jetox
 * @date  2025/07/24 07:57
 **/
@Getter
@Setter
@TableName("wine_device")
public class WineDevice extends CommonEntity {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 设备编码 */
    @Schema(description = "设备编码")
    private String deviceCode;

    /** uuid */
    @Schema(description = "uuid")
    private String uuid;

    /** 运营方式 */
    @Schema(description = "运营方式")
    private String operationType;

    /** 设备名称 */
    @Schema(description = "设备名称")
    private String deviceName;

    /** 所属门店ID */
    @Schema(description = "所属门店ID")
    @Trans(type = TransType.SIMPLE,target = WineStore.class, fields = "storeName")
    private String storeId;

    /** 当前绑定的酒品ID */
    @Schema(description = "当前绑定的酒品ID")
    @Trans(type = TransType.SIMPLE,target = WineProduct.class, fields = "productName")
    private String currentProductId;

    /** 库存 */
    @Schema(description = "库存")
    private Integer stock;

    /** 单位脉冲数 */
    @Schema(description = "单位脉冲数")
    private Double pulseRatio;

    /** 设备状态 */
    @Schema(description = "设备状态")
    private String status;

    /** 绑定时间 */
    @Schema(description = "绑定时间")
    private Date bindTime;



    /** 代理商用户ID */
    @Schema(description = "代理商用户ID")
    @Trans(type = TransType.RPC, targetClassName = "vip.xiaonuo.client.modular.user.entity.ClientUser", fields = {"name","phone"}, alias = "agent")
    private String agentUserId;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;


}
