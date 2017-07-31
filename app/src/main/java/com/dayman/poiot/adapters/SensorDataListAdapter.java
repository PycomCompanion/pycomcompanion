package com.dayman.poiot.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dayman.poiot.R;

import java.util.ArrayList;

/**
 * Created by 25143j on 21/07/2017.
 */

public class SensorDataListAdapter extends ArrayAdapter<SensorData> {

    private static class SensorDataViewHolder {
        TextView dataTextView;
        TextView dateTimeTextView;
    }

    private Context mContext;
    private int mResource;

    public SensorDataListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<SensorData> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SensorDataViewHolder holder;

        LayoutInflater mInflater = LayoutInflater.from(mContext);

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, null);
            holder = new SensorDataViewHolder();
            holder.dataTextView = convertView.findViewById(R.id.data_text_view);
            holder.dateTimeTextView = convertView.findViewById(R.id.date_time_textview);
            convertView.setTag(holder);
        } else {
            holder = (SensorDataViewHolder) convertView.getTag();
        }

        SensorData sd = getItem(position);

        holder.dataTextView.setText(sd.getData());
        holder.dateTimeTextView.setText(sd.getDateTime());


        return convertView;
    }
}
