package com.example.nagion.myapplication;

/**
 * Created by Nagion on 2016. 9. 15..
 */
public class DayInfo
{
    private String day;
    private String profit = "";
    private boolean inMonth;

    /**
     * 날짜를 반환한다.
     *
     * @return day 날짜
     */
    public String getDay()
    {
        return day;
    }

    /**
     * 날짜를 저장한다.
     *
     * @param day 날짜
     */
    public void setDay(String day)
    {
        this.day = day;
    }

    public String getProfit(){
        return profit;
    }

    public void setProfit(String profit)
    {
        this.profit = profit;
    }

    /**
     * 이번달의 날짜인지 정보를 반환한다.
     *
     * @return inMonth(true/false)
     */
    public boolean isInMonth()
    {
        return inMonth;
    }

    /**
     * 이번달의 날짜인지 정보를 저장한다.
     *
     * @param inMonth(true/false)
     */
    public void setInMonth(boolean inMonth)
    {
        this.inMonth = inMonth;
    }

}