package com.example.fileexplorer.models;

public class FileType {
    private String[] extensions;
    private int resId;
    private String intentType;

    public FileType(String[] extensions, int resId, String intentType) {
        this.extensions = extensions;
        this.resId = resId;
        this.intentType = intentType;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public int getResId() {
        return resId;
    }

    public String getIntentType() {
        return intentType;
    }
}
