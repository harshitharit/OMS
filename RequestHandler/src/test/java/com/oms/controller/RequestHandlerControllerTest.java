package com.oms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.dtos.OnlineRequestDto;
import com.oms.exceptions.UnsupportedFileTypeException;
import com.oms.services.RequestHandlerService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class RequestHandlerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestHandlerService requestHandlerService;

    @SneakyThrows
    @Test
    @DisplayName("Online Request handler test")
    void onlineRequest() {
        //build request body
        OnlineRequestDto input = OnlineRequestDto.builder()
                .cifNumber(1231241l)
                .accountNumber(1233412l)
                .build();
        //call controller endpoints
        Mockito.when(requestHandlerService.handleOnlineRequest(ArgumentMatchers.any())).thenReturn("");
        mockMvc.perform(post("/api/handle/onlineRequest").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(asJsonString(input))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @SneakyThrows
    @Test
    @DisplayName("Online Request handler test")
    void onlineRequestException() {
        //build request body
        OnlineRequestDto input = OnlineRequestDto.builder()
                .cifNumber(null)
                .accountNumber(1233412l)
                .build();
        //call controller endpoints
        Mockito.when(requestHandlerService.handleOnlineRequest(ArgumentMatchers.any())).thenReturn("");
        mockMvc.perform(post("/api/handle/onlineRequest").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(asJsonString(input))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @SneakyThrows
    @Test
    @DisplayName("Bulk Request handler test")
    void bulkRequest() {
        //bulk request body
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "multipartFile",
                "demo.csv",
                "text/csv",
                new ClassPathResource("demo.csv").getInputStream());
        //call controller endpoints
        Mockito.when(requestHandlerService.handleBulkRequest(ArgumentMatchers.any())).thenReturn("");
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/handle/bulkRequest")
                        .file(mockMultipartFile))
                .andExpect(status().isCreated());
    }

    @SneakyThrows
    @Test
    @DisplayName("Bulk Request handler test")
    void bulkRequestWithException() {
        //bulk request body
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "multipartFile",
                "dummy.pdf",
                "application/pdf",
                new ClassPathResource("dummy.pdf").getInputStream());
        //call controller endpoints
        Mockito.when(requestHandlerService.handleBulkRequest(ArgumentMatchers.any())).thenThrow(new UnsupportedFileTypeException(""));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/handle/bulkRequest")
                        .file(mockMultipartFile))
                .andExpect(status().is4xxClientError());
    }

    private String asJsonString(Object object) {
        try {
            ObjectMapper op = new ObjectMapper();
            op.findAndRegisterModules();
            return op.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.getStackTrace();
        }
        return null;
    }
}