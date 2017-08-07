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
import com.dayman.poiot.enums.Tags;

import java.util.List;

/**
 * Created by 25143j on 26/07/2017.
 */

public class ApiKeyAdapter extends ArrayAdapter<ApiKey> {

    private static class ApiKeyViewHolder {
        TextView apiKeyName;
    }

    private Context mContext;
    private int mResource;

    public ApiKeyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ApiKey> objects) {
        super(context, resource, objects);

        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ApiKeyViewHolder holder;

        LayoutInflater mInflater = LayoutInflater.from(mContext);

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, null);
            holder = new ApiKeyViewHolder();
            holder.apiKeyName = convertView.findViewById(R.id.api_key_name_textview);
            convertView.setTag(holder);
        } else {
            holder = (ApiKeyViewHolder) convertView.getTag();
        }

        String name = getItem(position).getName();
        String colour = getItem(position).getColour();

        holder.apiKeyName.setText(name);

        // If we haven't set a colour, just keep default background one
        if (!colour.equals(Tags.NO_COLOUR + ""))
            holder.apiKeyName.setBackgroundColor(Integer.parseInt(colour));

        return convertView;
    }
}
