package com.lvsijian8.flowerpot.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import com.lvsijian8.flowerpot.global.GoglePlayApplication;


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
        if (nowid==geMainThreadId()){
            return true;
        }else {
            return false;
        }
    }

    //运行在主线程的方法
    public static void runOnUiThread(Runnable r){
        if (isRunOnUiThread()){
            r.run();//在主线程,直接运行
        }else {
            getHandle().post(r);//在子线程，借助handle，运行在主线程
        }
    }
}
