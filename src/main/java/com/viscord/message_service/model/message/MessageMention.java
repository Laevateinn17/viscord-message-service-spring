package com.viscord.message_service.model.message;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@NoArgsConstructor
@Entity
@Table(name = "message_mention")
public class MessageMention {

    @Setter(AccessLevel.NONE)
    @EmbeddedId
    private MessageMentionId id = new MessageMentionId();

    @ManyToOne
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    private Message message;

    public UUID getUserId() {
        return id.getUserId();
    }

    public UUID getMessageId() {
        return id.getMessageId();
    }

    public void setUserId(UUID userId) {
        id.setUserId(userId);
    }

    public void setMessageId(UUID messageId) {
        id.setMessageId(messageId);
    }
}
