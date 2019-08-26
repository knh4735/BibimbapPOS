package com.example.nagion.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.NumberFormat;

/**
 * Created by Nagion on 2016. 7. 24..
 */
public class DayHistoryListRow extends TableRow {
    public DayHistoryListRow(Context context){
        super(context);
    }

    public DayHistoryListRow(Context context, AttributeSet as){
        super(context, as);
    }

    public DayHistoryListRow(Context context, JSONObject menu){
        super(context);

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.day_history_list_row, this, false);
        addView(v);

        TextView nameTv = (TextView)findViewById(R.id.name);
        TextView priceTv = (TextView)findViewById(R.id.price);
        TextView numTv = (TextView)findViewById(R.id.num);

        try{
            nameTv.setText(menu.getString("menu"));
            priceTv.setText(NumberFormat.getInstance().format(menu.getInt("num")*menu.getInt("price")));
            numTv.setText(String.valueOf(menu.getInt("num")));
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public DayHistoryListRow(Context context, String name, int price){
        super(context);

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.day_history_list_row, this, false);
        addView(v);

        TextView nameTv = (TextView)findViewById(R.id.name);
        TextView priceTv = (TextView)findViewById(R.id.price);

        nameTv.setText(name);
        priceTv.setText(NumberFormat.getInstance().format(price));
    }

}
