package com.dayman.sigfoxcompanion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.dayman.sigfoxcompanion.adapters.ApiKey;
import com.dayman.sigfoxcompanion.adapters.ApiKeyAdapter;
import com.dayman.sigfoxcompanion.backend.APIManager;
import com.dayman.sigfoxcompanion.backend.Util;
import com.dayman.sigfoxcompanion.enums.Tags;
import com.dayman.sigfoxcompanion.interfaces.OnDeviceInfoDialogClickListener;
import com.kunzisoft.androidclearchroma.ChromaDialog;
import com.kunzisoft.androidclearchroma.IndicatorMode;
import com.kunzisoft.androidclearchroma.colormode.ColorMode;
import com.kunzisoft.androidclearchroma.listener.OnColorSelectedListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private APIManager mApiManager;

    private ArrayList<ApiKey> mApiKeys;
    private ApiKeyAdapter mApiKeyAdapter;

    public static String VERSION = "0.0.6-A";

    // TODO WARN ABOUT DUPLICATES
    // TODO DISPLAY LINEARLAYOUT WITH TEXTVIEW IF THERE ARE NO SIGFOX ACCOUNTS
    // TODO SPINNER IN ADD DEVICE TO SELECT DEVICE TYPE FOR DATA GRAPHS
    // TODO ABILITY TO CREATE GRAPHS
    // TODO DELETE ALL BUTTON IN TOOLBAR
    // TODO SAVE SIGFOX DEVICE MESSAGES TO TEXT FILE (E.G. JSON, CSV, PLAIN TEXT, ETC)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO DO THIS IN ALL ACTIVITIES
        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        tb.setTitle(R.string.main_activity_title);

        setSupportActionBar(tb);

        mApiKeys = new ArrayList<>();
        mApiKeyAdapter = new ApiKeyAdapter(this, R.layout.api_key_layout, mApiKeys);

        try {
            mApiManager = new APIManager(getApplicationContext());

            ArrayList<String> creds = mApiManager.getCreds();

            if (!(creds.size() <= 0)) {
                for (String c : creds) {
                    String[] split_creds = c.split(",");

                    ApiKey key = new ApiKey(split_creds[2], split_creds[0], split_creds[1], split_creds[3]);
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
                final CharSequence[] items = { "Info", "Edit", "Delete" };

                final int index = i;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("" + mApiKeys.get(i).getName());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Convert to tags maybe?
                        if (items[i].equals("Edit")) {
                            // Edit dialog
                            final LayoutInflater dialoginflater = LayoutInflater.from(MainActivity.this);
                            final View dialogView = dialoginflater.inflate(R.layout.api_key_dialog, null);

                            ((EditText) dialogView.findViewById(R.id.loginid_edittext)).setText(mApiKeys.get(index).getLoginID());
                            ((EditText) dialogView.findViewById(R.id.password_edittext)).setText(mApiKeys.get(index).getPassword());
                            ((EditText) dialogView.findViewById(R.id.name_edittext)).setText(mApiKeys.get(index).getName());
                            final Button colourButton = dialogView.findViewById(R.id.sigfox_device_colour_button);

                            // If we haven't set a colour don't change it
                            // Otherwise it would get set to Tags.BLANK
                            if (!(colourButton.getTag() == null))
                                if (!colourButton.getTag().equals(Tags.BLANK))
                                    colourButton.setTag(Integer.parseInt(mApiKeys.get(index).getColour()));

                            dialogView.findViewById(R.id.sigfox_device_colour_button).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    ChromaDialog b = new ChromaDialog.Builder().initialColor(Integer.parseInt(mApiKeys.get(index).getColour())).colorMode(ColorMode.ARGB)
                                            .indicatorMode(IndicatorMode.HEX).create();

                                    b.setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onPositiveButtonClick(@ColorInt int color) {
                                            colourButton.setTag(color);
                                        }

                                        @Override
                                        public void onNegativeButtonClick(@ColorInt int color) {

                                        }
                                    });
                                    b.show(getSupportFragmentManager(), "ChromaDialog");
                                }
                            });

                            createApiKeyDialog(MainActivity.this, dialogView, "Edit " + mApiKeys.get(index).getName(), new OnDeviceInfoDialogClickListener() {
                                @Override
                                public void onPositiveClicked(View dialogView) {
                                    outputCreds(Tags.EDIT_MODE, index, dialogView);
                                }

                                @Override
                                public void onNegativeClicked() {

                                }
                            });

                        } else if (items[i].equals("Delete")) {
                            String name = mApiKeys.get(index).getName();

                            mApiKeys.remove(index);
                            mApiKeyAdapter.notifyDataSetChanged();

                            try {
                                mApiManager.deleteCreds(index);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Snackbar.make(findViewById(R.id.content_parent), "Deleted " + name, Snackbar.LENGTH_SHORT).show();
                        } else if (items[i].equals("Info")) {
                            // TODO SHOW DEVICE INFO HERE
                            // CREATION DATE AND TIME?
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

        final FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.add_api_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater dialoginflater = LayoutInflater.from(MainActivity.this);
                final View dialogView = dialoginflater.inflate(R.layout.api_key_dialog, null);

                final Button colourButton = dialogView.findViewById(R.id.sigfox_device_colour_button);

                colourButton.setTag(Tags.BLANK);

                colourButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChromaDialog b = new ChromaDialog.Builder().initialColor(Color.GRAY).colorMode(ColorMode.ARGB)
                                .indicatorMode(IndicatorMode.HEX).create();

                        b.setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onPositiveButtonClick(@ColorInt int color) {
                                colourButton.setTag(color);
                            }

                            @Override
                            public void onNegativeButtonClick(@ColorInt int color) {

                            }
                        });
                        b.show(getSupportFragmentManager(), "ChromaDialog");
                    }
                });

                AlertDialog d = createApiKeyDialog(MainActivity.this, dialogView, "Add Sigfox API Key", new OnDeviceInfoDialogClickListener() {
                    @Override
                    public void onPositiveClicked(View dialogView) {
                        outputCreds(Tags.WRITE_MODE, 0, dialogView);
                    }

                    @Override
                    public void onNegativeClicked() {
                        Snackbar.make(findViewById(R.id.content_parent), "Cancelled adding Sigfox API Key", Snackbar.LENGTH_SHORT).show();
                    }
                });

                Button b = (Button) d.findViewById(R.id.sigfox_device_colour_button);

                // Make this it's own dedicated listener and share it between the dialogs
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChromaDialog d = new ChromaDialog.Builder().initialColor(Util.fetchPrimaryColorDark(MainActivity.this)).colorMode(ColorMode.ARGB)
                                .indicatorMode(IndicatorMode.HEX).create();

                        d.setOnColorSelectedListener(new OnColorSelectedListener() {

                            @Override
                            public void onPositiveButtonClick(@ColorInt int color) {
                                colourButton.setTag(color);
                            }

                            @Override
                            public void onNegativeButtonClick(@ColorInt int color) {

                            }
                        });

                        d.show(getSupportFragmentManager(), "ChromaDialog");
                    }
                });
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete_all_devices:
                // Naming 101
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);

                adb.setTitle("Delete All Sigfox Devices");
                adb.setMessage("Are you sure you wish to delete ALL Sigfox devices? This action cannot be undone.");
                adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            mApiManager.clearCreds();
                            mApiKeys.clear();
                            mApiKeyAdapter.notifyDataSetChanged();

                            Snackbar.make(findViewById(R.id.content_parent), "Deleted All Devices", Snackbar.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog ad = adb.create();
                ad.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog createApiKeyDialog(final Context context, final View dialogView, String title, final OnDeviceInfoDialogClickListener listener) {
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

        return dialog;
    }

    private void outputCreds(int mode, int index, View dialogView) {
        String apiKey = ((EditText) dialogView.findViewById(R.id.loginid_edittext)).getText().toString();
        String apiPass = ((EditText) dialogView.findViewById(R.id.password_edittext)).getText().toString();
        String apiName = ((EditText) dialogView.findViewById(R.id.name_edittext)).getText().toString();

        String colour = dialogView.findViewById(R.id.sigfox_device_colour_button).getTag().toString();

        if (!(apiKey.equals("") || apiPass.equals("") || apiName.equals(""))) {
            try {
                if (mode == Tags.WRITE_MODE) {
                    ApiKey key = new ApiKey(apiName, apiKey, apiPass, colour);
                    mApiKeys.add(key);

                    mApiManager.writeCreds(apiKey, apiPass, apiName, colour);

                    Snackbar.make(findViewById(R.id.content_parent), "Created Sigfox API Key (" + apiName + ")", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (!(dialogView.findViewById(R.id.sigfox_device_colour_button).getTag() == null)) {
                        colour = dialogView.findViewById(R.id.sigfox_device_colour_button).getTag().toString();
                    }

                    mApiManager.editCreds(index, apiKey, apiPass, apiName, colour);

                    mApiKeys.get(index).setLoginID(apiKey);
                    mApiKeys.get(index).setPassword(apiPass);
                    mApiKeys.get(index).setName(apiName);
                    mApiKeys.get(index).setColour(colour);

                    Snackbar.make(findViewById(R.id.content_parent), "Edited Sigfox API Key (" + apiName + ")", Snackbar.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mApiKeyAdapter.notifyDataSetChanged();
        } else {
            Snackbar.make(findViewById(R.id.content_parent), "Error: Blank Fields", Snackbar.LENGTH_SHORT).show();
        }
    }
}
