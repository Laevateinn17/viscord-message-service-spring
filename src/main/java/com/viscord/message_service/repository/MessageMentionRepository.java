package com.viscord.message_service.repository;

import com.viscord.message_service.model.message.MessageMention;
import com.viscord.message_service.model.message.MessageMentionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageMentionRepository extends JpaRepository<MessageMention, MessageMentionId> {

}
