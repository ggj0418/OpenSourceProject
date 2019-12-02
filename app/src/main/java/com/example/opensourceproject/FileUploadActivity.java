package com.example.opensourceproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.opensourceproject.Class.File;
import com.example.opensourceproject.Utils.APIClient;
import com.example.opensourceproject.Utils.APIInterface;
import com.example.opensourceproject.Utils.Encryption;
import com.example.opensourceproject.Utils.FileAdapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
This activity is for uploading a file in your device.
You can upload it including your masked userID, encrypted content and policies.
When you choose policies, the process is a little bit complicated.
After you choose a file willing to upload, click the checkboxes you want to use as 'AND' policies. And click the 'AND' button.
Then the chosen checkboxes will be unset and values will stored.
The process to choose 'OR' policies is same with 'AND' one. Click the the checkboxes you want to use as 'OR' policies and the 'OR' button, either.
All values are ready to be uploade. The only work you should do is to click the upload button.
Back button is a way to FileBrowseActivity.
 */

public class FileUploadActivity extends AppCompatActivity {

    private APIInterface apiInterface;

    private TextView toUploadText, fileNameText, userIdText, contentText, objectText;
    private Button encryptButton, uploadButton;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
    private CheckBox checkBox7, checkBox8, checkBox9, checkBox10, checkBox11, checkBox12;
    private Button andButton, orButton;

    private ListView listView;
    private FileAdapter fileAdapter;

    private java.io.File internalDir;

    private Integer andIndex = 0, orIndex = 0;
    private StringBuilder andBuilder = new StringBuilder();
    private StringBuilder orBuilder = new StringBuilder();
    private String andRemarks, orRemarks;
    private String userID, remarks;

    ArrayList<File> fileDataList = new ArrayList<File>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_file_upload);

        Intent fromMainIntent = getIntent();
        userID = fromMainIntent.getExtras().getString("loginID");
        remarks = fromMainIntent.getExtras().getString("remarks");

        apiInterface = APIClient.getClient().create(APIInterface.class);

        internalDir = new java.io.File(getFilesDir().toString());

        fileNameText = (TextView) findViewById(R.id.browse_file_name);
        contentText = (TextView) findViewById(R.id.browse_file_content);
        userIdText = (TextView) findViewById(R.id.browse_user_id);
        objectText = (TextView) findViewById(R.id.objectText);

        listView = (ListView) findViewById(R.id.fileListView);
        fileAdapter = new FileAdapter(this, fileDataList);
        listView.setAdapter(fileAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                fileNameText.setText(fileAdapter.getItem(i).getFileName());
                userIdText.setText(userID);

                java.io.File readFile = new java.io.File(internalDir, fileAdapter.getItem(i).getFileName());

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

        andButton = (Button) findViewById(R.id.andButton);
        andButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                andCheckBoxBuilder(checkBox1);
                andCheckBoxBuilder(checkBox2);
                andCheckBoxBuilder(checkBox3);
                andCheckBoxBuilder(checkBox4);
                andCheckBoxBuilder(checkBox5);
                andCheckBoxBuilder(checkBox6);
                andCheckBoxBuilder(checkBox7);
                andCheckBoxBuilder(checkBox8);
                andCheckBoxBuilder(checkBox9);
                andCheckBoxBuilder(checkBox10);
                andCheckBoxBuilder(checkBox11);
                andCheckBoxBuilder(checkBox12);

                unsetCheckBox();
            }
        });
        orButton = (Button) findViewById(R.id.orButton);
        orButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orCheckBoxBuilder(checkBox1);
                orCheckBoxBuilder(checkBox2);
                orCheckBoxBuilder(checkBox3);
                orCheckBoxBuilder(checkBox4);
                orCheckBoxBuilder(checkBox5);
                orCheckBoxBuilder(checkBox6);
                orCheckBoxBuilder(checkBox7);
                orCheckBoxBuilder(checkBox8);
                orCheckBoxBuilder(checkBox9);
                orCheckBoxBuilder(checkBox10);
                orCheckBoxBuilder(checkBox11);
                orCheckBoxBuilder(checkBox12);

                unsetCheckBox();
            }
        });

        encryptButton = (Button) findViewById(R.id.encryptButton);
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                makeBodyCall(userID, objectText.getText().toString(), andRemarks, orRemarks);
            }
        });
    }

    // Search your files in inner storage
    public void searchTextFile() {
        java.io.File file = new java.io.File(getFilesDir().toString());

        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        }

        java.io.File[] files = file.listFiles();

        for (java.io.File f : files) {
            fileDataList.add(new File(f.getName(), f.getPath()));
        }

        fileAdapter.notifyDataSetChanged();
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

    private void unsetCheckBox() {
        checkBox1.setChecked(false);
        checkBox2.setChecked(false);
        checkBox3.setChecked(false);
        checkBox4.setChecked(false);
        checkBox5.setChecked(false);
        checkBox6.setChecked(false);
        checkBox7.setChecked(false);
        checkBox8.setChecked(false);
        checkBox9.setChecked(false);
        checkBox10.setChecked(false);
        checkBox11.setChecked(false);
        checkBox12.setChecked(false);
    }

    private void makeBodyCall(final String userID, String content, String and, String or) {
        HashMap<String, String> body = new HashMap<>();

        body.put("ID", userID);
        body.put("Content", content);
        body.put("AND", and);
        body.put("OR", or);

        Call<ResponseBody> uploadCall = apiInterface.uploadFile(body);
        uploadCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();

                    if(result.equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Success to upload", Toast.LENGTH_SHORT).show();
                        Intent toBrowseIntent = new Intent(FileUploadActivity.this, FileBrowseActivity.class);
                        toBrowseIntent.putExtra("userID", userID);
                        toBrowseIntent.putExtra("remarks", remarks);
                        startActivity(toBrowseIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Fail to upload", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FileUploadActivity.this, FileBrowseActivity.class);
        intent.putExtra("userID", userID);
        intent.putExtra("remarks", remarks);
        startActivity(intent);
        finish();
    }
}