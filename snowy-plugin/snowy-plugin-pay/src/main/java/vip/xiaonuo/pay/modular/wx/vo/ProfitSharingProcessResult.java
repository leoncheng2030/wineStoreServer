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
package vip.xiaonuo.pay.modular.wx.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 分账处理结果
 *
 * @author xuyuxiang
 * @date 2024/12/19
 */
@Getter
@Setter
public class ProfitSharingProcessResult {

    /** 分账状态：SUCCESS-成功，PROCESSING-处理中，FAILED-失败 */
    @Schema(description = "分账状态")
    private String state;

    /** 分账单号 */
    @Schema(description = "分账单号")
    private String outOrderNo;

    /** 微信分账单号 */
    @Schema(description = "微信分账单号")
    private String orderNo;

    /** 分账结果描述 */
    @Schema(description = "分账结果描述")
    private String description;

    /** 接收方数量 */
    @Schema(description = "接收方数量")
    private Integer receiverCount;

    /** 是否需要后续查询 */
    @Schema(description = "是否需要后续查询")
    private Boolean needQuery;

    public ProfitSharingProcessResult() {
    }

    public ProfitSharingProcessResult(String state, String outOrderNo, String description) {
        this.state = state;
        this.outOrderNo = outOrderNo;
        this.description = description;
        this.needQuery = "PROCESSING".equals(state);
    }
}