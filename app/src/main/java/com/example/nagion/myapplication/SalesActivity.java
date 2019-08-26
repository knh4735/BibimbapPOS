package com.example.nagion.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;


/**
 * 그리드뷰를 이용한 달력 예제
 *
 * @blog http://croute.me
 * @link http://croute.me/335
 *
 * @author croute
 * @since 2011.03.08
 */
public class SalesActivity extends AppCompatActivity implements OnItemClickListener, OnClickListener
{
    public static int SUNDAY        = 1;
    public static int MONDAY        = 2;
    public static int TUESDAY       = 3;
    public static int WEDNSESDAY    = 4;
    public static int THURSDAY      = 5;
    public static int FRIDAY        = 6;
    public static int SATURDAY      = 7;

    private TextView mTvCalendarTitle;
    private TextView monthTotalTv;
    private TextView monthCashTv;
    private TextView monthCardTv;
    private GridView mGvCalendar;
    private Button backToList;

    private ArrayList<DayInfo> mDayList;
    private CalendarAdapter mCalendarAdapter;

    Calendar mLastMonthCalendar;
    Calendar mThisMonthCalendar;
    Calendar mNextMonthCalendar;

    PosDatabase salesDB;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        Button bLastMonth = (Button)findViewById(R.id.preMonth);
        Button bNextMonth = (Button)findViewById(R.id.nextMonth);

        mTvCalendarTitle = (TextView)findViewById(R.id.tvDate);
        monthTotalTv = (TextView)findViewById(R.id.monthTotal);
        monthCashTv = (TextView)findViewById(R.id.monthCash);
        monthCardTv = (TextView)findViewById(R.id.monthCard);
        mGvCalendar = (GridView)findViewById(R.id.calendar);
        backToList = (Button)findViewById(R.id.backToList);

        salesDB = new PosDatabase(getApplicationContext());


        bLastMonth.setOnClickListener(this);
        bNextMonth.setOnClickListener(this);
        mGvCalendar.setOnItemClickListener(this);

        backToList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesActivity.this.finish();
            }
        });

        mDayList = new ArrayList<DayInfo>();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // 이번달 의 캘린더 인스턴스를 생성한다.
        mThisMonthCalendar = Calendar.getInstance();
        mThisMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        getCalendar(mThisMonthCalendar);
    }

    /**
     * 달력을 셋팅한다.
     *
     * @param calendar 달력에 보여지는 이번달의 Calendar 객체
     */
    private void getCalendar(Calendar calendar)
    {
        int lastMonthStartDay;
        int dayOfMonth;
        int thisMonthLastDay;
        String thisYear = String.valueOf(mThisMonthCalendar.get(Calendar.YEAR));
        String thisMonth = String.valueOf(mThisMonthCalendar.get(Calendar.MONTH) + 1);
        String[] profits;

        mDayList.clear();

        calendar.get(Calendar.YEAR);
        calendar.get(Calendar.MONTH);


        // 이번달 시작일의 요일을 구한다. 시작일이 일요일인 경우 인덱스를 1(일요일)에서 8(다음주 일요일)로 바꾼다.)
        dayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, -1);

        // 지난달의 마지막 일자를 구한다.
        lastMonthStartDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, 1);

        if(dayOfMonth == SUNDAY)
        {
            dayOfMonth += 7;
        }

        lastMonthStartDay -= (dayOfMonth-1)-1;


        // 캘린더 타이틀(년월 표시)을 세팅한다.
        mTvCalendarTitle.setText(thisYear + "년 " + thisMonth + "월");

        profits = salesDB.getMonthProfit(thisYear, thisMonth);

        monthTotalTv.setText(NumberFormat.getInstance().format(Integer.parseInt(profits[50])));
        monthCashTv.setText(NumberFormat.getInstance().format(Integer.parseInt(profits[51])));
        monthCardTv.setText(NumberFormat.getInstance().format(Integer.parseInt(profits[52])));


        DayInfo day;

        day = new DayInfo();
        day.setDay("일");
        day.setInMonth(true);
        mDayList.add(day);

        day = new DayInfo();
        day.setDay("월");
        day.setInMonth(true);
        mDayList.add(day);

        day = new DayInfo();
        day.setDay("화");
        day.setInMonth(true);
        mDayList.add(day);

        day = new DayInfo();
        day.setDay("수");
        day.setInMonth(true);
        mDayList.add(day);

        day = new DayInfo();
        day.setDay("목");
        day.setInMonth(true);
        mDayList.add(day);

        day = new DayInfo();
        day.setDay("금");
        day.setInMonth(true);
        mDayList.add(day);

        day = new DayInfo();
        day.setDay("토");
        day.setInMonth(true);
        mDayList.add(day);

        for(int i=0; i<dayOfMonth-1; i++)
        {
            int date = lastMonthStartDay+i;
            day = new DayInfo();
            day.setDay(Integer.toString(date));
            day.setInMonth(false);

            mDayList.add(day);
        }
        for(int i=1; i <= thisMonthLastDay; i++)
        {
            day = new DayInfo();
            day.setDay(Integer.toString(i));
            day.setProfit(profits[i]);
            day.setInMonth(true);

            mDayList.add(day);
        }
        for(int i=1; i<42-(thisMonthLastDay+dayOfMonth-1)+1; i++)
        {
            day = new DayInfo();
            day.setDay(Integer.toString(i));
            day.setInMonth(false);
            mDayList.add(day);
        }

        initCalendarAdapter();
    }

    /**
     * 지난달의 Calendar 객체를 반환합니다.
     *
     * @param calendar
     * @return LastMonthCalendar
     */
    private Calendar getLastMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, -1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        return calendar;
    }

    /**
     * 다음달의 Calendar 객체를 반환합니다.
     *
     * @param calendar
     * @return NextMonthCalendar
     */
    private Calendar getNextMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, +1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        return calendar;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long arg3)
    {

    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.preMonth:
                mThisMonthCalendar = getLastMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar);
                break;
            case R.id.nextMonth:
                mThisMonthCalendar = getNextMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar);
                break;
        }
    }

    private void initCalendarAdapter()
    {
        mCalendarAdapter = new CalendarAdapter(this, R.layout.date_sales, mDayList);
        mGvCalendar.setAdapter(mCalendarAdapter);
    }
}
