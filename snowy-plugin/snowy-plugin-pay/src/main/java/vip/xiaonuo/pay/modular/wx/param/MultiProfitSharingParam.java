package vip.xiaonuo.pay.modular.wx.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 多接收方分账参数
 *
 * @author xuyuxiang
 * @date 2024/12/19
 */
@Getter
@Setter
public class MultiProfitSharingParam {
    
    /** 商户订单编号 */
    @Schema(description = "商户订单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "outTradeNo不能为空")
    private String outTradeNo;

    /** 分账接收方列表 */
    @Schema(description = "分账接收方列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分账接收方列表不能为空")
    private List<ProfitReceiver> receivers;

    /** 分账描述 */
    @Schema(description = "分账描述")
    private String description;

    /**
     * 分账接收方
     */
    @Getter
    @Setter
    public static class ProfitReceiver {
        /** 接收方类型：PERSONAL_OPENID-个人openid, MERCHANT_ID-商户号 */
        @Schema(description = "接收方类型")
        private String type;

        /** 接收方账户：openid或商户号 */
        @Schema(description = "接收方账户", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "接收方账户不能为空")
        private String account;

        /** 分账金额（元） */
        @Schema(description = "分账金额", requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal amount;

        /** 分账描述 */
        @Schema(description = "分账描述")
        private String description;

        /** 内部用户ID（用于关联佣金记录） */
        @Schema(description = "内部用户ID")
        private String userId;

        /** 佣金类型：PLATFORM-平台, STORE-门店, AGENT-代理, CHANNEL-渠道 */
        @Schema(description = "佣金类型")
        private String commissionType;

        /** 佣金记录ID */
        @Schema(description = "佣金记录ID")
        private String commissionRecordId;
    }

    /**
     * 计算总分账金额
     */
    public BigDecimal getTotalAmount() {
        return receivers.stream()
                .map(ProfitReceiver::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 验证分账参数
     */
    public void validate() {
        if (receivers == null || receivers.isEmpty()) {
            throw new IllegalArgumentException("分账接收方列表不能为空");
        }
        
        // 检查接收方数量限制（微信限制每次最多50个接收方）
        if (receivers.size() > 50) {
            throw new IllegalArgumentException("分账接收方数量不能超过50个");
        }
        
        for (ProfitReceiver receiver : receivers) {
            if (receiver.getAmount() == null || receiver.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("分账金额必须大于0");
            }
            
            // 检查分账金额精度（微信要求最小单位为分）
            if (receiver.getAmount().scale() > 2) {
                throw new IllegalArgumentException("分账金额精度不能超过2位小数");
            }
        }
    }
}
