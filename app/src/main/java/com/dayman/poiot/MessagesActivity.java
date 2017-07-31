package com.dayman.poiot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.dayman.poiot.adapters.SensorData;
import com.dayman.poiot.adapters.SensorDataListAdapter;
import com.dayman.poiot.backend.JParser;
import com.dayman.poiot.backend.JSigfox;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    private JSigfox sigfox;
    private JParser parser;

    private ArrayList<SensorData> mSensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        tb.setTitle(R.string.messages_activity_title);

        String loginID = getIntent().getExtras().getString("loginID");
        String password = getIntent().getExtras().getString("password");

        mSensorData = new ArrayList<>();

        sigfox = new JSigfox(loginID, password);
        parser = new JParser();

        try {
            String deviceID = getDeviceID();
            getMessages(deviceID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView msgsList = (ListView) findViewById(R.id.messages_listview);

        SensorDataListAdapter sdla = new SensorDataListAdapter(this, R.layout.sensor_data_adapter_view, mSensorData);
        msgsList.setAdapter(sdla);

        AccountHeaderBuilder ahb = new AccountHeaderBuilder().withActivity(this).withSelectionFirstLine("Pycom Companion").withSelectionSecondLine("View & Manage SiPy Devices");

        int count = 0;

        final Drawer d = new DrawerBuilder()
                .withActivity(this)
                .withToolbar((Toolbar) findViewById(R.id.main_toolbar))
                .withAccountHeader(ahb.build())
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(count).withName("Data List"),
                        new PrimaryDrawerItem().withIdentifier(++count).withName("Graphs"),
                        new PrimaryDrawerItem().withIdentifier(++count).withName("Device Info"),
                        new DividerDrawerItem().withIdentifier(++count),
                        new PrimaryDrawerItem().withIdentifier(++count).withName("About").withTag("Info").withIcon(R.drawable.ic_info),
                        new PrimaryDrawerItem().withIdentifier(++count).withName("Settings").withIcon(R.drawable.ic_settings)
                ).withSelectedItem(0).build();

        d.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (drawerItem.getTag() == "Info") {
                    Intent intent = new Intent(view.getContext(), AboutActivity.class);

                    startActivity(intent);

                    d.setSelection(0);
                }

                return false;
            }
        });
    }

    private String getDeviceID() throws Exception {
        String deviceIDparameter = "devicetypes";

        String json = sigfox.GET(deviceIDparameter);

        // Returning the first because there can only be one attached to each API key

        return parser.getValues(json, "id")[0];
    }

    private void getMessages(String devID) throws Exception {
        String messagesparameter = "devicetypes/" + devID + "/messages";
        String[] mMsgsArray = parser.parseMessageData(sigfox.GET(messagesparameter));

        mSensorData.clear();

        ArrayList<String> mMsgData = new ArrayList<>();
        ArrayList<String> mMsgDateTime = new ArrayList<>();

        for (int i = 0; i < mMsgsArray.length; i++) {
            mMsgData.add(i, mMsgsArray[i].split(",")[0]);
            mMsgDateTime.add(i, mMsgsArray[i].split(",")[1]);

            mSensorData.add(new SensorData(mMsgData.get(i), mMsgDateTime.get(i)));
        }
    }
}
