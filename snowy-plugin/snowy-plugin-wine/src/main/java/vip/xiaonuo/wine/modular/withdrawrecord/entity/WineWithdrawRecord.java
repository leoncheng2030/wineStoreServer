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
package vip.xiaonuo.wine.modular.withdrawrecord.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现记录表实体
 *
 * @author jetox
 * @date  2025/07/24 08:50
 **/
@Getter
@Setter
@TableName("wine_withdraw_record")
public class WineWithdrawRecord {

    /** 主键ID */
    @TableId
    @Schema(description = "主键ID")
    private String id;

    /** 提现单号 */
    @Schema(description = "提现单号")
    private String withdrawNo;

    /** 用户ID */
    @Schema(description = "用户ID")
    private String userId;

    /** 用户昵称 */
    @Schema(description = "用户昵称")
    private String userNickname;

    /** 提现金额(元) */
    @Schema(description = "提现金额(元)")
    private BigDecimal withdrawAmount;

    /** 手续费(元) */
    @Schema(description = "手续费(元)")
    private BigDecimal serviceFee;

    /** 实际到账金额(元) */
    @Schema(description = "实际到账金额(元)")
    private BigDecimal actualAmount;

    /** 提现方式 */
    @Schema(description = "提现方式")
    private String withdrawType;

    /** 账户信息 */
    @Schema(description = "账户信息")
    private String accountInfo;

    /** 提现状态 */
    @Schema(description = "提现状态")
    private String status;

    /** 失败原因 */
    @Schema(description = "失败原因")
    private String failReason;

    /** 第三方交易号 */
    @Schema(description = "第三方交易号")
    private String transactionId;

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

    /** 删除标志 */
    @Schema(description = "删除标志")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;
}
