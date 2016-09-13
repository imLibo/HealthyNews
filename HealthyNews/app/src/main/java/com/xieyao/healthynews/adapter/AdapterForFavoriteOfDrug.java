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
import com.xieyao.healthynews.entity.FavoriteOfNewsEntity;

import java.util.ArrayList;

/**
 * Created by libo on 2016/4/26.
 */
public class AdapterForFavoriteOfDrug extends RecyclerView.Adapter<AdapterForFavoriteOfDrug.MyViewHolder>{
    private Context context;
    private ArrayList<FavoriteOfNewsEntity> newsListEntities;
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public void setOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterForFavoriteOfDrug(Context context, ArrayList<FavoriteOfNewsEntity> newsListEntities) {
        this.context = context;
        this.newsListEntities = newsListEntities;
    }

    public void setData(ArrayList<FavoriteOfNewsEntity> entities){
        newsListEntities = entities;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recyclerview_notice,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onItemLongClick(position);
                return false;
            }
        });
        FavoriteOfNewsEntity data = newsListEntities.get(position);
        holder.mTitle.setText(data.getNewsTitle());
        holder.mTime.setText("收藏时间："+data.getCreateTime());
        if(!data.getNewsImgUrl().equals("null")){
            Glide.with(context).load(data.getNewsImgUrl()).into(holder.mDigest);
        }else {
            holder.mDigest.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return newsListEntities.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTitle;
        private final TextView mTime;
        private final ImageView mDigest;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.textview_item_favorite_title);
            mTime = (TextView) itemView.findViewById(R.id.textview_item_favorite_time);
            mDigest = (ImageView) itemView.findViewById(R.id.imageview_item_favorite);
        }
    }

}
