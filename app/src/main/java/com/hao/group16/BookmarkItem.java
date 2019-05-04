package com.hao.group16;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;
import java.util.List;

public class BookmarkItem implements Parcelable{

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("newsId")
    @Expose
    private String newsId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("pageView")
    @Expose
    private Integer pageView;
    @SerializedName("vote")
    @Expose
    private Integer vote;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("pictures")
    @Expose
    private List<String> pictures = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPageView() {
        return pageView;
    }

    public void setPageView(Integer pageView) {
        this.pageView = pageView;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.date);
        dest.writeString(this.description);
        dest.writeList(this.pictures);
        dest.writeInt(this.pageView);
        dest.writeInt(this.vote);
    }

    protected BookmarkItem(@NonNull Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.date = in.readString();
        this.description = in.readString();
        pictures = new ArrayList<>();
        in.readList(pictures, null);
        this.pageView = in.readInt();
        this.vote = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BookmarkItem createFromParcel(Parcel in) {
            return new BookmarkItem(in);
        }

        public BookmarkItem[] newArray(int size) {
            return new BookmarkItem[size];
        }
    };



    @Override
    public String toString() {
        return String.format("%s-%s", title, date);
    }

}