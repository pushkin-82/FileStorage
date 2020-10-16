package com.example.filestorage.repository;

import com.example.filestorage.model.MyFile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends ElasticsearchRepository<MyFile, String> {

    List<MyFile> findAllByTags(Set<String> tags);
}
