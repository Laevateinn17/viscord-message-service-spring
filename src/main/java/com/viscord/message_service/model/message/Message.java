package com.viscord.message_service.model.message;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    private String content;

    @Column(name = "is_pinned", nullable = false, columnDefinition = "boolean default false")
    private boolean isPinned;

    @Column(name = "channel_id", nullable = false)
    private UUID channelId;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MessageMention> mentions = new ArrayList<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    public void addMention(MessageMention mention) {
        mentions.add(mention);
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }
}
