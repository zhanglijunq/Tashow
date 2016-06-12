package com.showjoy.tashow;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.showjoy.tashow.listener.MyItemClickListener;

/**
 * Created by mac on 16/5/6.
 */
public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private MyItemClickListener mListener;

    public final View mView;

    public SimpleDraweeView headImg,bgImg;

    public TextView nameTxt,viewNumTxt;

    public MyViewHolder(View rootView,MyItemClickListener listener) {
        super(rootView);
        this.headImg = (SimpleDraweeView) rootView.findViewById(R.id.img_head);
        this.bgImg = (SimpleDraweeView) rootView.findViewById(R.id.img_bg);
        this.nameTxt = (TextView) rootView.findViewById(R.id.txt_name);
        this.viewNumTxt = (TextView) rootView.findViewById(R.id.txt_view_num);
        this.mView = rootView;
        this.mListener = listener;
        rootView.setOnClickListener(this);
    }

    /**
     * 点击监听
     */
    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onItemClick(v, getPosition()-1);
        }
    }
}
