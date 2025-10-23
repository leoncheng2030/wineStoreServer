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
package vip.xiaonuo.wine.modular.store.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 门店管理表添加参数
 *
 * @author jetox
 * @date  2025/07/24 08:01
 **/
@Getter
@Setter
public class WineStoreAddParam {

    /** 门店名称 */
    @Schema(description = "门店名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "storeName不能为空")
    private String storeName;

    /** 门店编码 */
    @Schema(description = "门店编码")
    private String storeCode;

    /** 门头照片 */
    @Schema(description = "营业执照图片")
    private String imageUrl;

    /** 门店电话 */
    @Schema(description = "门店电话")
    private String storePhone;

    /** 门店管理员ID（关联client_user） */
    @Schema(description = "门店管理员ID（关联client_user）")
    private String storeManagerId;

    /** 门店管理员姓名 */
    @Schema(description = "门店管理员姓名")
    private String storeManagerName;

    /** 省份 */
    @Schema(description = "省份")
    private String province;

    /** 城市 */
    @Schema(description = "城市")
    private String city;

    /** 区县 */
    @Schema(description = "区县")
    private String district;

    /** 详细地址 */
    @Schema(description = "详细地址")
    private String detailAddress;

    /** 营业执照号 */
    @Schema(description = "营业执照号")
    private String businessLicense;

    /** 联系人 */
    @Schema(description = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Schema(description = "联系电话")
    private String contactPhone;

    /** 联系邮箱 */
    @Schema(description = "联系邮箱")
    private String contactEmail;

    /** 营业时间 */
    @Schema(description = "营业时间")
    private String businessHours;

    /** 门店面积(平方米) */
    @Schema(description = "门店面积(平方米)")
    private BigDecimal storeArea;

    /** 纬度 */
    @Schema(description = "纬度")
    private BigDecimal latitude;

    /** 经度 */
    @Schema(description = "经度")
    private BigDecimal longitude;

    /** 门店状态(ENABLE/DISABLE) */
    @Schema(description = "门店状态(ENABLE/DISABLE)")
    private String status;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 门店描述 */
    @Schema(description = "门店描述")
    private String description;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

    /** 代理商用户ID */
    @Schema(description = "代理商用户ID")
    private String agentClientUserId;

}
