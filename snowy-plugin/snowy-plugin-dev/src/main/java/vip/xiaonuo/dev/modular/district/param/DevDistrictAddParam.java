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
package vip.xiaonuo.dev.modular.district.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 地区表添加参数
 *
 * @author jetox
 * @date  2025/08/12 07:53
 **/
@Getter
@Setter
public class DevDistrictAddParam {

    /** 父级地区编码 */
    @Schema(description = "父级地区编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "parentId不能为空")
    private String parentId;

    /** 等级 */
    @Schema(description = "等级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "level不能为空")
    private Boolean level;

    /** 名称 */
    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "name不能为空")
    private String name;

    /** 简称 */
    @Schema(description = "简称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "shortName不能为空")
    private String shortName;

    /** 地区编码 */
    @Schema(description = "地区编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "cityCode不能为空")
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

}
