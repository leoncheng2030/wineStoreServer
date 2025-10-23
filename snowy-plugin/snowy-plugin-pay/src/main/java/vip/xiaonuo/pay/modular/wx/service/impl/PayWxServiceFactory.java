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
package vip.xiaonuo.pay.modular.wx.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.pay.core.config.PayConfigure;

import jakarta.annotation.Resource;

/**
 * 微信支付服务工厂类
 * 根据配置选择普通模式或服务商模式的实现
 *
 * @author xuyuxiang
 * @date 2024/12/19 17:00
 **/
@Slf4j
@Component
public class PayWxServiceFactory {

    @Resource
    private PayWxNormalServiceImpl payWxNormalService;

    @Resource
    private PayWxPartnerServiceImpl payWxPartnerService;

    /**
     * 根据配置获取对应的微信支付服务实现
     *
     * @return 微信支付服务实现
     */
    public AbstractPayWxService getPayWxService() {
        boolean isPartnerMode = PayConfigure.isWxPartnerPayEnabled();
        
        if (isPartnerMode) {
            log.debug("使用微信支付服务商模式");
            return payWxPartnerService;
        } else {
            log.debug("使用微信支付普通模式");
            return payWxNormalService;
        }
    }

    /**
     * 获取普通模式服务实现
     *
     * @return 普通模式服务实现
     */
    public PayWxNormalServiceImpl getNormalService() {
        return payWxNormalService;
    }

    /**
     * 获取服务商模式服务实现
     *
     * @return 服务商模式服务实现
     */
    public PayWxPartnerServiceImpl getPartnerService() {
        return payWxPartnerService;
    }

    /**
     * 检查是否为服务商模式
     *
     * @return true-服务商模式，false-普通模式
     */
    public boolean isPartnerMode() {
        return PayConfigure.isWxPartnerPayEnabled();
    }
}
