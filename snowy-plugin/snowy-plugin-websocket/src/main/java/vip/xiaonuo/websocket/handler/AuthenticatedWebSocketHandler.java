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
package vip.xiaonuo.websocket.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

import vip.xiaonuo.auth.core.util.StpClientUtil;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.SaManager;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 带认证的WebSocket处理器
 * 提供基于Sa-Token的身份验证WebSocket连接管理功能
 *
 * @author AI Assistant
 * @date 2025/01/20
 **/
@Slf4j
public class AuthenticatedWebSocketHandler implements WebSocketHandler {

    // 存储WebSocket连接，key为clientId，value为WebSocketSession
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENT_SESSIONS = new ConcurrentHashMap<>();
    
    // 存储用户ID与clientId的映射关系
    private static final ConcurrentHashMap<String, String> LOGIN_ID_TO_CLIENT_ID = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("收到新的WebSocket连接请求，sessionId: {}", session.getId());
        
        try {
            // 从URL参数中获取clientId、token等信息
            URI uri = session.getUri();
            String query = uri.getQuery();
            
            log.info("WebSocket连接URI: {}", uri.toString());
            log.info("WebSocket连接查询参数: {}", query);
            
            String clientId = null;
            String token = null;
            String loginId = null;
            
            if (StrUtil.isNotEmpty(query)) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        
                        switch (key) {
                            case "clientId":
                                clientId = value;
                                break;
                            case "token":
                                token = value;
                                break;
                            case "loginId":
                                loginId = value;
                                break;
                        }
                    }
                }
            }

            // 验证参数
            if (StrUtil.isEmpty(clientId)) {
                log.warn("WebSocket连接缺少clientId参数，关闭连接，sessionId: {}", session.getId());
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            
            log.info("解析到的连接参数 - clientId: {}, token: {}, loginId: {}", clientId, 
                    token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "null", loginId);

            // Token验证（必需）
            if (StrUtil.isEmpty(token)) {
                log.warn("WebSocket连接缺少token参数，拒绝连接，sessionId: {}", session.getId());
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Missing token"));
                return;
            }

            // 验证token有效性
            String verifiedLoginId = null;
            try {
                // 使用Sa-Token验证C端用户token
                verifiedLoginId = (String) StpClientUtil.getLoginIdByToken(token);
                if (StrUtil.isEmpty(verifiedLoginId)) {
                    log.warn("WebSocket连接token无效，拒绝连接，sessionId: {}, token前缀: {}", 
                            session.getId(), token.substring(0, Math.min(10, token.length())));
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid token"));
                    return;
                }
                
                // 验证token是否过期
                String tokenKey = StpClientUtil.stpLogic.splicingKeyTokenValue(token);
                long tokenTimeout = SaManager.getSaTokenDao().getTimeout(tokenKey);
                if (tokenTimeout == -2) {
                    log.warn("WebSocket连接token已过期，拒绝连接，sessionId: {}, loginId: {}", session.getId(), verifiedLoginId);
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Token expired"));
                    return;
                }
                
                log.info("WebSocket连接token验证成功，loginId: {}, tokenTimeout: {}", verifiedLoginId, tokenTimeout);
                
            } catch (NotLoginException e) {
                log.warn("WebSocket连接token验证失败，sessionId: {}, 错误: {}", session.getId(), e.getMessage());
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Token verification failed"));
                return;
            } catch (Exception e) {
                log.error("WebSocket连接token验证异常，sessionId: {}", session.getId(), e);
                session.close(CloseStatus.SERVER_ERROR.withReason("Token verification error"));
                return;
            }
            
            // 如果没有提供loginId参数，使用从token中获取的loginId
            if (StrUtil.isEmpty(loginId)) {
                loginId = verifiedLoginId;
                log.info("使用token中的loginId: {}", loginId);
            } else if (!loginId.equals(verifiedLoginId)) {
                // 如果提供了loginId但与token不匹配，拒绝连接
                log.warn("WebSocket连接loginId与token不匹配，拒绝连接，提供的loginId: {}, token中的loginId: {}", 
                        loginId, verifiedLoginId);
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("LoginId mismatch"));
                return;
            }

            // 存储连接
            CLIENT_SESSIONS.put(clientId, session);
            
            // 如果有loginId，建立映射关系
            if (StrUtil.isNotEmpty(loginId)) {
                LOGIN_ID_TO_CLIENT_ID.put(loginId, clientId);
            }

            log.info("WebSocket连接建立成功，clientId: {}, loginId: {}", clientId, loginId);

            // 发送连接成功消息
            JSONObject response = new JSONObject();
            response.put("type", "auth_success");
            response.put("clientId", clientId);
            response.put("timestamp", System.currentTimeMillis());
            
            session.sendMessage(new TextMessage(response.toString()));

        } catch (Exception e) {
            log.error("WebSocket连接建立失败", e);
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            if (message instanceof TextMessage) {
                String payload = ((TextMessage) message).getPayload();
                log.debug("收到WebSocket消息: {}", payload);

                // 解析消息
                JSONObject messageJson = JSONUtil.parseObj(payload);
                String type = messageJson.getStr("type");

                switch (type) {
                    case "heartbeat":
                        // 处理心跳消息
                        handleHeartbeat(session, messageJson);
                        break;
                    case "ping":
                        // 处理ping消息
                        handlePing(session, messageJson);
                        break;
                    default:
                        log.debug("收到未知类型的WebSocket消息: {}", type);
                        break;
                }
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误", exception);
        
        // 清理连接
        removeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket连接关闭，状态: {}", closeStatus);
        
        // 清理连接
        removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 处理心跳消息
     */
    private void handleHeartbeat(WebSocketSession session, JSONObject messageJson) {
        try {
            // 响应心跳
            JSONObject response = new JSONObject();
            response.put("type", "heartbeat_response");
            response.put("timestamp", System.currentTimeMillis());
            
            session.sendMessage(new TextMessage(response.toString()));
        } catch (Exception e) {
            log.error("处理心跳消息失败", e);
        }
    }

    /**
     * 处理ping消息
     */
    private void handlePing(WebSocketSession session, JSONObject messageJson) {
        try {
            // 响应pong
            JSONObject response = new JSONObject();
            response.put("type", "pong");
            response.put("timestamp", System.currentTimeMillis());
            
            session.sendMessage(new TextMessage(response.toString()));
        } catch (Exception e) {
            log.error("处理ping消息失败", e);
        }
    }

    /**
     * 移除会话
     */
    private void removeSession(WebSocketSession session) {
        // 查找并移除对应的clientId
        String clientIdToRemove = null;
        for (String clientId : CLIENT_SESSIONS.keySet()) {
            if (CLIENT_SESSIONS.get(clientId).equals(session)) {
                clientIdToRemove = clientId;
                break;
            }
        }

        if (StrUtil.isNotEmpty(clientIdToRemove)) {
            CLIENT_SESSIONS.remove(clientIdToRemove);
            
            // 移除loginId映射
            String loginIdToRemove = null;
            for (String loginId : LOGIN_ID_TO_CLIENT_ID.keySet()) {
                if (clientIdToRemove.equals(LOGIN_ID_TO_CLIENT_ID.get(loginId))) {
                    loginIdToRemove = loginId;
                    break;
                }
            }
            if (StrUtil.isNotEmpty(loginIdToRemove)) {
                LOGIN_ID_TO_CLIENT_ID.remove(loginIdToRemove);
            }
            
            log.info("WebSocket连接已清理，clientId: {}", clientIdToRemove);
        }
    }

    // ==================== 公共方法，供外部调用 ====================

    /**
     * 发送消息给所有客户端
     */
    public static void sendMessageToAllClients(String message) {
        CLIENT_SESSIONS.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (Exception e) {
                log.error("向客户端发送消息失败", e);
            }
        });
    }

    /**
     * 发送消息给指定客户端
     */
    public static void sendMessageToClient(String clientId, String message) {
        WebSocketSession session = CLIENT_SESSIONS.get(clientId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("向客户端{}发送消息失败", clientId, e);
            }
        } else {
            log.warn("客户端{}不在线或连接已关闭", clientId);
        }
    }

    /**
     * 根据用户ID获取客户端ID
     */
    public static String getClientIdByLoginId(String loginId) {
        return LOGIN_ID_TO_CLIENT_ID.get(loginId);
    }

    /**
     * 检查客户端是否在线
     */
    public static boolean isClientOnline(String clientId) {
        WebSocketSession session = CLIENT_SESSIONS.get(clientId);
        return session != null && session.isOpen();
    }

    /**
     * 获取在线客户端数量
     */
    public static int getOnlineClientCount() {
        return (int) CLIENT_SESSIONS.values().stream()
                .filter(WebSocketSession::isOpen)
                .count();
    }
}