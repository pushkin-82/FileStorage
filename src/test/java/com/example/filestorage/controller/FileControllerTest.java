package com.example.filestorage.controller;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FileController.class)
class FileControllerTest {

    private static final String BASE_URL = "/file";

    private final static String FILE_1_ID = "128";

    private final MyFile FILE_1 = new MyFile("128", "qwe.qwe", 12000L);

    private final MyFile FILE_2 = new MyFile("129dv", "wer.mp3", 123123L);

    private final MyFile FILE_3 = new MyFile("qq", "erty.12f", 12L);

    private  final MyFile NEW_FILE = new MyFile("name.txt", 121L);

    private final MyFile NEW_FILE_BLANK_NAME = new MyFile("   ", 127L);

    private final MyFile NEW_FILE_WRONG_NAME = new MyFile("qeqe.w", 1212L);

    private final MyFile NEW_FILE_WRONG_SIZE = new MyFile("qwe.qwe", -1L);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FileService fileService;

    @Autowired
    private FileController fileController;

    @Test
    void shouldDeleteFileByIdAndReturnHttpStatusOk() throws Exception {
        when(fileService.deleteById("128")).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/{id}", FILE_1_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\": true}"));
    }

    @Test
    void shouldReturnHttpStatusNotFoundWhenDeleteFileThatNotExists() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", FILE_1_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\n \"success\": false,\n" +
                        "  \"error\": \"file not found\"\n}\n"));
    }

    @Test
    void shouldUploadFileAndReturnHttpStatusOk() throws Exception {
        when(fileService.uploadFile(NEW_FILE)).thenReturn(Optional.empty());
        String jsonBody = objectMapper.writeValueAsString(NEW_FILE);

        mockMvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().json("\"ID\": \"" + NEW_FILE.getId() + "\""));
    }

    @Test
    void shouldReturnHttpStatusBadRequestWhenUploadFileWithEmptyName() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(NEW_FILE_BLANK_NAME);

        when(fileService.uploadFile(NEW_FILE_BLANK_NAME)).thenReturn(Optional.of("Name should not be empty"));

        mockMvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "  \"success\": false,\n" +
                        "  \"error\": \"Name should not be empty\"\n" +
                        "}\n"));
    }

    @Test
    void shouldReturnHttpStatusBadRequestWhenUploadFileWithWrongName() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(NEW_FILE_WRONG_NAME);

        when(fileService.uploadFile(NEW_FILE_WRONG_NAME)).thenReturn(Optional.of("File should have proper extension"));

        mockMvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "  \"success\": false,\n" +
                        "  \"error\": \"File should have proper extension\"\n" +
                        "}\n"));
    }

    @Test
    void shouldReturnHttpStatusBadRequestWhenUploadFileWithWrongSize() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(NEW_FILE_WRONG_SIZE);

        when(fileService.uploadFile(NEW_FILE_WRONG_SIZE)).thenReturn(Optional.of("File size should be positive number"));

        mockMvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "  \"success\": false,\n" +
                        "  \"error\": \"File size should be positive number\"\n" +
                        "}\n"));
    }


}