package com.showjoy.tashow.data;

import java.io.Serializable;

/**
 * Created by mac on 16/5/10.
 * 房间类
 */
public class RoomData implements Serializable{
    //描述
    public String desc;
    //房间id
    public String id;
    //房间封面
    public String image;
    //房间名称
    public String name;
    //播放地址
    public String rtmpPlayUrl;
    //用户id
    public String userId;
    //用户头像
    public String userImage;
    //用户名称
    public String username;
    //观看人数
    public String viewNum;
}
