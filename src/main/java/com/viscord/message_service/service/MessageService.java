package com.viscord.message_service.service;

import com.viscord.message_service.dto.CreateMessageRequest;
import com.viscord.message_service.dto.MessageResponse;
import com.viscord.message_service.exception.BadRequestException;
import com.viscord.message_service.mapper.MessageMapper;
import com.viscord.message_service.mapper.MessageMentionMapper;
import com.viscord.message_service.model.message.Message;
import com.viscord.message_service.model.message.MessageMention;
import com.viscord.message_service.repository.MessageRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
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
        message = messageRepository.save(message);

        if (!request.getMentions().isEmpty()) {
            for (UUID userId : request.getMentions()) {
                MessageMention mention = new MessageMention();
                mention.setMessage(message);
                mention.setMessageId(message.getId());
                mention.setUserId(userId);

                message.addMention(mention);

            }

            message = messageRepository.save(message);
        }

        return messageMapper.toDto(message);
    }

}
