package com.dayman.poiot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.ArrayList;

import adapters.SensorData;
import adapters.SensorDataListAdapter;
import backend.JParser;
import backend.JSigfox;

public class MessagesActivity extends AppCompatActivity {

    private String deviceID;

    private JSigfox sigfox;
    private JParser parser;

    private ArrayList<SensorData> mSensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        String loginID = getIntent().getExtras().getString("loginID");
        String password = getIntent().getExtras().getString("password");

        mSensorData = new ArrayList<>();

        sigfox = new JSigfox(loginID, password);
        parser = new JParser();

        try {
            deviceID = getDeviceID();
            getMessages(deviceID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView msgsList = (ListView) findViewById(R.id.messages_listview);

        SensorDataListAdapter sdla = new SensorDataListAdapter(this, R.layout.sensor_data_adapter_view, mSensorData);
        msgsList.setAdapter(sdla);

        AccountHeaderBuilder ahb = new AccountHeaderBuilder().withActivity(this).withSelectionFirstLine("Pycom Companion").withSelectionSecondLine("View & Manage SiPy Devices");

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar((Toolbar) findViewById(R.id.main_toolbar))
                .withAccountHeader(ahb.build())
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(0).withName("Data List"),
                        new PrimaryDrawerItem().withIdentifier(1).withName("Graphs"),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(2).withName("About").withIcon(R.drawable.ic_info),
                        new PrimaryDrawerItem().withIdentifier(3).withName("Settings").withIcon(R.drawable.ic_settings)
                ).build();
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