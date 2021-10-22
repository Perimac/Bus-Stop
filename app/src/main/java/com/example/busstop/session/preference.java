package com.example.busstop.session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class preference {
    private static final String DATA_LOGIN = "status_login";
    private static final String DATA_AS = "as";

    private static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setDataAs(Context context, String data){
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor preferenceEditor = getSharedPreference(context).edit();
         preferenceEditor.putString(DATA_AS,data);
         preferenceEditor.apply();
    }

    public static String getDataAs(Context context){
        return getSharedPreference(context).getString(DATA_AS,"");
    }

    public static void setDataLogin(Context context,boolean status){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(DATA_LOGIN,status);
        editor.apply();
    }

    public static boolean getDataLogin(Context context){
        return getSharedPreference(context).getBoolean(DATA_LOGIN,false);
    }
    public static void clearData(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(DATA_AS);
        editor.remove(DATA_LOGIN);
        editor.apply();
    }


}
