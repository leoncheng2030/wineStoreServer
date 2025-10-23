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
package vip.xiaonuo.wine.modular.devicerole.param;

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
 * 设备角色定义表编辑参数
 *
 * @author jetox
 * @date  2025/09/21 09:16
 **/
@Getter
@Setter
public class WineDeviceRoleEditParam {

    /** 主键 */
    @ExcelProperty("主键")
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 角色编码 */
    @ExcelProperty("角色编码")
    @Schema(description = "角色编码")
    private String roleCode;

    /** 角色名称 */
    @ExcelProperty("角色名称")
    @Schema(description = "角色名称")
    private String roleName;

    /** 角色描述 */
    @ExcelProperty("角色描述")
    @Schema(description = "角色描述")
    private String roleDescription;

    /** 权限列表（JSON格式存储权限代码数组） */
    @ExcelProperty("权限列表（JSON格式存储权限代码数组）")
    @Schema(description = "权限列表（JSON格式存储权限代码数组）")
    private String permissions;

    /** 默认佣金比例（百分比） */
    @ExcelProperty("默认佣金比例（百分比）")
    @Schema(description = "默认佣金比例（百分比）")
    private BigDecimal defaultCommissionRate;

    /** 角色级别（数字越小级别越高） */
    @ExcelProperty("角色级别（数字越小级别越高）")
    @Schema(description = "角色级别（数字越小级别越高）")
    private Integer roleLevel;

    /** 状态（ENABLE-启用, DISABLE-禁用） */
    @ExcelProperty("状态（ENABLE-启用, DISABLE-禁用）")
    @Schema(description = "状态（ENABLE-启用, DISABLE-禁用）")
    private String status;

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
