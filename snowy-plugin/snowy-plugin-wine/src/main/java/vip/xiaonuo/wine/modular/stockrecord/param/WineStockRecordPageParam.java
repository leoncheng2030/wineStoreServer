package vip.xiaonuo.wine.modular.stockrecord.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 库存记录查询参数
 *
 * @author system
 * @date 2025/09/21
 **/
@Getter
@Setter
public class WineStockRecordPageParam {

    /** 设备ID */
    @Schema(description = "设备ID")
    private String deviceId;

    /** 操作类型：RESTOCK-补货，CONSUME-消费，ADJUST-调整 */
    @Schema(description = "操作类型")
    private String operationType;

    /** 操作人员ID */
    @Schema(description = "操作人员ID")
    private String operatorId;

    /** 操作人员类型：DEVICE_MANAGER-设备管理员，AGENT-代理商，SYSTEM-系统 */
    @Schema(description = "操作人员类型")
    private String operatorType;

}