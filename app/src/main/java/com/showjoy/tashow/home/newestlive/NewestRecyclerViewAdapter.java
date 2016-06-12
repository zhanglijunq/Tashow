package com.showjoy.tashow.home.newestlive;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

//import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.showjoy.tashow.R;
import com.showjoy.tashow.data.RoomData;
import com.showjoy.tashow.listener.MyItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class NewestRecyclerViewAdapter extends RecyclerView.Adapter<NewestRecyclerViewAdapter.ViewHolder> {

    private List<RoomData> roomDataList;
    private MyItemClickListener onItemClickListener;

    public NewestRecyclerViewAdapter(List<RoomData> roomDataList) {
        this.roomDataList = roomDataList;
    }

    @Override
    public NewestRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_newest_live_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewestRecyclerViewAdapter.ViewHolder holder, int position) {
        final View view = holder.mView;
        holder.headImg.setImageURI(Uri.parse(roomDataList.get(position).image));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return roomDataList!=null?roomDataList.size():0;
    }

    public void setDatas(RoomData[] datas) {
       for (RoomData roomData:datas){
           roomDataList.add(roomData);
       }
    }

    public List<RoomData> getRoomDataList() {
        return roomDataList;
    }

    public void setOnItemClickListener(MyItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public SimpleDraweeView headImg;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            headImg = (SimpleDraweeView) view.findViewById(R.id.img_head);
            this.headImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(headImg,getPosition());
                }
            });
        }
    }
}
