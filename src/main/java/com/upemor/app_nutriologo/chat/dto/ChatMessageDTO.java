package com.upemor.app_nutriologo.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String chatId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private String timestamp;
}
