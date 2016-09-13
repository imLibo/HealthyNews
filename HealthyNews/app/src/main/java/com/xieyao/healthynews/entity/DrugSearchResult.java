package com.xieyao.healthynews.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bobo1 on 2016/5/1.
 */
public class DrugSearchResult implements Parcelable{
    private int allPage;
    private int currentPage;
    private ArrayList<DrugListEntity> entityArrayList;
    private String keyword;

    public DrugSearchResult() {
    }

    public int getAllPage() {
        return allPage;
    }

    public void setAllPage(int allPage) {
        this.allPage = allPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public ArrayList<DrugListEntity> getEntityArrayList() {
        return entityArrayList;
    }

    public void setEntityArrayList(ArrayList<DrugListEntity> entityArrayList) {
        this.entityArrayList = entityArrayList;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public static Creator<DrugSearchResult> getCREATOR() {
        return CREATOR;
    }

    protected DrugSearchResult(Parcel in) {
        allPage = in.readInt();
        currentPage = in.readInt();
        entityArrayList = in.createTypedArrayList(DrugListEntity.CREATOR);
        keyword = in.readString();
    }

    public static final Creator<DrugSearchResult> CREATOR = new Creator<DrugSearchResult>() {
        @Override
        public DrugSearchResult createFromParcel(Parcel in) {
            return new DrugSearchResult(in);
        }

        @Override
        public DrugSearchResult[] newArray(int size) {
            return new DrugSearchResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(allPage);
        dest.writeInt(currentPage);
        dest.writeTypedList(entityArrayList);
        dest.writeString(keyword);
    }
}
