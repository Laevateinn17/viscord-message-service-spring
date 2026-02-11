package com.viscord.message_service.service;

import com.viscord.message_service.dto.CreateMessageRequest;
import com.viscord.message_service.dto.MessageResponse;
import com.viscord.message_service.exception.BadRequestException;
import com.viscord.message_service.grpc.ChannelsServiceGrpc;
import com.viscord.message_service.grpc.GetChannelByIdRequest;
import com.viscord.message_service.grpc.GetChannelByIdResponse;
import com.viscord.message_service.mapper.MessageMapper;
import com.viscord.message_service.mapper.MessageMentionMapper;
import com.viscord.message_service.model.message.Message;
import com.viscord.message_service.model.message.MessageMention;
import com.viscord.message_service.repository.MessageRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @GrpcClient("guild-service")
    private ChannelsServiceGrpc.ChannelsServiceBlockingStub channelStub;

    public List<MessageResponse> getAllMessages() {
        return messageMapper.toDto(messageRepository.findAll());
    }

    public List<MessageResponse> getChannelMessages(UUID channelId) {
        return messageMapper.toDto(messageRepository.findAllByChannelIdOrderByCreatedAtAsc(channelId));
    }

    public MessageResponse createMessage(CreateMessageRequest request) {
        System.out.println(request.getContent());
        boolean isContentEmpty = request.getContent() == null || request.getContent().isBlank();
        boolean isAttachmentEmpty = request.getAttachments() == null || request.getAttachments().stream().allMatch(file -> file.getSize() == 0);

        if (isContentEmpty && isAttachmentEmpty) {
            throw new BadRequestException("Message content cannot be empty");
        }

        GetChannelByIdResponse response = channelStub.getChannelById(GetChannelByIdRequest.newBuilder()
                .setChannelId(request.getChannelId().toString())
                .setUserId(request.getSenderId().toString()).build());

        System.out.println("status: " + response.getStatus());

        Message message = messageMapper.toEntity(request);
        message = messageRepository.save(message);

        if (!request.getMentions().isEmpty()) {
            for (UUID userId : request.getMentions()) {
                MessageMention mention = new MessageMention();
                mention.setMessage(message);
                mention.setMessageId(message.getId());
                mention.setUserId(userId);

                message.addMention(mention);

            }

            message = messageRepository.save(message);
        }

        return messageMapper.toDto(message);
    }

}
