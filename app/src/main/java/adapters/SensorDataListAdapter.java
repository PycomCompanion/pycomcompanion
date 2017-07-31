package adapters;

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
        String data = getItem(position).getData();
        String dateTime = getItem(position).getDateTime();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView dataTv = convertView.findViewById(R.id.data_text_view);
        TextView dateTimeTv = convertView.findViewById(R.id.date_time_textview);

        dataTv.setText(data);
        dateTimeTv.setText(dateTime);

        return convertView;
    }
}
