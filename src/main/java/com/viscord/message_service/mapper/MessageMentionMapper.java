package com.viscord.message_service.mapper;

import com.viscord.message_service.model.message.MessageMention;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MessageMentionMapper {
    default UUID toDto(MessageMention mention) {
        return mention.getUserId();
    }

    default List<UUID> toDto(List<MessageMention> mentions) {
        if (mentions == null) return null;
        return mentions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
