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
package vip.xiaonuo.wine.modular.productcategory.param;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 酒品分类表编辑参数
 *
 * @author jetox
 * @date  2025/07/24 08:11
 **/
@Getter
@Setter
public class WineProductCategoryEditParam {

    /** 主键 */
    @ExcelProperty("主键")
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 分类名称 */
    @ExcelProperty("分类名称")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "categoryName不能为空")
    private String categoryName;

    /** 父级分类ID，0表示顶级分类 */
    @ExcelProperty("父级分类ID，0表示顶级分类")
    @Schema(description = "父级分类ID，0表示顶级分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "parentId不能为空")
    private String parentId;

    /** 分类级别，1为一级分类 */
    @ExcelProperty("分类级别，1为一级分类")
    @Schema(description = "分类级别，1为一级分类")
    private Integer categoryLevel;

    /** 分类描述 */
    @ExcelProperty("分类描述")
    @Schema(description = "分类描述")
    private String description;

    /** 状态 */
    @ExcelProperty("状态")
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "status不能为空")
    private String status;

    /** 排序码，数字越小越靠前 */
    @ExcelProperty("排序码，数字越小越靠前")
    @Schema(description = "排序码，数字越小越靠前")
    private Integer sortCode;

    /** 备注 */
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

}
