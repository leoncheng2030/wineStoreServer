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
package vip.xiaonuo.wine.modular.withdrawrecord.param;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现记录表编辑参数
 *
 * @author jetox
 * @date  2025/07/24 08:50
 **/
@Getter
@Setter
public class WineWithdrawRecordEditParam {

    /** 主键ID */
    @ExcelProperty("主键ID")
    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 提现单号 */
    @ExcelProperty("提现单号")
    @Schema(description = "提现单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "withdrawNo不能为空")
    private String withdrawNo;

    /** 用户ID */
    @ExcelProperty("用户ID")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "userId不能为空")
    private String userId;

    /** 用户昵称 */
    @ExcelProperty("用户昵称")
    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "userNickname不能为空")
    private String userNickname;

    /** 提现金额(元) */
    @ExcelProperty("提现金额(元)")
    @Schema(description = "提现金额(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "withdrawAmount不能为空")
    private BigDecimal withdrawAmount;

    /** 手续费(元) */
    @ExcelProperty("手续费(元)")
    @Schema(description = "手续费(元)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "serviceFee不能为空")
    private BigDecimal serviceFee;

    /** 实际到账金额(元) */
    @ExcelProperty("实际到账金额(元)")
    @Schema(description = "实际到账金额(元)")
    private BigDecimal actualAmount;

    /** 提现方式 */
    @ExcelProperty("提现方式")
    @Schema(description = "提现方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "withdrawType不能为空")
    private String withdrawType;

    /** 账户信息 */
    @ExcelProperty("账户信息")
    @Schema(description = "账户信息")
    private String accountInfo;

    /** 提现状态 */
    @ExcelProperty("提现状态")
    @Schema(description = "提现状态")
    private String status;

    /** 失败原因 */
    @ExcelProperty("失败原因")
    @Schema(description = "失败原因")
    private String failReason;

    /** 第三方交易号 */
    @ExcelProperty("第三方交易号")
    @Schema(description = "第三方交易号")
    private String transactionId;

    /** 备注 */
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @ExcelProperty("扩展信息")
    @Schema(description = "扩展信息")
    private String extJson;

}
