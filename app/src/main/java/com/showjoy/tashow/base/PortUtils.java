package com.showjoy.tashow.base;

import com.showjoy.tashow.okhttp.OkHttpClientManager.Param;

import java.io.File;

public class PortUtils {
    /**
     * 获取注册验证码
     */
    public static final String registerCaptcha = ServerConfig.serverName+"user/getLoginCaptcha";
    /**
     * 登录
     */
    public static final String login = ServerConfig.serverName+"user/login";
    /**
     * 最热房间列表
     */
    public static final String hotestRoom = ServerConfig.serverName+"room/list/hotest";
    /**
     * 最新房间列表
     */
    public static final String newestRoom = ServerConfig.serverName+"room/list/newest";
    /**
     * 关注房间列表
     */
    public static final String followedRoom = ServerConfig.serverName+"room/list/followed";
    /**
     * 发布房间
     */
    public static final String startRoom = ServerConfig.serverName+"room/start";
    /**
     * 关注
     */
    public static final String follow = ServerConfig.serverName+"user/follow";
    /**
     * 取消关注
     */
    public static final String unFollow = ServerConfig.serverName+"user/unFollow";
    /**
     * 获取用户信息
     */
    public static final String getUserInfo = ServerConfig.serverName+"user/getUserInfo";
    /**
     * 房间用户列表
     */
    public static final String getUsers = ServerConfig.serverName+"room/users";
    /**
     * 关注列表
     */
    public static final String followList = ServerConfig.serverName+"user/followList";
    /**
     * 粉丝列表
     */
    public static final String followerList = ServerConfig.serverName+"user/followerList";
    /**
     * 更新用户信息
     */
    public static final String updateUserInfo = ServerConfig.serverName+"user/update";
    /**
     * 更新用户信息，不带头像
     */
    public static final String updateWithNoImg = ServerConfig.serverName+"user/update_without_image";
    /**
     * 获取最热直播列表传参
     * @param page 分页
     * @return params
     */
    public static Param[] getHotestRoom(String page) {
        Param[] params = new Param[1];
        Param param = new Param("page",page);
        params[0] = param;
        return params;
    }

    /**
     * 传参
     * @param key key
     * @param value value
     * @return
     */
    public static Param[] getCode(String key, String value) {
        Param[] params = new Param[1];
        Param param = new Param(key,value);
        params[0] = param;
        return params;
    }

    /**
     * 用户登录传参
     * @param tel 手机号
     * @param code  验证码
     * @return
     */
    public static Param[] login(String tel, String code) {
        Param[] params = new Param[2];
        Param telParam = new Param("tel",tel);
        Param codeParam = new Param("checkCode",code);
        params[0] = telParam;
        params[1] = codeParam;
        return params;
    }
    /**
     * 最新直播传参
     * @param page
     * @return
     */
    public static Param[] getNewestRooms(String page) {
        Param[] params = new Param[1];
        Param param = new Param("page",page);
        params[0] = param;
        return params;
    }
    /**
     * 开启直播传参
     * @param userId 用户ID
     * @return
     */
    public static Param[] startRoom(String userId) {
        Param[] params = new Param[1];
        Param param = new Param("userId",userId);
        params[0] = param;
        return params;
    }
    /**
     * 关注用户
     * @param userId 用户ID
     * @param followId 关注ID
     * @return
     */
    public static Param[] follow(String userId,String followId) {
        Param[] params = new Param[2];
        Param param = new Param("userId",followId);
        Param imgParam = new Param("followerId",userId);
        params[0] = param;
        params[1] = imgParam;
        return params;
    }
    /**
     * 获取用户信息
     * @param currentUserId 当前用户ID
     * @param userId 当前主播ID
     * @return
     */
    public static Param[] getUserInfo(String userId, String currentUserId) {
        Param[] params = new Param[2];
        Param param = new Param("userId",userId);
        Param imgParam = new Param("currentUserId",currentUserId);
        params[0] = param;
        params[1] = imgParam;
        return params;
    }
    /**
     * 获取房间用户列表
     * @param roomId 房间id
     * @param page 分页
     * @return
     */
    public static Param[] getUsers(String roomId, String page) {
        Param[] params = new Param[2];
        Param param = new Param("roomId",roomId);
        Param imgParam = new Param("page",page);
        params[0] = param;
        params[1] = imgParam;
        return params;
    }
    /**
     * 获取关注列表和粉丝列表
     * @param userId 用户id
     * @param page 分页
     * @return
     */
    public static Param[] getFollowList(String userId, String page) {
        Param[] params = new Param[2];
        Param param = new Param("userId",userId);
        Param imgParam = new Param("page",page);
        params[0] = param;
        params[1] = imgParam;
        return params;
    }

    /**
     * 取消关注
     * @param userId 被关注人的用户id
     * @param followId 取消关注的人的用户id
     * @return
     */
    public static Param[] getUnFollow(String userId, String followId) {
        Param[] params = new Param[2];
        Param param = new Param("userId",followId);
        Param secParam = new Param("followerId",userId);
        params[0] = param;
        params[1] = secParam;
        return params;
    }
    /**
     * 更新用户信息
     * @param userId 用户id
     * @param username 用户昵称
     * @param sex 用户性别
     * @return
     */
    public static Param[] updataParam(String userId, String username,String sex) {
        Param[] params = new Param[3];
        Param param = new Param("userId",userId);
        Param secParam = new Param("username",username);
        Param thirdParam = new Param("sex",sex);
        params[0] = param;
        params[1] = secParam;
        params[2] = thirdParam;
        return params;
    }
}
