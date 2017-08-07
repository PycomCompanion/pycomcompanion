package com.dayman.poiot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dayman.poiot.adapters.SensorData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity_ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        ArrayList<SensorData> data = getIntent().getParcelableArrayListExtra("data");

        LineChart lc = (LineChart) findViewById(R.id.data_line_chart);

        List<Entry> entries = new ArrayList<>();

//        for (SensorData d : data)
//            Log.d("GraphActivity.java", d.getData() + "");

        // Fix later, maybe preference for data delimiter
        for (SensorData d : data) {
            String[] splitData = d.getData().split("-");
            Log.d("GraphActivity.java", splitData[0] + "," + splitData[1]);

            entries.add(new Entry(Float.parseFloat(splitData[0]), Float.parseFloat(splitData[1])));

//            entries.add(new Entry(10, 10));
//            entries.add(new Entry(10, 20));
//            entries.add(new Entry(10, 30));
//            entries.add(new Entry(10, 40));
//            entries.add(new Entry(10, 50));
        }

        LineDataSet lds = new LineDataSet(entries, "Temperatures");
        LineData ld = new LineData(lds);

        lc.setData(ld);
        lc.invalidate();
    }
}
