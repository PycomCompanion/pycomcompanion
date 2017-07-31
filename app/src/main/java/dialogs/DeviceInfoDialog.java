package dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.dayman.poiot.R;

/**
 * Created by 25143j on 28/07/2017.
 */

public class DeviceInfoDialog extends Dialog {

    private String title;

    public DeviceInfoDialog(final Context context, String title) {
        super(context, R.style.AppTheme_NoActionBar);

        this.setContentView(R.layout.api_key_dialog);

        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(title);

    }
}
