package com.example.nagion.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import android.content.res.Resources;

/**
 * Created by Nagion on 2016. 8. 30..
 */
public class PosDatabase extends SQLiteOpenHelper {
    private Context dbContext;
    public PosDatabase(Context context) {
        super(context, "pos.db", null, 5);
        dbContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS sales(_id INTEGER PRIMARY KEY AUTOINCREMENT, year TEXT, month TEXT, date TEXT, card TEXT, cash TEXT, total TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS menu(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, price TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE IF NOT EXISTS sales(_id INTEGER PRIMARY KEY AUTOINCREMENT, year TEXT, month TEXT, date TEXT, card TEXT, cash TEXT, total TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS menu(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, price TEXT);");

        long cnt = DatabaseUtils.queryNumEntries(db, "menu");
        Log.e("Menu Count", "Menu Count " + cnt);
        if(cnt == 0){
            Writer writer = new StringWriter();
            try {
                InputStream is = dbContext.getResources().openRawResource(R.raw.menu_data);
                char[] buffer = new char[1024];
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }

                is.close();
                String jsonString = writer.toString();

                JSONArray ja = new JSONArray(jsonString);
                String query = "INSERT INTO menu (name, price) VALUES";
                for(int i = 0; i < ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);
                    if(i != 0) query += ",";
                    query += " ('" + jo.getString("name") + "', " + jo.getString("price") + ")";
                }

                db.execSQL(query);

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void insert(String query){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void close(String year, String month, String date, String card, String cash, String total){
        SQLiteDatabase db = getWritableDatabase();

        String query = "DELETE FROM sales WHERE year = " + year + " AND month = " + month + " AND date = " + date;
        db.execSQL(query);

        query = "INSERT INTO sales (year, month, date, card, cash, total) VALUES ("+year+", "+month+", "+date+", "+card+", "+cash+", "+total+")";
        db.execSQL(query);

        db.close();
    }

    public String[] getMonthProfit(String year, String month){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM sales WHERE year = " + year + " AND month = " + month + "";
        Cursor c = db.rawQuery(query, null);

        String[] result = new String[53];

        for(int i=0;i<51;i++) result[i] = "";

        while(c.moveToNext()) {
            String date = c.getString(c.getColumnIndex("date"));
            String total = c.getString(c.getColumnIndex("total"));

            result[Integer.parseInt(date)] = total;
        }

        query = "SELECT SUM(card) as totalCard, SUM(cash) as totalCash, SUM(total) as total FROM sales WHERE year = " + year + " AND month = " + month + "";
        c = db.rawQuery(query, null);
        while(c.moveToNext()) {
            result[50] = c.getString(c.getColumnIndex("total"));
            result[51] = c.getString(c.getColumnIndex("totalCash"));
            result[52] = c.getString(c.getColumnIndex("totalCard"));
        }

        db.close();

        return result;
    }

    public void addMenu(String name, String price){
        SQLiteDatabase db = getWritableDatabase();

        String query =
                "INSERT " +
                "INTO menu (name, price) " +
                "VALUES ('"+name+"', '"+price+"')";
        db.execSQL(query);

        db.close();
    }

    public void editMenu(int id, String name, String price){
        SQLiteDatabase db = getWritableDatabase();

        String query =
                "UPDATE menu " +
                "SET name = '" + name  + "' " +
                 ", price = '" + price + "' " +
                "WHERE _id = " + id;
        db.execSQL(query);

        db.close();
    }


    public ArrayList<MenuItem> getAllMenu() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM menu";
        Cursor c = db.rawQuery(query, null);

        ArrayList<MenuItem> menuList = new ArrayList<>();

        while(c.moveToNext()) {
            MenuItem item = new MenuItem(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("name")), Integer.parseInt(c.getString(c.getColumnIndex("price"))));
            menuList.add(item);
        }

        db.close();

        return menuList;
    }

    public void deleteMenu(int id){
        SQLiteDatabase db = getWritableDatabase();

        String query = "DELETE FROM menu WHERE _id = " + id;
        db.execSQL(query);

        db.close();
    }
}
