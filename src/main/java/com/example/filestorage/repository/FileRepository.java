package com.example.filestorage.repository;

import com.example.filestorage.model.MyFile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FileRepository extends ElasticsearchRepository<MyFile, Long> {
}
