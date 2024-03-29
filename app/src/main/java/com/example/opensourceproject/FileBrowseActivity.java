package com.example.opensourceproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.opensourceproject.Class.UploadFile;
import com.example.opensourceproject.Utils.APIClient;
import com.example.opensourceproject.Utils.APIInterface;
import com.example.opensourceproject.Utils.Encryption;
import com.example.opensourceproject.Utils.UploadFileAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
This Activity is for browsing a file uploaded in server.
If your policies which are chosen in the registering process match with file's policys, you can decrypt it.
 */

public class FileBrowseActivity extends AppCompatActivity {

    private String userID, remarks;
    private String andPolicy, orPolicy;
    ArrayList<UploadFile> uploadFileArrayList = new ArrayList<UploadFile>();

    private APIInterface apiInterface;
    private ListView listView;
    public UploadFileAdapter uploadFileAdapter;

    private TextView userIdText, fileContentText, policyAndText, policyOrText;
    private TextView plainText, toUploadText;
    private Button checkPolicyButton, decryptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_file_browse);

        Intent fromMainIntent = getIntent();
        userID = fromMainIntent.getExtras().getString("loginID");
        remarks = fromMainIntent.getExtras().getString("remarks");

        apiInterface = APIClient.getClient().create(APIInterface.class);

        userIdText = (TextView) findViewById(R.id.browse_user_id);
        fileContentText = (TextView) findViewById(R.id.browse_file_content);
        policyAndText = (TextView) findViewById(R.id.browse_policy_and);
        policyOrText = (TextView) findViewById(R.id.browse_policy_or);
        plainText = (TextView) findViewById(R.id.plainText);
        toUploadText = (TextView) findViewById(R.id.to_upload_text);
        toUploadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toUploadIntent = new Intent(FileBrowseActivity.this, FileUploadActivity.class);
                toUploadIntent.putExtra("loginID", userID);
                toUploadIntent.putExtra("remarks", remarks);
                startActivity(toUploadIntent);
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.fileListView);
        uploadFileAdapter = new UploadFileAdapter(this, uploadFileArrayList);
        listView.setAdapter(uploadFileAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                plainText.setText("");
                decryptButton.setEnabled(false);
                userIdText.setText(Encryption.masking(uploadFileAdapter.getItem(i).getUser()));
                fileContentText.setText(uploadFileAdapter.getItem(i).getContent());
                policyAndText.setText(uploadFileAdapter.getItem(i).getAndPolicy());
                policyOrText.setText(uploadFileAdapter.getItem(i).getOrPolicy());
            }
        });

        decryptButton = (Button) findViewById(R.id.decrypt_button);
        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String encryptText = fileContentText.getText().toString();
                String and = policyAndText.getText().toString();
                try {
                    plainText.setText(Encryption.decrypt(encryptText, and));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        checkPolicyButton = (Button) findViewById(R.id.check_policy_button);
        checkPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] userPolicy = remarks.split(",");
                String[] fileAndPolicy = policyAndText.getText().toString().split(",");
                String[] fileOrPolicy = policyOrText.getText().toString().split(",");
                int index = 0;
                boolean value1 = false, value2 = false, value3 = false;

                if(!policyAndText.getText().toString().equals("") && !policyOrText.getText().toString().equals("")) {
                    for(int i=0;i<fileAndPolicy.length;i++) {
                        for(int j=0;j<userPolicy.length;j++) {
                            if(fileAndPolicy[i].equals(userPolicy[j])) {
                                index++;
                            }
                        }
                    }
                    if(index == fileAndPolicy.length) {
                        value1 = true;
                    }
                    for(int i=0;i<fileOrPolicy.length;i++) {
                        for(int j=0;j<userPolicy.length;j++) {
                            if(fileOrPolicy[i].equals(userPolicy[j])) {
                                value2 = true;
                                break;
                            }
                        }
                    }
                    if(value1 && value2) {
                        Toast.makeText(getApplicationContext(), "Policy Check Success\nYou can decrypt this file", Toast.LENGTH_LONG).show();
                        decryptButton.setEnabled(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Policy Check Fail", Toast.LENGTH_LONG).show();
                    }
                    index = 0;
                } else if(policyAndText.getText().toString().equals("")) {
                    for(int i=0;i<fileOrPolicy.length;i++) {
                        for(int j=0;j<userPolicy.length;j++) {
                            if(fileOrPolicy[i].equals(userPolicy[j])) {
                                index++;
                            }
                        }
                    }
                    if(index > 0) {
                        Toast.makeText(getApplicationContext(), "Policy Check Success\nYou can decrypt this file", Toast.LENGTH_LONG).show();
                        decryptButton.setEnabled(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Policy Check Fail", Toast.LENGTH_LONG).show();
                    }
                    index = 0;
                } else if(policyOrText.getText().toString().equals("")) {
                    for(int i=0;i<fileAndPolicy.length;i++) {
                        for(int j=0;j<userPolicy.length;j++) {
                            if(fileAndPolicy[i].equals(userPolicy[j])) {
                                index++;
                            }
                        }
                    }
                    if(index == fileAndPolicy.length) {
                        Toast.makeText(getApplicationContext(), "Policy Check Success\nYou can decrypt this file", Toast.LENGTH_LONG).show();
                        decryptButton.setEnabled(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Policy Check Fail", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        setBrowseFileList();
    }

    private void setBrowseFileList() {
        Call<ResponseBody> browseFileCall = apiInterface.browseFile();
        browseFileCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    JsonParser parser = new JsonParser();
                    JsonArray jsonArray = parser.parse(result).getAsJsonArray();
                    JsonObject jsonObject;

                    for(int i=0;i<jsonArray.size();i++) {
                        jsonObject = (JsonObject) jsonArray.get(i);

                        uploadFileArrayList.add(
                                new UploadFile(
                                        jsonObject.get("ID").getAsString(),
                                        jsonObject.get("Content").getAsString(),
                                        jsonObject.get("AND").getAsString(),
                                        jsonObject.get("OR").getAsString()
                                        ));
                    }

                    uploadFileAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Success to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
