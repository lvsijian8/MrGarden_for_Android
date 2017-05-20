package com.lvsijian8.flowerpot.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.global.GoglePlayApplication;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2016/11/20.
 */
public class UIUtils {
    public static Context getContext(){
        return GoglePlayApplication.getContext();
    }
    public static Handler getHandle(){
        return GoglePlayApplication.getHandler();
    }
    public static int geMainThreadId(){
        return GoglePlayApplication.getMainThreadid();
    }
    public static Typeface getTypeFace(){return GoglePlayApplication.getTypeface();}//获取字体文件
    public static Gson getGson(){return GoglePlayApplication.getGson();}//返回Gson
    ////////////////////////加载资源文件//////////////////////////////

    /**
     * 获取字符串
     * @param id
     * @return
     */
    public static String getString(int id){
        return getContext().getResources().getString(id);
    }

    /**
     * 获取字符串数组

     */
    public static String[] getStringArray(int id){
        return getContext().getResources().getStringArray(id);
    }
    //获取图片
    public static Drawable getDrawable(int id){
        return getContext().getResources().getDrawable(id);
    }

    public static int  getColor(int id){
        return getContext().getResources().getColor(id);
    }

    //根据id获取颜色的状态选择器
    public static ColorStateList getColorStateList(int id){
        return getContext().getResources().getColorStateList(id);
    }

    ////////////////////////加载资源文件//////////////////////////////

    //返回具体的像素值，保存的是dp，返回的是px。
    public static int getDime(int id) {
        return getContext().getResources().getDimensionPixelSize(id);}

    //dp转px
    public static int dip2px(float dip){
        float density=getContext().getResources().getDisplayMetrics().density;//设备密度
        return (int) (dip*density+0.5f);//加0.5f是为了四合五入
    }

    //px转dp
    public static float px2dip(float px){
        float density=getContext().getResources().getDisplayMetrics().density;//设备密度
        return px/density;
    }

    //加载布局文件
    public static View inflate(int id){
        return View.inflate(getContext(),id,null);
    }

    //判断是否在主线程运行
    public static boolean isRunOnUiThread(){
        int nowid=android.os.Process.myTid();
        return nowid == geMainThreadId();
    }

    //运行在主线程的方法
    public static void runOnUiThread(Runnable r){
        if (isRunOnUiThread()){
            r.run();//在主线程,直接运行
        }else {
            getHandle().post(r);//在子线程，借助handle，运行在主线程
        }
    }

    //获取sp以及设置sp
    private static SharedPreferences sp=getContext().getSharedPreferences("config",Context.MODE_PRIVATE);
    private static SharedPreferences.Editor editor=sp.edit();

    public static void  setSpNumInt(String key,int value)
    {
        editor.putInt(key,value).commit();
    }
    public static void  setSpString(String key,String value) {
        editor.putString(key, value).commit();
    }
    public static void  setSpBoolean(String key,boolean value){
        editor.putBoolean(key, value).commit();
    }

    public static int getSpInt(String key){
        return sp.getInt(key,-1);
    }
    public static String getSpString(String key){
        return sp.getString(key, null);
    }
    public static boolean getSpBoolean(String key){
        return sp.getBoolean(key, false);
    }
    //----------------------------------------------------------------------------------------
    public static int currentapiVersion=android.os.Build.VERSION.SDK_INT;//获取api版本好

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName() {
        String versionName = "";
        int versioncode=0;
        try {
            // ---get the package info---
            PackageManager pm = getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo( getContext().getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 返回系统当前时间
     * @return
     */
    public static String currentTime(){
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

}
