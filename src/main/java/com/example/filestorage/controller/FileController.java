package com.example.filestorage.controller;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.service.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public MyFile getById(@PathVariable("id") String id) {
        return fileService.getById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> uploadFile(@RequestBody MyFile myFile) {
        Map<String, Object> response = new HashMap<>();

        if (fileService.uploadFile(myFile).isEmpty()) {
            response.put("id", myFile.getId());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put(SUCCESS, false);
            response.put(ERROR, fileService.uploadFile(myFile).get());

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
        Page<MyFile> resultPage;

        if (tags == null) {
            if (q == null) {
                resultPage = fileService.getAll(pageable);
            } else {
                resultPage = fileService.getAllByNameContaining(q, pageable);
            }
        } else {
            if (q == null) {
                resultPage = fileService.getAllByTags(tags, pageable);
            } else {
                resultPage = fileService.getAllByTagsAndNameContaining(tags, q, pageable);
            }
        }

        List<MyFile> resultList = resultPage.getContent();

        Map<String, Object> response = new HashMap<>();

        response.put("total", resultPage.getTotalElements());
        response.put("page", resultList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
