package com.example.filestorage.service;

import com.example.filestorage.extensions.AudioExtension;
import com.example.filestorage.extensions.DocumentExtension;
import com.example.filestorage.extensions.ImageExtension;
import com.example.filestorage.extensions.VideoExtension;
import com.example.filestorage.model.MyFile;
import com.example.filestorage.repository.FileRepository;
import com.example.filestorage.service.exception.NoSuchFileException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository repository;

    public FileServiceImpl(FileRepository repository) {
        this.repository = repository;
    }

    @Override
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

        if (myFile != null) {
            addDefaultTagsToFile(myFile);
            repository.save(myFile);
        }

        return Optional.empty();
    }

    @Override
    public boolean assignTags(String id, String[] tags) {
        if(repository.findById(id).isPresent()) {
            MyFile currentFile = getById(id);

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

    @Override
    public boolean removeTags(String id, String[] tags) {
        if (repository.findById(id).isPresent()) {
            MyFile current = getById(id);

            boolean result = current.removeTags(Arrays.asList(tags));

            repository.save(current);

            return result;
        }
        return false;
    }

    @Override
    public Page<MyFile> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<MyFile> getAllByTags(String[] tags, Pageable pageable) {
       return repository.findAllByTags(Arrays.asList(tags), pageable);
    }

    @Override
    public Page<MyFile> getAllByNameContaining(String template, Pageable pageable) {
        return repository.findAllByNameContaining(template, pageable);
    }

    @Override
    public Page<MyFile> getAllByTagsAndNameContaining(String[] tags, String template, Pageable pageable) {
        return repository.findAllByTagsAndNameContaining(Arrays.asList(tags), template, pageable);
    }

    @Override
    public MyFile getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchFileException(format("No file with id:%s was found", id)));
    }

    @Override
    public boolean deleteById(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    private void addDefaultTagsToFile(MyFile myFile) {
        String extension = "";
        String fileName = myFile.getName();
        if (fileName.indexOf('.') >= 0) {
            int index = fileName.lastIndexOf('.');
            extension = fileName.substring(index + 1);
        }
        if (AudioExtension.isInValues(extension)) {
            myFile.addTag("audio");
        } else if (VideoExtension.isInValues(extension)) {
            myFile.addTag("video");
        } else if (DocumentExtension.isInValues(extension)) {
            myFile.addTag("document");
        } else if (ImageExtension.isInValues(extension)) {
            myFile.addTag("image");
        }
    }

}
