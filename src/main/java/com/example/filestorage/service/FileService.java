package com.example.filestorage.service;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.repository.FileRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private final FileRepository repository;

    public FileService(FileRepository repository) {
        this.repository = repository;
    }

    public boolean uploadFile(MyFile myFile) {
        if (myFile != null && repository.existsById(myFile.getId())) {
            if (myFile.getName() != null && !myFile.getName().isBlank() && myFile.getSize() > 0) {
                return true;
            }
        }
        return false;
    }

    public List<MyFile> getAll() {
        List<MyFile> resultList = new ArrayList<>();
        repository.findAll().forEach(t -> resultList.add(t));

        return resultList;
    }

    public MyFile getById(Long id) {
        return repository.findById(id).get();
    }

    public boolean deleteById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
