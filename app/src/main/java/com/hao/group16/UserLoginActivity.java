package com.hao.group16;

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

public class UserLoginActivity extends AppCompatActivity {
    EditText etName;
    EditText etPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //populate the screen using the layout
        setContentView(R.layout.activity_login);

        Callback = new Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                if (response.code() == RestHelper.HTTP_CODE_OK) {
                    UserItem item = response.body();
                    if (item != null && item.getId().compareTo("-1") != 0) {
                        MainActivity.mUserDetail = item;
                        MainActivity.userFragment.checkLoginStatus();
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserLoginActivity.this);
                        builder.setTitle("Login error")
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
            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {
                //Log.i(TAG, "Get user detail failed");
                AlertDialog.Builder builder = new AlertDialog.Builder(UserLoginActivity.this);
                builder.setTitle("Login error")
                        .setMessage("Network issue, please try again later.")
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
                Toast.makeText(UserLoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        };
        etName = (EditText) findViewById(R.id.input_username);
        etPassword = (EditText) findViewById(R.id.input_password);


    }

    private Callback<UserItem> Callback;

    public void onSubmit(View v) {
        //Pass relevant data back as a result
        RestHelper.login(etName.getText().toString(), etPassword.getText().toString(), Callback);
        setResult(RESULT_OK);
        //finish();
    }

}
