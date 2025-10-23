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
package vip.xiaonuo.common.websocket;

/**
 * WebSocket消息推送接口
 * 提供抽象的消息推送能力，具体实现由WebSocket插件模块提供
 *
 * @author AI Assistant
 * @date 2025/01/20
 **/
public interface WebSocketMessageService {

    /**
     * 向所有客户端发送消息
     *
     * @param message 消息内容
     */
    void sendMessageToAllClients(String message);

    /**
     * 向指定客户端发送消息
     *
     * @param clientId 客户端ID
     * @param message 消息内容
     */
    void sendMessageToClient(String clientId, String message);

    /**
     * 根据用户ID发送消息
     *
     * @param loginId 用户ID
     * @param message 消息内容
     */
    void sendMessageToUser(String loginId, String message);

    /**
     * 检查客户端是否在线
     *
     * @param clientId 客户端ID
     * @return 是否在线
     */
    boolean isClientOnline(String clientId);

    /**
     * 获取在线客户端数量
     *
     * @return 在线数量
     */
    int getOnlineClientCount();

    /**
     * 根据用户ID获取客户端ID
     *
     * @param loginId 用户ID
     * @return 客户端ID，如果用户不在线则返回null
     */
    String getClientIdByLoginId(String loginId);
}