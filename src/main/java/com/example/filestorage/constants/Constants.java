package com.example.filestorage.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Constants {
    DEFAULT_TAGS(initTags());

    private final Map<String, List<String>> defaultTags;

    Constants(Map<String, List<String>> tags) {
        this.defaultTags = tags;
    }

    public Map<String, List<String>> getDefaultTags() {
        return defaultTags;
    }

    private static Map<String, List<String>> initTags() {
        Map<String, List<String>> tags = new HashMap<>();
        List<String> audioExt = new ArrayList<>(Arrays.asList("mp3", "flac"));
        List<String> videoExt = new ArrayList<>(Arrays.asList("mp4", "avi"));
        List<String> docExt = new ArrayList<>(Arrays.asList("doc", "txt"));
        List<String> imageExt = new ArrayList<>(Arrays.asList("jpeg", "png"));

        tags.put("audio", audioExt);
        tags.put("video", videoExt);
        tags.put("document", docExt);
        tags.put("image", imageExt);

        return tags;
    }

}
