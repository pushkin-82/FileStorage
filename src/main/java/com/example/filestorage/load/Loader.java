package com.example.filestorage.load;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
public class Loader {

    @Autowired
    ElasticsearchOperations operations;

    @Autowired
    FileRepository fileRepository;

    @PostConstruct
    @Transactional
    public void loadAll() {
        operations.indexOps(MyFile.class);
    }

}