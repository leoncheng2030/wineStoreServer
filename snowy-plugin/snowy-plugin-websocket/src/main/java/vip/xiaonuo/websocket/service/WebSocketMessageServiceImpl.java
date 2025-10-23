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
package vip.xiaonuo.websocket.service;

import org.springframework.stereotype.Service;
import vip.xiaonuo.common.websocket.WebSocketMessageService;
import vip.xiaonuo.websocket.handler.AuthenticatedWebSocketHandler;

/**
 * WebSocket消息服务实现类
 * 实现通用接口，提供具体的WebSocket消息推送功能
 *
 * @author AI Assistant
 * @date 2025/01/20
 **/
@Service
public class WebSocketMessageServiceImpl implements WebSocketMessageService {

    @Override
    public void sendMessageToAllClients(String message) {
        AuthenticatedWebSocketHandler.sendMessageToAllClients(message);
    }

    @Override
    public void sendMessageToClient(String clientId, String message) {
        AuthenticatedWebSocketHandler.sendMessageToClient(clientId, message);
    }

    @Override
    public void sendMessageToUser(String loginId, String message) {
        String clientId = getClientIdByLoginId(loginId);
        if (clientId != null) {
            sendMessageToClient(clientId, message);
        }
    }

    @Override
    public boolean isClientOnline(String clientId) {
        return AuthenticatedWebSocketHandler.isClientOnline(clientId);
    }

    @Override
    public int getOnlineClientCount() {
        return AuthenticatedWebSocketHandler.getOnlineClientCount();
    }

    @Override
    public String getClientIdByLoginId(String loginId) {
        return AuthenticatedWebSocketHandler.getClientIdByLoginId(loginId);
    }
}