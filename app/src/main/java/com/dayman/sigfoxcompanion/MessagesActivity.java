package com.dayman.sigfoxcompanion;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.dayman.sigfoxcompanion.adapters.SensorData;
import com.dayman.sigfoxcompanion.adapters.SensorDataListAdapter;
import com.dayman.sigfoxcompanion.backend.JParser;
import com.dayman.sigfoxcompanion.backend.JSigfox;
import com.dayman.sigfoxcompanion.backend.Util;
import com.dayman.sigfoxcompanion.enums.Tags;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    // TODO ABILITY TO SEARCH ITEMS, MAYBE IN FILTER SCREEN
    // TODO MAYBE IMPLEMENT LISTENERS INSTEAD OF USING INNER CLASSES?

    private JSigfox sigfox;
    private JParser parser;

    private String[][] graphData;

    private ArrayList<SensorData> mSensorData;

    private SwipeRefreshLayout srl;

    private int year_x, month_x, day_x;
    private int DIALOG_ID_START = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        tb.setTitle(R.string.messages_activity_title);

        final String loginID = getIntent().getExtras().getString("loginID");
        final String password = getIntent().getExtras().getString("password");
        final String name = getIntent().getExtras().getString("name");

        mSensorData = new ArrayList<>();

        sigfox = new JSigfox(loginID, password);
        parser = new JParser();

        srl = (SwipeRefreshLayout) findViewById(R.id.messages_swipe_refresh_layout);
        srl.setOnRefreshListener(this);

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
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {
                final CharSequence[] items = { "Copy Data", "Copy Date and Time" };

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
//                        new PrimaryDrawerItem().withIdentifier(++count).withTag(Tags.DEVICE_INFO).withName(R.string.drawer_device_info_text).withIcon(R.drawable.ic_device_info),
                        new DividerDrawerItem().withIdentifier(++count),
                        new PrimaryDrawerItem().withIdentifier(++count).withTag(Tags.ABOUT).withName(R.string.about_activity_title).withIcon(R.drawable.ic_info)
//                        new PrimaryDrawerItem().withIdentifier(++count).withTag(Tags.SETTINGS).withName(R.string.settings_activity_title).withIcon(R.drawable.ic_settings)
                ).withSelectedItem(0).build();

        d.addStickyFooterItem(new PrimaryDrawerItem().withIdentifier(++count).withName(name).withTag(Tags.BLANK).withSelectable(false).withEnabled(false));

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
                    Intent intent = new Intent(view.getContext(), DatePickerActivity.class);

                    intent.putExtra("LoginID", getIntent().getExtras().getString("loginID"));
                    intent.putExtra("Name", getIntent().getExtras().getString("name"));
                    intent.putExtra("Password", getIntent().getExtras().getString("password"));

                    startActivity(intent);

                    d.setSelection(0);
                }

                d.closeDrawer();

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

        String endDate = getIntent().getExtras().getString("endDate");

        if (endDate == null) {
            for (int i = 0; i < mMsgsArray.length; i++) {
                mMsgData.add(i, mMsgsArray[i].split(",")[0]);
                mMsgDateTime.add(i, mMsgsArray[i].split(",")[1]);

                mSensorData.add(new SensorData(mMsgData.get(i), mMsgDateTime.get(i)));
            }
        } else {
            for (int i = 0; i < mMsgsArray.length; i++) {
                int j = 0;
                SimpleDateFormat dF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat dF1 = new SimpleDateFormat("dd/MM/yyyy");
                Date date = dF2.parse(mMsgsArray[i].split(",")[1]);
                long epochDate = date.getTime();
                long epochStartDate = dF1.parse(getIntent().getExtras().getString("startDate")).getTime();
                long epochEndDate = dF1.parse(getIntent().getExtras().getString("endDate")).getTime() + 86400000;

                if (epochDate > epochStartDate && epochDate < epochEndDate) {
                    mMsgData.add(j, mMsgsArray[i].split(",")[0].split(" ")[0]);
                    mMsgDateTime.add(j, mMsgsArray[i].split(",")[1]);

                    mSensorData.add(new SensorData(mMsgData.get(j), mMsgDateTime.get(j)));
                    j++;
                }
            }
        }

        graphData = new String[mMsgData.size()][2];

        for(int i = 0; i < mMsgData.size(); i++){
            graphData[i][0] = mMsgDateTime.get(i);
            graphData[i][1] = mMsgData.get(i);
        }
    }

    @Override
    public void onRefresh() {
        try {
            new DownloadMessages().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        srl.setRefreshing(false);
    }

    private class DownloadMessages extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(MessagesActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(MessagesActivity.this, "Updated Items", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getMessages(getDeviceID());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
