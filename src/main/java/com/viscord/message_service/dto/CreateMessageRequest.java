package com.viscord.message_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CreateMessageRequest {
    @NotBlank(message = "Message content cannot be empty")
    private String content;

    private UUID channelId;

    private List<UUID> mentions = new ArrayList<>();

    private UUID senderId;
//    private List<File> attachments;
}
