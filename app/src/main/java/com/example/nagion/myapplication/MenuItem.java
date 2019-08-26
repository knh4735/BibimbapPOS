package com.example.nagion.myapplication;

public class MenuItem {
    int id;
    String name;
    int price;
    int selectedPosition;

    public MenuItem(int id, String name, int price){
        this.id = id;
        this.name = name;
        this.price = price;
        this.selectedPosition = -1;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public int getPrice(){
        return this.price;
    }

    public int getPosition(){
        return this.selectedPosition;
    }

    public void setPosition(int position){
        this.selectedPosition = position;
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
