package com.xieyao.healthynews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.entity.DrugListEntity;
import com.xieyao.healthynews.entity.NewsListEntity;

import java.util.ArrayList;

/**
 * Created by libo on 2016/4/26.
 */
public class AdapterForDrugList extends RecyclerView.Adapter<AdapterForDrugList.MyViewHolder>{
    private Context context;
    private ArrayList<DrugListEntity> drugListEntities;
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterForDrugList(Context context, ArrayList<DrugListEntity> drugListEntities) {
        this.context = context;
        this.drugListEntities = drugListEntities;
    }

    public void setData(ArrayList<DrugListEntity> entities){
        drugListEntities = entities;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_durglist,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
        DrugListEntity entity = drugListEntities.get(position);
        holder.mTitle.setText(entity.getDrugName());
        if(entity.getImg()!=null && !entity.getImg().equals("")){
            Glide.with(context).load(entity.getImg()).into(holder.mImg);
        }
        holder.mScqy.setText(entity.getManu());
        holder.mCkjg.setText(entity.getPrice());
        holder.mGgxh.setText(entity.getGgxh());
        holder.mYfyl.setText(entity.getYfyl());

    }

    @Override
    public int getItemCount() {
        return drugListEntities.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTitle;
        private final ImageView mImg;
        private final TextView mScqy;
        private final TextView mGgxh;
        private final TextView mCkjg;
        private final TextView mYfyl;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.textview_druglist_name);
            mImg = (ImageView) itemView.findViewById(R.id.imageview_durglist);
            mScqy = (TextView) itemView.findViewById(R.id.textview_druglist_scqy);
            mGgxh = (TextView) itemView.findViewById(R.id.textview_druglist_ggxh);
            mCkjg = (TextView) itemView.findViewById(R.id.textview_druglist_ckjg);
            mYfyl = (TextView) itemView.findViewById(R.id.textview_druglist_yfyl);
        }
    }

}
