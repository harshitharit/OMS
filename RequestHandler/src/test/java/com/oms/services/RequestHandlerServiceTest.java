package com.oms.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oms.dtos.OnlineRequestDto;
import com.oms.exceptions.UnsupportedFileTypeException;
import com.oms.kafka.producer.SendMessageToKafka;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestHandlerServiceTest {

	@Mock
	private SendMessageToKafka messageToKafka;

	@InjectMocks
	private RequestHandlerService requestHandlerService;

	@Test
	@DisplayName("Handling online request.")
	void handleOnlineRequest() throws JsonProcessingException {
		OnlineRequestDto input = OnlineRequestDto.builder()
				.cifNumber(1231241l)
				.accountNumber(1233412l)
				.build();
		String result = requestHandlerService.handleOnlineRequest(input);
		Assertions.assertEquals(result,"Success");
	}

	@Test
	void handleBulkRequest() throws IOException, UnsupportedFileTypeException {
		MockMultipartFile mockMultipartFile = new MockMultipartFile(
				"multipartFile",
				"demo.csv",
				"text/csv",
				new ClassPathResource("demo.csv").getInputStream());
		String result = requestHandlerService.handleBulkRequest(mockMultipartFile);
		Assertions.assertEquals(result,"Success");
	}

	@Test
	void handleBulkRequestWithException() throws IOException, UnsupportedFileTypeException {
		MockMultipartFile mockMultipartFile = new MockMultipartFile(
				"multipartFile",
				"demo.pdf",
				"application/pdf",
				new ClassPathResource("dummy.pdf").getInputStream());
		assertThrows(UnsupportedFileTypeException.class,
				()-> when(requestHandlerService.handleBulkRequest(mockMultipartFile)).thenThrow(new UnsupportedFileTypeException("")));
	}
}