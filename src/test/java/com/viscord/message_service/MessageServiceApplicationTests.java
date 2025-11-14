package com.viscord.message_service;

import com.viscord.message_service.dto.CreateMessageRequest;
import com.viscord.message_service.service.MessageService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class MessageServiceApplicationTests {

	@Test
	void contextLoads() {
	}


}
