package com.example.filestorage.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document(indexName = "file")
public class MyFile {

    @Id
    private String id;

    private String name;

    private Long size;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<String> tags = new HashSet<>();

    public MyFile() {
    }

    public MyFile(String name, Long size) {
        this(null, name, size);
    }

    public MyFile(String id, String name, Long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public void setTags(Collection<String> tags) {
        this.tags.addAll(tags);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public boolean removeTags(Collection<String> tags) {
        for (String tag : tags) {
            if (!this.tags.remove(tag)) {
                return false;
            }
        }
        return true;
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

    @Override
    public String toString() {
        return "MyFile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", tags=" + tags +
                '}';
    }
}
