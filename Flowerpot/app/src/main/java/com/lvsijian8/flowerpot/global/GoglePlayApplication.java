package com.lvsijian8.flowerpot.global;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.ViewConfiguration;

import com.google.gson.Gson;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/11/20.
 */
public class GoglePlayApplication extends Application {
    private static Context context;
    private static Handler handler;
    private static int mainThreadid;
    private static Typeface typeface;
    private static Gson gson;

    @Override
    public void onCreate() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception ex) {
            // Ignore
        }
        super.onCreate();
        context=getApplicationContext();
        handler=new Handler();
        mainThreadid=android.os.Process.myTid();//主线程ID
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/pop.ttf");
        gson=new Gson();

    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadid() {
        return mainThreadid;
    }
    public static Typeface getTypeface(){
        return typeface;
    }
    public static Gson getGson(){return gson;}

}
