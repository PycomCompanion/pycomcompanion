package com.dayman.poiot.adapters;

/**
 * Created by 25143j on 21/07/2017.
 */

public class SensorData {

    private String data;
    private String dateTime;

    public SensorData(String data, String dateTime) {
        this.data = data;
        this.dateTime = dateTime;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getData() {
        return this.data;
    }

    public String getDateTime() {
        return this.dateTime;
    }

}
