package com.example.opensourceproject.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.opensourceproject.Class.File;
import com.example.opensourceproject.R;

import java.util.ArrayList;

public class FileAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<File> fileArrayList = new ArrayList<File>();

    public FileAdapter(Context context, ArrayList<File> data) {
        mContext = context;
        fileArrayList = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return fileArrayList.size();
    }

    @Override
    public File getItem(int i) {
        return fileArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View baseView = mLayoutInflater.inflate(R.layout.item_file, null);

        TextView nameText = (TextView) baseView.findViewById(R.id.item_file_name);
        TextView pathText = (TextView) baseView.findViewById(R.id.item_file_path);

        nameText.setText(fileArrayList.get(i).getFileName());
        pathText.setText(fileArrayList.get(i).getFilePath());

        return baseView;
    }
}
