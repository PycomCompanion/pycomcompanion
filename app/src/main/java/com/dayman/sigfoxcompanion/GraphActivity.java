package com.dayman.sigfoxcompanion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        tb.setTitle(R.string.graph_activity_title);

        setSupportActionBar(tb);

        LineChart chart = (LineChart) findViewById(R.id.data_line_chart);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        String[][] graphData = (String[][]) bundle.getSerializable("list");

        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat dF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime=0;
        try {
            startTime  = dF.parse(graphData[graphData.length-1][0]).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Turn your data into Entry objects
        for(int i = 0; i < graphData.length; i++) {

            // The mask 'a' value in the Mask represents AM / PM - h means hours in AM/PM mode
            // parsing the String into a Date using the mask

            long epochTime = 0;
            try {
                Date date = dF.parse(graphData[graphData.length -1 -i][0]);

                epochTime = date.getTime();

                BigDecimal bd = new BigDecimal(epochTime);

                bd = bd.round(new MathContext(7));

                epochTime = bd.longValue();
                epochTime -= startTime;
                Log.e("date", Float.toString(epochTime));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Date.getTime() method gives you the Long with milliseconds since Epoch.
            try {
                entries.add(new Entry(epochTime, Float.parseFloat(graphData[graphData.length -1 -i][1])));
            }
            catch (NumberFormatException e){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(graphData[graphData.length -1 -i][1])
                        .setTitle("Number Format Error")
                        .setPositiveButton("SKIP", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(GraphActivity.this, MainActivity.class);

                                GraphActivity.this.startActivity(myIntent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}
