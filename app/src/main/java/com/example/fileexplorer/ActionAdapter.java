package com.example.fileexplorer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fileexplorer.enums.FileAction;

public class ActionAdapter extends BaseAdapter {
    private final LayoutInflater inflater;

    public ActionAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return FileAction.values().length;
    }

    @Override
    public Object getItem(int i) {
        return FileAction.values()[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View v, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.option_layout, null);
        TextView txtOption = view.findViewById(R.id.txtOption);
        ImageView imgOption = view.findViewById(R.id.imgOption);
        txtOption.setText(FileAction.values()[i].toString());
        if (FileAction.values()[i] == FileAction.DETAILS) {
            imgOption.setImageResource(R.drawable.ic_details);
        } else if (FileAction.values()[i] == FileAction.RENAME) {
            imgOption.setImageResource(R.drawable.ic_rename);
        } else if (FileAction.values()[i] == FileAction.DELETE) {
            imgOption.setImageResource(R.drawable.ic_delete);
        } else if (FileAction.values()[i] == FileAction.SHARE) {
            imgOption.setImageResource(R.drawable.ic_share);
        }
        return view;
    }
}
