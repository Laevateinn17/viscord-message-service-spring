package com.viscord.message_service.mapper;

import com.viscord.message_service.dto.CreateMessageRequest;
import com.viscord.message_service.dto.MessageResponse;
import com.viscord.message_service.model.message.Message;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MessageMentionMapper.class, AttachmentMapper.class})
public abstract class MessageMapper {
    public abstract MessageResponse toDto(Message message);
    public abstract List<MessageResponse> toDto(List<Message> messages);
    public abstract Message toEntity(CreateMessageRequest request);
}
