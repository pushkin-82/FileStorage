package com.example.filestorage.service;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.repository.FileRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final FileRepository repository;

    public FileService(FileRepository repository) {
        this.repository = repository;
    }

    public Optional<String> uploadFile(MyFile myFile) {
        String error;

        if (myFile == null) {
            error = "file cannot be null";
        } else if (myFile.getName() == null || myFile.getName().isBlank()) {
            error = "Name should not be empty";
        } else if (!myFile.getName().matches("[a-z1-9]+\\.[a-z1-9]{3,4}")) {
            error = "File should have proper extension";
        } else if (myFile.getSize() <= 0) {
            error = "File size should be positive number";
        } else {
            error = "";
        }

        if (!error.isBlank()) {
            return Optional.of(error);
        }

        repository.save(myFile);
        return Optional.empty();
    }

    public boolean assignTags(String id, String[] tags) {
        if(repository.findById(id).isPresent()) {
            MyFile currentFile = repository.findById(id).get();

            currentFile.setTags(Arrays.asList(tags));
            repository.save(currentFile);

            return true;
        }
        return false;
    }

    public boolean removeTags(String id, String[] tags) {
        if (repository.findById(id).isPresent()) {
            MyFile current = repository.findById(id).get();

            boolean result = current.removeTags(Arrays.asList(tags));

            repository.save(current);

            return result;
        }
        return false;
    }

    public List<MyFile> getAll() {
        List<MyFile> resultList = new ArrayList<>();
        Iterable<MyFile>  list = repository.findAll();
        list.forEach(resultList::add);

        return resultList;
    }

    public List<MyFile> getAllWithFilter(String[] tags) {
        Iterable<MyFile>  list = repository.findAll();

        List<MyFile> resultList = new ArrayList<>();

        list.forEach(resultList::add);

        return resultList
                .stream()
                .filter(file -> file.getTags().containsAll(Arrays.asList(tags))).collect(Collectors.toList());
    }

    public MyFile getById(String id) {
        return repository.findById(id).get();
    }

    public boolean deleteById(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
