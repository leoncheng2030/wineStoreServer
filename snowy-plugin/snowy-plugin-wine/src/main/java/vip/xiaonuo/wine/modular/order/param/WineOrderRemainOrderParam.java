package vip.xiaonuo.wine.modular.order.param;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WineOrderRemainOrderParam {
    /** 订单号 */
    @ExcelProperty("订单号")
    @Schema(description = "订单id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 剩余脉冲 */
    @Schema(description = "剩余脉冲")
    private Integer quantity;
}
