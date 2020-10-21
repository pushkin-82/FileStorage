package com.example.filestorage.service;

import com.example.filestorage.model.File;
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

    private final File FILE_1 = new File("128", "Qwe.qWe", 12000L);

    private final File FILE_2 = new File("129dv", "wer.mp3", 123123L);

    private final File FILE_3 = new File("qq", "eqwy.12f", 12L);

    private final File FILE_4 = new File("q", "qwe.qwe", 12000L);

    private final File FILE_5 = new File("a", "wer.mp3", 123123L);

    private final File FILE_6 = new File("z", "erty.12f", 12L);

    private final File AUDIO_FILE = new File("a", "dfwdc.mp3", 148L);

    private final File VIDEO_FILE = new File("v", "dfwdc.mp4", 148L);

    private final File DOC_FILE = new File("d", "dfwdc.doc", 148L);

    private final File IMAGE_FILE = new File("img", "dfwdc.jpeg", 148L);

    private final File PLAIN_FILE = new File("plain", "dfwdc.x", 148L);

    private  final File NEW_FILE = new File("name.txt", 121L);

    private final File NEW_FILE_BLANK_NAME = new File("   ", 127L);

    private final File NEW_FILE_WRONG_SIZE = new File("qwe.qwe", -1L);

    private final File UPLOAD_FILE_1 = new File("ZZZZ.txt", 123L);

    private final File UPLOAD_FILE_2 = new File("тЕсТ.txt", 123L);

    private final File UPLOAD_FILE_3 = new File("test", 123L);

    private final File UPLOAD_FILE_4 = new File("test.txt", 0L);

    private final File UPLOAD_FILE_5 = new File("aaa.txt", null);

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    @BeforeEach
    void setUp() {
        IndexOperations operations = elasticsearchRestTemplate.indexOps(File.class);
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

        File updated = fileService.getById("qq");
        assertThat(updated.getTags()).containsExactlyInAnyOrder("a", "s", "d");
    }

    @Test
    void shouldAssignTagsToFileWithTags() {
        String[] tags = new String[]{"a", "s", "d"};
        boolean result = fileService.assignTags("128", tags);

        assertTrue(result);

        File updated = fileService.getById("128");
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

    @Test
    void shouldReturnFileListWithPaginatingConditionalsAndFilters() {
        Pageable pageable = PageRequest.of(0, 2);
        String[] tags = new String[]{"w", "e"};

        List<File> resultList = fileService.getAllByTags(tags, pageable).getContent();

        assertThat(resultList).containsExactlyInAnyOrder(FILE_1, FILE_5);

        String[] tags1 = new String[]{"r"};

        List<File> resultList1 = fileService.getAllByTags(tags1, pageable).getContent();

        assertThat(resultList1).containsExactlyInAnyOrder(FILE_5, FILE_6);
    }

    @Test
    void shouldReturnFileListByNameContainingTemplateWithPaginating() {
        Pageable pageable = PageRequest.of(0, 10);
        String template = "qw";

        List<File> resultList = fileService.getAllByNameContaining(template, pageable).getContent();

        assertThat(resultList).containsExactlyInAnyOrder(FILE_1, FILE_4, FILE_3);
    }

    @Test
    void shouldReturnFileListByTagsAndNameContainingTemplateWithPaginating() {
        Pageable pageable = PageRequest.of(0, 10);
        String[] tags = new String[]{"w", "e"};
        String template = "qw";

        List<File> resultList = fileService.getAllByTagsAndNameContaining(tags, template, pageable).getContent();

        assertThat(resultList).containsExactlyInAnyOrder(FILE_1);
    }

    @Test
    void shouldAddAudioTagWhenUploadWithProperExtension() {
        fileService.uploadFile(AUDIO_FILE);

        File expected = fileService.getById("a");

        assertThat(expected.getTags()).containsExactlyInAnyOrder("audio");
    }

    @Test
    void shouldAddVideoTagWhenUploadWithProperExtension() {
        fileService.uploadFile(VIDEO_FILE);

        File expected = fileService.getById("v");

        assertThat(expected.getTags()).containsExactlyInAnyOrder("video");
    }

    @Test
    void shouldAddDocumentTagWhenUploadWithProperExtension() {
        fileService.uploadFile(DOC_FILE);

        File expected = fileService.getById("d");

        assertThat(expected.getTags()).containsExactlyInAnyOrder("document");
    }

    @Test
    void shouldAddImageTagWhenUploadWithProperExtension() {
        fileService.uploadFile(IMAGE_FILE);

        File expected = fileService.getById("img");

        assertThat(expected.getTags()).containsExactlyInAnyOrder("image");
    }

    @Test
    void shouldNotAddTagsWhenUploadWithNotProperExtension() {
        fileService.uploadFile(PLAIN_FILE);

        File expected = fileService.getById("plain");

        assertThat(expected.getTags()).isEmpty();
    }

    private List<File> getData() {
        FILE_1.addTags(Arrays.asList("q", "w", "e"));
        FILE_4.addTags(Arrays.asList("q", "w"));
        FILE_5.addTags(Arrays.asList("r", "w", "e"));
        FILE_6.addTags(Arrays.asList("q", "w", "e", "r"));
        return new ArrayList<>(Arrays.asList(FILE_1, FILE_2, FILE_3, FILE_4, FILE_5, FILE_6));
    }
}