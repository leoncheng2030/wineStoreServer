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
package vip.xiaonuo.dev.modular.district.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 地区表实体
 *
 * @author jetox
 * @date  2025/08/12 07:53
 **/
@Getter
@Setter
@TableName("dev_district")
public class DevDistrict {

    /** 地区编号 */
    @TableId
    @Schema(description = "地区编号")
    private String id;

    /** 租户 */
    @Schema(description = "租户")
    private String tenantId;

    /** 父级地区编码 */
    @Schema(description = "父级地区编码")
    private Integer parentId;

    /** 等级 */
    @Schema(description = "等级")
    private Boolean level;

    /** 名称 */
    @Schema(description = "名称")
    private String name;

    /** 简称 */
    @Schema(description = "简称")
    private String shortName;

    /** 地区编码 */
    @Schema(description = "地区编码")
    private String cityCode;

    /** 邮政编码 */
    @Schema(description = "邮政编码")
    private String zipCode;

    /** 纬度 */
    @Schema(description = "纬度")
    private String gcj02Lng;

    /** 经度 */
    @Schema(description = "经度")
    private String gcj02Lat;

    /** 纬度 */
    @Schema(description = "纬度")
    private String db09Lng;

    /** 经度 */
    @Schema(description = "经度")
    private String db09Lat;

    /** REMARK1 */
    @Schema(description = "REMARK1")
    private String remark1;

    /** REMARK2 */
    @Schema(description = "REMARK2")
    private String remark2;

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

    /** 排序码 */
    @Schema(description = "排序码")
    @TableField(exist = false)
    private Integer sortCode;
}
