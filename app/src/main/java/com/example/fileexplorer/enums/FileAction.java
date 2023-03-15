package com.example.fileexplorer.enums;

import androidx.annotation.NonNull;

public enum FileAction {
    DETAILS,
    RENAME,
    DELETE,
    SHARE;

    @NonNull
    @Override
    public String toString() {
        String str = super.toString().toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
