package com.example.filestorage.service;

import com.example.filestorage.model.MyFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FileService {
    Optional<String> uploadFile(MyFile myFile);

    boolean assignTags(String id, String[] tags);

    boolean removeTags(String id, String[] tags);

    Page<MyFile> getAll(Pageable pageable);

    Page<MyFile> getAllByTags(String[] tags, Pageable pageable);

    Page<MyFile> getAllByNameContaining(String template, Pageable pageable);

    Page<MyFile> getAllByTagsAndNameContaining(String[] tags, String template, Pageable pageable);

    MyFile getById(String id);

    boolean deleteById(String id);
}
