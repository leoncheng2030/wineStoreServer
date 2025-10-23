package vip.xiaonuo.pay.core.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.pay.core.wx.WxPayApi;
import vip.xiaonuo.pay.core.wx.WxPayApiConfigKit;

@Configuration
public class PayConfigure {
    /**
     * 微信相关配置
     */
    // 应用编号
    private static final String SNOWY_PAY_WX_APP_ID_KEY = "SNOWY_PAY_WX_APP_ID";
    // 公众号AppSecret
    private static final String SNOWY_PAY_WX_APP_SECRET_KEY = "SNOWY_PAY_WX_APP_SECRET";
    // 商户号
    private static final String SNOWY_PAY_WX_MCH_ID_KEY = "SNOWY_PAY_WX_MCH_ID";
    // 商户V2密钥
    private static final String SNOWY_PAY_WX_MCH_KEY_KEY = "SNOWY_PAY_WX_MCH_KEY";
    // p12证书
    private static final String SNOWY_PAY_WX_KEY_PATH_KEY = "SNOWY_PAY_WX_KEY_PATH";
    // apiclient_key.pem证书
    private static final String SNOWY_PAY_WX_PRIVATE_KEY_PATH_KEY = "SNOWY_PAY_WX_PRIVATE_KEY_PATH";
    // apiclient_cert.pem证书
    private static final String SNOWY_PAY_WX_PRIVATE_CERT_PATH_KEY = "SNOWY_PAY_WX_PRIVATE_CERT_PATH";
    // API V3证书序列号值
    private static final String SNOWY_PAY_WX_CERT_SERIAL_NO_KEY = "SNOWY_PAY_WX_CERT_SERIAL_NO";
    // API V3秘钥值
    private static final String SNOWY_PAY_WX_API_V3_KEY_KEY = "SNOWY_PAY_WX_API_V3_KEY";
    // 支付回调地址
    private static final String SNOWY_PAY_WX_NOTIFY_URL_KEY = "SNOWY_PAY_WX_NOTIFY_URL";
    // 退款回调地址
    private static final String SNOWY_PAY_WX_REFUND_NOTIFY_URL_KEY = "SNOWY_PAY_WX_REFUND_NOTIFY_URL";
    // 提现回调地址
    private static final String SNOWY_PAY_WX_TRANSFER_NOTIFY_URL_KEY = "SNOWY_PAY_WX_TRANSFER_NOTIFY_URL";

    /* =========微信服务商支付配置========= */
    
    /** 微信服务商模式是否启用 */
    private static final String SNOWY_PAY_WX_PARTNER_ENABLED_KEY = "SNOWY_PAY_WX_PARTNER_ENABLED";
    
    /** 微信服务商AppId */
    private static final String SNOWY_PAY_WX_PARTNER_APP_ID_KEY = "SNOWY_PAY_WX_PARTNER_APP_ID";
    
    /** 微信服务商AppSecret */
    private static final String SNOWY_PAY_WX_PARTNER_APP_SECRET_KEY = "SNOWY_PAY_WX_PARTNER_APP_SECRET";
    
    /** 微信服务商商户号 */
    private static final String SNOWY_PAY_WX_PARTNER_MCH_ID_KEY = "SNOWY_PAY_WX_PARTNER_MCH_ID";
    
    /** 微信服务商商户密钥 */
    private static final String SNOWY_PAY_WX_PARTNER_MCH_KEY_KEY = "SNOWY_PAY_WX_PARTNER_MCH_KEY";
    
    /** 微信服务商证书路径 */
    private static final String SNOWY_PAY_WX_PARTNER_KEY_PATH_KEY = "SNOWY_PAY_WX_PARTNER_KEY_PATH";
    
    /** 微信服务商私钥路径 */
    private static final String SNOWY_PAY_WX_PARTNER_PRIVATE_KEY_PATH_KEY = "SNOWY_PAY_WX_PARTNER_PRIVATE_KEY_PATH";
    
