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
package vip.xiaonuo.websocket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import vip.xiaonuo.common.websocket.WebSocketMessageService;

/**
 * WebSocket测试控制器
 * 用于测试WebSocket功能
 *
 * @author AI Assistant
 * @date 2025/01/20
 **/
@RestController
@RequestMapping("/api/websocket")
public class WebSocketTestController {

    @Resource
    private WebSocketMessageService webSocketMessageService;

    /**
     * 获取在线客户端数量
     */
    @GetMapping("/online-count")
    public ResponseEntity<Integer> getOnlineCount() {
        int count = webSocketMessageService.getOnlineClientCount();
        return ResponseEntity.ok(count);
    }

    /**
     * 检查客户端是否在线
     */
    @GetMapping("/check-online")
    public ResponseEntity<Boolean> checkOnline(@RequestParam String clientId) {
        boolean online = webSocketMessageService.isClientOnline(clientId);
        return ResponseEntity.ok(online);
    }

    /**
     * 向指定客户端发送消息
     */
    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(@RequestParam String clientId, @RequestParam String message) {
        try {
            webSocketMessageService.sendMessageToClient(clientId, message);
            return ResponseEntity.ok("消息发送成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("消息发送失败: " + e.getMessage());
        }
    }

    /**
     * 向指定用户发送消息
     */
    @PostMapping("/send-user-message")
    public ResponseEntity<String> sendUserMessage(@RequestParam String loginId, @RequestParam String message) {
        try {
            webSocketMessageService.sendMessageToUser(loginId, message);
            return ResponseEntity.ok("用户消息发送成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("用户消息发送失败: " + e.getMessage());
        }
    }

    /**
     * 向所有客户端广播消息
     */
    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcast(@RequestParam String message) {
        try {
            webSocketMessageService.sendMessageToAllClients(message);
            return ResponseEntity.ok("广播消息发送成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("广播消息发送失败: " + e.getMessage());
        }
    }
}