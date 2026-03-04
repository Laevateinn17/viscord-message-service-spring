package com.viscord.message_service.service;

import com.viscord.message_service.dto.CreateMessageRequest;
import com.viscord.message_service.dto.MessageResponse;
import com.viscord.message_service.exception.BadRequestException;
import com.viscord.message_service.exception.ForbiddenException;
import com.viscord.message_service.exception.NotFoundException;
import com.viscord.message_service.grpc.CanUserDeleteMessageResponse;
import com.viscord.message_service.grpc.CanUserSendMessageResponse;
import com.viscord.message_service.grpc.ChannelsServiceGrpc;
import com.viscord.message_service.mapper.AttachmentMapper;
import com.viscord.message_service.mapper.MessageMapper;
import com.viscord.message_service.model.message.Message;
import com.viscord.message_service.repository.MessageRepository;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Spy
    private MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    @Spy
    private AttachmentMapper attachmentMapper = Mappers.getMapper(AttachmentMapper.class);

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelsServiceGrpc.ChannelsServiceBlockingStub channelStub;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(messageMapper, "attachmentMapper", attachmentMapper);
    }

    private CreateMessageRequest createRequest(String content) {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setContent(content);
        req.setChannelId(UUID.randomUUID());
        req.setSenderId(UUID.randomUUID());

        return req;
    }

    private CanUserSendMessageResponse createCanUserSendMessageResponse(boolean allowed, int status, String msg) {
        return CanUserSendMessageResponse.newBuilder()
                .setData(allowed)
                .setMessage(msg)
                .setStatus(status)
                .build();
    }

    private Message createMessage(String content) {
        Message message = new Message();
        message.setContent(content);
        message.setId(UUID.randomUUID());
        message.setSenderId(UUID.randomUUID());
        message.setChannelId(UUID.randomUUID());

        return message;
    }


    @Test
    void shouldReturnChannelMessages() {
        UUID channelId = UUID.randomUUID();

        Message entity = new Message();
        MessageResponse dto = new MessageResponse();

        Mockito.when(messageRepository.findAllByChannelIdOrderByCreatedAtAsc(channelId)).thenReturn(List.of(entity));
        Mockito.when(messageMapper.toDto(List.of(entity))).thenReturn(List.of(dto));

        List<MessageResponse> result = messageService.getChannelMessages(channelId);

        Assertions.assertEquals(1, result.size());
        Assertions.assertSame(dto, result.get(0));
    }

    @Test
    @DisplayName("Unhappy path: given empty content, should throw bad request error")
    void createMessage_EmptyRequest_ThrowsBadRequest() {
        CreateMessageRequest req = createRequest("");

        Assertions.assertThrows(BadRequestException.class, () -> {
            messageService.createMessage(req);
        });

        Mockito.verify(channelStub, Mockito.never()).canUserSendMessage(Mockito.any());
    }

    @Test
    @DisplayName("Unhappy path: when user is not allowed, should throw forbidden error")
    void createMessage_UserNotAllowed_ThrowsForbidden() {
        CreateMessageRequest req = createRequest("This is a test message");

        Mockito.when(channelStub.canUserSendMessage(Mockito.any()))
                .thenReturn(createCanUserSendMessageResponse(false, HttpStatus.FORBIDDEN.value(), "User is not allowed"));

        Assertions.assertThrows(ForbiddenException.class, () -> {
            messageService.createMessage(req);
        });

        Mockito.verify(messageRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Unhappy path: when user/channel id is invalid, should throw bad request error")
    void createMessage_InvalidRequest_ThrowsBadRequest() {
        CreateMessageRequest req = createRequest("This is a test message");

        Mockito.when(channelStub.canUserSendMessage(Mockito.any()))
                .thenReturn(createCanUserSendMessageResponse(false, HttpStatus.BAD_REQUEST.value(), "Invalid channel or user ID"));

        Assertions.assertThrows(BadRequestException.class, () -> {
            messageService.createMessage(req);
        });

        Mockito.verify(messageRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    @DisplayName("Happy path: given empty content but with attachment, should save message")
    void createMessage_EmptyContentWithAttachment_ReturnsMessageResponse() {
        CreateMessageRequest req = createRequest("");
        req.setAttachments(List.of(new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes())));

        Mockito.when(channelStub.canUserSendMessage(Mockito.any())).thenReturn(createCanUserSendMessageResponse(true, HttpStatus.OK.value(), ""));
        Mockito.when(messageRepository.save(ArgumentMatchers.any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0, Message.class);
            message.setId(UUID.randomUUID());

            return message;
        });
        Mockito.when(storageService.uploadFile(Mockito.any(), Mockito.any(), Mockito.any())).thenAnswer(invocation -> {
            MultipartFile file = invocation.getArgument(0, MultipartFile.class);

            return String.format("example/key/%s", file.getOriginalFilename());
        });

        MessageResponse result = messageService.createMessage(req);

        Mockito.verify(messageMapper).toEntity(req);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(req.getSenderId(), result.getSenderId());
        Assertions.assertEquals(req.getContent(), result.getContent());
        Assertions.assertEquals(req.getAttachments().size(), result.getAttachments().size());
    }

    @Test
    @DisplayName("Happy path: given valid request, should save message")
    void createMessage_ValidRequest_ReturnsMessageResponse() {
        CreateMessageRequest req = createRequest("Test message");

        MessageResponse expectedResponse = new MessageResponse();

        expectedResponse.setChannelId(req.getChannelId());
        expectedResponse.setContent(req.getContent());
        expectedResponse.setSenderId(req.getSenderId());

        Mockito.when(channelStub.canUserSendMessage(Mockito.any())).thenReturn(createCanUserSendMessageResponse(true, HttpStatus.OK.value(), ""));
        Mockito.when(messageRepository.save(ArgumentMatchers.any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageResponse result = messageService.createMessage(req);

        Mockito.verify(messageMapper).toEntity(req);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(req.getSenderId(), result.getSenderId());
        Assertions.assertEquals(req.getContent(), result.getContent());
    }

    @Test
    @DisplayName("Happy path: given valid request, should delete message")
    void deleteMessage_ValidRequest_ReturnsEmpty() {
        Message message = createMessage("test");

        Mockito.when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));
        Mockito.when(channelStub.canUserDeleteMessage(Mockito.any())).thenReturn(
                CanUserDeleteMessageResponse.newBuilder().setAllowed(true).build());

        messageService.deleteMessage(message.getSenderId(), message.getId());

        Mockito.verify(messageRepository, Mockito.times(1)).delete(message);
    }

    @Test
    @DisplayName("Unhappy path: given invalid message id, should throw not found error")
    void deleteMessage_InvalidMessageId_ThrowsNotFound() {
        Mockito.when(messageRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            messageService.deleteMessage(UUID.randomUUID(), UUID.randomUUID());
        });

        Mockito.verify(messageRepository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(channelStub, Mockito.never()).canUserDeleteMessage(Mockito.any());
    }

    @Test
    @DisplayName("Unhappy path: when delete message permission is denied, should throw forbidden")
    void deleteMessage_PermissionDenied_ThrowsForbidden() {
        Message message = createMessage("test");

        Mockito.when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));
        Mockito.when(channelStub.canUserDeleteMessage(Mockito.any())).thenReturn(CanUserDeleteMessageResponse.newBuilder().setAllowed(false).build());

        Assertions.assertThrows(ForbiddenException.class, () -> {
            messageService.deleteMessage(message.getSenderId(), message.getId());
        });

        Mockito.verify(messageRepository, Mockito.never()).delete(Mockito.any());
    }
}

