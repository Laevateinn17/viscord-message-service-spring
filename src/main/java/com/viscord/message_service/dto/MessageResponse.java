package com.viscord.message_service.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Data
public class MessageResponse {
    private UUID id;
    private UUID senderId;
    private String content;
    private UUID channelId;
    private String isPinned;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> mentions;
    private List<AttachmentResponse> attachments;
}
