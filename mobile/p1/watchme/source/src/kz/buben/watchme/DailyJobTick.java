/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kz.buben.watchme;

import de.enough.polish.io.Serializable;

/**
 *
 * @author german
 */
public class DailyJobTick implements Serializable{
    private int id;
    //private DailyJob job;
    private int done;
    private SimpleDate date;

    public SimpleDate getDate() {
        return date;
    }

    public void setDate(SimpleDate date) {
        this.date = date;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public DailyJob getJob() {
//        return job;
//    }
//
//    public void setJob(DailyJob job) {
//        this.job = job;
//    }

    public String getDateAsString(){
        return date.toString();
    }
}