    /** 微信服务商私钥证书路径 */
    private static final String SNOWY_PAY_WX_PARTNER_PRIVATE_CERT_PATH_KEY = "SNOWY_PAY_WX_PARTNER_PRIVATE_CERT_PATH";
    
    /** 微信服务商证书序列号 */
    private static final String SNOWY_PAY_WX_PARTNER_CERT_SERIAL_NO_KEY = "SNOWY_PAY_WX_PARTNER_CERT_SERIAL_NO";
    
    /** 微信服务商APIv3密钥 */
    private static final String SNOWY_PAY_WX_PARTNER_API_V3_KEY_KEY = "SNOWY_PAY_WX_PARTNER_API_V3_KEY";
    
    /** 微信服务商支付回调地址 */
    private static final String SNOWY_PAY_WX_PARTNER_NOTIFY_URL_KEY = "SNOWY_PAY_WX_PARTNER_NOTIFY_URL";
    
    /** 微信服务商退款回调地址 */
    private static final String SNOWY_PAY_WX_PARTNER_REFUND_NOTIFY_URL_KEY = "SNOWY_PAY_WX_PARTNER_REFUND_NOTIFY_URL";
    
    /** 微信服务商转账回调地址 */
    private static final String SNOWY_PAY_WX_PARTNER_TRANSFER_NOTIFY_URL_KEY = "SNOWY_PAY_WX_PARTNER_TRANSFER_NOTIFY_URL";

