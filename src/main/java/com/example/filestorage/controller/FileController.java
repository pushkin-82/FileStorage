package com.example.filestorage.controller;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.service.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/file")
public class FileController {

    private static final int DEFAULT_PAGE = 0;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{id}")
    public MyFile getById(@PathVariable("id") String id) {
        return fileService.getById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> uploadFile(@RequestBody MyFile myFile) {
        if (fileService.uploadFile(myFile).isEmpty()) {
            return new ResponseEntity<>("\"ID\": \"" + myFile.getId() + "\"" , HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\n" +
                    "  \"success\": false,\n" +
                    "  \"error\": \"" + fileService.uploadFile(myFile).get() + "\"\n" +
                    "}\n", HttpStatus.BAD_REQUEST );
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Object> deleteFile(@PathVariable("id") String id) {
        if (fileService.deleteById(id)) {
            return new ResponseEntity("{\"success\": true}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\n \"success\": false,\n" +
                    "  \"error\": \"file not found\"\n}\n", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{id}/tags", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> assignTags(@PathVariable("id") String id,
                                             @RequestBody String[] tags) {
        if (fileService.assignTags(id, tags)) {
            return new ResponseEntity<>("{\"success\": true}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\n \"success\": false,\n" +
                    "  \"error\": \"file not found\"\n}\n", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}/tags", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> removeTags(@PathVariable("id") String id,
                                             @RequestBody String[] tags) {
        if (fileService.removeTags(id, tags)) {
            return new ResponseEntity<>("{\"success\": true}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\n" +
                    "  \"success\": false,\n" +
                    "  \"error\": \"tag not found on file\"\n" +
                    "}\n", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Object> getFilesWithFilter(@RequestParam("tags") Optional<String[]> tags,
                                                     @RequestParam("page") Optional<Integer> page,
                                                     @RequestParam("size") Optional<Integer> size) {
        String[] filterTags = tags.orElse(new String[0]);
        int currentPage = page.orElse(DEFAULT_PAGE);
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);

        List<MyFile> resultList;
        if (filterTags.length == 0) {
            resultList = fileService.getAll();
        } else {
            resultList = fileService.getAllWithFilter(filterTags);
        }

        int total = resultList.size();
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        Page<MyFile> myFilePage = new PageImpl<>(resultList.subList(start, end), pageable, total);

        return new ResponseEntity<>("{\n" +
                "   \"total\": " + total + ",\n" +
                "   \"page\": " + myFilePage.getContent() + "\n}\n", HttpStatus.OK);
    }
}
