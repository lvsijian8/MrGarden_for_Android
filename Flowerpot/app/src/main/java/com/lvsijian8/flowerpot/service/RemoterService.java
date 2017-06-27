package com.lvsijian8.flowerpot.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpOkHelper;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/25.
 */
public class RemoterService extends Service{
    private Messenger ActivityMessenger;
    private Messenger ServicerMessenger;
    private Handler mHandler;
    private HashMap<String,Object> params;
    private HttpOkHelper mOkHelper;
    private Timer mTimer;
    private TimerTask mTask;

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread=new HandlerThread("RemoterService");
        handlerThread.start();
        mHandler=new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Const.SERVICE_REMOTER:
                        if (ActivityMessenger==null){
                            ActivityMessenger=msg.replyTo;
                        }
                        params.put("pot_id", msg.arg1 + "");
                        Log.e("ZDLW","接收从Activity发送来的信息");
                        StarConnection();
                        break;
                }
            }
        };
        init();

    }

    private void init(){
        params=new HashMap<>();
        mOkHelper=HttpOkHelper.getInstances();
        mOkHelper.setOnConnectMenuListener(new HttpOkHelper.OnConnectMenuListener() {
            @Override
            public void onSuccess(String data) {
                if (!TextUtils.isEmpty(data)){
                    Message message=new Message();
                    message.what=Const.REMOTE_CONNECTION_SUCCESS;
                    message.obj=data;
                    try {
                        ActivityMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFail() {

            }
        });
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                //间隔n秒执行以下操作
                mOkHelper.ConnectHttp(Const.URL_REMOTE,params);
            }
        };
        ServicerMessenger=new Messenger(mHandler);
    }

    private void StarConnection(){
        mTimer.schedule(mTask,300,3000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return ServicerMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }

        if (mTask!=null){
            mTask.cancel();
            mTask=null;
        }
    }
}
