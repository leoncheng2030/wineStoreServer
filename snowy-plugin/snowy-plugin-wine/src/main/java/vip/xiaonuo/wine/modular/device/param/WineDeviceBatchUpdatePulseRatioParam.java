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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * 设备批量更新脉冲数参数
 *
 * @author jetox
 * @date 2025/01/30
 */
@Getter
@Setter
@Schema(description = "设备批量更新脉冲数参数")
public class WineDeviceBatchUpdatePulseRatioParam {

    /** 脉冲比 */
    @Schema(description = "脉冲比", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "脉冲比不能为空")
    @DecimalMin(value = "0.01", message = "脉冲比必须大于0")
    private Double pulseRatio;
}