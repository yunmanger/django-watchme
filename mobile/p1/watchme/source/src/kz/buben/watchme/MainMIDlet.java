/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kz.buben.watchme;

import de.enough.polish.ui.TabbedForm;
import de.enough.polish.ui.TabbedPane;
import de.enough.polish.util.Locale;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 *
 * @author german
 */
public class MainMIDlet extends MIDlet implements CommandListener{

    List menuScreen;
    List [] jobList = new List[3];
    TabbedForm settingsForm;
    TextField usernameInput;
    TextField passwordInput;
    Alert alert;
//    TabbedPane pane;
    Image red, green, gray;
    Command enterCmd = new Command(Locale.get( "menu.enter" ), Command.ITEM, 8);
    Command quitCmd = new Command(Locale.get( "menu.quit" ), Command.EXIT, 10);
    Command backCmd = new Command(Locale.get("menu.back"), Command.BACK, 100);
    Command saveCmd = new Command(Locale.get("menu.save"), Command.ITEM, 20);
    Command doneCmd = new Command(Locale.get("menu.done"), Command.ITEM, 20);
    Command updCmd = new Command(Locale.get("menu.update"), Command.ITEM, 20);
    Command notdoneCmd = new Command(Locale.get("menu.notdone"), Command.ITEM, 20);
    //#ifdef polish.debugEnabled
//#     Command refCmd = new Command("refresh", Command.ITEM, 15);
    //#endif
    Display display;
    ChoiceGroup settings;

    public MainMIDlet(){
        super();
        try{
            WS.init(this);
            //#style startMenu
            this.menuScreen = new List(Locale.get( "menu.title" ), List.IMPLICIT);
//            //#style startMenuItem
//            menuScreen.append(Locale.get("menu.dailyjobs"), null);
            //#style startMenuItem
            menuScreen.append(Locale.get("global.today"), null);
            //#style startMenuItem
            menuScreen.append(Locale.get("global.yesterday"), null);
            //#style startMenuItem
            menuScreen.append(Locale.get("global.dbyesterday"), null);
            //#style startMenuItem
            menuScreen.append(Locale.get("menu.settings"), null);
            //#style startMenuItem
            menuScreen.append(Locale.get("menu.quit"), null);
            menuScreen.addCommand(enterCmd);
            menuScreen.addCommand(quitCmd);
            menuScreen.setCommandListener(this);

            String [] tabtitles = {Locale.get("settings.accountsettings"),
            Locale.get("settings.programsettings")};
            //#style tabbedForm
            settingsForm = new TabbedForm(Locale.get("settings.title"),tabtitles,null);
            User u = WS.getUser();
            usernameInput = new TextField(Locale.get("global.username"), u.getUsername(), 20, TextField.ANY);
            passwordInput = new TextField(Locale.get("global.password"), u.getPassword(), 20, TextField.PASSWORD);

            settingsForm.append(0, usernameInput);
            settingsForm.append(0, passwordInput);
            settingsForm.setActiveTab(0);
            settings = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
            Enumeration l = WS.getSettingList();
            int g = 0;
            while (l.hasMoreElements()){
                String label = (String)l.nextElement();
                settings.append(label, null);
                settings.setSelectedIndex(g, WS.settingBoolean(label));
                g+=1;
            }
            settingsForm.append(1, settings);
            settingsForm.addCommand(backCmd);
            settingsForm.addCommand(saveCmd);
            //#ifdef polish.debugEnabled
//#             settingsForm.addCommand(refCmd);
            //#endif
            settingsForm.setCommandListener(this);

            for (int i = 0; i < 3; i++){
                //#style listItself
                jobList[i] = new List(WS.dates[i].toString(), List.IMPLICIT);
                jobList[i].addCommand(updCmd);
                jobList[i].addCommand(backCmd);
                jobList[i].addCommand(doneCmd);
                jobList[i].addCommand(notdoneCmd);
                jobList[i].setCommandListener(this);
            }
            alert = new Alert(Locale.get("global.warning"));
            alert.setCommandListener(this);
            WS.refreshLists();
        }catch(Exception e){
            //#debug
            System.out.println(e);
        }
    }

    protected void startApp() throws MIDletStateChangeException {
        this.display = Display.getDisplay(this);
        this.display.setCurrent(menuScreen);
    }

    protected void pauseApp() {
        System.gc();
        //pass
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        //pass
    }

    public void switchDisplay(Displayable now){
        Display d = Display.getDisplay(this);
        d.setCurrent(now);
    }

    public Alert getLoadErrorAlert(){
        alert.setString(Locale.get("alert.loaderror"));
        alert.setTimeout(10000);
        return alert;
    }

    public Alert getSaveErrorAlert(){
        alert.setString(Locale.get("alert.saveerror"));
        alert.setTimeout(10000);
        return alert;
    }

    public Image getGreenImage(){
        if (green == null){
            try{
                green = Image.createImage("/bullet_green.png");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return green;
    }

    public Image getRedImage(){
        if (red == null){
            try{
                red = Image.createImage("/bullet_red.png");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return red;
    }

    public Image getGrayImage(){
        if (gray == null){
            try{
                gray = Image.createImage("/bullet_black.png");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return gray;
    }

    public void save(){
        if (WS.settingBoolean("saveOnClose")){
            try{
                WS.saveAll();
            }catch(IOException e){
                switchDisplay(getSaveErrorAlert());
                e.printStackTrace();
            }
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (d == this.menuScreen){
            if (c == List.SELECT_COMMAND){
                int k = menuScreen.getSelectedIndex();
                if (k == 0 || k == 1 || k == 2){
                    WS.curTab = k;
                    switchDisplay(jobList[k]);
                    WS.refreshJobList(false);
                }else if (k == 3){
                    switchDisplay(settingsForm);
                }else if (k == 4){
                    save();
                    notifyDestroyed();
                }
            }else if (c == this.quitCmd){
                save();
                notifyDestroyed();
            }
        }else if (d == settingsForm){
                if (c == backCmd){
                WS.getUser().setUsername(usernameInput.getString());
                WS.getUser().setPassword(passwordInput.getString());
                Hashtable s = WS.getSettings();
                int k = settings.size();
                for (int i = 0; i < k; i++){
                    if (settings.isSelected(i)){
                        s.put(settings.getString(i), Boolean.TRUE);
                    }else{
                        s.put(settings.getString(i), Boolean.FALSE);
                    }
                }
                switchDisplay(menuScreen);
            }else if (c == saveCmd){
                try{
                    WS.saveAccountState();
                }catch(IOException e){
                    //#debug
                    System.out.println(e.toString());
                }
            }
            //#ifdef polish.debugEnabled
//#             else if (c == refCmd){
//#                 User u = WS.getUser();
//#                 usernameInput.setString(u.getUsername());
//#                 passwordInput.setString(u.getPassword());
//#             }
            //#endif
        }
        else if (d == alert){
            if (c == Alert.DISMISS_COMMAND){
                alert.setTimeout(0);
            }
        }else if (d == jobList[WS.curTab]){
            if (c == backCmd){
                switchDisplay(menuScreen);
            }else if (c == doneCmd){
                WS.pressDone(-1);
            }else if (c == notdoneCmd){
                WS.pressDone(0);
            }else if (c.getCommandType() == TabbedPane.RIGHT){
                WS.next(1);
            }else if (c.getCommandType() == TabbedPane.LEFT){
                WS.next(-1);
            }else if (c == updCmd){
                try{
                    WS.refreshJobList(true);
                }catch(Exception e){
                    switchDisplay(getLoadErrorAlert());
                }
            }
        }
    }

}
