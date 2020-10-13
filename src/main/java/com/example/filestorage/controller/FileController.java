package com.example.filestorage.controller;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public List<MyFile> getAll() {
        return fileService.getAll();
    }

    @GetMapping("/{id:\\d+}")
    public MyFile getById(@PathVariable("id") Long id) {
        return fileService.getById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> uploadFile(@RequestBody MyFile myFile) {
        if (fileService.uploadFile(myFile)) {
            return new ResponseEntity<>("ID:" + myFile.getId(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Success: false, error: error", HttpStatus.BAD_REQUEST );
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Object> deleteFile(@PathVariable("id") Long id) {
        if (fileService.deleteById(id)) {
            return new ResponseEntity("{\"success\": true}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\n \"success\": false,\n" +
                    "  \"error\": \"file not found\"\n}\n", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{id:\\d+}/tags", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> assignTags(@PathVariable("id") Long id,
                                             @RequestBody String[] tags) {
        if (fileService.assignTags(id, tags)) {
            return new ResponseEntity<>("{\"success\": true}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\n \"success\": false,\n" +
                    "  \"error\": \"file not found\"\n}\n", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id:\\d+}/tags", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> removeTags(@PathVariable("id") Long id,
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

//    @GetMapping
//    public ResponseEntity<Object> getFiles(@RequestParam("tags") String[] tags,
//                         @RequestParam("page") Long page,
//                         @RequestParam("size") Long size) {
//        return ResponseEntity.ok().build();
//    }
}
