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
package vip.xiaonuo.wine.modular.deviceuser.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分佣配置添加参数
 *
 * @author jetox
 * @date  2025/07/24 08:44
 **/
@Getter
@Setter
public class WineDeviceUserAddParam {
    
    /** 设备ID */
    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    
    /** 用户ID */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    /** 佣金比例 */
    @Schema(description = "佣金比例（百分比，如5.5表示5.5%）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "佣金比例不能为空")
    @DecimalMin(value = "0", message = "佣金比例不能小于0")
    @DecimalMax(value = "100", message = "佣金比例不能大于100")
    private BigDecimal commissionRate;
    
    /** 角色编码 */
    @Schema(description = "角色编码")
    private String roleCode;
    
    /** 设备编码（保留字段，向后兼容） */
    @Schema(description = "设备编码")
    private String deviceCode;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
