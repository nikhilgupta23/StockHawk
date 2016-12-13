package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Nikhil Gupta on 12-12-2016.
 */

public class StockDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURSOR_LOADER_ID = 0;
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stock);
        Intent intent = getIntent();
        symbol = intent.getStringExtra("symbol");
        Bundle args = new Bundle();
        args.putString("symbol", intent.getStringExtra("symbol"));
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.Quote.URI,
                new String[]{ Contract.Quote.COLUMN_HISTORY},
                Contract.Quote.COLUMN_SYMBOL + " = ?",
                new String[]{args.getString("symbol")},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String history = "";
        for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            // The Cursor is now set to the right position
            history = data.getString(0);
        }

        StringTokenizer st = new StringTokenizer(history, ",\n");
        List<Entry> entries = new ArrayList<Entry>();
        int count=0;
        float date, price;
        while(st.hasMoreTokens())
        {
            date = Float.parseFloat(st.nextToken());
            price = Float.parseFloat(st.nextToken());
            entries.add(new Entry(count, price));
            count++;
        }

        LineChart chart = (LineChart) findViewById(R.id.chart);

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.stock_prices)); // add entries to dataset
        dataSet.setColor(R.color.colorPrimary);
        dataSet.setValueTextColor(R.color.colorPrimaryDark); // styling, ...
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(R.color.colorPrimary);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTextColor(R.color.colorPrimary);
        yAxis = chart.getAxisRight();
        yAxis.setTextColor(R.color.colorPrimary);

        chart.setBackgroundColor(Color.WHITE);

        Description desc = new Description();
        desc.setText(symbol + " " + getString(R.string.chart_desc));
        desc.setTextColor(R.color.colorPrimaryDark);
        chart.setDescription(desc);
        chart.invalidate();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
