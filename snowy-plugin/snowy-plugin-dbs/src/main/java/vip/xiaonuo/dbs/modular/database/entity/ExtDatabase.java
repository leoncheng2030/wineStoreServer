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
package vip.xiaonuo.dbs.modular.database.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;
import vip.xiaonuo.dbs.core.utils.PasswordEncryptUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据源实体
 *
 * @author jetox
 * @date  2025/07/28 00:35
 **/
@Getter
@Setter
@TableName("ext_database")
public class ExtDatabase extends CommonEntity {

    /** ID */
    @TableId
    @Schema(description = "ID")
    private String id;

    /** 租户id */
    @Schema(description = "租户id")
    private String tenantId;

    /** 名称 */
    @Schema(description = "名称")
    private String poolName;

    /** 连接URL */
    @Schema(description = "连接URL")
    private String url;

    /** 用户名 */
    @Schema(description = "用户名")
    private String username;

    /** 密码 */
    @Schema(description = "密码")
    private String password;

    /** 驱动名称 */
    @Schema(description = "驱动名称")
    private String driverName;

    /** 分类 */
    @Schema(description = "分类")
    private String category;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

    /**
     * 获取密码（解密后）
     *
     * @return 原始密码
     */
    public String getDecryptedPassword() {
        if (StrUtil.isNotEmpty(this.password)) {
            try {
                // 尝试解密密码
                return PasswordEncryptUtil.decrypt(this.password);
            } catch (Exception e) {
                // 如果解密失败，可能是因为密码未加密，直接返回原值
                // 这样可以兼容数据库中已有的未加密密码
                return this.password;
            }
        }
        return this.password;
    }

    /**
     * 设置密码（加密后）
     *
     * @param password 原始密码
     */
    public void setEncryptedPassword(String password) {
        if (StrUtil.isNotEmpty(password)) {
            this.password = PasswordEncryptUtil.encrypt(password);
        } else {
            this.password = password;
        }
    }
}
