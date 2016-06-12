package com.showjoy.tashow.data;

import com.google.gson.Gson;

public class Result {

    private int     count;

    private Object  data;

    private int     isRedirect;

    private boolean isSuccess;

    private int     login;

    private String  msg;

    /** 异常，传入的异常类必须含有默认构造方法 */
    private Object  exception;

    public Result(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Result(boolean isSuccess, Object obj) {
        this.isSuccess = isSuccess;
        this.data = obj;
    }

    public Result(String jsonDataStr) {
        Result jsonVO = null;
        jsonVO = new Gson().fromJson(jsonDataStr, Result.class);
        if (jsonVO != null) {
            this.setCount(jsonVO.getCount());
            this.setData(jsonVO.getData());
            this.setIsRedirect(jsonVO.getIsRedirect());
            this.setIsSuccess(jsonVO.getIsSuccess());
            this.setLogin(jsonVO.getLogin());
            this.setMsg(jsonVO.getMsg());
            this.setException(jsonVO.getException());
        }
    }

    public Result() {
    }

    public int getCount() {
        return count;
    }

    public Object getData() {
        return data;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public int getLogin() {
        return login;
    }

    public String getMsg() {
        return msg;
    }

    public Result setCount(int count) {
        this.count = count;
        return this;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public Result setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
        return this;
    }

    public Result setLogin(int login) {
        this.login = login;
        return this;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public int getIsRedirect() {
        return isRedirect;
    }

    public Result setIsRedirect(int isRedirect) {
        this.isRedirect = isRedirect;
        return this;
    }

//    public <T> T getObjectData(Class<T> clazz) {
//        if (this.getIsSuccess() && this.getData() != null) {
//            if (this.getData() instanceof JSONObject) {
//                return JSON.toJavaObject((JSONObject) this.getData(), clazz);
//            } else {
//                return clazz.cast(this.getData());
//            }
//        }
//        return null;
//    }
//
//    public <T> T getExpcetionData(Class<T> clazz) {
//        if (this.getIsSuccess() && this.getException() != null) {
//            if (this.getException() instanceof JSONObject) {
//                return JSON.toJavaObject((JSONObject) this.getException(), clazz);
//            } else {
//                return clazz.cast(this.getException());
//            }
//        }
//        return null;
//    }
//
//    public <T> List<T> getArrayData(Class<T> clazz) {
//        if (this.getIsSuccess() && this.getData() != null) {
//            return JSON.parseArray(JSON.toJSONString(this.getData()), clazz);
//        }
//        return null;
//    }

//    @Override
//    public String toString() {
//        return JSON.toJSONString(this);
//    }

    public Object getException() {
        return exception;
    }

    public Result setException(Object exception) {
        this.exception = exception;
        return this;
    }

}
