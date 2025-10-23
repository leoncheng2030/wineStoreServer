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
package vip.xiaonuo.dev.modular.subscriptiontemplate.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.dev.modular.subscriptiontemplate.entity.WechatSubscriptionTemplate;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplateAddParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplateEditParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplateIdParam;
import vip.xiaonuo.dev.modular.subscriptiontemplate.param.WechatSubscriptionTemplatePageParam;
import java.io.IOException;
import java.util.List;

/**
 * 微信订阅消息模板表Service接口
 *
 * @author jetox
 * @date  2025/09/29 00:49
 **/
public interface WechatSubscriptionTemplateService extends IService<WechatSubscriptionTemplate> {

    /**
     * 获取微信订阅消息模板表分页
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    Page<WechatSubscriptionTemplate> page(WechatSubscriptionTemplatePageParam wechatSubscriptionTemplatePageParam);

    /**
     * 添加微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void add(WechatSubscriptionTemplateAddParam wechatSubscriptionTemplateAddParam);

    /**
     * 编辑微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void edit(WechatSubscriptionTemplateEditParam wechatSubscriptionTemplateEditParam);

    /**
     * 删除微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void delete(List<WechatSubscriptionTemplateIdParam> wechatSubscriptionTemplateIdParamList);

    /**
     * 获取微信订阅消息模板表详情
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    WechatSubscriptionTemplate detail(WechatSubscriptionTemplateIdParam wechatSubscriptionTemplateIdParam);

    /**
     * 获取微信订阅消息模板表详情
     *
     * @author jetox
     * @date  2025/09/29 00:49
     **/
    WechatSubscriptionTemplate queryEntity(String id);

    /**
     * 下载微信订阅消息模板表导入模板
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出微信订阅消息模板表
     *
     * @author jetox
     * @date  2025/09/29 00:49
     */
    void exportData(List<WechatSubscriptionTemplateIdParam> wechatSubscriptionTemplateIdParamList, HttpServletResponse response) throws IOException;
}
