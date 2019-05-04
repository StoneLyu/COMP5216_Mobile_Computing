package com.hao.group16;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegisterActivity extends AppCompatActivity {
    private EditText etName;
    private EditText etPassword;
    private EditText etEmail;
    private EditText getEtPasswordRepeat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //populate the screen using the layout
        setContentView(R.layout.activity_register);

        Callback = new Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                UserItem item = response.body();
                if (item != null && item.getId().compareTo("-1") != 0) {
                    MainActivity.mUserDetail = item;
                    MainActivity.userFragment.checkLoginStatus();
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                    builder.setTitle("Register error")
                            .setMessage("Please check your detail and try again.")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    //finish();
                                }
                            })
                            .create().show();
                }
            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {
                //Log.i(TAG, "Get user detail failed");
                Toast.makeText(UserRegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        };
        etName = (EditText) findViewById(R.id.input_name);
        etEmail = (EditText) findViewById(R.id.input_email);
        etPassword = (EditText) findViewById(R.id.input_password);
        getEtPasswordRepeat = (EditText) findViewById(R.id.input_password_repeat);

        Button button = (Button) findViewById(R.id.btn_signup);
    }
    private Callback<UserItem> Callback;

    private void showErrorDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
        builder.setTitle("Input error")
                .setMessage(msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing
                    }
                }).create().show();
    }


    public void onSubmit(View v) {


        Intent data = new Intent();
        //Pass relevant data back as a result
        String password = etPassword.getText().toString(),
                password_repeat = getEtPasswordRepeat.getText().toString(),
                name = etName.getText().toString(),
                email = etEmail.getText().toString();
        if (!password.equals(password_repeat)) { // password not match
            showErrorDialog("Your passwords are not match, please try again.");
            return;
        }
        /*data.putExtra("name", etName.getText().toString());
        data.putExtra("pass", etPassword.getText().toString());
        data.putExtra("email", etEmail.getText().toString());*/
        RestHelper.userRegister(name, password, email, Callback);
        setResult(RESULT_OK, data);
        //finish();


    }


}