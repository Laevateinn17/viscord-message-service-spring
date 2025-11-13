package com.viscord.message_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AttachmentResponse {
    private UUID id;
    private String type;
    private String url;
}
