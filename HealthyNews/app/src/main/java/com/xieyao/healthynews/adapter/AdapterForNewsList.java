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
import com.xieyao.healthynews.entity.NewsListEntity;
import com.xieyao.healthynews.util.TimeUtil;

import java.util.ArrayList;

/**
 * Created by libo on 2016/4/26.
 */
public class AdapterForNewsList  extends RecyclerView.Adapter<AdapterForNewsList.MyViewHolder>{
    private Context context;
    private ArrayList<NewsListEntity> newsListEntities;
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterForNewsList(Context context, ArrayList<NewsListEntity> newsListEntities) {
        this.context = context;
        this.newsListEntities = newsListEntities;
    }

    public void setData(ArrayList<NewsListEntity> entities){
        newsListEntities = entities;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recyclerview_latestnews,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
        NewsListEntity news = newsListEntities.get(position);
        holder.mTitle.setText(news.getTitle());
        holder.mTime.setText(TimeUtil.FormatTimeToYearAndMonth(Long.valueOf(news.getTime())));
        holder.mDigest.setText(news.getDescription());

        Glide.with(context).load("http://tnfs.tngou.net/image"+news.getImg()).into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return newsListEntities.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTitle;
        private final TextView mTime;
        private final TextView mDigest;
        private final ImageView mImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.textview_item_latestnews_title);
            mTime = (TextView) itemView.findViewById(R.id.textview_item_latestnews_time);
            mDigest = (TextView) itemView.findViewById(R.id.textview_item_latestnews_digest);
            mImage = (ImageView) itemView.findViewById(R.id.imageview_item_latestnews_image);
        }
    }

}
