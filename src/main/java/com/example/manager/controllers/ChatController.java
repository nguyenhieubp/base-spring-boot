package com.example.manager.controllers;

import com.example.manager.dto.requests.chat.ChatMessageRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage") // Định nghĩa endpoint cho việc gửi tin nhắn
    @SendTo("/topic/messages") // Gửi tin nhắn đến tất cả client
    public String sendMessage(String message) {
        return message;
    }


    @MessageMapping("/chat.sendMessageToRoom") // Định nghĩa endpoint cho việc gửi tin nhắn theo phòng
    public void sendMessageToRoom(ChatMessageRequest chatMessage) {
        messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), chatMessage);
    }
}
