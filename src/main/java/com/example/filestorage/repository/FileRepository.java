package com.example.filestorage.repository;

import com.example.filestorage.model.MyFile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends ElasticsearchRepository<MyFile, String> {
}
