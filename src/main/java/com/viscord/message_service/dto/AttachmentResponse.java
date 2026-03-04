package com.viscord.message_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AttachmentResponse {
    private UUID id;
    private String filename;
    private String type;
    private long size;
    private String url;
}
