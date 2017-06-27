package com.lvsijian8.flowerpot.http;

import com.lvsijian8.flowerpot.utils.UIUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/6.
 */
public class HttpOkHelper {
    private static HttpOkHelper helper=null;
    private  OkHttpClient mclient;
    private static Call mcall;
    private HttpOkHelper(){
        mclient=new OkHttpClient();
    }

    public static HttpOkHelper getInstances(){
        if(helper==null){
            synchronized (HttpHelper.class){
                if (helper==null){
                    helper=new HttpOkHelper();
                }
            }
        }
        return helper;
    }

    public  void ConnectHttp(String UrlPath,HashMap<String,Object> paramMap){
        FormBody.Builder builder=new FormBody.Builder();
        if (paramMap!=null&&paramMap.size()>0){
            for (Map.Entry<String,Object> entry : paramMap.entrySet()){
                builder.add(entry.getKey(), entry.getValue()+"");
            }
        }
        Request request=new Request.Builder()
                .url(UrlPath)
                .post(builder.build())
                .build();
        mcall= mclient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.Log_e(HttpOkHelper.class,"Connect Fail");
                if (mMenuListener!=null){
                    mMenuListener.onFail();
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                UIUtils.Log_e(HttpOkHelper.class,"Connect Success");
                String data=response.body().string().trim();
                if (mMenuListener!=null){
                    mMenuListener.onSuccess(data);
                }
            }
        });
    }


    /**
     * 关闭网络连接
     */
    public static void CancleCall(){
        if (mcall!=null){
            mcall.cancel();
        }
    }

    public interface OnConnectMenuListener{
        void onSuccess(String data);
        void onFail();
    }

    private static OnConnectMenuListener mMenuListener;
    public void setOnConnectMenuListener(OnConnectMenuListener listener){
        mMenuListener=listener;
    }
}
