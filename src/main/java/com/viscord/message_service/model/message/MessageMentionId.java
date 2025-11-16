package com.viscord.message_service.model.message;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Embeddable
public class MessageMentionId implements Serializable {
    @Column(name = "message_id")
    private UUID messageId;
    @Column(name = "user_id")
    private UUID userId;

    public MessageMentionId(UUID messageId, UUID userId) {
        this.messageId = messageId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageMentionId that = (MessageMentionId) o;
        return Objects.equals(messageId, that.messageId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, userId);
    }
}
