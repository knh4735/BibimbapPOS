package com.example.nagion.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nagion on 2016. 7. 23..
 */
public class DayHistoryListLayout extends LinearLayout{

    int no;
    boolean isCash;
    int totalPrice = 0;
    final String time;
    JSONArray ListComponent;
    boolean isSelected = false;
    public static ArrayList<DayHistoryListLayout> menuDayHistoryListLayout = new ArrayList<>();
    public static int totalCash = 0;
    public static int totalCard = 0;
    public static int total = 0;
    LinearLayout ll;

    public DayHistoryListLayout(Context context){
        super(context);
        time ="";
        initView();
    }
    public DayHistoryListLayout(Context context, AttributeSet as){
        super(context, as);
        time = "";
        initView();
    }

    public DayHistoryListLayout(Context context, int no, boolean isCash, String time, JSONArray ListComponent){
        super(context);
        this.no = no;
        this.isCash = isCash;
        this.ListComponent = ListComponent;
        this.time = time;
        initView();
    }

    public void initView(){
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.day_history_list, this, false);
        addView(v);

        TextView noTv = (TextView) findViewById(R.id.no);
        TextView timeTv = (TextView) findViewById(R.id.time);

        TableLayout listTable = (TableLayout)findViewById(R.id.listTable);
        noTv.setText(String.valueOf(no));
        timeTv.setText(time);

        JSONArray a = new JSONArray();
        JSONObject b = new JSONObject();
        a.put(b);


        // 총합
        for(int i=0;i<ListComponent.length();i++){
            try{
            JSONObject ml = ListComponent.getJSONObject(i);
            totalPrice += ml.getInt("price") * ml.getInt("num");
            DayHistoryListRow lr = new DayHistoryListRow(getContext(), ml);
            listTable.addView(lr);

            }
            catch (Exception e){e.printStackTrace();}
        }

        String payKind;

        if(isCash){
            totalCash += totalPrice;
            payKind = " (현금)";
        }
        else{
            totalCard += totalPrice;
            payKind = " (카드)";
        }

        total += totalPrice;

        DayHistoryListRow lr = new DayHistoryListRow(getContext(), "총 합" + payKind, totalPrice);
        listTable.addView(lr);

        ll = (LinearLayout) findViewById(R.id.menuInList);
        ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectList();
            }
        });

        menuDayHistoryListLayout.add(this);

        Context c = getContext();
        SharedPreferences sp = c.getSharedPreferences("today", c.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString("lists", getListJSONArray().toString());
        spe.putInt("maxNo", no);
        spe.commit();
    }

    public void selectList(){
        if(isSelected) {
            ll.setBackgroundResource(R.drawable.day_history_item);
            isSelected = false;
        }
        else {
            ll.setBackgroundResource(R.drawable.day_history_item_selected);
            isSelected = true;
        }
    }

    public static JSONArray getListJSONArray(){
        JSONArray listJSONArray = new JSONArray();
        try {
            for(int i = 0; i< menuDayHistoryListLayout.size(); i++){
                DayHistoryListLayout l = (DayHistoryListLayout) menuDayHistoryListLayout.get(i);
                JSONObject thisListJSON = new JSONObject();
                thisListJSON.put("no", l.no);
                thisListJSON.put("isCash", l.isCash);
                thisListJSON.put("time", l.time);
                thisListJSON.put("ListComponent", l.ListComponent);

                listJSONArray.put(thisListJSON);
            }

        }
        catch(Exception e){e.printStackTrace();}

        return listJSONArray;
    }
}
