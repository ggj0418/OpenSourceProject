package com.example.opensourceproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.opensourceproject.Utils.APIClient;
import com.example.opensourceproject.Utils.APIInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private APIInterface apiInterface;
    private EditText idText, passwordText;
    private Button loginButton, registerButton;

    private String id, password;
    private HashMap<String, String> loginBody = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        idText = (EditText) findViewById(R.id.loginActivity_edittext_id);
        passwordText = (EditText) findViewById(R.id.loginActivity_edittext_password);

        loginButton = (Button) findViewById(R.id.loginActivity_button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idText.getText().toString().replace(" ", "").equals("")
                        || passwordText.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill in the blanks", Toast.LENGTH_SHORT).show();
                } else {
                    id = idText.getText().toString();
                    password = passwordText.getText().toString();

                    loginBody.put("LoginID", id);
                    loginBody.put("PassID", password);

                    Call<ResponseBody> loginCall = apiInterface.loginUser(loginBody);
                    loginCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String result = response.body().string();

                                if(result.equals("FAIL")) {
                                    Toast.makeText(getApplicationContext(), "Wrong Password!\nPlease check your password", Toast.LENGTH_LONG).show();
                                    passwordText.requestFocus();
                                } else if(result.equals("NOT EXIST")) {
                                    Toast.makeText(getApplicationContext(), "There is no user in this ID\nPlease proceed with the registration process", Toast.LENGTH_LONG).show();
                                    idText.setText("");
                                    passwordText.setText("");
                                    idText.requestFocus();
                                } else {
                                    JsonParser parser = new JsonParser();
                                    JsonObject jsonObject = parser.parse(result).getAsJsonObject();
                                    if(jsonObject.isJsonNull()) {
                                        Toast.makeText(getApplicationContext(), "No element!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String loginID = jsonObject.get("LoginID").getAsString();
                                        String remarks = jsonObject.get("REMARKS").getAsString();

                                        Intent toFileIntent = new Intent(MainActivity.this, FileBrowseActivity.class);
                                        toFileIntent.putExtra("loginID", loginID);
                                        toFileIntent.putExtra("remarks", remarks);
                                        idText.setText("");
                                        passwordText.setText("");
                                        idText.requestFocus();
                                        startActivity(toFileIntent);
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } // 클릭이벤트 끝
        });

        registerButton = (Button) findViewById(R.id.loginActivity_button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRegisterIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(toRegisterIntent);
                finish();
            }
        });
    }
}
