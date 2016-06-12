package com.showjoy.tashow.tcpclient;

/**
 * Created by mac on 16/5/15.
 */
public class TcpHandler {
    /**
     * 心跳检测
     * 检测连接是否正常
     */
    public static final short HEART_BEAT_HANDLER=0;
    /**
     * 用户处理
     * 处理用户的登录，登出，关注等逻辑
     */
    public static final short USER_HANDLER=1;
    /**
     * 消息处理
     * 处理消息的发送，礼物的赠送等逻辑
     */
    public static final short MESSAGE_HANDLER=2;
    /**
     * 房间处理
     * 处理加入房间，退出房间，踢人等逻辑
     */
    public static final short ROOM_HANDLER=3;
    /**
     * 礼物处理
     * 处理礼物赠送等逻辑
     */
    public static final short GIFT_HANDLER=4;

}
