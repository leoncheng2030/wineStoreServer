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
package vip.xiaonuo.wine.modular.useraccount.param;

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
 * 账户列表编辑参数
 *
 * @author jetox
 * @date  2025/07/24 08:55
 **/
@Getter
@Setter
public class WineUserAccountEditParam {

    /** 主键ID */
    @ExcelProperty("主键ID")
    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 用户ID */
    @ExcelProperty("用户ID")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "userId不能为空")
    private String userId;

    /** 用户昵称 */
    @ExcelProperty("用户昵称")
    @Schema(description = "用户昵称")
    private String userNickname;

    /** 总余额(元) */
    @ExcelProperty("总余额(元)")
    @Schema(description = "总余额(元)")
    private BigDecimal totalBalance;

    /** 可用余额(元) */
    @ExcelProperty("可用余额(元)")
    @Schema(description = "可用余额(元)")
    private BigDecimal availableBalance;

    /** 冻结余额(元) */
    @ExcelProperty("冻结余额(元)")
    @Schema(description = "冻结余额(元)")
    private BigDecimal frozenBalance;

    /** 累计佣金(元) */
    @ExcelProperty("累计佣金(元)")
    @Schema(description = "累计佣金(元)")
    private BigDecimal totalCommission;

    /** 累计提现(元) */
    @ExcelProperty("累计提现(元)")
    @Schema(description = "累计提现(元)")
    private BigDecimal totalWithdraw;

    /** 账户状态(NORMAL:正常,FROZEN:冻结,DISABLED:禁用) */
    @ExcelProperty("账户状态(NORMAL:正常,FROZEN:冻结,DISABLED:禁用)")
    @Schema(description = "账户状态(NORMAL:正常,FROZEN:冻结,DISABLED:禁用)")
    private String status;

    /** 最后佣金时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("最后佣金时间")
    @Schema(description = "最后佣金时间")
    private Date lastCommissionTime;

    /** 最后提现时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("最后提现时间")
    @Schema(description = "最后提现时间")
    private Date lastWithdrawTime;

    /** 备注 */
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    /** 扩展信息 */
    @ExcelProperty("扩展信息")
    @Schema(description = "扩展信息")
    private String extJson;

}
