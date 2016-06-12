package com.showjoy.tashow.tcpclient;

/**
 * Created by mac on 16/5/15.
 */
public class TcpCommand {
    /**
     * heartBeatHandler
     * 心跳检测
     */
    public static final short HEART_BEAT=1;

    /**
     * userHandler
     * 用户登录
     */
    public static final short USER_LOGIN=1;
    /**
     * userHandler
     * 用户登出
     */
    public static final short USER_LOGOUT=2;
    /**
     * userHandler
     * 用户关注
     */
    public static final short USER_LIKE=3;
    /**
     * userHandler
     * 用户取消关注
     */
    public static final short USER_UNLIKE=4;

    /**
     * messageHandler
     * 消息的发送
     */
    public static final short MESSAGE_SEND=1;
    /**
     * messageHandler
     * 消息同步
     */
    public static final short MESSAGE_SYNC=2;
    /**
     * roomHandler
     * 加入房间
     */
    public static final short ROOM_JOIN=1;
    /**
     * roomHandler
     * 离开房间
     */
    public static final short ROOM_LEAVE=2;
    /**
     * roomHandler
     * 房间禁言
     */
    public static final short ROOM_BLACKLIST=3;
    /**
     * giftHandler
     * 礼物发送
     */
    public static final short GIFT_SEND=1;

    /**
     * JSON格式发送
     */
    public static final short CODE_ID=1;
}
