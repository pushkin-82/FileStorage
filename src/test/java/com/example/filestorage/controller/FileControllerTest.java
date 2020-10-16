package com.example.filestorage.controller;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private final MyFile FILE_4 = new MyFile("q", "qwe.qwe", 12000L);

    private final MyFile FILE_5 = new MyFile("a", "wer.mp3", 123123L);

    private final MyFile FILE_6 = new MyFile("z", "erty.12f", 12L);

    private final MyFile NEW_FILE_BLANK_NAME = new MyFile("   ", 127L);

    private final MyFile NEW_FILE_WRONG_SIZE = new MyFile("qwe.qwe", -1L);

    private final MyFile UPLOAD_FILE_5 = new MyFile("aaa.txt", null);

    private final static String[] TAGS = new String[]{"q", "w", "e"};

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
        String responseJson = "{\"success\": true}";
        when(fileService.deleteById("128")).thenReturn(true);

        mockMvc.perform(delete(BASE_URL + "/{id}", FILE_1_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldReturnHttpStatusNotFoundWhenDeleteFileThatNotExists() throws Exception {
        String responseJson = "{\n \"success\": false,\n" +
                "  \"error\": \"file not found\"\n}\n";

        mockMvc.perform(delete(BASE_URL + "/{id}", FILE_1_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldUploadFileAndReturnHttpStatusOk() throws Exception {
        MyFile expected = FILE_1;
        when(fileService.uploadFile(expected)).thenReturn(Optional.empty());
        String jsonBody = objectMapper.writeValueAsString(expected);
        String responseJson = "{\"id\":\"128\"}";

        mockMvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldReturnHttpStatusBadRequestWhenUploadFileWithEmptyName() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(NEW_FILE_BLANK_NAME);
        String responseJson = "{\n" +
                "  \"success\": false,\n" +
                "  \"error\": \"Name should not be empty\"\n" +
                "}\n";

        when(fileService.uploadFile(NEW_FILE_BLANK_NAME)).thenReturn(Optional.of("Name should not be empty"));

        mockMvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldReturnHttpStatusBadRequestWhenUploadFileWithWrongSize() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(NEW_FILE_WRONG_SIZE);
        String responseJson = "{\n" +
                "  \"success\": false,\n" +
                "  \"error\": \"File size shouldn't be a negative number\"\n" +
                "}\n";

        when(fileService.uploadFile(NEW_FILE_WRONG_SIZE)).thenReturn(Optional.of("File size shouldn't be a negative number"));

        mockMvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldReturnHttpStatusBadRequestWhenUploadFileWithNullSize() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(UPLOAD_FILE_5);
        String responseJson = "{\n" +
                "  \"success\": false,\n" +
                "  \"error\": \"File size should not be null\"\n" +
                "}\n";

        when(fileService.uploadFile(UPLOAD_FILE_5)).thenReturn(Optional.of("File size should not be null"));

        mockMvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldAssignTagsAndReturnHttpStatusOkIfExistsById() throws Exception {
        when(fileService.assignTags(FILE_1_ID, TAGS)).thenReturn(true);

        String jsonBody = objectMapper.writeValueAsString(TAGS);
        String responseJson = "{\"success\": true}";

        mockMvc.perform(post(BASE_URL + "/{id}/tags", FILE_1_ID)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldAssignTagsAndReturnHttpStatusNotFoundIfExistsById() throws Exception {
        when(fileService.assignTags(FILE_1_ID, TAGS)).thenReturn(false);

        String jsonBody = objectMapper.writeValueAsString(TAGS);
        String responseJson = "{\n \"success\": false,\n" +
                "  \"error\": \"file not found\"\n}\n";

        mockMvc.perform(post(BASE_URL + "/{id}/tags", FILE_1_ID)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isNotFound())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldRemoveTagsAndReturnHttpStatusOkIfExistsById() throws Exception {
        when(fileService.removeTags(FILE_1_ID, TAGS)).thenReturn(true);

        String jsonBody = objectMapper.writeValueAsString(TAGS);
        String responseJson = "{\"success\": true}";

        mockMvc.perform(delete(BASE_URL + "/{id}/tags", FILE_1_ID)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldRemoveTagsAndReturnHttpStatusNotFoundIfExistsById() throws Exception {
        when(fileService.removeTags(FILE_1_ID, TAGS)).thenReturn(false);

        String jsonBody = objectMapper.writeValueAsString(TAGS);
        String responseJson = "{\n" +
                "  \"success\": false,\n" +
                "  \"error\": \"tag not found on file\"\n" +
                "}\n";

        mockMvc.perform(delete(BASE_URL + "/{id}/tags", FILE_1_ID)
                .contentType(APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseJson));
    }

    @Test
    void shouldReturnMyFileListDueToFilterAndPaginatingByDefault() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MyFile> files = new PageImpl<>(Arrays.asList(FILE_1, FILE_2, FILE_3, FILE_4, FILE_5, FILE_6));
        when(fileService.getAll(pageable)).thenReturn(files);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnMyFileListDueToFilterAndPaginating() throws Exception {
        Pageable pageable = PageRequest.of(1, 2);
        String[] tags = new String[]{"w", "e"};
        FILE_1.setTags(Arrays.asList("q", "w", "e"));
        FILE_4.setTags(Arrays.asList("q", "w"));
        FILE_5.setTags(Arrays.asList("r", "w", "e"));
        FILE_6.setTags(Arrays.asList("q", "w", "e", "r"));
        Page<MyFile> files = new PageImpl<>(Arrays.asList(FILE_6));

        when(fileService.getAllByTags(tags, pageable)).thenReturn(files);

        mockMvc.perform(get(BASE_URL + "?tags=w,e&page=1&size=2"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnMyFileListByNameContainingTemplateDueToFilterAndPaginatingByDefault() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        String q = "qw";
        Page<MyFile> files = new PageImpl<>(Arrays.asList(FILE_1, FILE_2, FILE_3, FILE_4, FILE_5, FILE_6));
        when(fileService.getAllByNameContaining(q, pageable)).thenReturn(files);

        mockMvc.perform(get(BASE_URL + "?q=qw"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnMyFileListByNameContainingTemplateDueToFilterAndPaginating() throws Exception {
        Pageable pageable = PageRequest.of(1, 2);
        String q = "qw";
        String[] tags = new String[]{"w", "e"};
        FILE_1.setTags(Arrays.asList("q", "w", "e"));
        FILE_4.setTags(Arrays.asList("q", "w"));
        FILE_5.setTags(Arrays.asList("r", "w", "e"));
        FILE_6.setTags(Arrays.asList("q", "w", "e", "r"));
        Page<MyFile> files = new PageImpl<>(Arrays.asList(FILE_6));

        when(fileService.getAllByTagsAndNameContaining(tags, q, pageable)).thenReturn(files);

        mockMvc.perform(get(BASE_URL + "?tags=w,e&page=1&size=2&q=qw"))
                .andExpect(status().isOk());
    }
}