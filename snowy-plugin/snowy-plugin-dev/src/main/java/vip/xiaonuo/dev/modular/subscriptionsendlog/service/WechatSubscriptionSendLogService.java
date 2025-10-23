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
package vip.xiaonuo.dev.modular.subscriptionsendlog.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.dev.modular.subscriptionsendlog.entity.WechatSubscriptionSendLog;
import vip.xiaonuo.dev.modular.subscriptionsendlog.param.WechatSubscriptionSendLogAddParam;
import vip.xiaonuo.dev.modular.subscriptionsendlog.param.WechatSubscriptionSendLogEditParam;
import vip.xiaonuo.dev.modular.subscriptionsendlog.param.WechatSubscriptionSendLogIdParam;
import vip.xiaonuo.dev.modular.subscriptionsendlog.param.WechatSubscriptionSendLogPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 订阅消息发送记录表Service接口
 *
 * @author jetox
 * @date  2025/09/29 00:50
 **/
public interface WechatSubscriptionSendLogService extends IService<WechatSubscriptionSendLog> {

    /**
     * 获取订阅消息发送记录表分页
     *
     * @author jetox
     * @date  2025/09/29 00:50
     */
    Page<WechatSubscriptionSendLog> page(WechatSubscriptionSendLogPageParam wechatSubscriptionSendLogPageParam);

    /**
     * 添加订阅消息发送记录表
     *
     * @author jetox
     * @date  2025/09/29 00:50
     */
    void add(WechatSubscriptionSendLogAddParam wechatSubscriptionSendLogAddParam);

    /**
     * 编辑订阅消息发送记录表
     *
     * @author jetox
     * @date  2025/09/29 00:50
     */
    void edit(WechatSubscriptionSendLogEditParam wechatSubscriptionSendLogEditParam);

    /**
     * 删除订阅消息发送记录表
     *
     * @author jetox
     * @date  2025/09/29 00:50
     */
    void delete(List<WechatSubscriptionSendLogIdParam> wechatSubscriptionSendLogIdParamList);

    /**
     * 获取订阅消息发送记录表详情
     *
     * @author jetox
     * @date  2025/09/29 00:50
     */
    WechatSubscriptionSendLog detail(WechatSubscriptionSendLogIdParam wechatSubscriptionSendLogIdParam);

    /**
     * 获取订阅消息发送记录表详情
     *
     * @author jetox
     * @date  2025/09/29 00:50
     **/
    WechatSubscriptionSendLog queryEntity(String id);

    /**
     * 下载订阅消息发送记录表导入模板
     *
     * @author jetox
     * @date  2025/09/29 00:50
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入订阅消息发送记录表
     *
     * @author jetox
     * @date  2025/09/29 00:50
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出订阅消息发送记录表
     *
     * @author jetox
     * @date  2025/09/29 00:50
     */
    void exportData(List<WechatSubscriptionSendLogIdParam> wechatSubscriptionSendLogIdParamList, HttpServletResponse response) throws IOException;

    /**
     * 检查用户当日内是否有发送成功的订阅消息
     *
     * @param openid 用户openid
     * @param templateId 模板ID
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 是否已发送成功
     * @author jetox
     * @date  2025/09/29 15:00
     */
    boolean hasSentSuccessToday(String openid, String templateId, String businessType, String businessId);
}
