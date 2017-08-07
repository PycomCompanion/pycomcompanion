package com.dayman.poiot.adapters;

/**
 * Created by 25143j on 26/07/2017.
 */

public class ApiKey {

    // Add colour to here
    private String name;
    private String loginID;
    private String password;
    private String colour;

    public ApiKey(String name, String loginID, String password, String colour) {
        this.name = name;
        this.loginID = loginID;
        this.password = password;
        this.colour = colour;
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

    public String getColour() {
        return colour;
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

    public void setColour(String colour) {
        this.colour = colour;
    }
}
