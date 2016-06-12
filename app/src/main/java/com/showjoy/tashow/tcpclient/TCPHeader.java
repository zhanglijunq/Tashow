package com.showjoy.tashow.tcpclient;

import java.io.Serializable;

/**
 * IM协议头
 */
public class TCPHeader implements Header,Serializable {

    public static final int PROTOCOL_HEADER_LENGTH = 8;

    private int             length;                    // 数据包长度，包括包头长度
    private short           version;
    private short           handlerId;                 // SID
    private short           commandId;                 // CID
    private short           codecId;

    public short getCodecId() {
        return codecId;
    }

    public void setCodecId(short codecId) {
        this.codecId = codecId;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(short handlerId) {
        this.handlerId = handlerId;
    }

    public short getCommandId() {
        return commandId;
    }

    public void setCommandId(short commandId) {
        this.commandId = commandId;
    }

    @Override
    public String toString() {
        return "Header{" + "length=" + length + ", version=" + version + ", handlerId=" + handlerId
               + ", commandId=" + commandId + ", codecId=" + codecId + "}";
    }

}