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
package vip.xiaonuo.wine.modular.deviceuser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备用户关联信息VO
 *
 * @author jetox
 * @date 2025/09/20
 */
@Getter
@Setter
public class WineDeviceUserVO {

    /** 关系ID */
    @Schema(description = "关系ID")
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

    /** 角色名称 */
    @Schema(description = "角色名称")
    private String roleName;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 用户姓名 */
    @Schema(description = "用户姓名")
    private String userName;

    /** 用户昵称 */
    @Schema(description = "用户昵称")
    private String nickName;

    /** 用户手机号 */
    @Schema(description = "用户手机号")
    private String phone;

    /** 头像 */
    @Schema(description = "头像")
    private String avatar;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private Date createTime;

    /** 最后活跃时间 */
    @Schema(description = "最后活跃时间")
    private Date lastActiveTime;
}