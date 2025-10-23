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
package vip.xiaonuo.wine.modular.accountflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 账户流水实体
 *
 * @author jetox
 * @date  2025/07/24 08:56
 **/
@Getter
@Setter
@TableName("wine_account_flow")
public class WineAccountFlow {

    /** 主键ID */
    @TableId
    @Schema(description = "主键ID")
    private String id;

    /** 流水号 */
    @Schema(description = "流水号")
    private String flowNo;

    /** 用户ID */
    @Schema(description = "用户ID")
    private String userId;

    /** 用户昵称 */
    @Schema(description = "用户昵称")
    private String userNickname;

    /** 流水类型(COMMISSION:佣金收入,WITHDRAW:提现支出,REFUND:退款,TRANSFER:转账) */
    @Schema(description = "流水类型(COMMISSION:佣金收入,WITHDRAW:提现支出,REFUND:退款,TRANSFER:转账)")
    private String flowType;

    /** 金额(元) */
    @Schema(description = "金额(元)")
    private BigDecimal amount;

    /** 余额变动(元) - 正数表示增加，负数表示减少 */
    @Schema(description = "余额变动(元) - 正数表示增加，负数表示减少")
    private BigDecimal balanceChange;

    /** 变动前余额(元) */
    @Schema(description = "变动前余额(元)")
    private BigDecimal beforeBalance;

    /** 变动后余额(元) */
    @Schema(description = "变动后余额(元)")
    private BigDecimal afterBalance;

    /** 关联ID(订单ID、提现ID等) */
    @Schema(description = "关联ID(订单ID、提现ID等)")
    private String relatedId;

    /** 关联类型(ORDER:订单,WITHDRAW:提现,MANUAL:手动调整) */
    @Schema(description = "关联类型(ORDER:订单,WITHDRAW:提现,MANUAL:手动调整)")
    private String relatedType;

    /** 关联单号 */
    @Schema(description = "关联单号")
    private String relatedNo;

    /** 流水描述 */
    @Schema(description = "流水描述")
    private String description;

    /** 流水状态(SUCCESS:成功,FAILED:失败,PENDING:处理中) */
    @Schema(description = "流水状态(SUCCESS:成功,FAILED:失败,PENDING:处理中)")
    private String status;

    /** 交易时间 */
    @Schema(description = "交易时间")
    private Date transactionTime;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 创建用户 */
    @Schema(description = "创建用户")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 更新用户 */
    @Schema(description = "更新用户")
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;

    /** 删除标志(Y:已删除,N:未删除) */
    @Schema(description = "删除标志(Y:已删除,N:未删除)")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;
}
