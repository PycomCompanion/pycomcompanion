package com.dayman.sigfoxcompanion.adapters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 25143j on 21/07/2017.
 */

public class SensorData implements Parcelable {

    private String data, dateTime;

    public SensorData(String data, String dateTime) {
        this.data = data;
        this.dateTime = dateTime;
    }

    protected SensorData(Parcel in) {
        this.data = in.readString();
        this.dateTime = in.readString();
    }

    public static final Creator<SensorData> CREATOR = new Creator<SensorData>() {
        @Override
        public SensorData createFromParcel(Parcel in) {
            return new SensorData(in);
        }

        @Override
        public SensorData[] newArray(int size) {
            return new SensorData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(data);
        parcel.writeString(dateTime);
    }
}
