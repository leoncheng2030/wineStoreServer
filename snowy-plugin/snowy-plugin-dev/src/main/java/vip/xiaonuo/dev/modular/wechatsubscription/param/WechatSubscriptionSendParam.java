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
package vip.xiaonuo.dev.modular.wechatsubscription.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 微信订阅消息发送参数
 *
 * @author xuyuxiang
 * @date 2024/12/25 10:35
 **/
@Getter
@Setter
public class WechatSubscriptionSendParam {

    /** 用户openid */
    @Schema(description = "用户openid")
    @NotBlank(message = "用户openid不能为空")
    private String openid;

    /** 模板ID */
    @Schema(description = "模板ID")
    @NotBlank(message = "模板ID不能为空")
    private String templateId;

    /** 消息数据 */
    @Schema(description = "消息数据")
    private Map<String, String> data;

    /** 跳转页面 */
    @Schema(description = "跳转页面")
    private String page;

    /** 小程序状态 */
    @Schema(description = "小程序状态(developer、trial、formal)")
    private String miniprogramState = "formal";

    /** 业务类型 */
    @Schema(description = "业务类型")
    private String businessType;

    /** 业务ID */
    @Schema(description = "业务ID")
    private String businessId;

    /** 关联的站内信ID */
    @Schema(description = "关联的站内信ID")
    private String messageId;
}