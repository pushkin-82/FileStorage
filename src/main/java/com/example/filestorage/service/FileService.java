package com.example.filestorage.service;

import com.example.filestorage.model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FileService {
    Optional<String> uploadFile(File file);

    boolean assignTags(String id, String[] tags);

    boolean removeTags(String id, String[] tags);

    Page<File> getAll(Pageable pageable);

    Page<File> getAllByTags(String[] tags, Pageable pageable);

    Page<File> getAllByNameContaining(String template, Pageable pageable);

    Page<File> getAllByTagsAndNameContaining(String[] tags, String template, Pageable pageable);

    File getById(String id);

    boolean deleteById(String id);
}
