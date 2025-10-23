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
package vip.xiaonuo.dev.modular.subscriptionsendlog.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订阅消息发送记录表实体
 *
 * @author jetox
 * @date  2025/09/29 00:50
 **/
@Getter
@Setter
@TableName("wechat_subscription_send_log")
public class WechatSubscriptionSendLog {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 用户ID */
    @Schema(description = "用户ID")
    private String clientUserId;

    /** 微信openid */
    @Schema(description = "微信openid")
    private String openid;

    /** 模板ID */
    @Schema(description = "模板ID")
    private String templateId;

    /** 关联的站内信ID */
    @Schema(description = "关联的站内信ID")
    private String messageId;

    /** 发送的数据内容JSON */
    @Schema(description = "发送的数据内容JSON")
    private String sendData;

    /** 发送状态(1成功/0失败) */
    @Schema(description = "发送状态(1成功/0失败)")
    private String sendStatus;

    /** 微信返回的消息ID */
    @Schema(description = "微信返回的消息ID")
    private String wechatMsgId;

    /** 错误码 */
    @Schema(description = "错误码")
    private String errorCode;

    /** 错误信息 */
    @Schema(description = "错误信息")
    private String errorMsg;

    /** 发送时间 */
    @Schema(description = "发送时间")
    private Date sendTime;

    /** 微信接口返回数据 */
    @Schema(description = "微信接口返回数据")
    private String responseData;

    /** 业务类型 */
    @Schema(description = "业务类型")
    private String businessType;

    /** 业务ID */
    @Schema(description = "业务ID")
    private String businessId;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户 */
    @Schema(description = "创建用户")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;
}
