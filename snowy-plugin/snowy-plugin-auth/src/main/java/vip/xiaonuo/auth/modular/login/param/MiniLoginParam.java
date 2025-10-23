package vip.xiaonuo.auth.modular.login.param;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MiniLoginParam {
    /** 微信小程序code */
    private String code;

    /** 昵称 */
    private String nickName;

    /** 头像 */
    private String avatarUrl;

    /** 手机号 */
    private String phone;

    /** 手机号验证码 */
    private String phoneCode;

    /** 手机号授权码 */
    private String phoneAuthCode;

    /** 手机号解密数据 */
    private String encryptedData;

    /** 手机号解密向量 */
    private String iv;

    /** 设备 */
    private String device;
}
