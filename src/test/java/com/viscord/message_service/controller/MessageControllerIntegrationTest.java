package com.viscord.message_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viscord.message_service.dto.CreateMessageRequest;
import com.viscord.message_service.dto.MessageResponse;
import org.hibernate.boot.model.source.spi.AssociationSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableConfigurationProperties
public class MessageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID channelId = UUID.randomUUID();
    private final String CHANNEL_MESSAGE_ENDPOINT = "/channels/" + channelId + "/messages";

    @Test
    @DisplayName("Happy path: create message and return saved message")
    public void testCreateMessage() throws Exception {
        CreateMessageRequest request = new CreateMessageRequest();
        request.setSenderId(UUID.randomUUID());
        request.setContent("Hello there!");
        request.setMentions(new ArrayList<>(List.of(UUID.randomUUID())));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(CHANNEL_MESSAGE_ENDPOINT)
                        .header("X-User-Id", request.getSenderId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        MessageResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), MessageResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getId());
        Assertions.assertEquals(request.getSenderId(), response.getSenderId());
        Assertions.assertEquals(request.getContent(), response.getContent());
        Assertions.assertEquals(request.getMentions().size(), response.getMentions().size());
    }

    @Test
    @DisplayName("Error path: create message with blank content")
    public void testCreateMessageWithBlankContent() throws Exception {
        CreateMessageRequest request = new CreateMessageRequest();
        request.setSenderId(UUID.randomUUID());
        request.setContent("   \n ");

        mockMvc.perform(MockMvcRequestBuilders.post(CHANNEL_MESSAGE_ENDPOINT)
                        .header("X-User-Id", request.getSenderId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get channel messages")
    public void testGetChannelMessages() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(CHANNEL_MESSAGE_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        List<MessageResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

        System.out.println(response.size());
    }

}
