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

import java.util.List;

/**
 * Created by 25143j on 26/07/2017.
 */

public class ApiKeyAdapter extends ArrayAdapter<ApiKey> {

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
        String name = getItem(position).getName();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView apiKeyName = convertView.findViewById(R.id.api_key_name_textview);

        apiKeyName.setText(name);

        return convertView;
    }
}
