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
public class User implements Serializable {
    private String username = "";
    private String password = "";
//    private String key = "";

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }



}
