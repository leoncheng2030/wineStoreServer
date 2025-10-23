package vip.xiaonuo.wine.modular.agentapply.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.util.Date;

/**
 * 代理商申请记录实体
 *
 * @author jetox
 * @date 2025/09/20
 */
@Getter
@Setter
@TableName("wine_agent_apply")
@Schema(description = "代理商申请记录")
public class WineAgentApply  extends CommonEntity {

    /** ID */
    @TableId
    @Schema(description = "ID")
    private String id;

    /** 申请用户ID */
    @Schema(description = "申请用户ID")
    private String clientUserId;

    /** 真实姓名 */
    @Schema(description = "真实姓名")
    private String realName;

    /** 手机号码 */
    @Schema(description = "手机号码")
    private String phone;

    /** 身份证号 */
    @Schema(description = "身份证号")
    private String idCard;

    /** 省份 */
    @Schema(description = "省份")
    private String province;

    /** 城市 */
    @Schema(description = "城市")
    private String city;

    /** 区县 */
    @Schema(description = "区县")
    private String district;

    /** 详细地址 */
    @Schema(description = "详细地址")
    private String address;

    /** 申请理由 */
    @Schema(description = "申请理由")
    private String reason;

    /** 申请状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝 */
    @Schema(description = "申请状态")
    private String status;

    /** 审核意见 */
    @Schema(description = "审核意见")
    private String auditRemark;

    /** 审核时间 */
    @Schema(description = "审核时间")
    private Date auditTime;

    /** 审核用户 */
    @Schema(description = "审核用户")
    private String auditUser;
}