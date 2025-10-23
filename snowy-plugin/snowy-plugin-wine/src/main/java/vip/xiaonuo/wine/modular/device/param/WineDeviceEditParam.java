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
package vip.xiaonuo.wine.modular.device.param;

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
 * 设备信息表编辑参数
 *
 * @author jetox
 * @date  2025/07/24 07:57
 **/
@Getter
@Setter
public class WineDeviceEditParam {

    /** 主键 */
    @ExcelProperty("主键")
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 设备编码 */
    @ExcelProperty("设备编码")
    @Schema(description = "设备编码")
    private String deviceCode;

    /** uuid */
    @ExcelProperty("uuid")
    @Schema(description = "uuid")
    private String uuid;

    /** 运营方式 */
    @Schema(description = "运营方式",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String operationType;


    /** 代理商用户ID */
    @Schema(description = "代理商用户ID")
    private String agentUserId;

    /** 设备名称 */
    @ExcelProperty("设备名称")
    @Schema(description = "设备名称")
    private String deviceName;

    /** 所属门店ID */
    @ExcelProperty("所属门店ID")
    @Schema(description = "所属门店ID")
    private String storeId;

    /** 当前绑定的酒品ID */
    @ExcelProperty("当前绑定的酒品ID")
    @Schema(description = "当前绑定的酒品ID")
    private String currentProductId;

    /** 库存 */
    @Schema(description = "库存")
    private Integer stock;

    /** 单位脉冲数 */
    @Schema(description = "单位脉冲数")
    private Double pulseRatio;



    /** 排序码 */
    @ExcelProperty("排序码")
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 备注 */
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @ExcelProperty("扩展信息")
    @Schema(description = "扩展信息")
    private String extJson;

}
