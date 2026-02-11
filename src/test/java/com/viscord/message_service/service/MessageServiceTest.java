package com.viscord.message_service.service;

import com.viscord.message_service.dto.CreateMessageRequest;
import com.viscord.message_service.dto.MessageResponse;
import com.viscord.message_service.mapper.AttachmentMapper;
import com.viscord.message_service.mapper.MessageMapper;
import com.viscord.message_service.model.message.Message;
import com.viscord.message_service.repository.MessageRepository;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Mock
    private AttachmentMapper attachmentMapper;

    @Spy
    private MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(messageMapper, "attachmentMapper", attachmentMapper);
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
    @DisplayName("Happy path: save message")
    void shouldSaveMessage() {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setContent("Test message");
        req.setChannelId(UUID.randomUUID());
        req.setSenderId(UUID.randomUUID());

        MessageResponse expectedResponse = new MessageResponse();

        expectedResponse.setChannelId(req.getChannelId());
        expectedResponse.setContent(req.getContent());
        expectedResponse.setSenderId(req.getSenderId());

        Mockito.when(messageRepository.save(ArgumentMatchers.any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageResponse result = messageService.createMessage(req);

        Mockito.verify(messageMapper).toEntity(req);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(req.getSenderId(), result.getSenderId());
        Assertions.assertEquals(req.getContent(), result.getContent());
    }
}
