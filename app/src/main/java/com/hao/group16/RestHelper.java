package com.hao.group16;

import android.content.Context;
import android.util.Log;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestHelper {

    private final static String TAG = RestHelper.class.getName();
    private final static long DEFAULT_TIMEOUT = 30;
    public static final int HTTP_CODE_OK = 200;
    public static boolean isUIReady = false;
    public static ClearableCookieJar cookieJar;
    //private boolean isNetworkReady = false;

    private static Retrofit getOneRequest() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .cookieJar(cookieJar)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(RestAPI.baseURI)
                .client(okHttpClient)
                .build();
    }

    public static void getListByCategory(String categoryId, Callback<List<NewsItem>> callback) {
        if (!isUIReady) {
            return;
        }
        Retrofit retrofit = getOneRequest();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<List<NewsItem>> call = restAPI.getListByCategory(categoryId);

        call.enqueue(callback);
    }

    public static void getListByCategoryAndLocation(String categoryId, double lat, double lng, Callback<List<NewsItem>> callback) {
        if (!isUIReady) {
            return;
        }
        Retrofit retrofit = getOneRequest();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<List<NewsItem>> call = restAPI.getListByCategoryAndLocation(categoryId, lat, lng);
        call.enqueue(callback);
    }


    public static void postNews(String title,
                                String content,
                                Double lat,
                                Double lng,
                                String picture,
                                String categoryId,
                                Callback<NewsItem> callback) {
        Retrofit retrofit = getOneRequest();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<NewsItem> call = restAPI.postNews(title, lat, lng, content, picture, categoryId);
        call.enqueue(callback);
    }
    /*public static void getAllCategories(Callback<List<CategoryItem>> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(RestAPI.baseURI)
                .build();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<List<CategoryItem>> call = restAPI.getAllCategories();
        call.enqueue(callback);
    }*/

    //get user login/register information
    public static void login(String name, String password, Callback<UserItem>callback){
        if (!isUIReady) {
            return;
        }
        Retrofit retrofit = getOneRequest();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<UserItem> call = restAPI.login(name,password);
        call.enqueue(callback);
    }

    public static void userRegister(String name, String password, String email, Callback<UserItem>callback){
        if (!isUIReady) {
            return;
        }
        Retrofit retrofit = getOneRequest();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<UserItem> call = restAPI.userRegister(name, password, email);
        call.enqueue(callback);
    }

    public static void getBookmarkList(String userId, Callback<List<BookmarkItem>> callback) {
        if (!isUIReady) {
            return;
        }
        Retrofit retrofit = getOneRequest();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<List<BookmarkItem>> call = restAPI.getBookmarksByUser(userId);
        call.enqueue(callback);
    }

    public static void deleteBookmark(String bookmarkId, Callback<ResponseBody> callback) {
        if (!isUIReady) {
            return;
        }
        Retrofit retrofit = getOneRequest();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<ResponseBody> call = restAPI.deleteBookmark(bookmarkId);
        call.enqueue(callback);
    }


    public static void getUserDetail(Callback<UserItem> callback) {
        if (!isUIReady) {
            return;
        }
        try {
            Retrofit retrofit = getOneRequest();
            RestAPI restAPI = retrofit.create(RestAPI.class);
            Call<UserItem> call = restAPI.getUserDetails();
            call.enqueue(callback);
        } catch (Exception e) {

        }
    }

    public static void getUserPosts(String userId, Callback<List<NewsItem>> callback) {
        if (!isUIReady) {
            return;
        }
        Retrofit retrofit = getOneRequest();
        RestAPI restAPI = retrofit.create(RestAPI.class);
        Call<List<NewsItem>> call = restAPI.getNewsByUser(userId);
        call.enqueue(callback);
    }

    public static void setUserDetail() {

    }


}
