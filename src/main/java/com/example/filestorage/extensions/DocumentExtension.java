package com.example.filestorage.extensions;

public enum DocumentExtension implements Extension {
    DOC("doc");

    private String title;

    DocumentExtension(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static boolean isInValues(String extension) {
        for (DocumentExtension ext : values()) {
            if (ext.getTitle().equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
