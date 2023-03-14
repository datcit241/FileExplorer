package com.example.fileexplorer.fragments;

import static androidx.core.content.ContextCompat.getExternalFilesDirs;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fileexplorer.FileAdapter;
import com.example.fileexplorer.R;
import com.example.fileexplorer.utilities.FileFilter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardFragment extends Fragment {
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    //    private ImageView img_back;
    private TextView tv_pahHolder;
    private File storage;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_card, container, false);

        tv_pahHolder = view.findViewById(R.id.tv_pathHolder);
//        img_back = view.findViewById(R.id.img_back);

        String externalStorageState = Environment.getExternalStorageState();

        String sdCardPath = null;
        File[] externalFilesDirs = getExternalFilesDirs(getActivity(), null);
        for (File file : externalFilesDirs) {
            if (file != null && !file.equals(getExternalFilesDirs(getActivity(), null))) {
                String filePath = file.getAbsolutePath();
                int index = filePath.indexOf("/Android/data");
                if (index > 0) {
                    sdCardPath = filePath.substring(0, index);
                }
            }
        }

        String sdCardAbsolutePath = null;
        if (sdCardPath != null) {
            File sdCard = new File(sdCardPath);
            if (sdCard.exists() && sdCard.canWrite()) {
                sdCardAbsolutePath = sdCard.getAbsolutePath();
                //Log.d(TAG, "ring dẫn của thẻ nhớ SD: " + sdCardAbsolutePath);
            }
        }


        tv_pahHolder.setText(sdCardAbsolutePath);
        runtimePermission();

        return view;
    }

    private void runtimePermission() {
        Dexter.withContext(getContext())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displayFiles();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fileList = new ArrayList<>();
        fileList.addAll(FileFilter.filter(storage, null));
        fileAdapter = new FileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(fileAdapter);
    }
}
