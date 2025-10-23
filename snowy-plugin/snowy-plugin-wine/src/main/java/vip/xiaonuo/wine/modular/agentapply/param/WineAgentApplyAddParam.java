package vip.xiaonuo.wine.modular.agentapply.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 代理商申请添加参数
 *
 * @author jetox
 * @date 2025/09/20
 */
@Getter
@Setter
public class WineAgentApplyAddParam {

    /** 申请用户ID */
    @Schema(description = "申请用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请用户ID不能为空")
    private String clientUserId;

    /** 真实姓名 */
    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /** 手机号码 */
    @Schema(description = "手机号码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号码不能为空")
    private String phone;

    /** 身份证号 */
    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    /** 省份 */
    @Schema(description = "省份", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "省份不能为空")
    private String province;

    /** 城市 */
    @Schema(description = "城市", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "城市不能为空")
    private String city;

    /** 区县 */
    @Schema(description = "区县", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "区县不能为空")
    private String district;

    /** 详细地址 */
    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "详细地址不能为空")
    private String address;

    /** 申请理由 */
    @Schema(description = "申请理由", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请理由不能为空")
    private String reason;
}