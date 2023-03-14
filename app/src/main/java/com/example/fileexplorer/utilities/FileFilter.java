package com.example.fileexplorer.utilities;

import com.example.fileexplorer.enums.FileTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFilter {
    public static List<File> filter(File file, FileTypeEnum fileType) {
        List<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files == null) {
            return arrayList;
        }

        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(filter(singleFile, fileType));
            } else if (!singleFile.getName().startsWith(".")) {
                if (fileType == null || fileType == FileTypeEnum.DOWNLOAD || FileValidator.validateFileName(fileType, singleFile.getName().toLowerCase())) {
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }
}
