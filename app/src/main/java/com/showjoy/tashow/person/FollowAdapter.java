package com.showjoy.tashow.person;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.showjoy.tashow.R;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.data.UserDTO;
import com.showjoy.tashow.listener.FollowItemClickListener;
import com.showjoy.tashow.localLive.HeadsAdapter;
import com.showjoy.tashow.utils.UIUtils;

/**
 * Created by mac on 16/5/16.
 */
public class FollowAdapter extends BaseAdapter{
    private Context context;
    private UserDTO[] userDTOs;
    private boolean isDelete;
    private FollowItemClickListener followItemClickListener;
    public FollowAdapter(Context context, UserDTO[] userDTOs, boolean isDelete, FollowItemClickListener followItemClickListener) {
        this.context = context;
        this.userDTOs = userDTOs;
        this.isDelete = isDelete;
        this.followItemClickListener = followItemClickListener;
    }

    @Override
    public int getCount() {
        return userDTOs.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.activity_follow_list_item,null);
        TextView itemRightTxt = (TextView) convertView.findViewById(R.id.txt_item_right);
        SimpleDraweeView headImg = (SimpleDraweeView) convertView.findViewById(R.id.img_head);
        TextView nameTxt = (TextView) convertView.findViewById(R.id.txt_name);
        LinearLayout item_right = (LinearLayout) convertView.findViewById(R.id.item_right);
        // 设置显示部分和隐藏部分的宽度
        LayoutParams lp2 = new LayoutParams(UIUtils.dip2px(context,130), LayoutParams.MATCH_PARENT);
        item_right.setLayoutParams(lp2);
        if (isDelete){
            itemRightTxt.setBackgroundResource(R.color.colorPink);
            itemRightTxt.setText("删除");
        }else {
            itemRightTxt.setBackgroundResource(R.color.colorBind);
            itemRightTxt.setText("关注");
        }
        final UserDTO userDTO = userDTOs[position];
        if (userDTO!=null){
            headImg.setImageURI(Uri.parse(userDTO.image));
            nameTxt.setText(userDTO.username);
            if (userDTO.isFollow && !isDelete){
                itemRightTxt.setText("已关注");
            }
        }
        itemRightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followItemClickListener.onItemClick(v,userDTO);
            }
        });
        return convertView;
    }
}