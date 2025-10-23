package vip.xiaonuo.client.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 客户端用户信息
 *
 * @author jetox
 * @date 2025/09/20
 */
@Getter
@Setter
public class ClientUserInfo {

    /** id */
    @Schema(description = "id")
    private String id;

    /** 姓名 */
    @Schema(description = "姓名")
    private String name;

    /** 昵称 */
    @Schema(description = "昵称")
    private String nickname;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 头像 */
    @Schema(description = "头像")
    private String avatar;

    /** 最后登录时间 */
    @Schema(description = "最后登录时间")
    private Date lastLoginTime;
}