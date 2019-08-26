package com.example.nagion.myapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;

public class MenuItemView extends LinearLayout {

    TextView menuName;
    TextView price;

    public MenuItemView(Context context) {
        super(context);

        init(context);
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.menu_item, this, true);

        menuName = (TextView) findViewById(R.id.menu_name);
        price = (TextView) findViewById(R.id.price);
    }

    public void setName(String name){
        this.menuName.setText(name);
    }

    public void setPrice(int price){
        this.price.setText(NumberFormat.getInstance().format(price));
    }
}
