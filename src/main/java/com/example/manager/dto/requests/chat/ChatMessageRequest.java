package com.example.manager.dto.requests.chat;

import com.example.manager.util.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageRequest {
    private String roomId;
    private String content;
}
