package com.example.filestorage.service;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final MyFile FILE_4 = new MyFile("q", "qwe.qwe", 12000L);

    private final MyFile FILE_5 = new MyFile("a", "wer.mp3", 123123L);

    private final MyFile FILE_6 = new MyFile("z", "erty.12f", 12L);

    private  final MyFile NEW_FILE = new MyFile("name.txt", 121L);

    private final MyFile NEW_FILE_BLANK_NAME = new MyFile("   ", 127L);

    private final MyFile NEW_FILE_WRONG_SIZE = new MyFile("qwe.qwe", -1L);

    private final MyFile UPLOAD_FILE_1 = new MyFile("ZZZZ.txt", 123L);

    private final MyFile UPLOAD_FILE_2 = new MyFile("тЕсТ.txt", 123L);

    private final MyFile UPLOAD_FILE_3 = new MyFile("test", 123L);

    private final MyFile UPLOAD_FILE_4 = new MyFile("test.txt", 0L);

    private final MyFile UPLOAD_FILE_5 = new MyFile("aaa.txt", null);

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
    void shouldUploadNewFIleCase1() {
        Optional<String> result = fileService.uploadFile(UPLOAD_FILE_1);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUploadNewFIleCase2() {
        Optional<String> result = fileService.uploadFile(UPLOAD_FILE_2);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUploadNewFIleCase3() {
        Optional<String> result = fileService.uploadFile(UPLOAD_FILE_3);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUploadNewFIleCase4() {
        Optional<String> result = fileService.uploadFile(UPLOAD_FILE_4);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotUploadNewFIleIfNameIsBlank() {
        Optional<String> result = fileService.uploadFile(NEW_FILE_BLANK_NAME);

        assertThat(result).hasValue("Name should not be empty");
    }

    @Test
    void shouldNotUploadNewFIleIfSizeIsNegative() {
        Optional<String> result = fileService.uploadFile(NEW_FILE_WRONG_SIZE);

        assertThat(result).hasValue("File size shouldn't be a negative number");
    }

    @Test
    void shouldNotUploadNewFIleIfSizeIsNull() {
        Optional<String> result = fileService.uploadFile(UPLOAD_FILE_5);

        assertThat(result).hasValue("File size should not be null");
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

//    @Test
//    void shouldReturnFileListWithPaginatingConditionalsAndFilters() {
//        Pageable pageable = PageRequest.of(0, 2);
//        String[] tags = new String[]{"w", "e"};
//
//        List<MyFile> resultList = fileService.getAllWithFilter(tags, pageable);
//
//        assertThat(resultList).containsExactlyInAnyOrder(FILE_1, FILE_5);
//
//        String[] tags1 = new String[]{"r"};
//
//        List<MyFile> resultList1 = fileService.getAllWithFilter(tags1, pageable);
//
//        assertThat(resultList1).containsExactlyInAnyOrder(FILE_5, FILE_6);
//    }

    private List<MyFile> getData() {
        FILE_1.setTags(Arrays.asList("q", "w", "e"));
        FILE_4.setTags(Arrays.asList("q", "w"));
        FILE_5.setTags(Arrays.asList("r", "w", "e"));
        FILE_6.setTags(Arrays.asList("q", "w", "e", "r"));
        return new ArrayList<>(Arrays.asList(FILE_1, FILE_2, FILE_3, FILE_4, FILE_5, FILE_6));
    }
}