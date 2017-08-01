package com.dayman.poiot.adapters;

/**
 * Created by 25143j on 26/07/2017.
 */

public class ApiKey {

    // Add colour to here
    private String name;
    private String loginID;
    private String password;

    public ApiKey(String name, String loginID, String password) {
        this.name = name;
        this.loginID = loginID;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getLoginID() {
        return loginID;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
