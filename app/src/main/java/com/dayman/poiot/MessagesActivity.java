package com.dayman.poiot;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dayman.poiot.adapters.SensorData;
import com.dayman.poiot.adapters.SensorDataListAdapter;
import com.dayman.poiot.backend.JParser;
import com.dayman.poiot.backend.JSigfox;
import com.dayman.poiot.backend.Util;
import com.dayman.poiot.enums.Tags;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    // TODO ABILITY TO SEARCH ITEMS, MAYBE IN FILTER SCREEN
    // TODO MAYBE IMPLEMENT LISTENERS INSTEAD OF USING INNER CLASSES?

    private JSigfox sigfox;
    private JParser parser;

    private String[][] graphData;

    private ArrayList<SensorData> mSensorData;

    private SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        tb.setTitle(R.string.messages_activity_title);

        String loginID = getIntent().getExtras().getString("loginID");
        String password = getIntent().getExtras().getString("password");
        String name = getIntent().getExtras().getString("name");

        mSensorData = new ArrayList<>();

        sigfox = new JSigfox(loginID, password);
        parser = new JParser();

        srl = (SwipeRefreshLayout) findViewById(R.id.messages_swipe_refresh_layout);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Preparation for swipe to refresh layout
                Toast.makeText(MessagesActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();

                try {
                    getMessages(getDeviceID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            String deviceID = getDeviceID();
            getMessages(deviceID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView msgsList = (ListView) findViewById(R.id.messages_listview);

        SensorDataListAdapter sdla = new SensorDataListAdapter(this, R.layout.sensor_data_adapter_view, mSensorData);
        msgsList.setAdapter(sdla);

        msgsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final CharSequence[] items = { "Copy Data", "Copy Date and Time" };

                final int index = i;

                AlertDialog.Builder builder = new AlertDialog.Builder(MessagesActivity.this);
                builder.setTitle("" + mSensorData.get(index).getData());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Copy Data"))
                            Util.copyToClipboard((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE), "Test", mSensorData.get(index).getData());
                        else if (items[i].equals("Copy Date And Time"))
                            Util.copyToClipboard((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE), "Test", mSensorData.get(index).getDateTime());
                        else
                            Log.d("MessagesActivity.java", "Unkown menu option selected");

                        Toast.makeText(MessagesActivity.this, R.string.text_copied_successfully, Toast.LENGTH_SHORT).show();
                    }
                }).create();

                builder.show();

                return true;
            }
        });

        AccountHeaderBuilder ahb = new AccountHeaderBuilder().withActivity(this).withSelectionFirstLine("Pycom Companion").withSelectionSecondLine("View & Manage SiPy Devices");

        int count = 0;

        final Drawer d = new DrawerBuilder()
                .withActivity(this)
                .withToolbar((Toolbar) findViewById(R.id.main_toolbar))
                .withAccountHeader(ahb.build())
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(count).withTag(Tags.DATA_LIST).withName(R.string.drawer_data_list_text).withIcon(R.drawable.ic_data_list),
                        new PrimaryDrawerItem().withIdentifier(++count).withTag(Tags.GRAPHS).withName(R.string.graph_activity_title).withIcon(R.drawable.ic_graph),
                        new PrimaryDrawerItem().withIdentifier(++count).withTag(Tags.FILTER).withName(R.string.drawer_filter_text).withIcon(R.drawable.ic_filter),
                        new PrimaryDrawerItem().withIdentifier(++count).withTag(Tags.DEVICE_INFO).withName(R.string.drawer_device_info_text).withIcon(R.drawable.ic_device_info),
                        new DividerDrawerItem().withIdentifier(++count),
                        new PrimaryDrawerItem().withIdentifier(++count).withTag(Tags.ABOUT).withName(R.string.about_activity_title).withIcon(R.drawable.ic_info),
                        new PrimaryDrawerItem().withIdentifier(++count).withTag(Tags.SETTINGS).withName(R.string.settings_activity_title).withIcon(R.drawable.ic_settings)
                ).withSelectedItem(0).build();

        d.addStickyFooterItem(new PrimaryDrawerItem().withIdentifier(++count).withName(name).withTag(Tags.BLANK).withSelectable(false));

        d.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (drawerItem.getTag().equals(Tags.GRAPHS)) {
                    Intent intent = new Intent(view.getContext(), GraphActivity.class);

                    intent.putExtra("data", graphData);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("list", graphData);
                    intent.putExtras(mBundle);
                    startActivity(intent);

                    d.setSelection(0);
                } else if (drawerItem.getTag().equals(Tags.ABOUT)) {
                    Intent intent = new Intent(view.getContext(), AboutActivity.class);

                    startActivity(intent);

                    d.setSelection(0);
                } else if (drawerItem.getTag().equals(Tags.FILTER)) {
                    d.setSelection(0);
                } else if (drawerItem.getTag().equals(Tags.DEVICE_INFO)) {
                    d.setSelection(0);
                }

                return true;
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

        graphData = new String[mMsgData.size()][2];

        for(int i = 0; i < mMsgData.size(); i++){
            graphData[i][0] = mMsgDateTime.get(i);
            graphData[i][1] = mMsgData.get(i);
        }
    }
}
