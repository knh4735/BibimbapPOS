package com.example.nagion.myapplication;

public class SelectedItem {
    String name;
    int price;
    int menuPosition;
    int num;

    public SelectedItem(String name, int price, int position){
        this.name = name;
        this.price = price;
        this.menuPosition = position;
        this.num = 1;
    }

    public String getName(){
        return this.name;
    }

    public int getPrice(){
        return this.price;
    }

    public int getMenuPosition(){
        return this.menuPosition;
    }

    public int getNum(){
        return this.num;
    }

    public void increase(){
        this.num += 1;
    }

    public void decrease(){
        this.num -= 1;
    }

    /*
    public void setName(String name){
        this.name = name;
    }

    public void setPrice(Integer price){
        this.price = price;
    }
    */
}
