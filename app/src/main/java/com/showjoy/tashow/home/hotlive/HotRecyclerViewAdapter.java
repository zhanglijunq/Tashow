package com.showjoy.tashow.home.hotlive;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.showjoy.tashow.MyViewHolder;
import com.showjoy.tashow.R;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.data.RoomData;
import com.showjoy.tashow.listener.MyItemClickListener;
import com.showjoy.tashow.utils.SpanUtils;
import com.showjoy.tashow.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class HotRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context mContext;

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    private MyItemClickListener mItemClickListener;
    private List<RoomData> roomDataList = new ArrayList<>();

    public HotRecyclerViewAdapter(Context mContext, List<RoomData> roomDataList) {
        this.mContext = mContext;
        if (roomDataList!=null){
            this.roomDataList = roomDataList;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hot_live_item, parent, false);
        ImageView bannerImg = new ImageView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(TaShowGlobal.appContext,180));
        bannerImg.setLayoutParams(params);
        bannerImg.setBackgroundResource(R.drawable.banner);
        return viewType == VIEW_TYPE_HEADER ? new MyViewHolder(bannerImg,mItemClickListener):new MyViewHolder(view,mItemClickListener);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (roomDataList!=null && holder.headImg!=null){
            RoomData roomData = roomDataList.get(position-1);
            holder.headImg.setImageURI(Uri.parse(roomData.userImage));
            holder.bgImg.setImageURI(Uri.parse(roomData.image));
            holder.nameTxt.setText(!TextUtils.isEmpty(roomData.username)?roomData.username:roomData.userId);
            String content = roomData.viewNum+"人在看";
            int start = 0;
            int end = content.indexOf("人在看");
            if (holder.viewNumTxt!=null){
                holder.viewNumTxt.setText("");
            }
            holder.viewNumTxt.append(SpanUtils.spanColor(mContext,content,start,end,R.color.colorBind,36,true));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;

    }

    @Override
    public int getItemCount() {
        return roomDataList.size() + 1;
    }

    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public List<RoomData> getRoomDataList() {
        return roomDataList;
    }

    public void setData(List<RoomData> roomDatas) {
        for (RoomData roomData : roomDatas){
            roomDataList.add(roomData);
        }
    }
}
