package com.hao.group16;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNewsActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private Callback<NewsItem> callback;
    private Button btnCategory, btnPhoto, btnLocation;
    private int selectIndex = 0;
    private String[] categoryList, categoryIdList;
    private String strCategory, strPhoto, strLocation;
    private  String photoPath;
    private long longTime;
    private String fileName;

    private static int RESULT_LOAD_IMAGE = 100;


    private void initUI() {
        android.content.res.Resources resourceBundle = getResources();

        categoryList = resourceBundle.getStringArray(R.array.category_list);
        categoryIdList = resourceBundle.getStringArray(R.array.category_id_list);
        btnCategory = (Button) findViewById(R.id.btn_select_category);
        btnPhoto = (Button) findViewById(R.id.btn_select_photo);
        btnLocation = (Button) findViewById(R.id.btn_update_location);
        strCategory = resourceBundle.getString(R.string.news_add_category);
        strLocation = resourceBundle.getString(R.string.news_add_location);
        strPhoto = resourceBundle.getString(R.string.news_add_photo);

    }

    private void updateBtnCategory(int index) {
        btnLocation.setText(String.format(strCategory, categoryList[index]));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_edit);

        initUI();

        if (MainActivity.mUserDetail.getId().compareTo("-1") ==0) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EditNewsActivity.this);
            builder.setTitle("Login required")
                    .setMessage("Please log in to post news.")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .create().show();
        }
        updateBtnCategory(selectIndex);
        callback = new Callback<NewsItem>() {
            @Override
            public void onResponse(Call<NewsItem> call, Response<NewsItem> response) {
                if (RestHelper.HTTP_CODE_OK != response.code()) {
                    Toast.makeText(getApplicationContext(), "Send failed. Please try again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Send success.", Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }

            @Override
            public void onFailure(Call<NewsItem> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        };
        setContentView(R.layout.activity_news_edit);
    }


    public void onPost(View view) {
        etTitle = (EditText) findViewById(R.id.et_addnews_title);
        etContent = (EditText) findViewById(R.id.et_addnews_content);

        String photoName;

        if (photoPath != null) {
            longTime = (new Long((new Date().getTime()) / 1000)).intValue();
            String suffix = photoPath.substring(photoPath.lastIndexOf(".") + 1);
            photoName = Long.toString(longTime) + "." + suffix;
            fileName = photoName;
            upload(photoPath);
        } else {
            photoName = "";
        }

        //传给后台并存入数据库

        String content = etContent.getText().toString(),
                title = etTitle.getText().toString();
//        Toast.makeText(this, "content: " + content + " ,title: " + title, Toast.LENGTH_SHORT).show();

        if (MainActivity.mLastLocation == null) {
            RestHelper.postNews(title, content, 0.0, 0.0, photoName, categoryIdList[selectIndex], callback);
        } else {
            RestHelper.postNews(title, content, MainActivity.mLastLocation.getLatitude(), MainActivity.mLastLocation.getLongitude(), photoName, categoryIdList[selectIndex],callback);
        }
    }

    public void onSelectPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void onUpdateLocation(View view) {

    }

    public void onSelectCategory(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, 3)
                .setTitle("Please choose one category")
                .setItems(categoryList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(EditNewsActivity.this,
                                String.format("You have chosen category %s", categoryList[which]),
                                Toast.LENGTH_SHORT).show();
                        selectIndex = which;
                        updateBtnCategory(selectIndex);
                    }
                });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();
            photoPath = picturePath;
           /*ImageView imageView = (ImageView) findViewById(R.id.imgView);
           imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/

        }
    }

    private void upload(String picturePath) {
        final ProgressDialog pb= new ProgressDialog(this);

        pb.setMessage("Uploading");
        pb.setCancelable(false);
        pb.show();

        imageUpLoad(picturePath, pb);
    }

    public void imageUpLoad(String localPath, final ProgressDialog progressDialog) {
        OkHttpClient client = new OkHttpClient();
        Log.i("文件路径： ",localPath + "<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        File f = new File(localPath);

//        String suffix = localPath.substring(localPath.lastIndexOf(".") + 1);
//        String newName = Long.toString(longTime) + "." + suffix;

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("application/octet-stream"), f))
                .build();

        Request request = new Request.Builder()
                .url(RestAPI.baseURI + "api/newsPic")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                progressDialog.dismiss();
                //Toast.makeText( getApplicationContext(), "Upload picture failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                progressDialog.dismiss();
                //Toast.makeText( getApplicationContext(), "Upload picture successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
