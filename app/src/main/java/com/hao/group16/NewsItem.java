
package com.hao.group16;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsItem implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("pictures")
    @Expose
    private List<String> pictures = null;
    @SerializedName("pageView")
    @Expose
    private Integer pageView;
    @SerializedName("vote")
    @Expose
    private Integer vote;
    @SerializedName("distance")
    @Expose
    private Double distance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public Double getDistance() {
        if (null == distance) {
            return 0.0;
        } else {
            return distance;
        }
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.time);
        dest.writeString(this.description);
        dest.writeList(this.pictures);
        dest.writeInt(this.pageView);
        dest.writeInt(this.vote);
        dest.writeDouble(this.distance);
    }

    protected NewsItem(@NonNull Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.time = in.readString();
        this.description = in.readString();
        pictures = new ArrayList<>();
        in.readList(pictures, null);
        this.pageView = in.readInt();
        this.vote = in.readInt();
        this.distance = in.readDouble();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    @Override
    public String toString() {
        return String.format("%s-%s", title, id);
    }

}