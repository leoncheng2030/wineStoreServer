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
package vip.xiaonuo.dev.modular.usersubscription.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.dev.modular.usersubscription.entity.WechatUserSubscription;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionAddParam;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionEditParam;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionIdParam;
import vip.xiaonuo.dev.modular.usersubscription.param.WechatUserSubscriptionPageParam;
import java.io.IOException;
import java.util.List;

/**
 * 用户订阅消息授权表Service接口
 *
 * @author jetox
 * @date  2025/09/29 00:49
 **/
public interface WechatUserSubscriptionService extends IService<WechatUserSubscription> {

    /**
     * 获取用户订阅消息授权表分页
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    Page<WechatUserSubscription> page(WechatUserSubscriptionPageParam wechatUserSubscriptionPageParam);

    /**
     * 添加用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void add(WechatUserSubscriptionAddParam wechatUserSubscriptionAddParam);

    /**
     * 编辑用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void edit(WechatUserSubscriptionEditParam wechatUserSubscriptionEditParam);

    /**
     * 删除用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void delete(List<WechatUserSubscriptionIdParam> wechatUserSubscriptionIdParamList);

    /**
     * 获取用户订阅消息授权表详情
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    WechatUserSubscription detail(WechatUserSubscriptionIdParam wechatUserSubscriptionIdParam);

    /**
     * 获取用户订阅消息授权表详情
     *
     * @author jetox
     * @date  2025/09/29 00:49
     **/
    WechatUserSubscription queryEntity(String id);

    /**
     * 下载用户订阅消息授权表导入模板
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出用户订阅消息授权表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void exportData(List<WechatUserSubscriptionIdParam> wechatUserSubscriptionIdParamList, HttpServletResponse response) throws IOException;

    /**
     * 更新用户订阅消息发送记录
     *
     * @param openid 用户openid
     * @param templateId 模板ID
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 是否更新成功
     * @author jetox
     * @date  2025/09/29 15:30
     */
    boolean updateSendRecord(String openid, String templateId, String businessType, String businessId);
}
