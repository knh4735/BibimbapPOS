package com.example.nagion.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    //static LinearLayout listWrap;
    //static int no=1;

    MenuAdapter menuAdapter;
    SelectedListAdapter selectedListAdapter;

    int totalPrice = 0;
    TextView totalPriceTV;

    static int currentOrderNumber = 1;

    @Override
    protected void onStop() {
        super.onStop();

        if(totalPrice > 0) {
            selectedListAdapter.removeAllItems();
        }

        totalPrice = 0;
        updateTotalPrice();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO 메뉴 불러오기 + 마지막엔 메뉴 편집 행 추가

        totalPriceTV = (TextView) findViewById(R.id.totalPrice);
        Button complete = (Button) findViewById(R.id.complete);
        //listWrap = (LinearLayout) findViewById(R.id.listWrap);

        // 메뉴판 생성
        GridView menuGrid = (GridView) findViewById(R.id.menu_grid);
        menuAdapter = new MenuAdapter();
        menuAdapter.initItem();
        menuGrid.setAdapter(menuAdapter);

        // 현재 주문 목록 준비
        ListView selectedList = (ListView) findViewById(R.id.selected_list);
        selectedListAdapter = new SelectedListAdapter();
        selectedList.setAdapter(selectedListAdapter);

        // 주문 내역 불러오기
        try {
            if (DayHistoryListLayout.menuDayHistoryListLayout.size() == 0) {
                Context context = getApplicationContext();
                SharedPreferences sp = context.getSharedPreferences("today", context.MODE_PRIVATE);
                String lists = sp.getString("lists", "");
                if(lists.length() != 0) {
                    JSONArray ja = new JSONArray(lists);
                    currentOrderNumber = sp.getInt("maxNo", 0) + 1;

                    if (ja.length() != 0) {
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);
                            DayHistoryListLayout dayHistoryListLayout = new DayHistoryListLayout(getApplicationContext(), jo.getInt("no"), jo.getBoolean("isCash"), jo.getString("time"), jo.getJSONArray("ListComponent"));
                        }
                    }
                }
            }
        }
        catch(Exception e){e.printStackTrace();}

        // 메뉴 선택시 주문 목록에 추가
        menuGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final MenuItem menuItem = (MenuItem) menuAdapter.getItem(position);
                if(menuItem.getId() == -1) {  // 메뉴 추가 버튼

                    AlertDialog.Builder addMenuDialog = new AlertDialog.Builder(MainActivity.this);
                    addMenuDialog.setTitle("메뉴 추가");

                    // 다이얼로그 구성
                    LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText editName = new EditText(MainActivity.this);
                    editName.setHint("메뉴 이름");
                    layout.addView(editName);

                    final EditText editPrice = new EditText(MainActivity.this);
                    editPrice.setHint("가격 (숫자로만)");
                    layout.addView(editPrice);

                    addMenuDialog.setView(layout)
                        .setCancelable(true)
                        .setPositiveButton("추가",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String newName = editName.getText().toString().trim();
                                    final String newPrice = editPrice.getText().toString().trim();

                                    if(!isInteger(newPrice)){
                                        AlertDialog.Builder nanPrice = new AlertDialog.Builder(MainActivity.this);
                                        nanPrice.setMessage("가격에는 숫자만 입력해주세요.")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alert = nanPrice.create();
                                        alert.show();

                                        return;
                                    }

                                    AlertDialog.Builder alertEdit = new AlertDialog.Builder(MainActivity.this);
                                    alertEdit.setMessage(newName + " " + newPrice + "원 메뉴를 추가하시겠습니까?")
                                        .setCancelable(true)
                                        .setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    PosDatabase menuDB = new PosDatabase(getApplicationContext());
                                                    menuDB.addMenu(newName, newPrice);
                                                    menuAdapter.initItem();
                                                    menuAdapter.notifyDataSetInvalidated();
                                                }
                                            })
                                        .setNegativeButton("취소",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = alertEdit.create();
                                    alert.show();
                                }
                            })
                        .setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = addMenuDialog.create();
                    alert.show();

                    return;
                }

                int selectedPosition = menuItem.getPosition();

                if(selectedPosition == -1) {    // 새로 추가
                    menuItem.setPosition(selectedListAdapter.getCount());
                    //view.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_on_click));

                    SelectedItem selectedItem = new SelectedItem(menuItem.name, menuItem.price, position);
                    selectedListAdapter.addItem(selectedItem);
                    selectedListAdapter.notifyDataSetChanged();
                }

                else{   // 수량 증가
                    SelectedItem selectedItem = (SelectedItem) selectedListAdapter.getItem(selectedPosition);
                    selectedItem.increase();

                    selectedListAdapter.notifyDataSetChanged();
                }

                totalPrice += menuItem.getPrice();
                updateTotalPrice();
            }
        });

        // 메뉴 수정 및 추가
        menuGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final MenuItem menuItem = (MenuItem) menuAdapter.getItem(position);
                if(menuItem.getId() == -1) return true;  // 메뉴 추가 버튼 제외

                AlertDialog.Builder editMenuDialog = new AlertDialog.Builder(MainActivity.this);
                editMenuDialog.setTitle("메뉴 수정");

                // 다이얼로그 구성
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText editName = new EditText(MainActivity.this);
                editName.setText(menuItem.getName());
                layout.addView(editName);

                final EditText editPrice = new EditText(MainActivity.this);
                editPrice.setText("" + menuItem.getPrice());
                layout.addView(editPrice);

                editMenuDialog.setView(layout)
                    .setCancelable(true)
                    .setPositiveButton("수정",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final int id = menuItem.getId();
                                final String newName = editName.getText().toString().trim();
                                final String newPrice = editPrice.getText().toString().trim();

                                if(!isInteger(newPrice)){
                                    AlertDialog.Builder nanPrice = new AlertDialog.Builder(MainActivity.this);
                                    nanPrice.setMessage("가격에는 숫자만 입력해주세요.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = nanPrice.create();
                                    alert.show();

                                    return;
                                }

                                AlertDialog.Builder alertEdit = new AlertDialog.Builder(MainActivity.this);
                                alertEdit.setMessage(newName + " " + newPrice + "원으로 수정하시겠습니까?")
                                        .setCancelable(true)
                                        .setPositiveButton("확인",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        PosDatabase menuDB = new PosDatabase(getApplicationContext());
                                                        menuDB.editMenu(id, newName, newPrice);
                                                        menuAdapter.initItem();
                                                        menuAdapter.notifyDataSetInvalidated();
                                                    }
                                                })
                                        .setNegativeButton("취소",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                AlertDialog alert = alertEdit.create();
                                alert.show();
                            }
                        })
                    .setNegativeButton("삭제",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final int id = menuItem.getId();
                                AlertDialog.Builder alertDelete = new AlertDialog.Builder(MainActivity.this);

                                alertDelete.setMessage("메뉴를 삭제하시겠습니까?")
                                        .setCancelable(true)
                                        .setPositiveButton("확인",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        PosDatabase menuDB = new PosDatabase(getApplicationContext());
                                                        menuDB.deleteMenu(id);
                                                        menuAdapter.initItem();
                                                        menuAdapter.notifyDataSetInvalidated();
                                                    }
                                                })
                                        .setNegativeButton("취소",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                AlertDialog alert = alertDelete.create();
                                alert.show();
                            }
                        });
                AlertDialog alert = editMenuDialog.create();
                alert.show();

                return true;
            }
        });

        // 주문
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalPrice > 0) {
                    AlertDialog.Builder alertOrder = new AlertDialog.Builder(MainActivity.this);
                    alertOrder.setMessage("결제 수단을 선택하세요.")
                        .setCancelable(true)
                        .setPositiveButton("현금",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    order(true);
                                }
                            })
                        .setNegativeButton("카드",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    order(false);
                                }
                            });
                    AlertDialog alert = alertOrder.create();
                    alert.show();
                }
                else{   // 그냥 완료 누르면 주문 내역 확인
                    Intent intent = new Intent(getApplicationContext(), DayHistoryActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    // 주문하기
    public void order(Boolean isCash){
        try {
            JSONArray currentSelectedJSONArray = selectedListAdapter.getItemsJSONArray();

            String time = new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));

            DayHistoryListLayout dayHistoryListLayout = new DayHistoryListLayout(getApplicationContext(), currentOrderNumber, isCash, time, currentSelectedJSONArray);

            Intent intent = new Intent(getApplicationContext(), DayHistoryActivity.class);
            startActivity(intent);

            /*
            for (int i = 0; i < currentSelectedJSONArray.length(); i++) {
                SelectedMenuLayout ml = (SelectedMenuLayout) SelectedMenuLayout.CurrentSelectedList.get(i);
                ml.menuItem.removeMenu();
            }
            SelectedMenuLayout.CurrentSelectedList.clear();
            listWrap.removeAllViews();
             */

            selectedListAdapter.removeAllItems();

            totalPrice = 0;
            updateTotalPrice();

            currentOrderNumber++;
        } catch (JSONException e){e.printStackTrace();}
    }


    public void updateTotalPrice(){
        String commaNum = NumberFormat.getInstance().format(totalPrice);
        totalPriceTV.setText("₩ " + commaNum);
    }

    public boolean isInteger(String str){
        try{
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


    class MenuAdapter extends BaseAdapter {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        PosDatabase menuDB;


        public MenuAdapter(){
            menuDB = new PosDatabase(getApplicationContext());
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItem item = items.get(position);

            if(item.getId() == -1){ // 메뉴 추가 버튼
                MenuItemView view = new MenuItemView(getApplicationContext());
                view.menuName.setText("메뉴");
                view.price.setText("추가");

                return view;
            }

            MenuItemView view = new MenuItemView(getApplicationContext());
            view.setName(item.name);
            view.setPrice(item.price);

            return view;
        }

        public void initItem(){
            items = menuDB.getAllMenu();

            MenuItem addMenu = new MenuItem(-1, "", 0);
            items.add(addMenu);
        }
    }


    class SelectedListAdapter extends BaseAdapter {
        ArrayList<SelectedItem> items = new ArrayList<>();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SelectedItemView view = new SelectedItemView(getApplicationContext());

            SelectedItem item = items.get(position);
            view.setName(item.name);
            view.setPrice(item.price);
            view.setNum(item.num);
            view.setPosition(position);

            // 주문 목록에서 삭제
            view.deleteBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = ((SelectedItemView) view.getParent().getParent()).getPosition();
                    SelectedItem selectedItem = (SelectedItem) selectedListAdapter.getItem(position);
                    MenuItem menuItem = (MenuItem) menuAdapter.getItem(selectedItem.getMenuPosition());
                    menuItem.setPosition(-1);

                    totalPrice -= selectedItem.getPrice() * selectedItem.getNum();
                    updateTotalPrice();
                    //((View) menuItem).setBackgroundDrawable(getResources().getDrawable(R.drawable.border));

                    selectedListAdapter.removeItem(position);
                    selectedListAdapter.notifyDataSetChanged();
                }
            });

            // 수량 증가
            view.upBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = ((SelectedItemView) view.getParent().getParent()).getPosition();
                    SelectedItem selectedItem = (SelectedItem) selectedListAdapter.getItem(position);
                    selectedItem.increase();

                    totalPrice += selectedItem.getPrice();
                    updateTotalPrice();

                    selectedListAdapter.notifyDataSetChanged();
                }
            });

            // 수량 감소
            view.downBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = ((SelectedItemView) view.getParent().getParent()).getPosition();
                    SelectedItem selectedItem = (SelectedItem) selectedListAdapter.getItem(position);
                    selectedItem.decrease();

                    totalPrice -= selectedItem.getPrice();
                    updateTotalPrice();

                    if(selectedItem.getNum() == 0) { // 0개면 삭제
                        MenuItem menuItem = (MenuItem) menuAdapter.getItem(selectedItem.getMenuPosition());
                        menuItem.setPosition(-1);

                        selectedListAdapter.removeItem(position);
                    }

                    selectedListAdapter.notifyDataSetChanged();
                }
            });


            return view;
        }

        public void addItem(SelectedItem item){
            items.add(item);
        }

        public void removeItem(int position){
            items.remove(position);
        }

        public JSONArray getItemsJSONArray() throws JSONException {
            JSONArray ja = new JSONArray();
            for(Iterator<SelectedItem> it = items.iterator(); it.hasNext() ; ) {
                SelectedItem selectedItem = it.next();
                JSONObject jo = new JSONObject();
                jo.put("menu", selectedItem.name);
                jo.put("price", selectedItem.price);
                jo.put("num", selectedItem.num);
                ja.put(jo);
            }

            return ja;
        }

        public void removeAllItems(){
            for(Iterator<SelectedItem> it = items.iterator(); it.hasNext() ; ) {
                SelectedItem selectedItem = it.next();
                MenuItem menuItem = (MenuItem) menuAdapter.getItem(selectedItem.getMenuPosition());

                menuItem.setPosition(-1);
                it.remove();
            }

            selectedListAdapter.notifyDataSetChanged();
        }
    }
}
