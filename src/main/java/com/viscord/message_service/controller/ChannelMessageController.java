package com.viscord.message_service.controller;


import com.viscord.message_service.dto.CreateMessageRequest;
import com.viscord.message_service.dto.MessageResponse;
import com.viscord.message_service.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels/{channelId}/messages")
public class ChannelMessageController {
    private final MessageService messageService;

    public ChannelMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getChannelMessages(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.getChannelMessages(channelId));
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID channelId,
            @Valid @RequestBody() CreateMessageRequest body
    ) {
        body.setSenderId(userId);
        body.setChannelId(channelId);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.createMessage(body));
    }
}
