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
package vip.xiaonuo.wine.modular.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fhs.core.trans.anno.Trans;
import com.fhs.core.trans.constant.TransType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;
import vip.xiaonuo.wine.modular.productcategory.entity.WineProductCategory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 酒品列表实体
 *
 * @author jetox
 * @date  2025/07/24 08:20
 **/
@Getter
@Setter
@TableName("wine_product")
public class WineProduct extends CommonEntity {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 分类ID */
    @Schema(description = "分类ID")
    @Trans(type = TransType.SIMPLE,target = WineProductCategory.class, fields = "categoryName")
    private String categoryId;

    /** 酒品编码 */
    @Schema(description = "酒品编码")
    private String productCode;

    /** 酒品名称 */
    @Schema(description = "酒品名称")
    private String productName;

    /** 酒品分类 */
    @Schema(description = "酒品分类")
    private String productType;

    /** 酒精度数 */
    @Schema(description = "酒精度数")
    private BigDecimal alcoholContent;

    /** 产地 */
    @Schema(description = "产地")
    private String origin;

    /** 生产厂家 */
    @Schema(description = "生产厂家")
    private String manufacturer;

    /** 成本价 */
    @Schema(description = "成本价")
    private BigDecimal costPrice;

    /** 零售单价 */
    @Schema(description = "零售单价")
    private BigDecimal unitPrice;

    /** 酒品图片URL */
    @Schema(description = "酒品图片URL")
    private String imageUrl;

    /** 酒品描述 */
    @Schema(description = "酒品描述")
    private String description;

    /** 状态 */
    @Schema(description = "状态")
    private String status;

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
