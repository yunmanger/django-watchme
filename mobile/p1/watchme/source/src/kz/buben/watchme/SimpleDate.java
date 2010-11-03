/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kz.buben.watchme;

import de.enough.polish.io.Serializable;
import de.enough.polish.util.Comparable;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author german
 */
public class SimpleDate implements Serializable{
    private int year, month, day;
    public SimpleDate(int y, int m, int d){
        year = y;
        month = m;
        day = d;
    }

    public SimpleDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);
    }

    public SimpleDate getYesterday(){
        int ny = year, nm =month, nd =day;
        nd-=1;
        if (nd <= 0){
            nd = 31;
            nm-=1;
        }
        if (nm <= 0){
            nm = 12;
            ny-=1;
        }
        Calendar cal = Calendar.getInstance();
        Date date= null;
        while (true){
            try{
                cal.set(Calendar.YEAR, ny);
                cal.set(Calendar.MONTH,nm-1);
                cal.set(Calendar.DAY_OF_MONTH,nd);
                date = cal.getTime();
                break;
            }catch(Exception e){
                nd-=1;
            }
        }
        return new SimpleDate(date);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public static String intToStr(int k, int l) {
        String s = ""+k;
        while(s.length() < l) s = "0"+s;
        return s;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String toString(){
        return ""+intToStr(year, 4)+"/"+intToStr(month, 2)+"/"+intToStr(day, 2);
    }

    public boolean equals(SimpleDate o){
        return (year == o.getYear() && month == o.getMonth() && day == o.getDay());
    }

}
