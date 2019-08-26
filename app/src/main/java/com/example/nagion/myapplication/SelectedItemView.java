package com.example.nagion.myapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;

public class SelectedItemView extends LinearLayout {

    int itemPosition;

    TextView name;
    TextView price;
    TextView num;

    Button upBtn;
    Button downBtn;
    Button deleteBtn;

    public SelectedItemView(Context context) {
        super(context);

        init(context);
    }

    public SelectedItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.selected_item, this, true);

        name = (TextView) findViewById(R.id.name);
        price = (TextView) findViewById(R.id.price);
        num = (TextView) findViewById(R.id.num);

        upBtn = (Button) findViewById(R.id.up);
        downBtn = (Button) findViewById(R.id.down);
        deleteBtn = (Button) findViewById(R.id.delete);

        deleteBtn.setOnClickListener(this.deleteBtnClickHandler());
    }

    public Button.OnClickListener deleteBtnClickHandler(){
        return new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = ((SelectedItemView) view.getParent().getParent()).getPosition();

            }
        };
    };

    public void setName(String name){
        this.name.setText(name);
    }

    public void setPrice(int price){
        this.price.setText(NumberFormat.getInstance().format(price));
    }

    public void setNum(int num){
        this.num.setText(NumberFormat.getInstance().format(num));
    }

    public void setPosition(int position){
        this.itemPosition = position;
    }

    public int getPosition(){
        return this.itemPosition;
    }
}
