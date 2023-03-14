package com.example.fileexplorer.utilities;

import static java.util.Map.entry;

import com.example.fileexplorer.enums.FileTypeEnum;

import java.util.Map;

public class FileValidator {
    private static Map<FileTypeEnum, String[]> fileTypeToExtensionMap = Map.ofEntries(
            entry(FileTypeEnum.IMAGE, new String[]{".jpeg", ".jpg", ".png"}),
            entry(FileTypeEnum.VIDEO, new String[]{".mp4"}),
            entry(FileTypeEnum.MUSIC, new String[]{".mp3", ".wav"}),
            entry(FileTypeEnum.DOCS, new String[]{".doc", ".pdf"}),
            entry(FileTypeEnum.APKS, new String[]{".apk"})
    );

    public static boolean validateFileName(FileTypeEnum fileType, String fileName) {
        String[] extensions = fileTypeToExtensionMap.get(fileType);
        assert extensions != null;
        for (String ext : extensions) {
            if (fileName.endsWith(ext)) return true;
        }

        return false;
    }
}
