package com.example.fileexplorer.Fragment;

import static androidx.core.content.ContextCompat.getExternalFilesDirs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fileexplorer.FileAdapter;
import com.example.fileexplorer.FileOpener;
import com.example.fileexplorer.MainActivity;
import com.example.fileexplorer.OnFileSelectedListener;
import com.example.fileexplorer.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CardFragment extends Fragment implements OnFileSelectedListener {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    private ImageView img_back;
    private TextView tv_pahHolder;
    File storage;
    String data;
    String[] items = {"Details", "Rename", "Delete", "Share"};
    String secStorage;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_card, container, false);

        tv_pahHolder = view.findViewById(R.id.tv_pathHolder);
        img_back = view.findViewById(R.id.img_back);

        String externalStorageState = Environment.getExternalStorageState();

//        if (Environment.MEDIA_MOUNTED.equals(externalStorageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState)) {
//            File externalStorageDirectory = Environment.getExternalStorageDirectory();
//            String externalStoragePath = externalStorageDirectory.getAbsolutePath();
//            // Show the path of external storage
////            Toast.makeText(this, "External Storage Path: " + externalStoragePath, Toast.LENGTH_SHORT).show();
//            Log.i("TAG", "External Storage Path: " + externalStoragePath);
//            secStorage = externalStoragePath;
//
//        } else {
//            // External storage is not available
////            Toast.makeText(this, "External Storage Not Available", Toast.LENGTH_SHORT).show();
//            Log.i("TAG", "External Storage Not Available");
//        }


//        File[] externalDirs = ContextCompat.getExternalFilesDirs(getActivity(), null);
//
//        if (externalDirs.length > 1 && externalDirs[0] != null) {
//            secStorage = externalDirs[0].getAbsolutePath();
//        } else {
//           // secStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
//        }
//
//        storage = new File(secStorage);
//
//        try {
//            data = getArguments().getString("path");
//            if (data != null) {
//                File file = new File(data);
//                if (file.exists()) {
//                    storage = file;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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
                .withListener(new MultiplePermissionsListener(){
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


    public ArrayList<File> findFile(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        if (file.exists()) { // kiểm tra xem file tồn tại hay không
            File[] files = file.listFiles();
            if (files != null) { // kiểm tra xem files có tồn tại hay không
                for (File singleFile : files) {
                    if (singleFile.isDirectory() && !singleFile.isHidden()) {
                        arrayList.add(singleFile);
                    }
                }
                for (File singleFile : files) {
                    if (singleFile.getName().toLowerCase().endsWith(".jpeg") ||
                            singleFile.getName().toLowerCase().endsWith(".jpg") ||
                            singleFile.getName().toLowerCase().endsWith(".png") ||
                            singleFile.getName().toLowerCase().endsWith(".mp3") ||
                            singleFile.getName().toLowerCase().endsWith(".mp4") ||
                            singleFile.getName().toLowerCase().endsWith(".wav") ||
                            singleFile.getName().toLowerCase().endsWith(".pdf") ||
                            singleFile.getName().toLowerCase().endsWith(".doc") ||
                            singleFile.getName().toLowerCase().endsWith(".apk")) {

                        arrayList.add(singleFile);
                    }
                }
            } else {
                // xử lý trường hợp files là null
            }
        } else {
            // xử lý trường hợp file không tồn tại
        }
        return arrayList;
    }



    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fileList = new ArrayList<>();
        fileList.addAll(findFile(storage));
        fileAdapter = new FileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onFileClicked(File file) {
        if (file.isDirectory()) {
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            CardFragment internalFragment = new CardFragment();
            internalFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();
        } else {
            try {
                FileOpener.openFlie(getContext(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onFileLongClicked(File file, int position) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options");
        ListView options = (ListView) optionDialog.findViewById(R.id.List);
        CustomAdapter customAdapter = new CustomAdapter();
        options.setAdapter(customAdapter);
        optionDialog.show();
        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                switch (selectedItem) {

                    case "Details":
                        AlertDialog.Builder detailDialog = new AlertDialog.Builder(getContext());
                        detailDialog.setTitle("Details: ");
                        final TextView details = new TextView(getContext());
                        detailDialog.setView(details);
                        Date lastModified = new Date(file.lastModified());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String formattedDate = formatter.format(lastModified);


                        details.setText("File Name: " + file.getName() + "\n" +
                                "Size: " + Formatter.formatShortFileSize(getContext(), file.length()) + "\n" +
                                "Path: " + file.getAbsolutePath() + "\n" +
                                "Last Modified: " + formattedDate);

                        detailDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();

                            }
                        });
                        AlertDialog alertdialog_details = detailDialog.create();
                        alertdialog_details.show();
                        break;

                    case "Rename":
                        AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                        renameDialog.setTitle("Rename File: ");
                        final EditText name = new EditText(getContext());
                        renameDialog.setView(name);

                        renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String new_name = name.getEditableText().toString();
                                String extention = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                File current = new File(file.getAbsolutePath());
                                File destination = new File(file.getAbsolutePath().replace(file.getName(), new_name) + extention);
                                if (current.renameTo(destination)) {
                                    fileList.set(position, destination);
                                    fileAdapter.notifyItemChanged(position);
                                    Toast.makeText(getContext(), "Renamed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Couldn't Rename!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertDialog_rename = renameDialog.create();
                        alertDialog_rename.show();

                        break;
                    case "Delete":
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                        deleteDialog.setTitle("Delete " + file.getName() + "?");
                        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                file.delete();
                                fileList.remove(position);
                                fileAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertDialog_delete = deleteDialog.create();
                        alertDialog_delete.show();
                        break;
                    case "Share":
                        String fileName = file.getName();
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file));
                        startActivity(Intent.createChooser(share, "Share " + fileName));
                        break;


                }
            }
        });
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.option_layout, null);
            TextView txtOption = myView.findViewById(R.id.txtOption);
            ImageView imgOption = myView.findViewById(R.id.imgOption);
            txtOption.setText(items[i]);
            if (items[i].equals("Details")) {
                imgOption.setImageResource(R.drawable.ic_details);
            } else if (items[i].equals("Rename")) {
                imgOption.setImageResource(R.drawable.ic_rename);
            } else if (items[i].equals("Delete")) {
                imgOption.setImageResource(R.drawable.ic_delete);
            } else if (items[i].equals("Share")) {
                imgOption.setImageResource(R.drawable.ic_share);
            }
            return myView;
        }
    }
}
