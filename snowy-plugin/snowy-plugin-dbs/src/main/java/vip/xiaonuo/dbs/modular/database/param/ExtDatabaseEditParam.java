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
package vip.xiaonuo.dbs.modular.database.param;

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
 * 数据源编辑参数
 *
 * @author jetox
 * @date  2025/07/28 00:35
 **/
@Getter
@Setter
public class ExtDatabaseEditParam {

    /** ID */
    @ExcelProperty("ID")
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 名称 */
    @ExcelProperty("名称")
    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "poolName不能为空")
    private String poolName;

    /** 连接URL */
    @ExcelProperty("连接URL")
    @Schema(description = "连接URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "url不能为空")
    private String url;

    /** 用户名 */
    @ExcelProperty("用户名")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "username不能为空")
    private String username;

    /** 密码 */
    @ExcelProperty("密码")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "password不能为空")
    private String password;

    /** 驱动名称 */
    @ExcelProperty("驱动名称")
    @Schema(description = "驱动名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "driverName不能为空")
    private String driverName;

    /** 分类 */
    @ExcelProperty("分类")
    @Schema(description = "分类")
    private String category;

    /** 排序码 */
    @ExcelProperty("排序码")
    @Schema(description = "排序码")
    private Integer sortCode;

}
