package vip.xiaonuo.wine.modular.store.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineStoreNearbyParam {
    /** 当前页 */
    @Schema(description = "当前页码")
    private Integer current;

    /** 每页条数 */
    @Schema(description = "每页条数")
    private Integer size;

    /** 经度 */
    @Schema(description = "经度")
    private String longitude;

    /** 纬度 */
    @Schema(description = "纬度")
    private String latitude;

    /** 排序字段 */
    @Schema(description = "排序字段，字段驼峰名称，如：userName")
    private String sortField;

    /** 排序方式 */
    @Schema(description = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;

    /** 关键词 */
    @Schema(description = "关键词")
    private String searchKey;

    /** 门店名称 */
    @Schema(description = "门店名称")
    private String storeName;
}
