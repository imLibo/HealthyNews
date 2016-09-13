package com.xieyao.healthynews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xieyao.healthynews.R;
import com.xieyao.healthynews.entity.YaopinFenleiEntity;

import java.util.List;

/**
 * 首页recyclerview的适配器，支持拖拽排序
 * Created by libo on 2016/3/28.
 */
public class GridRecylerViewAdapter extends
        RecyclerView.Adapter<GridRecylerViewAdapter.MyViewHodler> {

    private Context context;
    private List<YaopinFenleiEntity> itemTexts;

    private OnItemClickLither onItemClickLither;

    public GridRecylerViewAdapter(Context context,  List<YaopinFenleiEntity> itemTexts
   ) {
        this.context = context;
        this.itemTexts = itemTexts;
    }


    /***
     * 点击时事件与顺序变更的接口
     */
    public interface OnItemClickLither{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    /***
     * 设置接口
     * @param onItemClickLitener
     */
    public void setOnItemClickAndDataChangedLitener(OnItemClickLither onItemClickLitener){
        this.onItemClickLither = onItemClickLitener;
    }

    @Override
    public MyViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHodler viewHodler = new MyViewHodler(
                LayoutInflater.from(context).inflate(R.layout.item_yaopinfenlei,parent,false));
        return viewHodler;
    }

    @Override
    public void onBindViewHolder(final MyViewHodler holder, final int position) {
        holder.textview.setText(itemTexts.get(position).getType());


        if(onItemClickLither != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //item点击事件的回调
                    onItemClickLither.onItemClick(v,holder.getLayoutPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //item长按事件的回调
                    onItemClickLither.onItemLongClick(v,holder.getLayoutPosition());
                    return false;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return itemTexts.size();
    }

    class MyViewHodler extends RecyclerView.ViewHolder{
        TextView textview;
        public MyViewHodler(View itemView) {
            super(itemView);
            textview = (TextView) itemView.findViewById(R.id.textview_item_yaopinfenlei);
        }
    }
}
