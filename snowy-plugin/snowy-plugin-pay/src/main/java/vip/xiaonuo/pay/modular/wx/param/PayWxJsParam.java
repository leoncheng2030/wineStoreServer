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
package vip.xiaonuo.pay.modular.wx.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


/**
 * 微信支付OpenId参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 17:52
 */
@Getter
@Setter
public class PayWxJsParam {

    /** 商户订单编号 */
    @Schema(description = "商户订单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "outTradeNo不能为空")
    private String outTradeNo;

    /** 订单金额 */
    @Schema(description = "订单金额:单位分")
    private BigDecimal totalFee;


    @Schema(description = "订单描述")
    private String desc;

    @Schema(description = "订单信息")
    private String body;

    /** 附加数据 */
    @Schema(description = "附加数据")
    private String attach;

    /** 子商户号 */
    @Schema(description = "子商户号")
    private String subMchId;

    /** 子商户AppId */
    @Schema(description = "子商户AppId")
    private String subAppId;

}
