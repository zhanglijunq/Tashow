package com.showjoy.tashow.data;

import java.io.Serializable;

public class LoginDTO implements Serializable {

    /** @author jiujie 2016年4月6日 上午10:28:03 */
    private static final long serialVersionUID = 1L;

    //用户 名
    private String            username;

    //用户密码
    private String            ticket;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String token) {
        this.ticket = token;
    }

}
