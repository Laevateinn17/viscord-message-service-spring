package com.viscord.message_service.controller;


import com.viscord.message_service.dto.MessageResponse;
import com.viscord.message_service.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
public class ChannelMessageController {
    private final MessageService messageService;

    public ChannelMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{channelId}/messages")
    public ResponseEntity<List<MessageResponse>> getChannelMessages(UUID channelId) {
        return ResponseEntity.ok(messageService.getAllMessages());
    }
}
