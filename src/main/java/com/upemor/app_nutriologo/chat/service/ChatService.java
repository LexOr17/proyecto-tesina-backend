package com.upemor.app_nutriologo.chat.service;

import com.upemor.app_nutriologo.chat.dto.ChatMessageDTO;
import com.upemor.app_nutriologo.chat.entity.ChatMessage;
import com.upemor.app_nutriologo.chat.repository.ChatMessageRepository;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UsuarioRepository usuarioRepository;

    public String getChatId(Long senderId, Long recipientId) {
        return senderId < recipientId ? senderId + "_" + recipientId : recipientId + "_" + senderId;
    }

    public ChatMessage save(ChatMessageDTO chatMessageDTO) {
        Usuario sender = usuarioRepository.findById(chatMessageDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario remitente no encontrado con ID: " + chatMessageDTO.getSenderId()));

        Usuario recipient = usuarioRepository.findById(chatMessageDTO.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario destinatario no encontrado con ID: " + chatMessageDTO.getRecipientId()));

        String chatId = getChatId(sender.getId(), recipient.getId());

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(chatId);
        chatMessage.setSender(sender);
        chatMessage.setRecipient(recipient);
        chatMessage.setContent(chatMessageDTO.getContent());

        java.time.LocalDateTime ts = java.time.LocalDateTime.now();
        if (chatMessageDTO.getTimestamp() != null && !chatMessageDTO.getTimestamp().trim().isEmpty()) {
            try {
                ts = java.time.Instant.parse(chatMessageDTO.getTimestamp())
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
            } catch (Exception e) {
                try {
                    ts = java.time.LocalDateTime.parse(chatMessageDTO.getTimestamp());
                } catch (Exception ex) {
                    // Fallback to current time
                }
            }
        }
        chatMessage.setTimestamp(ts);

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatHistory(Long senderId, Long recipientId) {
        String chatId = getChatId(senderId, recipientId);
        return chatMessageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }
}
