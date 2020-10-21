package com.example.filestorage.repository;

import com.example.filestorage.model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface FileRepository extends ElasticsearchRepository<File, String> {

    Page<File> findAllByTags(Collection<String> tags, Pageable pageable);

    Page<File> findAllByNameContaining(String template, Pageable pageable);

    Page<File> findAllByTagsAndNameContaining(Collection<String> tags, String template, Pageable pageable);
}
