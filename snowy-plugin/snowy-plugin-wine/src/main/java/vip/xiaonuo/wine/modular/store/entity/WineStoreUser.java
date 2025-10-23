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
package vip.xiaonuo.wine.modular.store.entity;

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
 * 店员表实体
 *
 * @author jetox
 * @date  2025/08/20 19:08
 **/
@Getter
@Setter
@TableName("wine_store_user")
public class WineStoreUser extends CommonEntity {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 门店ID */
    @Schema(description = "门店ID")
    @Trans(type = TransType.SIMPLE,target = WineStore.class,fields = "storeName",alias = "")
    private String storeId;

    /** 用户ID */
    @Schema(description = "用户ID")
    private String clientUserId;

    /** 用户昵称 */
    @Schema(description = "用户昵称")
    private String clientUserName;

    /** 用户手机号 */
    @Schema(description = "用户手机号")
    private String clientUserPhone;

    /** 状态 */
    @Schema(description = "状态")
    private String status;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

}
