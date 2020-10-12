package com.example.filestorage.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document(indexName = "file")
public class MyFile {

    @Id
    private Long id;

    private String name;

    private Long size;

    private Set<String> tags = new HashSet<>();

    public MyFile() {
    }

    public MyFile(String name, Long size) {
        this.name = name;
        this.size = size;
    }

    public MyFile(Long id, String name, Long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags.addAll(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MyFile myFile = (MyFile) o;

        return Objects.equals(id, myFile.id)
                && Objects.equals(name, myFile.name)
                && Objects.equals(size, myFile.size)
                && Objects.equals(tags, myFile.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, size);
    }
}
