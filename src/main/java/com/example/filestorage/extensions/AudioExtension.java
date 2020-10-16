package com.example.filestorage.extensions;

public enum AudioExtension implements Extension {
    MP3 ("mp3"),
    FLAC("flac");

    private String title;

    AudioExtension(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public static boolean isInValues(String extension) {
        for (AudioExtension ext : values()) {
            if (ext.getTitle().equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}