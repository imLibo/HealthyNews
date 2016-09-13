package com.xieyao.healthynews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.entity.FavoriteOfNewsEntity;

import java.util.ArrayList;

/**
 * 收藏列表的适配器
 * Created by libo on 2016/4/26.
 */
public class AdapterForFavoriteOfNews extends RecyclerView.Adapter<AdapterForFavoriteOfNews.MyViewHolder>{
    private Context context;
    private ArrayList<FavoriteOfNewsEntity> newsListEntities;
    private OnItemClickListener onItemClickListener;
    /**是否显示选择框的标志位，默认不显示*/
    public boolean showDelete = false;
    /**选择要删除的收藏列表，通过监听每一个选择框的选择状态来动态调整*/
    public ArrayList<FavoriteOfNewsEntity> mDeleteFavorites = new ArrayList<>();
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemClickOfLong(int position);
    }

    public void setOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterForFavoriteOfNews(Context context, ArrayList<FavoriteOfNewsEntity> newsListEntities) {
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onItemClickOfLong(position);
                return false;
            }
        });
        //通过标志位来判断是否显示选择框
        if(showDelete){
            holder.mRadioDelete.setVisibility(View.VISIBLE);
        }else {
            holder.mRadioDelete.setVisibility(View.GONE);
        }
        final FavoriteOfNewsEntity data = newsListEntities.get(position);
        holder.mRadioDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //动态添加或移除删除列表
                if(isChecked){
                    mDeleteFavorites.add(data);
                }else {
                    mDeleteFavorites.remove(data);
                }
            }
        });

        holder.mTitle.setText(data.getNewsTitle());
        holder.mTime.setText(data.getCreateTime());
        if(!data.getNewsImgUrl().isEmpty()){
            Glide.with(context).load(data.getNewsImgUrl()).into(holder.mDigest);
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
        private final CheckBox mRadioDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.textview_item_favorite_title);
            mTime = (TextView) itemView.findViewById(R.id.textview_item_favorite_time);
            mDigest = (ImageView) itemView.findViewById(R.id.imageview_item_favorite);
            mRadioDelete = (CheckBox) itemView.findViewById(R.id.radiobutton_favorite_delete);
        }
    }

}
