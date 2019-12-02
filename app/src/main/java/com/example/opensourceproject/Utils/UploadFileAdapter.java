package com.example.opensourceproject.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.opensourceproject.R;
import com.example.opensourceproject.Class.UploadFile;

import java.util.ArrayList;

public class UploadFileAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater =  null;
    ArrayList<UploadFile> uploadFileArrayList = new ArrayList<UploadFile>();

    public UploadFileAdapter(Context context, ArrayList<UploadFile> data) {
        mContext = context;
        uploadFileArrayList = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return uploadFileArrayList.size();
    }

    @Override
    public UploadFile getItem(int i) {
        return uploadFileArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View baseView = mLayoutInflater.inflate(R.layout.item_browse_file, null);
        String index = Integer.toString(i+1);
        String makingID = Encryption.masking(uploadFileArrayList.get(i).getUser());

        TextView userText = (TextView) baseView.findViewById(R.id.item_upload_user);
        TextView contentText = (TextView) baseView.findViewById(R.id.item_file_content);
        TextView indexText = (TextView) baseView.findViewById(R.id.index);

        userText.setText(makingID);
        contentText.setText(uploadFileArrayList.get(i).getContent());
        indexText.setText(index);

        return baseView;
    }
}