    @Bean
    public WxPayService wxPayService() {
        // 缓存微信支付配置
        if (!isWxPartnerPayEnabled()) {
            WxPayConfig wxPayApiConfig = getWxPayApiConfig();
            WxPayApiConfigKit.setThreadLocalWxPayConfig(wxPayApiConfig);
            WxPayService wxPayService = new WxPayServiceImpl();
            wxPayService.setConfig(wxPayApiConfig);
            return wxPayService;
         }else {
            WxPayConfig wxPayConfig = getWxPartnerPayApiConfig();
            WxPayApiConfigKit.setThreadLocalWxPayConfig(wxPayConfig);
            WxPayService wxPayService = new WxPayServiceImpl();
            wxPayService.setConfig(wxPayConfig);
            return wxPayService;
        }

    }
    /**
     * 获取微信支付配置
     *
     * @author xuyuxiang
     * @date 2022/8/16 14:08
     **/
    public static WxPayConfig getWxPayApiConfig() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String appId = devConfigApi.getValueByKey(SNOWY_PAY_WX_APP_ID_KEY);
        if (ObjectUtil.isEmpty(appId)) {
            throw new CommonException("微信支付参数未正确配置：appId为空");
        }
        String appSecret = devConfigApi.getValueByKey(SNOWY_PAY_WX_APP_SECRET_KEY);
        if (ObjectUtil.isEmpty(appSecret)) {
            throw new CommonException("微信支付参数未正确配置：appSecret为空");
        }
        String mchId = devConfigApi.getValueByKey(SNOWY_PAY_WX_MCH_ID_KEY);
        if (ObjectUtil.isEmpty(mchId)) {
            throw new CommonException("微信支付参数未正确配置：mchId为空");
        }
        String mchKey = devConfigApi.getValueByKey(SNOWY_PAY_WX_MCH_KEY_KEY);
        if (ObjectUtil.isEmpty(mchKey)) {
            throw new CommonException("微信支付参数未正确配置：mchKey为空");
        }
        String keyPath = devConfigApi.getValueByKey(SNOWY_PAY_WX_KEY_PATH_KEY);
        if (ObjectUtil.isEmpty(keyPath)) {
            throw new CommonException("微信支付参数未正确配置：keyPath为空");
        }
        String privateKeyPath = devConfigApi.getValueByKey(SNOWY_PAY_WX_PRIVATE_KEY_PATH_KEY);
        if (ObjectUtil.isEmpty(privateKeyPath)) {
            throw new CommonException("微信支付参数未正确配置：privateKeyPath为空");
        }
        String privateCertPath = devConfigApi.getValueByKey(SNOWY_PAY_WX_PRIVATE_CERT_PATH_KEY);
        if (ObjectUtil.isEmpty(privateCertPath)) {
            throw new CommonException("微信支付参数未正确配置：privateCertPath为空");
        }
        String certSerialNo = devConfigApi.getValueByKey(SNOWY_PAY_WX_CERT_SERIAL_NO_KEY);
        if (ObjectUtil.isEmpty(certSerialNo)) {
            throw new CommonException("微信支付参数未正确配置：certSerialNo为空");
        }
        String apiV3Key = devConfigApi.getValueByKey(SNOWY_PAY_WX_API_V3_KEY_KEY);
        if (ObjectUtil.isEmpty(apiV3Key)) {
            throw new CommonException("微信支付参数未正确配置：apiV3Key为空");
        }
        String notifyUrl = devConfigApi.getValueByKey(SNOWY_PAY_WX_NOTIFY_URL_KEY);
        if (ObjectUtil.isEmpty(notifyUrl)) {
            throw new CommonException("微信支付参数未正确配置：notifyUrl为空");
        }
        String refundNotifyUrl = devConfigApi.getValueByKey(SNOWY_PAY_WX_REFUND_NOTIFY_URL_KEY);
        if (ObjectUtil.isEmpty(refundNotifyUrl)) {
            throw new CommonException("微信支付参数未正确配置：refundNotifyUrl为空");
        }
        String transferNotifyUrl = devConfigApi.getValueByKey(SNOWY_PAY_WX_TRANSFER_NOTIFY_URL_KEY);
        if (ObjectUtil.isEmpty(transferNotifyUrl)) {
            throw new CommonException("微信支付参数未正确配置：transferNotifyUrl为空");
        }
        try {
            WxPayConfig wxPayConfig = new WxPayConfig();
            wxPayConfig.setAppId(appId);
            wxPayConfig.setMchId(mchId);
            wxPayConfig.setMchKey(mchKey);
            wxPayConfig.setKeyPath(keyPath);
            wxPayConfig.setPrivateKeyPath(privateKeyPath);
            wxPayConfig.setPrivateCertPath(privateCertPath);
            wxPayConfig.setCertSerialNo(certSerialNo);
            wxPayConfig.setApiV3Key(apiV3Key);
            wxPayConfig.setNotifyUrl(notifyUrl);
            // 微信配置微信公众号AppSecret，此处设置到WxPayApi的属性
            WxPayApi.WX_WP_APP_SECRET = appSecret;
            // 微信配置无退款回调地址，此处设置到WxPayApi的属性
            WxPayApi.REFUND_NOTIFY_URL = refundNotifyUrl;
            // 微信配置无转账回调地址，此处设置到WxPayApi的属性
            WxPayApi.TRANSFER_NOTIFY_URL = transferNotifyUrl;
            return wxPayConfig;
        } catch (Exception exception) {
            throw new CommonException("微信支付参数配置存在错误，原因：{}", exception.getMessage());
        }
    }
    /**
     * 获取微信服务商支付配置
     *
     * @author xuyuxiang
     * @date 2024/12/19 14:08
     **/
    public static WxPayConfig getWxPartnerPayApiConfig() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        
        // 检查是否启用服务商模式
        String enabled = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_ENABLED_KEY);
        if (ObjectUtil.isEmpty(enabled) || !"true".equals(enabled)) {
            throw new CommonException("微信服务商支付未启用");
        }
        
        String appId = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_APP_ID_KEY);
        if (ObjectUtil.isEmpty(appId)) {
            throw new CommonException("微信服务商支付参数未正确配置：appId为空");
        }
        String appSecret = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_APP_SECRET_KEY);
        if (ObjectUtil.isEmpty(appSecret)) {
            throw new CommonException("微信服务商支付参数未正确配置：appSecret为空");
        }
        String mchId = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_MCH_ID_KEY);
        if (ObjectUtil.isEmpty(mchId)) {
            throw new CommonException("微信服务商支付参数未正确配置：mchId为空");
        }
        String mchKey = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_MCH_KEY_KEY);
        if (ObjectUtil.isEmpty(mchKey)) {
            throw new CommonException("微信服务商支付参数未正确配置：mchKey为空");
        }
        String keyPath = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_KEY_PATH_KEY);
        if (ObjectUtil.isEmpty(keyPath)) {
            throw new CommonException("微信服务商支付参数未正确配置：keyPath为空");
        }
        String privateKeyPath = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_PRIVATE_KEY_PATH_KEY);
        if (ObjectUtil.isEmpty(privateKeyPath)) {
            throw new CommonException("微信服务商支付参数未正确配置：privateKeyPath为空");
        }
        String privateCertPath = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_PRIVATE_CERT_PATH_KEY);
        if (ObjectUtil.isEmpty(privateCertPath)) {
            throw new CommonException("微信服务商支付参数未正确配置：privateCertPath为空");
        }
        String certSerialNo = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_CERT_SERIAL_NO_KEY);
        if (ObjectUtil.isEmpty(certSerialNo)) {
            throw new CommonException("微信服务商支付参数未正确配置：certSerialNo为空");
        }
        String apiV3Key = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_API_V3_KEY_KEY);
        if (ObjectUtil.isEmpty(apiV3Key)) {
            throw new CommonException("微信服务商支付参数未正确配置：apiV3Key为空");
        }
        
        // 获取服务商模式的回调地址配置
        String notifyUrl = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_NOTIFY_URL_KEY);
        String refundNotifyUrl = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_REFUND_NOTIFY_URL_KEY);
        String transferNotifyUrl = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_TRANSFER_NOTIFY_URL_KEY);
        
        try {
            WxPayConfig wxPayConfig = new WxPayConfig();
            wxPayConfig.setAppId(appId);
            wxPayConfig.setMchId(mchId);
            wxPayConfig.setMchKey(mchKey);
            wxPayConfig.setKeyPath(keyPath);
            wxPayConfig.setPrivateKeyPath(privateKeyPath);
            wxPayConfig.setPrivateCertPath(privateCertPath);
            wxPayConfig.setCertSerialNo(certSerialNo);
            wxPayConfig.setApiV3Key(apiV3Key);
            // 设置服务商模式的回调地址
            // 设置服务商模式的回调地址
            if (ObjectUtil.isNotEmpty(notifyUrl)) {
                wxPayConfig.setNotifyUrl(notifyUrl);
            }
            // 关键修复：设置服务商模式的退款和转账回调地址到静态变量
            // 根据记忆知识，服务商模式时需要显式设置这些静态变量
            if (ObjectUtil.isNotEmpty(refundNotifyUrl)) {
                WxPayApi.REFUND_NOTIFY_URL = refundNotifyUrl;
            }
            if (ObjectUtil.isNotEmpty(transferNotifyUrl)) {
                WxPayApi.TRANSFER_NOTIFY_URL = transferNotifyUrl;
            }
            return wxPayConfig;
        } catch (Exception exception) {
            throw new CommonException("微信服务商支付参数配置存在错误，原因：{}", exception.getMessage());
        }
    }

    /**
     * 检查微信服务商支付是否启用
     *
     * @return boolean
     * @author xuyuxiang
     * @date 2024/12/19 14:10
     **/
    public static boolean isWxPartnerPayEnabled() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String enabled = devConfigApi.getValueByKey(SNOWY_PAY_WX_PARTNER_ENABLED_KEY);
        return "true".equals(enabled);
    }
    
    /**
     * 获取微信服务商支付配置启用状态
     *
     * @return boolean
     * @author xuyuxiang  
     * @date 2024/12/19 14:10
     **/
    public static boolean getWxPartnerPayConfigEnable() {
        return isWxPartnerPayEnabled();
    }
    
    /**
     * 获取微信服务商子商户号
     *
     * @return String
     * @author xuyuxiang
     * @date 2024/12/19 14:10
     **/
    public static String getWxPartnerPayConfigSubMchId() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        return devConfigApi.getValueByKey("SNOWY_PAY_WX_PARTNER_SUB_MCH_ID");
    }
}
