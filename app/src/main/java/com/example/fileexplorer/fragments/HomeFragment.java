package com.example.fileexplorer.fragments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.util.Map.entry;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fileexplorer.FileAdapter;
import com.example.fileexplorer.R;
import com.example.fileexplorer.enums.FileTypeEnum;
import com.example.fileexplorer.utilities.FileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private static final int REQUEST_CODE = 23;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    private LinearLayout linearImage, linearVideo, linearMusic, linearDocs, linearDownload, linearApks;

    String currentAndroidVersion = android.os.Build.VERSION.RELEASE;
    int currentApiVersion = android.os.Build.VERSION.SDK_INT;

    File storage;
    String data;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        linearImage = view.findViewById(R.id.linearImage);
        linearVideo = view.findViewById(R.id.linearVideo);
        linearMusic = view.findViewById(R.id.linearMusic);
        linearDownload = view.findViewById(R.id.linearDownloads);
        linearApks = view.findViewById(R.id.linearApks);
        linearDocs = view.findViewById(R.id.linearDocs);

        Map<FileTypeEnum, LinearLayout> map = Map.ofEntries(
                entry(FileTypeEnum.IMAGE, linearImage),
                entry(FileTypeEnum.VIDEO, linearVideo),
                entry(FileTypeEnum.MUSIC, linearMusic),
                entry(FileTypeEnum.DOWNLOAD, linearDownload),
                entry(FileTypeEnum.APKS, linearApks),
                entry(FileTypeEnum.DOCS, linearDocs)
        );

        setListeners(map);

        if (checkPermission()) {
            displayFiles();
        }

        return view;
    }

    private void setListeners(Map<FileTypeEnum, LinearLayout> map) {
        for (Map.Entry<FileTypeEnum, LinearLayout> entry : map.entrySet()) {
            entry.getValue().setOnClickListener(view -> {
                System.out.println("Clicked");
                Bundle args = new Bundle();
                args.putString("fileType", entry.getKey().toString());
                CategorizedFragment categorizedFragment = new CategorizedFragment();
                categorizedFragment.setArguments(args);
                getParentFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();
            });
        }
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    public ArrayList<File> findFile11(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = {MediaStore.Files.FileColumns.DATA};

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " +
                MediaStore.Files.FileColumns.MEDIA_TYPE_NONE +
                " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = " +
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = " +
                MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO +
                " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = " +
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        String[] selectionArgs = null;

        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                File singleFile = new File(filePath);

                if (singleFile.getParent().equals(file.getAbsolutePath())) {
                    arrayList.add(singleFile);
                }
            }
            cursor.close();
        }

        return arrayList;
    }

    private void displayFiles() {
        String[] parts = currentAndroidVersion.split("\\.");
        int firstPart = Integer.parseInt(parts[0]);
        recyclerView = view.findViewById(R.id.recycler_recents);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        fileList = new ArrayList<>();
        fileList.addAll(FileFilter.filter(Environment.getExternalStorageDirectory(), null));
        fileList.sort(Comparator.comparing(File::lastModified).reversed());
        ;
        fileAdapter = new FileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(fileAdapter);
    }
}
