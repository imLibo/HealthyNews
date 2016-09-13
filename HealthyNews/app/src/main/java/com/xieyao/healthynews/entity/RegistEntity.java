package com.xieyao.healthynews.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bobo1 on 2016/5/4.
 */
public class RegistEntity implements Parcelable{
    private String userName;
    private String password;

    public RegistEntity() {
    }

    protected RegistEntity(Parcel in) {
        userName = in.readString();
        password = in.readString();
    }

    public static final Creator<RegistEntity> CREATOR = new Creator<RegistEntity>() {
        @Override
        public RegistEntity createFromParcel(Parcel in) {
            return new RegistEntity(in);
        }

        @Override
        public RegistEntity[] newArray(int size) {
            return new RegistEntity[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(password);
    }
}
