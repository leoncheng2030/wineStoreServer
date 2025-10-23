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
package vip.xiaonuo.pay.core.wx;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

/**
 * 微信支付Api封装
 *
 * @author xuyuxiang
 * @date 2023/3/31 15:53
 **/
public class WxPayApi {

    /**
     * 微信公众号AppSecret（用于JSAPI支付）
     */
    public static String WX_WP_APP_SECRET;

    /**
     * 退款回调地址
     */
    public static String REFUND_NOTIFY_URL;

    /**
     * 提现回调地址
     */
    public static String TRANSFER_NOTIFY_URL;

    /**
     * 获取微信支付Api
     *
     * @author xuyuxiang
     * @date 2023/3/31 16:22
     **/
    public static WxPayService me() {
        WxPayConfig wxPayConfig = WxPayApiConfigKit.getWxPayConfig();
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        return wxPayService;
    }
}
