package com.showjoy.tashow.tcpclient;

/**
 * 聊天协议头
 * @author jiujie
 * @version $Id: Header.java, v 0.1 2016年3月31日 下午1:51:03 jiujie Exp $
 */
public interface Header {

    /**
     * 数据包长度，包括包头长度
     * @author jiujie
     * 2016年3月31日 下午2:56:00
     * @return
     */
    int getLength();

    /**
     * 获取协议版本
     * @author jiujie
     * 2016年3月31日 下午1:56:40
     * @return
     */
    short getVersion();

    /**
     * 获取处理器Id
     * @author jiujie
     * 2016年3月31日 下午1:58:25
     * @return
     */
    short getHandlerId();

    /**
     * 获取命令Id
     * @author jiujie
     * 2016年3月31日 下午1:58:40
     * @return
     */
    short getCommandId();

    /**
     * 获取Body编解码器的Id
     * @author jiujie
     * 2016年3月31日 下午1:59:07
     * @return
     */
    short getCodecId();

}
