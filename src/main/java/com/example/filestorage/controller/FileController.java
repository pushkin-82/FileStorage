package com.example.filestorage.controller;

import com.example.filestorage.model.File;
import com.example.filestorage.service.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/file")
public class FileController {

    private static final String SUCCESS = "success";

    private static final String ERROR = "error";

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{id}")
    public File getById(@PathVariable("id") String id) {
        return fileService.getById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> uploadFile(@RequestBody File file) {
        Map<String, Object> response = new HashMap<>();

        if (fileService.uploadFile(file).isEmpty()) {
            response.put("id", file.getId());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put(SUCCESS, false);
            response.put(ERROR, fileService.uploadFile(file).get());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST );
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Object> deleteFile(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();

        if (fileService.deleteById(id)) {
            response.put(SUCCESS, true);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put(SUCCESS, false);
            response.put(ERROR, "file not found");

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{id}/tags", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> assignTags(@PathVariable("id") String id,
                                             @RequestBody String[] tags) {
        Map<String, Object> response = new HashMap<>();

        if (fileService.assignTags(id, tags)) {
            response.put(SUCCESS, true);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put(SUCCESS, false);
            response.put(ERROR, "file not found");

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}/tags", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> removeTags(@PathVariable("id") String id,
                                             @RequestBody String[] tags) {
        Map<String, Object> response = new HashMap<>();

        if (fileService.removeTags(id, tags)) {
            response.put(SUCCESS, true);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put(SUCCESS, false);
            response.put(ERROR, "tag not found on file");

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Object> getFilesWithFilter(@RequestParam(required = false) String[] tags,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) String q) {
        Pageable pageable = PageRequest.of(page, size);
        Page<File> resultPage;

        resultPage = fileService.getAllByTagsAndNameContaining(tags, q, pageable);

        List<File> resultList = resultPage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("total", resultPage.getTotalElements());
        response.put("page", resultList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
