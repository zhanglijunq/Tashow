package com.showjoy.tashow.localLive;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.showjoy.tashow.R;
import com.showjoy.tashow.data.MessageDTO;

import java.util.List;

/**
 * Created by mac on 16/5/15.
 */
public class MessageAdapter extends BaseAdapter{
    private List<MessageDTO> messageDTOs;
    private Context context;

    public MessageAdapter(List<MessageDTO> messageDTOs,Context context) {
        this.messageDTOs = messageDTOs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messageDTOs.size();
    }

    @Override
    public Object getItem(int position) {
        return messageDTOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.activity_message_item,null);
        TextView nameTxt = (TextView) convertView.findViewById(R.id.txt_name);
        TextView messageTxt = (TextView) convertView.findViewById(R.id.txt_message);
        MessageDTO messageDTO = messageDTOs.get(position);
        if (messageDTO != null){
            nameTxt.setText(!TextUtils.isEmpty(messageDTO.fromUsername)?messageDTO.fromUsername:String.valueOf(messageDTO.from));
            messageTxt.setText(messageDTO.message);
        }
        return convertView;
    }

    public void addMessage(MessageDTO messageDTO) {
            this.messageDTOs.add(messageDTO);
    }
}
