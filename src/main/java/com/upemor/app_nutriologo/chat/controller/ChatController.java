package com.upemor.app_nutriologo.chat.controller;

import com.upemor.app_nutriologo.chat.dto.ChatMessageDTO;
import com.upemor.app_nutriologo.chat.entity.ChatMessage;
import com.upemor.app_nutriologo.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDTO chatMessageDTO) {
        ChatMessage savedMessage = chatService.save(chatMessageDTO);

        ChatMessageDTO responseDto = ChatMessageDTO.builder()
                .chatId(savedMessage.getChatId())
                .senderId(savedMessage.getSender().getId())
                .recipientId(savedMessage.getRecipient().getId())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .build();

        // Enviar el mensaje al destinatario a través de WebSocket
        messagingTemplate.convertAndSendToUser(
                savedMessage.getRecipient().getId().toString(), // ID del usuario destinatario
                "/queue/messages", // Destino privado del usuario
                responseDto // El mensaje en formato DTO
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(
            @PathVariable Long senderId,
            @PathVariable Long recipientId) {

        List<ChatMessage> messages = chatService.getChatHistory(senderId, recipientId);

        List<ChatMessageDTO> messageDTOs = messages.stream().map(msg ->
                ChatMessageDTO.builder()
                        .chatId(msg.getChatId())
                        .senderId(msg.getSender().getId())
                        .recipientId(msg.getRecipient().getId())
                        .content(msg.getContent())
                        .timestamp(msg.getTimestamp())
                        .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(messageDTOs);
    }
}