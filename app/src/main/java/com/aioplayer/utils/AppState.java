package com.aioplayer.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by akankshadhanda on 16/07/17.
 */

public class AppState {
    private Context context;
    private SharedPreferences sharedPreferences;
    private AppState(Context context)
    {
        this.context=context;
        this.sharedPreferences=context.getSharedPreferences("appstate", MODE_PRIVATE);
    }
    public static AppState getInstance(Context context)
    {
        return new AppState(context);
    }
    public void saveBoolean(String key,boolean value)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public boolean getBoolean(String key)
    {
        return sharedPreferences.getBoolean(key,false);
    }




}
