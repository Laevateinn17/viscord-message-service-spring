package com.viscord.message_service.service;

import com.viscord.message_service.dto.CreateMessageRequest;
import com.viscord.message_service.dto.MessageResponse;
import com.viscord.message_service.exception.BadRequestException;
import com.viscord.message_service.mapper.MessageMapper;
import com.viscord.message_service.model.message.Message;
import com.viscord.message_service.repository.MessageRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService  {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    public List<MessageResponse> getAllMessages() {
        return messageMapper.toDto(messageRepository.findAll());
    }

    public List<MessageResponse> getChannelMessages(UUID channelId) {
        return messageMapper.toDto(messageRepository.findAllByChannelIdOrderByCreatedAtAsc(channelId));
    }

    public MessageResponse createMessage(CreateMessageRequest request) {
        Message message = messageMapper.toEntity(request);

        return messageMapper.toDto(messageRepository.save(message));
    }

}
