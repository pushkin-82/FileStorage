package com.example.filestorage.service;

import com.example.filestorage.extensions.AudioExtension;
import com.example.filestorage.extensions.DocumentExtension;
import com.example.filestorage.extensions.ImageExtension;
import com.example.filestorage.extensions.VideoExtension;
import com.example.filestorage.model.File;
import com.example.filestorage.repository.FileRepository;
import com.example.filestorage.service.exception.NoSuchFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            File currentFile = getById(id);

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
            File current = getById(id);

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
    public Page<File> getAllByTagsAndNameContaining(String[] tags, String template, Pageable pageable) {
        LOG.debug("In getAllByNameContaining receive files that contain tags and " +
                "with name that contains template with paginating");

        return repository.findAllByTagsAndNameContaining(Arrays.asList(tags), template, pageable);
    }

    @Override
    public File getById(String id) {
        LOG.debug("In getById find file with id: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> new NoSuchFileException(format("No file with id:%s was found", id)));
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

    private void addDefaultTagsToFile(File file) {
        String extension = "";
        String fileName = file.getName();

        if (fileName.indexOf('.') >= 0) {
            int index = fileName.lastIndexOf('.');
            extension = fileName.substring(index + 1);
        }
        if (AudioExtension.isInValues(extension)) {
            file.addTag("audio");
        } else if (VideoExtension.isInValues(extension)) {
            file.addTag("video");
        } else if (DocumentExtension.isInValues(extension)) {
            file.addTag("document");
        } else if (ImageExtension.isInValues(extension)) {
            file.addTag("image");
        }
    }

}
