package com.xieyao.healthynews.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 药品实体类
 * Created by bobo1 on 2016/4/30.
 */
public class DrugListEntity implements Parcelable{
    //不良反应
    private String blfy;
    //药品名称
    private String drugName;
    //规格型号
    private String ggxh;
    //id
    private String id;
    //图片地址
    private String img;
    //禁忌
    private String jj;
    //生产企业
    private String manu;
    //参考价格
    private String price;
    //批准文号
    private String pzwh;
    //适应症
    private String syz;
    //药品类别
    private String type;
    //性状
    private String xz;
    //用法用量
    private String yfyl;
    //药物相互作用
    private String ywxhzy;
    //有效期
    private String yxq;
    //储藏
    private String zc;
    //执行标准
    private String zxbz;
    //主要成分
    private String zycf;
    //注意事项
    private String zysx;
    //主治疾病
    private String zzjb;

    protected DrugListEntity(Parcel in) {
        blfy = in.readString();
        drugName = in.readString();
        ggxh = in.readString();
        id = in.readString();
        img = in.readString();
        jj = in.readString();
        manu = in.readString();
        price = in.readString();
        pzwh = in.readString();
        syz = in.readString();
        type = in.readString();
        xz = in.readString();
        yfyl = in.readString();
        ywxhzy = in.readString();
        yxq = in.readString();
        zc = in.readString();
        zxbz = in.readString();
        zycf = in.readString();
        zysx = in.readString();
        zzjb = in.readString();
    }

    public static final Creator<DrugListEntity> CREATOR = new Creator<DrugListEntity>() {
        @Override
        public DrugListEntity createFromParcel(Parcel in) {
            return new DrugListEntity(in);
        }

        @Override
        public DrugListEntity[] newArray(int size) {
            return new DrugListEntity[size];
        }
    };

    @Override
    public String toString() {
        return "DrugListEntity{" +
                "blfy='" + blfy + '\'' +
                ", drugName='" + drugName + '\'' +
                ", ggxh='" + ggxh + '\'' +
                ", id='" + id + '\'' +
                ", img='" + img + '\'' +
                ", jj='" + jj + '\'' +
                ", manu='" + manu + '\'' +
                ", price='" + price + '\'' +
                ", pzwh='" + pzwh + '\'' +
                ", syz='" + syz + '\'' +
                ", type='" + type + '\'' +
                ", xz='" + xz + '\'' +
                ", yfyl='" + yfyl + '\'' +
                ", ywxhzy='" + ywxhzy + '\'' +
                ", yxq='" + yxq + '\'' +
                ", zc='" + zc + '\'' +
                ", zxbz='" + zxbz + '\'' +
                ", zycf='" + zycf + '\'' +
                ", zysx='" + zysx + '\'' +
                ", zzjb='" + zzjb + '\'' +
                '}';
    }

    public String getBlfy() {
        return blfy;
    }

    public void setBlfy(String blfy) {
        this.blfy = blfy;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getGgxh() {
        return ggxh;
    }

    public void setGgxh(String ggxh) {
        this.ggxh = ggxh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getJj() {
        return jj;
    }

    public void setJj(String jj) {
        this.jj = jj;
    }

    public String getManu() {
        return manu;
    }

    public void setManu(String manu) {
        this.manu = manu;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPzwh() {
        return pzwh;
    }

    public void setPzwh(String pzwh) {
        this.pzwh = pzwh;
    }

    public String getSyz() {
        return syz;
    }

    public void setSyz(String syz) {
        this.syz = syz;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXz() {
        return xz;
    }

    public void setXz(String xz) {
        this.xz = xz;
    }

    public String getYfyl() {
        return yfyl;
    }

    public void setYfyl(String yfyl) {
        this.yfyl = yfyl;
    }

    public String getYwxhzy() {
        return ywxhzy;
    }

    public void setYwxhzy(String ywxhzy) {
        this.ywxhzy = ywxhzy;
    }

    public String getYxq() {
        return yxq;
    }

    public void setYxq(String yxq) {
        this.yxq = yxq;
    }

    public String getZc() {
        return zc;
    }

    public void setZc(String zc) {
        this.zc = zc;
    }

    public String getZxbz() {
        return zxbz;
    }

    public void setZxbz(String zxbz) {
        this.zxbz = zxbz;
    }

    public String getZycf() {
        return zycf;
    }

    public void setZycf(String zycf) {
        this.zycf = zycf;
    }

    public String getZysx() {
        return zysx;
    }

    public void setZysx(String zysx) {
        this.zysx = zysx;
    }

    public String getZzjb() {
        return zzjb;
    }

    public void setZzjb(String zzjb) {
        this.zzjb = zzjb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(blfy);
        dest.writeString(drugName);
        dest.writeString(ggxh);
        dest.writeString(id);
        dest.writeString(img);
        dest.writeString(jj);
        dest.writeString(manu);
        dest.writeString(price);
        dest.writeString(pzwh);
        dest.writeString(syz);
        dest.writeString(type);
        dest.writeString(xz);
        dest.writeString(yfyl);
        dest.writeString(ywxhzy);
        dest.writeString(yxq);
        dest.writeString(zc);
        dest.writeString(zxbz);
        dest.writeString(zycf);
        dest.writeString(zysx);
        dest.writeString(zzjb);
    }
}
