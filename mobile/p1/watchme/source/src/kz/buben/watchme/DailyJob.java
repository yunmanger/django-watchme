package kz.buben.watchme;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import de.enough.polish.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
//import java.util.Vector;

/**
 *
 * @author german
 */
public class DailyJob implements Serializable{
    public int id;
    public String title="";
    public String text="";
    public int n;
    public int importance;
    public Vector ticks = null;

    public int getId(){
        return id;
    }


    public void setId(int id){
        this.id = id;
    }
//    public boolean getIsDone(){
//        return isDone;
//    }
//
//    public void setIsDone(boolean isDone){
//        this.isDone = isDone;
//    }
//
//    public int getDone(){
//        return done;
//    }
//
//    public void setDone(int done){
//        this.done = done;
//    }

    public DailyJobTick isTicked(SimpleDate date){
        Vector t = getTicks();
        int n = t.size();
        if (t == null || n == 0)
            return null;
        DailyJobTick o = null;
        for (int i = 0; i < n; i++){
            o = (DailyJobTick)t.elementAt(i);
            if (o != null && o.getDate().equals(date)){
                return o;
            }
        }
        return null;
    }
    public boolean isDone(DailyJobTick o, SimpleDate date){
        return (o.getDate().equals(date) && o.getDone() > 0);
    }
    public DailyJobTick isDone(SimpleDate date){
        Vector t = getTicks();
        int n = t.size();
        if (t == null || n == 0)
            return null;
        DailyJobTick o = null;
        for (int i = 0; i < n; i++){
            o = (DailyJobTick)t.elementAt(i);
            if (o != null && o.getDate().equals(date) && o.getDone() > 0){
                return o;
            }
        }
        return null;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Vector getTicks() {
        if (ticks == null){
            ticks = new Vector();
        }
        return ticks;
    }

    public void setTicks(Vector ticks) {
        this.ticks = ticks;
    }

    public void clearTicks(){
        Vector ticks = getTicks();
        int n = ticks.size();
        String tmp;
        if (n > 3){
            for (int i = 3; i < n; i++)
                ticks.removeElementAt(i);
            System.gc();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void printTicks(){
        Vector t = getTicks();
        System.out.println(getTitle());
        int n = t.size();
        for (int i = 0; i < n; i++){
            DailyJobTick jt = (DailyJobTick)t.elementAt(i);
            System.out.println(jt.getDateAsString() + "--"+jt.getDone());
        }
    }

}
