package vip.xiaonuo.auth.modular.login.param;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MiniIsRegParam {
    /** 微信小程序code */
    private String code;

    /** 设备 */
    private String device;
}