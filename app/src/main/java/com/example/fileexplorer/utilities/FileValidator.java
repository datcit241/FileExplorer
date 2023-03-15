package com.example.fileexplorer.utilities;

import static java.util.Map.entry;

import com.example.fileexplorer.R;
import com.example.fileexplorer.enums.FileTypeEnum;
import com.example.fileexplorer.models.FileType;

import java.util.Map;

public class FileValidator {
    private static Map<FileTypeEnum, FileType> fileTypeToExtensionMap = Map.ofEntries(
            entry(FileTypeEnum.IMAGE, new FileType(
                    new String[]{".jpeg", ".jpg", ".png"},
                    R.drawable.ic_baseline_image_24,
                    "image/jpeg"
            )),
            entry(FileTypeEnum.VIDEO, new FileType(
                    new String[]{".mp4"},
                    R.drawable.ic_play,
                    "video/*"
            )),
            entry(FileTypeEnum.MUSIC, new FileType(
                    new String[]{".mp3", ".wav"},
                    R.drawable.ic_music,
                    "audio/x-wav"
            )),
            entry(FileTypeEnum.DOC, new FileType(
                    new String[]{".doc"},
                    R.drawable.ic_doc,
                    "application/msword"
            )),
            entry(FileTypeEnum.PDF, new FileType(
                    new String[]{".pdf"},
                    R.drawable.ic_pdf,
                    "application/pdf"
            )),
            entry(FileTypeEnum.DOCS, new FileType(
                    new String[]{".doc", ".pdf"},
                    R.drawable.ic_doc,
                    "application/*"
            )),
            entry(FileTypeEnum.APKS, new FileType(
                    new String[]{".apk"},
                    R.drawable.ic_android,
                    "*/*"
            ))
    );

    public static boolean validateFileName(FileTypeEnum fileType, String fileName) {
        String[] extensions = fileTypeToExtensionMap.get(fileType).getExtensions();
        assert extensions != null;
        for (String ext : extensions) {
            if (fileName.endsWith(ext)) return true;
        }

        return false;
    }

    public static FileTypeEnum getFileTypeEnum(String fileName) {
        for (Map.Entry<FileTypeEnum, FileType> entry : fileTypeToExtensionMap.entrySet()) {
            for (String ext : entry.getValue().getExtensions()) {
                if (fileName.endsWith(ext)) return entry.getKey();
            }
        }

        return null;
    }

    public static String getIntentType(String fileName) {
        FileTypeEnum fileTypeEnum = getFileTypeEnum(fileName);
        if (fileTypeEnum == null) return "*.*";
        return fileTypeToExtensionMap.get(fileTypeEnum).getIntentType();
    }

    public static int getIcon(String fileName) {
        FileTypeEnum fileTypeEnum = getFileTypeEnum(fileName);
        if (fileTypeEnum == null) return 0;
        return fileTypeToExtensionMap.get(fileTypeEnum).getResId();
    }
}
