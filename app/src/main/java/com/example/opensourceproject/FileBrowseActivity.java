package com.example.opensourceproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBrowseActivity extends AppCompatActivity {

    private String userID, remarks;
    ArrayList<UploadFile> uploadFileArrayList = new ArrayList<UploadFile>();

    private APIInterface apiInterface;
    private ListView listView;
    private UploadFileAdapter uploadFileAdapter;

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
                startActivity(toUploadIntent);
            }
        });

        listView = (ListView) findViewById(R.id.fileListView);
        uploadFileAdapter = new UploadFileAdapter(this, uploadFileArrayList);
        listView.setAdapter(uploadFileAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        checkPolicyButton = (Button) findViewById(R.id.check_policy_button);
        decryptButton = (Button) findViewById(R.id.decrypt_button);
    }
}
