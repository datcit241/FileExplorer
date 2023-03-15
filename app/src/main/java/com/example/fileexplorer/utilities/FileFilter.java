package com.example.fileexplorer.utilities;

import com.example.fileexplorer.enums.FileTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFilter {
    public static List<File> filter(File file, FileTypeEnum fileType, boolean stickWithCurrentDirectory) {
        List<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files == null) {
            return arrayList;
        }

        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                if (stickWithCurrentDirectory) {
                    arrayList.add(singleFile);
                } else {
                    arrayList.addAll(filter(singleFile, fileType, false));
                }
            } else if (!singleFile.getName().startsWith(".")) {
                String fileName = singleFile.getName().toLowerCase();
                if (fileType == null || fileType == FileTypeEnum.DOWNLOAD || FileValidator.validateFileName(fileType, fileName)) {
                    if (FileValidator.getFileTypeEnum(fileName) != null) {
                        arrayList.add(singleFile);
                    }
                }
            }
        }
        return arrayList;
    }
}
