package com.example.filestorage.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Extensions {

    private static final Map<String, List<String>> defaultTags = new HashMap<>();

    static {
        List<String> audioExt = new ArrayList<>(Arrays.asList("mp3", "flac"));
        List<String> videoExt = new ArrayList<>(Arrays.asList("mp4", "avi"));
        List<String> docExt = new ArrayList<>(Arrays.asList("doc", "txt"));
        List<String> imageExt = new ArrayList<>(Arrays.asList("jpeg", "png"));

        defaultTags.put("audio", audioExt);
        defaultTags.put("video", videoExt);
        defaultTags.put("document", docExt);
        defaultTags.put("image", imageExt);
    }

    private Extensions() {
    }

    public static Map<String, List<String>> getDefaultTags() {
        return defaultTags;
    }

}
