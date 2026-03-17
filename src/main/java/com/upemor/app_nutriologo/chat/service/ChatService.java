package com.upemor.app_nutriologo.chat.service;

import com.upemor.app_nutriologo.chat.dto.ChatMessageDTO;
import com.upemor.app_nutriologo.chat.model.ChatMessage;
import com.upemor.app_nutriologo.chat.repository.ChatMessageRepository;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository, UsuarioRepository usuarioRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public String getChatId(Long senderId, Long recipientId) {
        return senderId < recipientId ? senderId + "_" + recipientId : recipientId + "_" + senderId;
    }

    public ChatMessage save(ChatMessageDTO chatMessageDTO) {
        Usuario sender = usuarioRepository.findById(chatMessageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Usuario recipient = usuarioRepository.findById(chatMessageDTO.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        String chatId = getChatId(sender.getId(), recipient.getId());

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(chatId);
        chatMessage.setSender(sender);
        chatMessage.setRecipient(recipient);
        chatMessage.setContent(chatMessageDTO.getContent());
        chatMessage.setTimestamp(chatMessageDTO.getTimestamp() != null ? chatMessageDTO.getTimestamp() : LocalDateTime.now());
        
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatHistory(Long senderId, Long recipientId) {
        String chatId = getChatId(senderId, recipientId);
        return chatMessageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }
}
