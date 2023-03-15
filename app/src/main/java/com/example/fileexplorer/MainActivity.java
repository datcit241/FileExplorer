package com.example.fileexplorer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fileexplorer.fragments.HomeFragment;
import com.example.fileexplorer.fragments.InternalFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    public static final int STORAGE_PERMISSION_CODE = 12;
    public static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 23;

    public float usedSize;
    private PieChart piechart;
    private TextView imgSize;
    private TextView vidSize;
    private TextView musicSize;
    private TextView apkSize;
    private TextView otherSize;
    private TextView docSize;
    private LinearLayout sizeLayout;
    private LinearLayout linearImage, linearVideo, linearMusic, linearDocs, linearDownload, linearApks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        piechart = findViewById(R.id.piechart);
        sizeLayout = findViewById(R.id.size_layout);
        sizeLayout.setVisibility(View.GONE);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open_drawer, R.string.Close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        requestPermission();


        if (checkPermission()) {
            sizeLayout.setVisibility(View.VISIBLE);
            buildPiechart();
            doShowPieChart();
            doShowSizeTable();
        }
        String fragmentName = getIntent().getStringExtra("fragment_name");
        Log.d("TAG", "Fragment name is " + fragmentName);
        // Open corresponding fragment
        if ("InternalCard".equals(fragmentName)) {
            InternalFragment internalCardFragment = new InternalFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, internalCardFragment).commit();
        }
        if ("HomeFragment".equals(fragmentName)) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment).commit();
        }

    }

    private void viewPermissionSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri url = Uri.fromParts("package", getPackageName(), null);
        intent.setData(url);
        startActivity(intent);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            try {
                Log.d("TAG", "requestPermission: try");

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Log.e("TAG", "requestPermission: catch", e);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("TAG", "onActivityResult: ");
                    //here we will handle the result of our intent
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        //Android is 11(R) or above
                        if (Environment.isExternalStorageManager()) {
                            //Manage External Storage Permission is granted
                            Log.d("TAG", "onActivityResult: Manage External Storage Permission is granted");

                        } else {
                            //Manage External Storage Permission is denied
                            Log.d("TAG", "onActivityResult: Manage External Storage Permission is denied");
                            Toast.makeText(MainActivity.this, "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Android is below 11(R)
                    }
                }
            }
    );

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    /*Handle permission request results*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                //check each permission if granted or not
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (write && read) {
                    //External Storage permissions granted
                    Log.d("TAG", "onRequestPermissionsResult: External Storage permissions granted");
                    buildPiechart();
                    doShowPieChart();
                    doShowSizeTable();
                } else {
                    //External Storage permission denied
                    Log.d("TAG", "onRequestPermissionsResult: External Storage permission denied");
                    Toast.makeText(this, "External Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                HomeFragment homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_internal:
                InternalFragment internalFragment = new InternalFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();
                break;
//            case R.id.nav_card:
//                CardFragment cardFragment = new CardFragment();
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cardFragment).addToBackStack(null).commit();
//                break;
//            case R.id.nav_Permission:
//                //checkPermissions();
//                break;
            case R.id.nav_PermissionSetting:
                viewPermissionSetting();
                break;
            case R.id.nav_about:
                String currentAndroidVersion = android.os.Build.VERSION.RELEASE;
                int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                Toast.makeText(this, "This is a final term project of 2DT group and this device is now using " +
                        "Android version: " + currentAndroidVersion + " and API: " + currentApiVersion, Toast.LENGTH_LONG).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStackImmediate();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void doShowPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        usedSize = getUsedInternalMemoryPercentage();
        entries.add(new PieEntry(usedSize, " "));
        entries.add(new PieEntry((100 - usedSize), " "));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(160, 206, 217));
        colors.add(Color.rgb(209, 207, 226));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        data.setValueFormatter(new PercentFormatter(piechart));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.rgb(6, 57, 112));

        piechart.setData(data);
        piechart.invalidate();
        piechart.animateY(3500, Easing.EaseOutQuart);
    }

    private void buildPiechart() {
        piechart.setDrawHoleEnabled(true);
        piechart.setUsePercentValues(false);
        piechart.setEntryLabelTextSize(15);
        piechart.setEntryLabelColor(Color.DKGRAY);

        double usedMemory = getTotalInternalMemorySizeInGB() - getAvailableInternalMemorySizeInGB();
        int usedMemoryRounded = (int) Math.round(usedMemory * 100) / 100;

        String coreText = usedMemoryRounded + "/" + (int) getTotalInternalMemorySizeInGB() + "\n" + "GB";
        piechart.setCenterText(coreText);

        piechart.setCenterTextSize(24);
        piechart.setTouchEnabled(false);
        piechart.getDescription().setEnabled(false);

        // Create a new Legend object and customize it
        Legend legend = piechart.getLegend();
        legend.setEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f);

        // Create a new ArrayList of LegendEntries and add entries for your data
        ArrayList<LegendEntry> entries = new ArrayList<>();
        entries.add(new LegendEntry("Used Memory", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(160, 206, 217)));
        entries.add(new LegendEntry("The Rest", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.rgb(209, 207, 226)));

        // Set the custom LegendEntries to the Legend object
        legend.setCustom(entries);
    }

    public float getTotalInternalMemorySizeInGB() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        return (totalBlocks * blockSize) / 1073741824f;
    }

    public float getTotalInternalMemorySizeInByte() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    public float getAvailableInternalMemorySizeInGB() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        long bytesAvailable = availableBlocks * blockSize;
        return bytesAvailable / 1073741824f;
    }

    public float getUsedInternalMemoryPercentage() {
        float totalSize = getTotalInternalMemorySizeInGB();
        float availableSize = getAvailableInternalMemorySizeInGB();
        float usedSize = totalSize - availableSize;
        return usedSize / totalSize * 100f;
    }

    private String formatByteSize(long byteSize) {
        String[] units = {"bytes", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = byteSize;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(size) + " " + units[unitIndex];
    }

    public void doShowSizeTable() {
        sizeLayout.setVisibility(View.VISIBLE);

        imgSize = findViewById(R.id.img_size);
        vidSize = findViewById(R.id.vid_size);
        musicSize = findViewById(R.id.music_size);
        apkSize = findViewById(R.id.apk_size);
        // otherSize = findViewById(R.id.other_size);
        docSize = findViewById(R.id.doc_size);

        imgSize.setText(formatByteSize(getTotalImageSize(this)));
        vidSize.setText(formatByteSize(getTotalVideoSize(this)));
        musicSize.setText(formatByteSize(getTotalMusicSize(this)));
        docSize.setText(formatByteSize(getTotalDocSize(this)));
        apkSize.setText(formatByteSize(getTotalAPKSize(this)));

        float other = getAvailableInternalMemorySizeInGB();
    }

    private long getTotalAPKSize(Context context) {
        return getTotalFileTypeSize(
                MediaStore.Files.getContentUri("external"),
                MediaStore.Files.FileColumns.MIME_TYPE + "=?", new String[]{"application/vnd.android.package-archive"},
                null
        );
    }

    private long getTotalDocSize(Context context) {
        return getTotalFileTypeSize(
                MediaStore.Files.getContentUri("external"),
                MediaStore.Files.FileColumns.MIME_TYPE + " IN (?, ?, ?, ?, ?, ?, ?)",
                new String[]{
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.ms-excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/vnd.ms-powerpoint",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/pdf"
                },
                null
        );
    }

    private long getTotalMusicSize(Context context) {
        return getTotalFileTypeSize(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null
        );
    }

    private long getTotalVideoSize(Context context) {
        return getTotalFileTypeSize(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null
        );
    }

    private long getTotalImageSize(Context context) {
        return getTotalFileTypeSize(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null
        );
    }

    private long getTotalFileTypeSize(Uri uri, String selection, String[] selectionArguments, String sortOrder) {
        long totalSize = 0;
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.MediaColumns.SIZE};
            cursor = this.getContentResolver().query(uri, projection, selection, selectionArguments, sortOrder);
            int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);
            while (cursor.moveToNext()) {
                long size = cursor.getLong(sizeIndex);
                totalSize += size;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return totalSize;
    }
}
