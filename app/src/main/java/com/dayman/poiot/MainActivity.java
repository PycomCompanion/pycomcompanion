package com.dayman.poiot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.dayman.poiot.adapters.ApiKey;
import com.dayman.poiot.adapters.ApiKeyAdapter;
import com.dayman.poiot.backend.APIManager;
import com.dayman.poiot.interfaces.OnDeviceInfoDialogClickListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private APIManager mApiManager;

    public static String VERSION = "0.0.3-A";

    // TODO WARN ABOUT DUPLICATES
    // TODO DISPLAY LINEARLAYOUT WITH TEXTVIEW IF THERE ARE NO SIGFOX ACCOUNTS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        tb.setTitle(R.string.main_activity_title);

        final ArrayList<ApiKey> mApiKeys = new ArrayList<>();
        final ApiKeyAdapter mApiKeyAdapter = new ApiKeyAdapter(this, R.layout.api_key_layout, mApiKeys);

        try {
            mApiManager = new APIManager(getApplicationContext());

            ArrayList<String> creds = mApiManager.getCreds();
            for (String c : creds)
                Log.d("MainActivity.java", "Cred is: " + c);

            if (!(creds.size() <= 0)) {
                for (String c : creds) {
                    String[] split_creds = c.split(",");
                    ApiKey key = new ApiKey(split_creds[2], split_creds[0], split_creds[1]);
                    mApiKeys.add(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ListView mApiKeyListView = (ListView) findViewById(R.id.api_key_list_view);
        mApiKeyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);

                intent.putExtra("loginID", mApiKeys.get(i).getLoginID());
                intent.putExtra("password", mApiKeys.get(i).getPassword());
                intent.putExtra("name", mApiKeys.get(i).getName());

                startActivity(intent);
            }
        });

        mApiKeyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                final CharSequence[] items = { "Edit", "Delete" };

                final int index = i;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("" + mApiKeys.get(i).getName());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO ITEM CLICK OPTIONS WITH items[i]

                        if (items[i].equals("Edit")) {
                            // Edit dialog
                            LayoutInflater dialoginflater = LayoutInflater.from(MainActivity.this);
                            final View dialogView = dialoginflater.inflate(R.layout.api_key_dialog, null);

                            ((EditText) dialogView.findViewById(R.id.loginid_edittext)).setText(mApiKeys.get(index).getLoginID());
                            ((EditText) dialogView.findViewById(R.id.password_edittext)).setText(mApiKeys.get(index).getPassword());
                            ((EditText) dialogView.findViewById(R.id.name_edittext)).setText(mApiKeys.get(index).getName());

                            createApiKeyDialog(MainActivity.this, dialogView, "Edit " + mApiKeys.get(i).getName(), new OnDeviceInfoDialogClickListener() {
                                @Override
                                public void onPositiveClicked(View dialogView) {
                                    String apiKey = ((EditText) dialogView.findViewById(R.id.loginid_edittext)).getText().toString();
                                    String apiPass = ((EditText) dialogView.findViewById(R.id.password_edittext)).getText().toString();
                                    String apiName = ((EditText) dialogView.findViewById(R.id.name_edittext)).getText().toString();

                                    if (!(apiKey.equals("") || apiPass.equals("") || apiName.equals(""))) {
                                        try {
                                            mApiManager.editCreds(index, apiKey, apiPass, apiName);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        mApiKeys.get(index).setLoginID(apiKey);
                                        mApiKeys.get(index).setPassword(apiPass);
                                        mApiKeys.get(index).setName(apiName);

                                        mApiKeyAdapter.notifyDataSetChanged();

                                        Snackbar.make(findViewById(R.id.content_parent), "Created SigFox API Key (" + apiName + ")", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(findViewById(R.id.content_parent), "Error: Blank Fields", Snackbar.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onNegativeClicked() {

                                }
                            });

                        } else if (items[i].equals("Delete")) {
                            mApiKeys.remove(index);
                            mApiKeyAdapter.notifyDataSetChanged();

                            try {
                                mApiManager.deleteCreds(index);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("MainActivity.java", "Unknown Option Selected");
                        }
                    }
                });
                builder.show();

                return true;
            }
        });
        mApiKeyListView.setAdapter(mApiKeyAdapter);

        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.add_api_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater dialoginflater = LayoutInflater.from(MainActivity.this);
                final View dialogView = dialoginflater.inflate(R.layout.api_key_dialog, null);

                createApiKeyDialog(MainActivity.this, dialogView, "Add SigFox API Key", new OnDeviceInfoDialogClickListener() {
                    @Override
                    public void onPositiveClicked(View dialogView) {
                        String apiKey = ((EditText) dialogView.findViewById(R.id.loginid_edittext)).getText().toString();
                        String apiPass = ((EditText) dialogView.findViewById(R.id.password_edittext)).getText().toString();
                        String apiName = ((EditText) dialogView.findViewById(R.id.name_edittext)).getText().toString();

                        if (!(apiKey.equals("") || apiPass.equals("") || apiName.equals(""))) {
                            ApiKey key = new ApiKey(apiName, apiKey, apiPass);
                            mApiKeys.add(key);

                            try {
                                mApiManager.writeCreds(apiKey, apiPass, apiName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Snackbar.make(findViewById(R.id.content_parent), "Created SigFox API Key (" + apiName + ")", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(R.id.content_parent), "Error: Blank Fields", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNegativeClicked() {
                        Snackbar.make(findViewById(R.id.content_parent), "Cancelled adding SigFox API Key", Snackbar.LENGTH_SHORT).show();
                    }
                });

                mApiKeyAdapter.notifyDataSetChanged();
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void createApiKeyDialog(final Context context, final View dialogView, String title, final OnDeviceInfoDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        AlertDialog dialog = builder.setView(dialogView).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onPositiveClicked(dialogView);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onNegativeClicked();
            }
        }).setTitle(title).create();

        dialog.show();
    }
}
