package com.viscord.message_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CreateMessageRequest {
    private String content;

    private UUID channelId;

    private List<UUID> mentions = new ArrayList<>();

    private UUID senderId;

    private List<MultipartFile> attachments = new ArrayList<>();
}
