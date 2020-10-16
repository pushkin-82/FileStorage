package com.example.filestorage.repository;

import com.example.filestorage.model.MyFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface FileRepository extends ElasticsearchRepository<MyFile, String> {

    Page<MyFile> findAllByTags(Collection<String> tags, Pageable pageable);

    Page<MyFile> findAllByNameContaining(String template, Pageable pageable);

    Page<MyFile> findAllByTagsAndNameContaining(Collection<String> tags, String template, Pageable pageable);
}
