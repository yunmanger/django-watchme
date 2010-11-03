/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kz.buben.watchme;

import de.enough.polish.io.RmsStorage;
import de.enough.polish.xml.XmlPullParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author german
 */
public class WS {
    private static MainMIDlet mlet;
    private static RmsStorage storage;
    private static User user;
    private static Vector jobList;
    public static SimpleDate today, yesterday, beforeYesterday;
    public static SimpleDate [] dates = new SimpleDate[3];
    public static int curTab;
    private static Hashtable settings;

    /* domain */
    
    //#if cfg.domain:defined
        //#= public static final String domain = "${cfg.domain}";
    //#else
        public static final String domain = "localhost:8000";
    //#endif
    //#if cfg.dailyjobs.url:defined
        //#= public static final String dailyJobsUrl= "http://"+domain+"${cfg.dailyjobs.url}";
    //#else
        public static final String dailyJobsUrl= "http://"+domain+"/m/";
    //#endif
    public static String jobapp_p= "/ja/daily";

    public static void init(MainMIDlet ml) throws IOException{
        storage = new RmsStorage("watchme");
        today = new SimpleDate(new Date());
        yesterday = today.getYesterday();
        beforeYesterday = yesterday.getYesterday();
        dates[0] = today;
        dates[1] = yesterday;
        dates[2] = beforeYesterday;
//        jobList = new Vector();
        curTab = 0;
        mlet = ml;
        getUser();
    }
    public static void next(int d){
        curTab=(curTab+d+3)%3;
        mlet.switchDisplay(mlet.jobList[curTab]);
    }
    public static void saveAccountState()throws IOException{
        String username = mlet.usernameInput.getString();
        User u = getUser();
        if (u.getUsername().equals(username)){
            u.setPassword(mlet.passwordInput.getString());
        }else{
            u.setUsername(mlet.usernameInput.getString());
            u.setPassword(mlet.passwordInput.getString());
            //User changed so, delete old DB
        }
        saveUser();
    }

    public static void deleteUser() throws IOException{
        if (hasRecord("user")){
            storage.delete("user");
        }
    }
    public static void saveUser() throws IOException{
        User u = getUser();
        deleteUser();
        storage.save(u, "user");
    }

    public static User getUser(){
        if (user == null){
            try {
                user = (User) storage.read("user");
            } catch (IOException e) {
                user = new User();
            }
        }
        return user;
    }

    public static void deleteSettings() throws IOException{
        if (hasRecord("settings")){
            storage.delete("settings");
        }
    }
    public static void saveSettings() throws IOException{
        Hashtable u = getSettings();
        deleteSettings();
        storage.save(u, "settings");
    }
    public static Hashtable getSettings(){
        if (settings == null){
            try {
                    settings = (Hashtable) storage.read("settings");
            } catch (IOException e) {
                    settings = new Hashtable(2);
                    settings.put("automaticLoad", Boolean.FALSE);
                    settings.put("saveOnClose", Boolean.TRUE);
            }
        }
        return settings;
    }

    public static boolean settingBoolean(String name){
        Hashtable s = getSettings();
        return ((Boolean)s.get(name)).booleanValue();
    }

    public static Enumeration getSettingList(){
        return getSettings().keys();
    }

    public static void deleteAll() throws IOException{
        deleteJobList();
        deleteSettings();
        deleteUser();
    }
    public static void saveAll()throws IOException{
        deleteAll();
        System.gc();
        saveJobList();
        saveSettings();
        saveUser();
    }

    public static Vector getJobList(){
        if (jobList == null){
            try {
                jobList = (Vector) storage.read("joblist");
            } catch (IOException e) {
                e.printStackTrace();
                jobList = new Vector();
            }
        }
        return jobList;
    }
    public static boolean hasRecord(String name){
        int k = storage.getRecordSetId(name);
        if (k == -1) return false;
        return true;
    }
    public static void deleteJobList()throws IOException{
        if (hasRecord("joblist")){
            storage.delete("joblist");
            System.gc();
        }
    }
    public static void saveJobList() throws IOException{
        Vector s = getJobList();
        deleteJobList();
        for(int i = 0; i < s.size(); i++){
            ((DailyJob)s.elementAt(i)).clearTicks();
        }
        storage.save(s, "joblist");
    }

