package com.example.filestorage.service;

import com.example.filestorage.constants.Constants;
import com.example.filestorage.model.File;
import com.example.filestorage.repository.FileRepository;
import com.example.filestorage.service.exception.NoSuchFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private final FileRepository repository;

    public FileServiceImpl(FileRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Optional<String> uploadFile(File file) {
        LOG.debug("In uploadFile attempt to upload file");

        String error;

        if (file == null) {
            error = "file cannot be null";
        } else if (file.getName() == null || file.getName().isBlank()) {
            error = "Name should not be empty";
        } else if (file.getSize() == null) {
            error = "File size should not be null";
        } else if (file.getSize() < 0) {
            error = "File size shouldn't be a negative number";
        } else {
            error = "";
        }

        if (!error.isBlank()) {
            return Optional.of(error);
        }

        addDefaultTagsToFile(file);
        repository.save(file);

        LOG.debug("In uploadFile successfully uploaded file with id={}", file.getId());

        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean assignTags(String id, String[] tags) {
        LOG.debug("In assignTags add new tags to file with id={}", id);

        if(repository.findById(id).isPresent()) {
            File currentFile = getById(id).get();

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
    @Transactional
    public boolean removeTags(String id, String[] tags) {
        LOG.debug("In removeTags remove tags from file with id={}", id);

        if (repository.findById(id).isPresent()) {
            File current = getById(id).get();

            boolean result = current.removeTags(Arrays.asList(tags));

            repository.save(current);

            return result;
        }
        return false;
    }

    @Override
    public Page<File> getAll(Pageable pageable) {
        LOG.debug("In getAll receive files with paginating");

        return repository.findAll(pageable);
    }

    @Override
    public Page<File> getAllByTags(String[] tags, Pageable pageable) {
        LOG.debug("In getAllByTags receive files that contain tags with paginating");

        return repository.findAllByTags(Arrays.asList(tags), pageable);
    }

    @Override
    public Page<File> getAllByNameContaining(String template, Pageable pageable) {
        LOG.debug("In getAllByNameContaining receive files with name that contains template with paginating");

        return repository.findAllByNameContaining(template, pageable);
    }

    @Override
    public Page<File> getAllByTagsAndNameContaining(@Nullable String[] tags, @Nullable String template, Pageable pageable) {
        LOG.debug("In getAllByNameContaining receive files that contain tags and " +
                "with name that contains template with paginating");

        Page<File> resultPage;
        if (tags == null) {
            if (template == null) {
                resultPage = repository.findAll(pageable);
            } else {
                resultPage = repository.findAllByNameContaining(template, pageable);
            }
        } else {
            if (template == null) {
                resultPage = repository.findAllByTags(Arrays.asList(tags), pageable);
            } else {
                resultPage = repository.findAllByTagsAndNameContaining(Arrays.asList(tags), template, pageable);
            }
        }
        return resultPage;
    }

    @Override
    public Optional<File> getById(String id) {
        LOG.debug("In getById find file with id: {}", id);

        if (repository.existsById(id)) {
            return repository.findById(id);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteById(String id) {
        LOG.debug("In deleteById attempt to delete file with id: {}", id);

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    private String getExtension(@NonNull File file) {
        String extension = "";
        String fileName = file.getName();

        if (fileName.indexOf('.') >= 0) {
            int index = fileName.lastIndexOf('.');
            extension = fileName.substring(index + 1);
        }

        return extension;
    }

    private void addDefaultTagsToFile(File file) {
        String extension = getExtension(file);

        Constants.DEFAULT_TAGS.getDefaultTags().forEach((k, v) -> {
            if (v.contains(extension)) {
                file.addTag(k);
            }
        });
    }

}
