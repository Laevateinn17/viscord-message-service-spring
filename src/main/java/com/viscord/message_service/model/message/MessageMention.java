package com.viscord.message_service.model.message;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter @NoArgsConstructor
@Entity
@Table(name = "message_mention")
public class MessageMention {

    @EmbeddedId
    private MessageMentionId id;

    @ManyToOne
    @MapsId("messageId")
//    @JoinColumn(name = "message_id")
    private Message message;

    public UUID getUserId() {
        return id.getUserId();
    }

    public UUID getMessageId() {
        return id.getMessageId();
    }
}
