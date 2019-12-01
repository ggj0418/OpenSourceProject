package com.example.opensourceproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class FileUploadActivity extends AppCompatActivity {

    private TextView toUploadText, fileNameText, userIdText, psedonimIdText, contentText, objectText;
    private Button psedonimButton, encryptButton, uploadButton;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
    private CheckBox checkBox7, checkBox8, checkBox9, checkBox10, checkBox11, checkBox12;

    private ListView listView;
    private MyAdapter myAdapter;

    private java.io.File internalDir;

    private Integer andIndex = 0, orIndex = 0;
    private StringBuilder andBuilder = new StringBuilder();
    private StringBuilder orBuilder = new StringBuilder();
    private String andRemarks, orRemarks;

    ArrayList<com.example.opensourceproject.File> fileDataList = new ArrayList<com.example.opensourceproject.File>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_file_upload);

        Intent fromMainIntent = getIntent();
        final String userID = fromMainIntent.getExtras().getString("userID");

        internalDir = new java.io.File(getFilesDir().toString());

        fileNameText = (TextView) findViewById(R.id.browse_file_name);
        contentText = (TextView) findViewById(R.id.browse_file_content);
        userIdText = (TextView) findViewById(R.id.browse_user_id);
        psedonimIdText = (TextView) findViewById(R.id.browse_psedonim_id);
        objectText = (TextView) findViewById(R.id.objectText);

        listView = (ListView) findViewById(R.id.fileListView);
        myAdapter = new MyAdapter(this, fileDataList);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                fileNameText.setText(myAdapter.getItem(i).getFileName());
                userIdText.setText(userID);

                java.io.File readFile = new java.io.File(internalDir, myAdapter.getItem(i).getFileName());

                try {
                    FileReader reader = new FileReader(readFile);
                    BufferedReader in = new BufferedReader(reader);
                    String content = in.readLine();

                    contentText.setText(content);

                    reader.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        searchTextFile();

        checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkbox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkbox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkbox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkbox5);
        checkBox6 = (CheckBox) findViewById(R.id.checkbox6);
        checkBox7 = (CheckBox) findViewById(R.id.checkbox7);
        checkBox8 = (CheckBox) findViewById(R.id.checkbox8);
        checkBox9 = (CheckBox) findViewById(R.id.checkbox9);
        checkBox10 = (CheckBox) findViewById(R.id.checkbox10);
        checkBox11 = (CheckBox) findViewById(R.id.checkbox11);
        checkBox12 = (CheckBox) findViewById(R.id.checkbox12);

        psedonimButton = (Button) findViewById(R.id.browse_psedonim_button);
        psedonimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String forPsedId = userIdText.getText().toString();
                psedonimIdText.setText(Encryption.mask(forPsedId, 6));
            }
        });
        encryptButton = (Button) findViewById(R.id.encryptButton);
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                andCheckBoxBuilder(checkBox1);
                andCheckBoxBuilder(checkBox2);
                andCheckBoxBuilder(checkBox3);
                andCheckBoxBuilder(checkBox4);
                andCheckBoxBuilder(checkBox5);
                andCheckBoxBuilder(checkBox6);
                orCheckBoxBuilder(checkBox7);
                orCheckBoxBuilder(checkBox8);
                orCheckBoxBuilder(checkBox9);
                orCheckBoxBuilder(checkBox10);
                orCheckBoxBuilder(checkBox11);
                orCheckBoxBuilder(checkBox12);

                andRemarks = andBuilder.toString();
                orRemarks = orBuilder.toString();

                String plainText = contentText.getText().toString();
                try {
                    objectText.setText(Encryption.encrypt(plainText, andRemarks));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void searchTextFile() {
        java.io.File file = new java.io.File(getFilesDir().toString());

        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        }

        java.io.File[] files = file.listFiles();

        for (java.io.File f : files) {
            fileDataList.add(new com.example.opensourceproject.File(f.getName(), f.getPath()));
        }

        myAdapter.notifyDataSetChanged();
    }

    private void andCheckBoxBuilder(CheckBox cb) {
        if(cb.isChecked()) {
            if(andIndex == 0) {
                andBuilder.append(cb.getText().toString());
                andIndex++;
            } else {
                andBuilder.append(",").append(cb.getText().toString());
                andIndex++;
            }
        }
    }

    private void orCheckBoxBuilder(CheckBox cb) {
        if(cb.isChecked()) {
            if(orIndex == 0) {
                orBuilder.append(cb.getText().toString());
                orIndex++;
            } else {
                orBuilder.append(",").append(cb.getText().toString());
                orIndex++;
            }
        }
    }
}