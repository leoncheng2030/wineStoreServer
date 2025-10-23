package vip.xiaonuo.wine.modular.order.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 代理商订单分页参数
 *
 * @author Qoder
 * @date 2024/09/20
 */
@Getter
@Setter
public class WineOrderAgentPageParam {
    
    /** 当前页 */
    @Schema(description = "当前页码")
    private Integer current;

    /** 每页条数 */
    @Schema(description = "每页条数")
    private Integer size;

    /** 排序字段 */
    @Schema(description = "排序字段，字段驼峰名称，如：createTime")
    private String sortField;

    /** 排序方式 */
    @Schema(description = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;

    /** 关键词 */
    @Schema(description = "关键词，可搜索订单号或设备编号")
    private String searchKey;
    
    /** 订单状态 */
    @Schema(description = "订单状态")
    private String status;
    
    /** 开始时间 */
    @Schema(description = "开始时间")
    private String startTime;
    
    /** 结束时间 */
    @Schema(description = "结束时间")
    private String endTime;
}