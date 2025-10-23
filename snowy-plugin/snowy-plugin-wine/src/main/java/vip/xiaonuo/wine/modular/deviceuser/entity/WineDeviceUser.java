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
package vip.xiaonuo.wine.modular.deviceuser.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分佣配置实体
 *
 * @author jetox
 * @date  2025/07/24 08:44
 **/
@Getter
@Setter
@TableName("wine_device_user")
public class WineDeviceUser {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 用户ID */
    @Schema(description = "用户ID")
    private String userId;

    /** 设备ID */
    @Schema(description = "设备ID")
    private String deviceId;

    /** 佣金比例 */
    @Schema(description = "佣金比例")
    private BigDecimal commissionRate;

    /** 角色编码 */
    @Schema(description = "角色编码")
    private String roleCode;

    /** 生效时间 */
    @Schema(description = "生效时间")
    private Date effectiveTime;

    /** 失效时间 */
    @Schema(description = "失效时间")
    private Date expireTime;

    /** 状态（ACTIVE-生效中, INACTIVE-已失效, SUSPENDED-暂停） */
    @Schema(description = "状态（ACTIVE-生效中, INACTIVE-已失效, SUSPENDED-暂停）")
    private String status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 删除标志 */
    @Schema(description = "删除标志")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户 */
    @Schema(description = "创建用户")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 修改时间 */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 修改用户 */
    @Schema(description = "修改用户")
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;
}
