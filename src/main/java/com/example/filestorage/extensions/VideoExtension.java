package com.example.filestorage.extensions;

public enum VideoExtension implements Extension {
    MP4("mp4");

    private final String title;

    VideoExtension(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static boolean isInValues(String extension) {
        for (VideoExtension ext : values()) {
            if (ext.getTitle().equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
