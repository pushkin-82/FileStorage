package com.example.filestorage.service;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTest {

    private final MyFile FILE_1 = new MyFile("128", "qwe.qwe", 12000L);

    private final MyFile FILE_2 = new MyFile("129", "wer.mp3", 123123L);

    private final MyFile FILE_3 = new MyFile("140", "erty.12f", 12L);

    private  final MyFile NEW_FILE = new MyFile("name.txt", 121L);

    private final MyFile NEW_FILE_BLANK_NAME = new MyFile("   ", 127L);

    private final MyFile NEW_FILE_WRONG_NAME = new MyFile("qeqe.w", 1212L);

    private final MyFile NEW_FILE_WRONG_SIZE = new MyFile("qwe.qwe", -1L);

    @Autowired
    ElasticsearchOperations operations;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;


    @BeforeEach
    void setUp() {
        operations.indexOps(MyFile.class);
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

    private List<MyFile> getData() {
        return new ArrayList<>(Arrays.asList(FILE_1, FILE_2, FILE_3));
    }
}