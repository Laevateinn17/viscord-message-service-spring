package com.viscord.message_service.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;


@Data
public class MessageResponse {
    private String id;
    private String senderId;
    private String content;
    private String channelId;
    private String isPinned;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> mentions;
    private List<AttachmentResponse> attachments;
}
