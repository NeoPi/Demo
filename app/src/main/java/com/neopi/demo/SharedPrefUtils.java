package com.neopi.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by neopi on 16-7-8.
 */
public class SharedPrefUtils {

  private static class SharedPrefHolder{
    private static final SharedPrefUtils INSTANCE = new SharedPrefUtils();
  }

  private SharedPrefUtils(){
  }

  public static SharedPrefUtils getInstance(){
    return SharedPrefHolder.INSTANCE;
  }

  public synchronized  void putStringPref(Context context,String key,String value){
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    editor.putString(key,value);
    editor.commit();
  }

  public String getStringPref(Context context,String key,String defaultString){
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    String values = sp.getString(key,defaultString);
    return values;
  }


}
