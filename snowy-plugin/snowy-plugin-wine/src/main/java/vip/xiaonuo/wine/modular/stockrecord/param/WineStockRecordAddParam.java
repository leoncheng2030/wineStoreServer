package vip.xiaonuo.wine.modular.stockrecord.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 库存记录添加参数
 *
 * @author system
 * @date 2025/09/21
 **/
@Getter
@Setter
public class WineStockRecordAddParam {

    /** 设备ID */
    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /** 操作类型：RESTOCK-补货，CONSUME-消费，ADJUST-调整 */
    @Schema(description = "操作类型")
    @NotBlank(message = "操作类型不能为空")
    private String operationType;

    /** 变更前库存 */
    @Schema(description = "变更前库存")
    @NotNull(message = "变更前库存不能为空")
    private Integer beforeStock;

    /** 变更后库存 */
    @Schema(description = "变更后库存")
    @NotNull(message = "变更后库存不能为空")
    private Integer afterStock;

    /** 变更数量（正数为增加，负数为减少） */
    @Schema(description = "变更数量")
    @NotNull(message = "变更数量不能为空")
    private Integer changeQuantity;

    /** 操作人员ID */
    @Schema(description = "操作人员ID")
    private String operatorId;

    /** 操作人员类型：DEVICE_MANAGER-设备管理员，AGENT-代理商，SYSTEM-系统 */
    @Schema(description = "操作人员类型")
    private String operatorType;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

}