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
package vip.xiaonuo.wine.modular.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fhs.core.trans.anno.Trans;
import com.fhs.core.trans.constant.TransType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * wine_agent实体
 *
 * @author jetox
 * @date  2025/09/19 07:13
 **/
@Getter
@Setter
@TableName("wine_agent")
public class WineAgent extends CommonEntity {

    /** ID */
    @TableId
    @Schema(description = "ID")
    private String id;

    /** 用户ID */
    @Schema(description = "用户ID")
    @Trans(type = TransType.RPC, targetClassName = "vip.xiaonuo.client.modular.user.entity.ClientUser", fields = {"nickname","phone"}, alias = "client")
    private String clientUserId;

    /** 代理商编号 */
    @Schema(description = "代理商编号")
    private String agentCode;

    /** 子商户号 */
    @Schema(description = "子商户号")
    private String subMerId;

    /** 子应用ID */
    @Schema(description = "子应用ID")
    private String subAppId;

    /** 子商户分账比例 */
    @Schema(description = "子商户分账比例")
    private BigDecimal profitSharingMaxRate;

}
