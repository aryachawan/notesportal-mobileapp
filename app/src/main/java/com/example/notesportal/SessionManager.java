package com.example.notesportal;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "notesportal";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context){
        prefs = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void SaveSession(String username,String email){
        editor.putBoolean(KEY_LOGGED_IN,true);
        editor.putString(KEY_USERNAME,username);
        editor.putString(KEY_EMAIL,email);
        editor.apply();
    }

    public boolean isLoggedIn(){
        return prefs.getBoolean(KEY_LOGGED_IN,false);
    }

    public String getUsername(){
        return prefs.getString(KEY_USERNAME,null);
    }

    public String getEmail(){
        return prefs.getString(KEY_EMAIL,null);
    }

    public void DestroySession(){
        editor.clear();
        editor.apply();
    }
}
