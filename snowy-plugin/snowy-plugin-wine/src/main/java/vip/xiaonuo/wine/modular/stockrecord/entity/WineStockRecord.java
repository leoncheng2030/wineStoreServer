package vip.xiaonuo.wine.modular.stockrecord.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.util.Date;

/**
 * 库存记录表实体
 *
 * @author system
 * @date 2025/09/21
 **/
@Getter
@Setter
@TableName("wine_stock_record")
public class WineStockRecord extends CommonEntity {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 设备ID */
    @Schema(description = "设备ID")
    private String deviceId;

    /** 操作类型：RESTOCK-补货，CONSUME-消费，ADJUST-调整 */
    @Schema(description = "操作类型")
    private String operationType;

    /** 变更前库存 */
    @Schema(description = "变更前库存")
    private Integer beforeStock;

    /** 变更后库存 */
    @Schema(description = "变更后库存")
    private Integer afterStock;

    /** 变更数量（正数为增加，负数为减少） */
    @Schema(description = "变更数量")
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