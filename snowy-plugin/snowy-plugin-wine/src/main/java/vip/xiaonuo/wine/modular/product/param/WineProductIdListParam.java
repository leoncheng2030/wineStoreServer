package vip.xiaonuo.wine.modular.product.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WineProductIdListParam {
    /** id集合 */
    @Schema(description = "id集合")
    @NotNull(message = "idList不能为空")
    private List<String> idList;
}
