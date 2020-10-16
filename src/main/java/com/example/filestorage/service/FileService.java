package com.example.filestorage.service;

import com.example.filestorage.model.MyFile;
import com.example.filestorage.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

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
        } else if (myFile.getSize() == null) {
            error = "File size should not be null";
        } else if (myFile.getSize() < 0) {
            error = "File size shouldn't be a negative number";
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

            for (String tag : tags) {
                if (!currentFile.getTags().contains(tag)) {
                    currentFile.addTag(tag);
                }
            }
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

    public Page<MyFile> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<MyFile> getAllByTags(String[] tags, Pageable pageable) {
       return repository.findAllByTags(Arrays.asList(tags), pageable);
    }

    public Page<MyFile> getAllByNameContaining(String template, Pageable pageable) {
        return repository.findAllByNameContaining(template, pageable);
    }

    public Page<MyFile> getAllByTagsAndNameContaining(String[] tags, String template, Pageable pageable) {
        return repository.findAllByTagsAndNameContaining(Arrays.asList(tags), template, pageable);
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
