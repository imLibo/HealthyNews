package com.xieyao.healthynews.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 新闻列表实体类
 * Created by libo on 2016/4/26.
 */
public class NewsListEntity implements Parcelable{
    private String author;
    private String id;
    private String tid;
    private String time;
    private String title;
    private String tname;
    private String img;
    private String description;

    @Override
    public String toString() {
        return "NewsListEntity{" +
                "author='" + author + '\'' +
                ", id='" + id + '\'' +
                ", tid='" + tid + '\'' +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                ", tname='" + tname + '\'' +
                ", img='" + img + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public NewsListEntity() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Creator<NewsListEntity> getCREATOR() {
        return CREATOR;
    }

    protected NewsListEntity(Parcel in) {
        author = in.readString();
        id = in.readString();
        tid = in.readString();
        time = in.readString();
        title = in.readString();
        tname = in.readString();
        img = in.readString();
        description = in.readString();
    }

    public static final Creator<NewsListEntity> CREATOR = new Creator<NewsListEntity>() {
        @Override
        public NewsListEntity createFromParcel(Parcel in) {
            return new NewsListEntity(in);
        }

        @Override
        public NewsListEntity[] newArray(int size) {
            return new NewsListEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(id);
        dest.writeString(tid);
        dest.writeString(time);
        dest.writeString(title);
        dest.writeString(tname);
        dest.writeString(img);
        dest.writeString(description);
    }
}
