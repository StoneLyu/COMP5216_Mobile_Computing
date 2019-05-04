package com.hao.group16;


import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface RestAPI {
    String baseURI = "http://doublehappy.australiasoutheast.cloudapp.azure.com:3000/";


//    String baseURI = "http://10.16.128.156:3000/";

    //String baseURI = "http://10.70.12.48:3000/";
    //String baseURI = "http://10.0.0.99:3000/";
    //String baseURI = "http://10.19.10.218:3000/";


     // category requests
    //@GET("api/category/")
    //Call<List<CategoryItem>> getAllCategories();

    @GET("api/category/{categoryId}/list")
    Call<List<NewsItem>> getListByCategory(@Path("categoryId")String categoryId);

    @GET("api/category/{categoryId}/list/{lat}/{lng}")
    Call<List<NewsItem>> getListByCategoryAndLocation(@Path("categoryId")String categoryId,
                                                    @Path("lat")double lat,
                                                    @Path("lng")double lng);

    //user login
    @FormUrlEncoded
    @POST("api/user/login")
    Call<UserItem> login(@Field("username")String name, @Field("password")String password);

    //user register
    @FormUrlEncoded
    @POST("api/user/register")
    Call<UserItem> userRegister(@Field("username")String name, @Field("password")String password,@Field("email")String email);



    // news
    @FormUrlEncoded
    @POST("api/newsAdd")
    Call<NewsItem> postNews(@Field("title")String title,
                            @Field("lat")Double lat,
                            @Field("lng")Double lng,
                            @Field("content")String content,
                            @Field("picture")String picture,
                            @Field("categoryId")String categoryId);



    //@GET()
    // bookmark requests
    /*@POST("api/bookmark/{userId}/{newsId}")
    Call<ResponseBody> putNewBookmark(@Path("userId")String userId,
                                      @Path("newsId")String newsId);*/

    @DELETE("api/bookmark/{bookmarkId}")
    Call<ResponseBody> deleteBookmark(@Path("bookmarkId")String bookmarkId);

    @GET("api/user/{userId}/bookmarks")
    Call<List<BookmarkItem>> getBookmarksByUser(@Path("userId")String userId);

    @GET("api/user/{userId}/news")
    Call<List<NewsItem>> getNewsByUser(@Path("userId")String userId);


    // user requests
    @GET("api/user")
    Call<UserItem> getUserDetails();
    @POST("api/user/{userId}")
    Call<UserItem> setUserDeatils(@Path("userId")String userId);

    

}
