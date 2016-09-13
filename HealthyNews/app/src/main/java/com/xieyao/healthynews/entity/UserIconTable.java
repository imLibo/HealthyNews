package com.xieyao.healthynews.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Arrays;

/**
 * 用户头像存储实体类
 * Created by libo on 2016/4/5.
 */
@Table(name = "UserIconTable")
public class UserIconTable implements Parcelable{
    @Column(name = "userPhone" , isId = true)
    String userPhone;
    @Column(name = "iconBytes")
    byte[] iconBytes ;//图片来源url

    public UserIconTable() {
    }

    @Override
    public String toString() {
        return "UserIconTable{" +
                "userPhone='" + userPhone + '\'' +
                ", iconBytes=" + Arrays.toString(iconBytes) +
                '}';
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public byte[] getIconBytes() {
        return iconBytes;
    }

    public void setIconBytes(byte[] iconBytes) {
        this.iconBytes = iconBytes;
    }

    public static Creator<UserIconTable> getCREATOR() {
        return CREATOR;
    }

    protected UserIconTable(Parcel in) {
        userPhone = in.readString();
        iconBytes = in.createByteArray();
    }

    public static final Creator<UserIconTable> CREATOR = new Creator<UserIconTable>() {
        @Override
        public UserIconTable createFromParcel(Parcel in) {
            return new UserIconTable(in);
        }

        @Override
        public UserIconTable[] newArray(int size) {
            return new UserIconTable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userPhone);
        dest.writeByteArray(iconBytes);
    }
}
