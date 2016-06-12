package com.showjoy.tashow.localLive;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.showjoy.tashow.R;
import com.showjoy.tashow.data.UserDTO;
import com.showjoy.tashow.data.UserInfoDTO;
import com.showjoy.tashow.listener.MyItemClickListener;
import com.showjoy.tashow.utils.UIUtils;

import java.util.List;

public class HeadsAdapter extends RecyclerView.Adapter<HeadsAdapter.ViewHolder>{
    // 数据集
    private List<UserDTO> headset;
    private Context context;
    private MyItemClickListener mItemClickListener;

    public HeadsAdapter(List<UserDTO> headset,Context context) {
        this.headset = headset;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
        View view = LayoutInflater.from(context).inflate(R.layout.activity_head_item,null);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view,mItemClickListener);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.simpleDraweeView.setImageURI(Uri.parse(headset.get(i).image));
    }
    @Override
    public int getItemCount() {
        return headset.size();
    }

    public List<UserDTO>  getUsers() {
        return headset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public SimpleDraweeView simpleDraweeView;
        public ViewHolder(View itemView, final MyItemClickListener itemClickListener) {
            super(itemView);
             simpleDraweeView= (SimpleDraweeView) itemView.findViewById(R.id.img_head);
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v,getPosition());
                }
            });
        }

    }

    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }
}