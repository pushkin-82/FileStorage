package com.example.filestorage.load;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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
        fileRepository.saveAll(getData());
    }

    private List<MyFile> getData() {
        List<MyFile> myFiles = new ArrayList<>();
        myFiles.add(new MyFile(123L, "qwe", 12000L));
        myFiles.add(new MyFile(124L, "wer", 123123L));
        myFiles.add(new MyFile(125L, "erty", 12L));
        return myFiles;
    }
}