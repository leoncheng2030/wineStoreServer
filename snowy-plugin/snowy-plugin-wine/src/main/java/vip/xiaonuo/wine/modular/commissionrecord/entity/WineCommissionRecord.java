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
package vip.xiaonuo.wine.modular.commissionrecord.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fhs.core.trans.anno.Trans;
import com.fhs.core.trans.constant.TransType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 佣金记录表实体
 *
 * @author jetox
 * @date  2025/07/24 08:50
 **/
@Getter
@Setter
@TableName("wine_commission_record")
public class WineCommissionRecord extends CommonEntity {

    /** 主键ID */
    @TableId
    @Schema(description = "主键ID")
    private String id;

    /** 订单ID */
    @Schema(description = "订单ID")
    private String orderId;

    /** 订单号 */
    @Schema(description = "订单号")
    private String orderNo;

    /** 受益用户ID */
    @Schema(description = "受益用户ID")
    @Trans(type = TransType.RPC, targetClassName = "vip.xiaonuo.client.modular.user.entity.ClientUser", fields = "nickname", alias = "user", ref = "userNickname")
    private String userId;

    /** 用户昵称 */
    @Schema(description = "用户昵称")
    private String userNickname;

    /** 设备ID */
    @Schema(description = "设备ID")
    private String deviceId;

    /** 设备编码 */
    @Schema(description = "设备编码")
    private String deviceCode;

    /** 酒品ID */
    @Schema(description = "酒品ID")
    private String wineId;

    /** 酒品名称 */
    @Schema(description = "酒品名称")
    private String wineName;

    /** 订单金额(元) */
    @Schema(description = "订单金额(元)")
    private BigDecimal orderAmount;

    /** 佣金类型 */
    @Schema(description = "佣金类型")
    private String commissionType;

    /** 佣金比例(%) */
    @Schema(description = "佣金比例(%)")
    private BigDecimal commissionRate;

    /** 佣金金额(元) */
    @Schema(description = "佣金金额(元)")
    private BigDecimal commissionAmount;

    /** 佣金状态(PENDING:待计算,CALCULATED:已计算,WAIT_PROFIT:待分账,PROFIT_SHARING:分账中,PROFIT_SHARED:分账成功,PROFIT_FAILED:分账失败,SETTLED:已发放,FROZEN:已冻结,CANCELLED:已取消) */
    @Schema(description = "佣金状态")
    private String status;

    /** 计算时间 */
    @Schema(description = "计算时间")
    private Date calculateTime;

    /** 发放时间 */
    @Schema(description = "发放时间")
    private Date settleTime;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

}
