package com.example.filestorage.controller;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public List<MyFile> getAll() {
        return fileService.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> uploadFile(@RequestBody MyFile myFile) {
        if (fileService.uploadFile(myFile)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Object> deleteFile(@PathVariable("id") Long id) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id:\\d+}/tags")
    public ResponseEntity<Object> assignTagsToFile(@PathVariable("id") Long id,
                                 @RequestParam("tags") String[] tags) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/tags")
    public ResponseEntity<Object> deleteTagsFromFile(@PathVariable("id") Long id,
                                 @RequestParam("tags") String[] tags) {
        return ResponseEntity.ok().build();
    }

//    @GetMapping
//    public ResponseEntity<Object> getFiles(@RequestParam("tags") String[] tags,
//                         @RequestParam("page") Long page,
//                         @RequestParam("size") Long size) {
//        return ResponseEntity.ok().build();
//    }
}
