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
package vip.xiaonuo.dev.modular.subscriptiontemplate.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 微信订阅消息模板表添加参数
 *
 * @author jetox
 * @date  2025/09/29 00:49
 **/
@Getter
@Setter
public class WechatSubscriptionTemplateAddParam {

    /** 微信模板ID */
    @Schema(description = "微信模板ID")
    private String templateId;

    /** 模板标题 */
    @Schema(description = "模板标题")
    private String templateTitle;

    /** 模板内容示例 */
    @Schema(description = "模板内容示例")
    private String templateContent;

    /** 模板类型 */
    @Schema(description = "模板类型")
    private String templateType;

    /** 状态 */
    @Schema(description = "状态")
    private String status;

    /** 关键词配置JSON */
    @Schema(description = "关键词配置JSON")
    private String keywordConfig;

    /** 跳转页面路径 */
    @Schema(description = "跳转页面路径")
    private String jumpPage;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

}
