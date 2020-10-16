package com.example.filestorage.extensions;

public enum ImageExtension implements Extension {
    JPEG("jpeg");

    private String title;

    ImageExtension(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static boolean isInValues(String extension) {
        for (ImageExtension ext : values()) {
            if (ext.getTitle().equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
