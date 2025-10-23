package vip.xiaonuo.wine.modular.agentapply.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 代理商申请分页参数
 *
 * @author jetox
 * @date 2025/09/20
 */
@Getter
@Setter
public class WineAgentApplyPageParam {

    /** 当前页 */
    @Schema(description = "当前页码")
    private Integer current;

    /** 每页条数 */
    @Schema(description = "每页条数")
    private Integer size;

    /** 排序字段 */
    @Schema(description = "排序字段，字段驼峰名称，如：createTime")
    private String sortField;

    /** 排序方式 */
    @Schema(description = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;

    /** 关键词 */
    @Schema(description = "关键词")
    private String searchKey;

    /** 申请用户ID */
    @Schema(description = "申请用户ID")
    private String clientUserId;

    /** 真实姓名 */
    @Schema(description = "真实姓名")
    private String realName;

    /** 手机号码 */
    @Schema(description = "手机号码")
    private String phone;

    /** 申请状态 */
    @Schema(description = "申请状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝")
    private String status;

    /** 开始时间 */
    @Schema(description = "开始时间")
    private String startTime;

    /** 结束时间 */
    @Schema(description = "结束时间")
    private String endTime;
}