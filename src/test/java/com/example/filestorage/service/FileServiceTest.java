package com.example.filestorage.service;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FileServiceTest {

    private final MyFile FILE_1 = new MyFile("128", "qwe.qwe", 12000L);

    private final MyFile FILE_2 = new MyFile("129dv", "wer.mp3", 123123L);

    private final MyFile FILE_3 = new MyFile("qq", "erty.12f", 12L);

    private  final MyFile NEW_FILE = new MyFile("name.txt", 121L);

    private final MyFile NEW_FILE_BLANK_NAME = new MyFile("   ", 127L);

    private final MyFile NEW_FILE_WRONG_NAME = new MyFile("qeqe.w", 1212L);

    private final MyFile NEW_FILE_WRONG_SIZE = new MyFile("qwe.qwe", -1L);

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    @BeforeEach
    void setUp() {
        IndexOperations operations = elasticsearchRestTemplate.indexOps(MyFile.class);
        operations.delete();
        operations.create();
        fileRepository.saveAll(getData());
    }

    @Test
    void shouldDeleteFileIfExists() {
        boolean result = fileService.deleteById("128");

        assertTrue(result);

        result = fileService.deleteById("128");

        assertFalse(result);
    }

    @Test
    void shouldUploadNewFIle() {
        Optional<String> result = fileService.uploadFile(NEW_FILE);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotUploadNewFIleIfNameIsBlank() {
        Optional<String> result = fileService.uploadFile(NEW_FILE_BLANK_NAME);

        assertThat(result).hasValue("Name should not be empty");
    }

    @Test
    void shouldNotUploadNewFIleIfNameDoesNotHaveProperExtension() {
        Optional<String> result = fileService.uploadFile(NEW_FILE_WRONG_NAME);

        assertThat(result).hasValue("File should have proper extension");
    }

    @Test
    void shouldNotUploadNewFIleIfSizeIsNotPositive() {
        Optional<String> result = fileService.uploadFile(NEW_FILE_WRONG_SIZE);

        assertThat(result).hasValue("File size should be positive number");
    }

    @Test
    void shouldAssignTagsToFile() {
        String[] tags = new String[]{"a", "s", "d"};
        boolean result = fileService.assignTags("qq", tags);

        assertTrue(result);

        MyFile updated = fileService.getById("qq");
        assertThat(updated.getTags()).containsExactlyInAnyOrder("a", "s", "d");
    }

    @Test
    void shouldAssignTagsToFileWithTags() {
        String[] tags = new String[]{"a", "s", "d"};
        boolean result = fileService.assignTags("128", tags);

        assertTrue(result);

        MyFile updated = fileService.getById("128");
        assertThat(updated.getTags()).containsExactlyInAnyOrder("a", "s", "d", "q", "w", "e");
    }

    @Test
    void shouldReturnFalseIfTryToAssignTagsToNotExistingFile() {
        String[] tags = new String[]{"a", "s", "d"};
        boolean result = fileService.assignTags("1298", tags);

        assertFalse(result);
    }

    @Test
    void shouldRemoveTagsFromExistingFileIfPresent() {
        String[] tags = new String[]{"w", "e"};

        boolean result = fileService.removeTags("128", tags);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseIfRemoveTagsFromExistingFileIfNotPresent() {
        String[] tags = new String[]{"w", "cv"};

        boolean result = fileService.removeTags("128", tags);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseIfTryToRemoveTagsFromNotExistingFile() {
        String[] tags = new String[]{"w", "e"};

        boolean result = fileService.removeTags("128", tags);

        assertTrue(result);
    }

    private List<MyFile> getData() {
        FILE_1.setTags(Arrays.asList("q", "w", "e"));
        return new ArrayList<>(Arrays.asList(FILE_1, FILE_2, FILE_3));
    }
}