    public static String loadPage(String url) throws IOException{
        HttpConnection hc = (HttpConnection)Connector.open(url);
        try{
            InputStream in = hc.openInputStream();
            try{
                return readAll(in);
            }finally{
                in.close();
            }
        }finally{
            hc.close();
            System.gc();
        }
    }
    public static String readAll(InputStream in)throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte [] buffer = new byte[1024];
        for (int n; (n = in.read(buffer)) > 0;)
            out.write(buffer, 0, n);
        return out.toString();
    }
    
    public static String auth(String url, User u){
        return url + "?user="+u.getUsername()+"&token="+u.getPassword();
    }

    public static void loadTheConf() throws IOException, XmlPullParserException{
//        System.out.println(loadPage(auth(dailyJobsUrl, getUser())));
        HttpConnection hc = (HttpConnection)Connector.open(auth(dailyJobsUrl+today.toString()+"/", getUser()));
        try{
            InputStream in = hc.openInputStream();
            try{
                KXmlParser parser = new KXmlParser();
                parser.setInput(new InputStreamReader(in,"UTF8"));
                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "description");
                while(true){
                    if (parser.getName().equals("date")) break;
                    parser.nextTag();
                }
                parser.require(XmlPullParser.START_TAG, null, "date");
                readDateData(parser);

                parser.nextTag();
                while(true){
                    if (parser.getName().equals("dailyjobs")) break;
                    parser.nextTag();
                }
                parser.require(XmlPullParser.START_TAG, null, "dailyjobs");
                //Iterate through our XML file
                getJobList().removeAllElements();
                int n = Integer.parseInt(parser.getAttributeValue(0));
                while (parser.nextTag () != XmlPullParser.END_TAG)
                         readDailyJobData(parser);
                parser.require(XmlPullParser.END_TAG, null, "dailyjobs");
                parser.nextTag();
                parser.require(XmlPullParser.END_TAG, null, "description");
                parser.next();
                parser.require(XmlPullParser.END_DOCUMENT, null, null);
            }
//            catch(XmlPullParserException e){
//                e.printStackTrace();
//            }
            finally{
                in.close();
            }
        }finally{
            hc.close();
            System.gc();
        }
    }

    public static SimpleDate readDateData(KXmlParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "date");
        int year=0, month=0, day=0;
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            parser.require(XmlPullParser.START_TAG, null, null);
            String name = parser.getName();
            String text = parser.nextText();
            //#debug
            System.out.println ("<"+name+">"+text);
            if (name.equals("year"))
                year = Integer.parseInt(text);
            else if (name.equals("month"))
                month = Integer.parseInt(text);
            else if (name.equals("day"))
                day = Integer.parseInt(text);

        parser.require(XmlPullParser.END_TAG, null, name);
        }
        parser.require(XmlPullParser.END_TAG, null, "date");
        return new SimpleDate(year,month,day);
    }

    public static DailyJobTick readTickData(KXmlParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "tick");
        DailyJobTick tick = new DailyJobTick();
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            parser.require(XmlPullParser.START_TAG, null, null);
            String name = parser.getName();
            if (name.equals("date")){
                tick.setDate(readDateData(parser));
            }else{
                String text = parser.nextText();
                //#debug
                System.out.println ("<"+name+">"+text);
                if (name.equals("id"))
                    tick.setId(Integer.parseInt(text));
                else if (name.equals("job_id"))
                    Integer.parseInt(text);
                else if (name.equals("done"))
                    tick.setDone(Integer.parseInt(text));
            }
        parser.require(XmlPullParser.END_TAG, null, name);
        }
        parser.require(XmlPullParser.END_TAG, null, "tick");
        return tick;
    }

    public static void readDailyJobData(KXmlParser parser) throws IOException, XmlPullParserException {
        //Parse our XML file
        parser.require(XmlPullParser.START_TAG, null, "dailyjob");
        DailyJob job = new DailyJob();
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            parser.require(XmlPullParser.START_TAG, null, null);
            String name = parser.getName();
            if (name.equals("latest_ticks")){
                int n = Integer.parseInt(parser.getAttributeValue(0));
                while (parser.nextTag () != XmlPullParser.END_TAG){
                         DailyJobTick tick = readTickData(parser);
//                         String key = tick.getDateAsString();
                         job.getTicks().addElement(tick);
                }
            }else{
                String text = parser.nextText();
                //#debug
                System.out.println ("<"+name+">"+text);
                if (name.equals("title"))
                    job.setTitle(text);
                else if (name.equals("text"))
                    job.setText(text);
                else if (name.equals("n"))
                    job.setN(Integer.parseInt(text));
                else if (name.equals("id"))
                    job.setId(Integer.parseInt(text));
                else if (name.equals("importance"))
                    job.setImportance(Integer.parseInt(text));
            }
            parser.require(XmlPullParser.END_TAG, null, name);
        }
        System.out.println(job.getTitle() + " - " + job.getN());
        jobList.addElement(job);
        parser.require(XmlPullParser.END_TAG, null, "dailyjob");
    }

    public static void refreshLists() throws IOException{
        Vector l = getJobList();
        int k = l.size();
        DailyJob cur;
        Image img = null;
        for (int i = 0; i < 3; i++)
            mlet.jobList[i].deleteAll();
        System.gc();
        for (int j = 0; j < k; j++){
            cur = (DailyJob)l.elementAt(j);
//            cur.printTicks();
            for (int i = 0; i < 3; i++){
                DailyJobTick o = cur.isTicked(dates[i]);
                if (o != null){
                    if (cur.isDone(o, dates[i])){
                        img = mlet.getGreenImage();
                    }else{
                        img = mlet.getRedImage();
                    }
                }else{
                    img = mlet.getGrayImage();
                }
//                new String("","UT")
                mlet.jobList[i].append(cur.getTitle(), img);
            }
        }
//        saveJobList();
    }
    public static void refreshListItem(int list, int itemId){
        DailyJob job = (DailyJob)getJobList().elementAt(itemId);;
        Image img;
        DailyJobTick o = job.isTicked(dates[list]);
        if (o != null){
            if (job.isDone(o, dates[list])){
                img = mlet.getGreenImage();
            }else{
                img = mlet.getRedImage();
            }
        }else{
            img = mlet.getGrayImage();
        }
        mlet.jobList[list].set(itemId, job.getTitle(), img);
    }
    public static void refreshJobList(boolean force){
        Thread t = new Thread(new LoadConfThread(force));
        t.start();
    }

    public static String getDoneUrl(SimpleDate d, DailyJob job, int n){
        StringBuffer s = new StringBuffer("http://");
        s.append(domain);
        s.append(jobapp_p);
        s.append("/");
        s.append(d.getYear());
        s.append("/");
        s.append(SimpleDate.intToStr(d.getMonth(), 2));
        s.append("/");
        s.append(SimpleDate.intToStr(d.getDay(), 2));
        s.append("/done/");
        s.append(job.getId());
        s.append("/n/");
        s.append(n);
        s.append("/mobile/");
        return s.toString();
    }

    public static void pressDone(int n){
        int k = mlet.jobList[curTab].getSelectedIndex();
        int curItem  = mlet.jobList[curTab].getSelectedIndex();
        DailyJob the_job = (DailyJob)jobList.elementAt(curItem);
        int the_n = n;
        if (n == -1)
          the_n = the_job.getN();
        Thread t = new Thread(new TickThread(the_job, the_n, curTab, curItem));
        t.start();
    }

    public static class LoadConfThread implements Runnable{
        
        boolean force_update;
        
        public LoadConfThread(boolean force_update){
            this.force_update = force_update;
        }

        public void run() {
            try{
                if (WS.settingBoolean("automaticLoad") || force_update){
                    loadTheConf();
                }
                refreshLists();
            }catch(Exception e){
                mlet.switchDisplay(mlet.getLoadErrorAlert());
                //#debug
                e.printStackTrace();
                //#ifdef polish.debugEnabled
    //#                     e.printStackTrace();
                //#endif
            }
        }
    }

    public static class TickThread implements Runnable {

        int n, currentTab, curItem;
        DailyJob job;
        SimpleDate date;

        public TickThread(DailyJob job, int n, int currentTab, int curItem){
            this.job = job; this.n = n; this.date = dates[currentTab];
            this.currentTab  = currentTab; this.curItem = curItem;
        }

        public void run() {
            try{
                String s = loadPage(auth(getDoneUrl(date, job, n), getUser()));
                if (s.equals("OK")){
                    DailyJobTick tick = job.isTicked(date);
                    if (tick == null){
                        tick = new DailyJobTick();
                        tick.setDate(date);
                    }
                    tick.setDone(n);
                }
                refreshListItem(currentTab, curItem);
            }catch(Exception e){
                mlet.switchDisplay(mlet.getLoadErrorAlert());
                //#debug
                e.printStackTrace();
                //#ifdef polish.debugEnabled
    //#                     e.printStackTrace();
                //#endif
            }
        }

    }

}
