package com.example.fileexplorer;

import static com.example.fileexplorer.enums.FileAction.valueOf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fileexplorer.enums.FileAction;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {
    private Context context;
    Fragment onClickFragment;
    private List<File> files;
    private Fragment fragment;

    public FileAdapter(Context context, Fragment onClickFragment, List<File> file, Fragment fragment) {
        this.context = context;
        this.onClickFragment = onClickFragment;
        this.files = file;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(context).inflate(R.layout.file_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.tvName.setText(files.get(holder.getAdapterPosition()).getName());
        holder.tvName.setSelected(true);
        int items = 0;
        if (files.get(position).isDirectory()) {
            File[] files = this.files.get(position).listFiles();
            if (files != null) { // kiểm tra xem mảng files có null hay không
                for (File singleFile : files) {
                    if (!singleFile.isHidden()) {
                        items += 1;
                    }
                }
            }
            holder.tvSize.setText(String.valueOf(items) + " Files");
        } else {
            holder.tvSize.setText(Formatter.formatShortFileSize(context, files.get(holder.getAdapterPosition()).length()));
        }

        if (files.get(holder.getAdapterPosition()).getName().toLowerCase().endsWith(".jpeg")) {
            holder.imgFile.setImageResource(R.drawable.ic_baseline_image_24);
        } else if (files.get(holder.getAdapterPosition()).getName().toLowerCase().endsWith(".jpg")) {
            holder.imgFile.setImageResource(R.drawable.ic_baseline_image_24);
        } else if (files.get(holder.getAdapterPosition()).getName().toLowerCase().endsWith(".png")) {
            holder.imgFile.setImageResource(R.drawable.ic_baseline_image_24);
        } else if (files.get(holder.getAdapterPosition()).getName().toLowerCase().endsWith(".pdf")) {
            holder.imgFile.setImageResource(R.drawable.ic_pdf);
        } else if (files.get(holder.getAdapterPosition()).getName().toLowerCase().endsWith(".doc")) {
            holder.imgFile.setImageResource(R.drawable.ic_doc);
        } else if (files.get(holder.getAdapterPosition()).getName().toLowerCase().endsWith(".mp3")) {
            holder.imgFile.setImageResource(R.drawable.ic_music);
        } else if (files.get(holder.getAdapterPosition()).getName().toLowerCase().endsWith(".wav")) {
            holder.imgFile.setImageResource(R.drawable.ic_music);
        } else if (files.get(holder.getAdapterPosition()).getName().toLowerCase().endsWith(".mp4")) {
            holder.imgFile.setImageResource(R.drawable.ic_play);
        } else if (files.get(holder.getAdapterPosition()).getName().toLowerCase().endsWith(".apk")) {
            holder.imgFile.setImageResource(R.drawable.ic_android);
        } else {
            holder.imgFile.setImageResource(R.drawable.icon_folder);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.onFileClicked(files.get(holder.getAdapterPosition()));
                File file = files.get(holder.getAdapterPosition());
                if (file.isDirectory()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("path", file.getAbsolutePath());
                    onClickFragment.setArguments(bundle);
                    fragment.getFragmentManager().beginTransaction().replace(R.id.fragment_container, onClickFragment).addToBackStack(null).commit();
                } else {
                    try {
                        FileOpener.openFile(context, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                File file = files.get(holder.getAdapterPosition());
                int position = holder.getAdapterPosition();

                final Dialog optionDialog = new Dialog(context);
                optionDialog.setContentView(R.layout.option_dialog);
                optionDialog.setTitle("Select Options");
                ListView options = (ListView) optionDialog.findViewById(R.id.List);
                FileActionAdapter actionAdapter = new FileActionAdapter(fragment.getLayoutInflater());
                options.setAdapter(actionAdapter);
                optionDialog.show();
                options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        FileAction selectedAction = valueOf(adapterView.getItemAtPosition(i).toString());
                        switch (selectedAction) {
                            case DETAILS:
                                AlertDialog.Builder detailDialog = new AlertDialog.Builder(context);
                                detailDialog.setTitle("Details: ");
                                final TextView details = new TextView(context);
                                detailDialog.setView(details);
                                Date lastModified = new Date(file.lastModified());
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                String formattedDate = formatter.format(lastModified);

                                details.setText("File Name: " + file.getName() + "\n" +
                                        "Size: " + Formatter.formatShortFileSize(context, file.length()) + "\n" +
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
                            case RENAME:
                                AlertDialog.Builder renameDialog = new AlertDialog.Builder(context);
                                renameDialog.setTitle("Rename File: ");
                                final EditText name = new EditText(context);
                                renameDialog.setView(name);

                                renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String new_name = name.getEditableText().toString();
                                        String extention = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                        File current = new File(file.getAbsolutePath());
                                        File destination = new File(file.getAbsolutePath().replace(file.getName(), new_name) + extention);
                                        if (current.renameTo(destination)) {
                                            files.set(position, destination);
                                            notifyItemChanged(position);
                                            Toast.makeText(context, "Renamed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Couldn't Rename!", Toast.LENGTH_SHORT).show();
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
                            case DELETE:
                                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
                                deleteDialog.setTitle("Delete " + file.getName() + "?");
                                deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        file.delete();
                                        files.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
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
                            case SHARE:
                                String fileName = file.getName();
                                Intent share = new Intent();
                                share.setAction(Intent.ACTION_SEND);
                                share.setType("image/jpeg");
                                share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file));
                                fragment.startActivity(Intent.createChooser(share, "Share " + fileName));
                                break;
                        }
                    }
                });
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return files.size();
    }
}
