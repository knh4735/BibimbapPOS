package com.example.nagion.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DayHistoryActivity extends AppCompatActivity {

    Button selBtn, allBtn, salesBtn, back;
    TextView totalCashTv, totalCardTv, totalTv;
    static LinearLayout listWrap;

    @Override
    protected void onStop() {
        super.onStop();

        ArrayList menuList = DayHistoryListLayout.menuDayHistoryListLayout;
        for(int i=0;i<menuList.size(); i++){
            DayHistoryListLayout l = (DayHistoryListLayout) menuList.get(i);
            if(l.isSelected) {
                l.selectList();
            }
        }
        listWrap.removeAllViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fillList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent intent = getIntent();

        final PosDatabase salesDB = new PosDatabase(getApplicationContext());

        fillList();

        selBtn = (Button) findViewById(R.id.selBtn);
        allBtn = (Button) findViewById(R.id.allBtn);
        salesBtn = (Button) findViewById(R.id.salesBtn);
        back = (Button) findViewById(R.id.back);
        totalCashTv = (TextView) findViewById(R.id.totalCash);
        totalCardTv = (TextView) findViewById(R.id.totalCard);
        totalTv = (TextView) findViewById(R.id.totalAll);

        totalCashTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.totalCash));
        totalCardTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.totalCard));
        totalTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.total));

        // 선택 삭제
        selBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = DayHistoryListLayout.menuDayHistoryListLayout.size();
                ArrayList objList = new ArrayList();

                for(int i=0;i<size; i++){
                    DayHistoryListLayout l = (DayHistoryListLayout) DayHistoryListLayout.menuDayHistoryListLayout.get(i);
                    if(l.isSelected) {
                        if(l.isCash){
                            DayHistoryListLayout.totalCash -= l.totalPrice;
                            DayHistoryListLayout.total -= l.totalPrice;
                            totalCashTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.totalCash));
                            totalTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.total));
                        }
                        else{
                            DayHistoryListLayout.totalCard -= l.totalPrice;
                            DayHistoryListLayout.total -= l.totalPrice;
                            totalCardTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.totalCard));
                            totalTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.total));
                        }


                        objList.add(l);
                        listWrap.removeView(l);

                    }
                }

                for(int i=0;i<objList.size();i++){
                    DayHistoryListLayout l = (DayHistoryListLayout) objList.get(i);
                    DayHistoryListLayout.menuDayHistoryListLayout.remove(l);
                    l = null;
                }
                objList.clear();

                Context c = getApplication();
                SharedPreferences sp = c.getSharedPreferences("today", c.MODE_PRIVATE);
                SharedPreferences.Editor spe = sp.edit();
                spe.putString("lists", DayHistoryListLayout.getListJSONArray().toString());
                spe.commit();
            }
        });

        // 정산
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertSales = new AlertDialog.Builder(DayHistoryActivity.this);
                alertSales.setMessage("정산하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //yes

                                String year = new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis()));
                                String month = new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis()));
                                String date = new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis()));
                                String card = String.valueOf(DayHistoryListLayout.totalCard);
                                String cash = String.valueOf(DayHistoryListLayout.totalCash);
                                String total = String.valueOf(DayHistoryListLayout.total);

                                salesDB.close(year, month, date, card, cash, total);

                                listWrap.removeAllViews();
                                for(int i = 0; i< DayHistoryListLayout.menuDayHistoryListLayout.size(); i++){
                                    DayHistoryListLayout l = (DayHistoryListLayout) DayHistoryListLayout.menuDayHistoryListLayout.get(i);
                                    l = null;
                                }
                                DayHistoryListLayout.menuDayHistoryListLayout.clear();

                                DayHistoryListLayout.totalCash = 0;
                                DayHistoryListLayout.totalCard = 0;
                                DayHistoryListLayout.total = 0;
                                totalCashTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.totalCash));
                                totalCardTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.totalCard));
                                totalTv.setText(NumberFormat.getInstance().format(DayHistoryListLayout.total));

                                MainActivity.currentOrderNumber = 1;

                                Context c = getApplication();
                                SharedPreferences sp = c.getSharedPreferences("today", c.MODE_PRIVATE);
                                SharedPreferences.Editor spe = sp.edit();
                                spe.putString("lists", DayHistoryListLayout.getListJSONArray().toString());
                                spe.putInt("maxNo", 1);
                                spe.commit();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alertSales.create();
                alert.show();

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DayHistoryActivity.this.finish();
            }
        });

        salesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SalesActivity.class);
                startActivity(intent);
            }
        });

    }

    public void fillList(){
        listWrap = (LinearLayout) findViewById(R.id.listWrap);
        for(int i = DayHistoryListLayout.menuDayHistoryListLayout.size()-1; i>=0; i--){
            DayHistoryListLayout l = (DayHistoryListLayout) DayHistoryListLayout.menuDayHistoryListLayout.get(i);
            listWrap.addView(l);
        }
    }

}
