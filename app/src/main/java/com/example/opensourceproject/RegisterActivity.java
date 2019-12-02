package com.example.opensourceproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.opensourceproject.Utils.APIClient;
import com.example.opensourceproject.Utils.APIInterface;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
This activity is for registeration.
Fill in the all attributes and click the confirm button.
If you succeed to register, your screen is appeared a MainActivity(Login).
 */

public class RegisterActivity extends AppCompatActivity {

    private APIInterface apiInterface;

    private EditText idText, emailText, passwordText, poCodeText, mobileNoText, cityText;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
    private CheckBox checkBox7, checkBox8, checkBox9, checkBox10, checkBox11, checkBox12;
    private Button confirmButton;

    private HashMap<String, String> registerBody = new HashMap<>();

    private String id, email, password, city, poCode, mobileNo, remarks;
    private boolean checkSum = false;
    private Integer index = 0;
    private StringBuilder builder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        idText = (EditText) findViewById(R.id.idText);
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        poCodeText = (EditText) findViewById(R.id.poCodeText);
        mobileNoText = (EditText) findViewById(R.id.mobileNoText);
        cityText = (EditText) findViewById(R.id.cityText);

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


        confirmButton = (Button) findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = idText.getText().toString();
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                poCode = poCodeText.getText().toString();
                mobileNo = mobileNoText.getText().toString();
                city = cityText.getText().toString();

                checkBoxBuilder(checkBox1);
                checkBoxBuilder(checkBox2);
                checkBoxBuilder(checkBox3);
                checkBoxBuilder(checkBox4);
                checkBoxBuilder(checkBox5);
                checkBoxBuilder(checkBox6);
                checkBoxBuilder(checkBox7);
                checkBoxBuilder(checkBox8);
                checkBoxBuilder(checkBox9);
                checkBoxBuilder(checkBox10);
                checkBoxBuilder(checkBox11);
                checkBoxBuilder(checkBox12);

                remarks = builder.toString();
                makeBody();

                if(checkSum) {
                    // 통신
                    Call<ResponseBody> registerCall = apiInterface.registerUser(registerBody);
                    registerCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String result = response.body().string();

                                if(result.equals("SUCCESS")) {
                                    Toast.makeText(getApplicationContext(), "Register Success", Toast.LENGTH_SHORT).show();
                                    Intent toMainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(toMainIntent);
                                    finish();
                                } else if(result.equals("FAIL")) {
                                    Toast.makeText(getApplicationContext(), "Register Fail\nPlease check your information", Toast.LENGTH_LONG).show();
                                    index = 0;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                            index = 0;
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Choose role!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void makeBody() {
        registerBody.put("LoginID", id);
        registerBody.put("PassID", password);
        registerBody.put("Email", email);
        registerBody.put("City", city);
        registerBody.put("POCode", poCode);
        registerBody.put("MobileNo", mobileNo);
        registerBody.put("REMARKS", remarks);
        checkSum = true;
    }

    private void checkBoxBuilder(CheckBox cb) {
        if(cb.isChecked()) {
            if(index == 0) {
                builder.append(cb.getText().toString());
                index++;
            } else {
                builder.append(",").append(cb.getText().toString());
                index++;
            }
        }
    }
}
