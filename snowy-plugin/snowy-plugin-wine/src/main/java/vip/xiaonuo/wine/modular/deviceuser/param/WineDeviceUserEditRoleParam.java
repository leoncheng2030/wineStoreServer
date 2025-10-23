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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * 编辑设备用户角色参数
 *
 * @author jetox
 * @date  2025/09/21 16:30
 **/
@Getter
@Setter
public class WineDeviceUserEditRoleParam {

    /** 设备用户关系ID */
    @Schema(description = "设备用户关系ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "关系ID不能为空")
    private String id;

    /** 角色编码 */
    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /** 佣金比例 */
    @Schema(description = "佣金比例")
    @NotNull(message = "佣金比例不能为空")
    @DecimalMin(value = "0", message = "佣金比例不能小于0")
    @DecimalMax(value = "100", message = "佣金比例不能大于100")
    private BigDecimal commissionRate;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}