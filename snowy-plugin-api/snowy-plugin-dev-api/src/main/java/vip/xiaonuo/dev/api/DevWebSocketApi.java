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
package vip.xiaonuo.dev.api;

/**
 * WebSocket API接口
 * 用于wine模块的WebSocket消息推送，不影响框架原有的SSE功能
 *
 * @author AI Assistant
 * @date 2025/01/20
 **/
public interface DevWebSocketApi {

    /**
     * 推送消息到所有WebSocket客户端
     *
     * @param msg 推送消息（JSON格式）
     * @author AI Assistant
     * @date 2025/01/20
     **/
    void sendMessageToAllClient(String msg);

    /**
     * 根据clientId发送消息给某一WebSocket客户端
     *
     * @param clientId 客户端id
     * @param msg 推送消息（JSON格式）
     * @author AI Assistant
     * @date 2025/01/20
     **/
    void sendMessageToOneClient(String clientId, String msg);

    /**
     * 根据用户id获取WebSocket客户端id
     *
     * @param loginId 用户id
     * @return 客户端id，如果未找到则返回null
     * @author AI Assistant
     * @date 2025/01/20
     **/
    String getClientIdByLoginId(String loginId);

    /**
     * 检查WebSocket客户端是否在线
     *
     * @param clientId 客户端id
     * @return true-在线，false-离线
     * @author AI Assistant
     * @date 2025/01/20
     **/
    boolean isClientOnline(String clientId);

    /**
     * 获取在线WebSocket客户端数量
     *
     * @return 在线客户端数量
     * @author AI Assistant
     * @date 2025/01/20
     **/
    int getOnlineClientCount();
}