package vip.xiaonuo.wine.modular.stockrecord.enums;

import lombok.Getter;

/**
 * 库存操作类型枚举
 *
 * @author system
 * @date 2025/09/21
 **/
@Getter
public enum WineStockOperationTypeEnum {

    /** 补货 */
    RESTOCK("RESTOCK", "补货"),

    /** 消费 */
    CONSUME("CONSUME", "消费"),

    /** 调整 */
    ADJUST("ADJUST", "调整");

    private final String value;
    private final String label;

    WineStockOperationTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}