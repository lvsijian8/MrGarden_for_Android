package com.lvsijian8.flowerpot.http;

import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

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
 * Created by Administrator on 2017/4/18.
 */
public class TestHttp {
    private static OkHttpClient client;
    private static TestHttp testHttp=null;
    private TestHttp(){
        client=new OkHttpClient();
    }

    public static TestHttp getInstances(){
        if (testHttp==null){
            synchronized (TestHttp.class){
                if (testHttp==null){
                    testHttp=new TestHttp();
                }
            }
        }
        return testHttp;
    }

    private static String data="g";
    public static String getTestData(String path,HashMap<String,Object> paramMap){
        client = new OkHttpClient();
        FormBody.Builder builder=new FormBody.Builder();
        if (paramMap!=null&&paramMap.size()>0){
            for (Map.Entry<String,Object> entry : paramMap.entrySet()){
                builder.add(entry.getKey(), (String) entry.getValue());
            }
        }
        Request request=new Request.Builder().url(path).post(builder.build()).build();
        Call call= client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ZDLW", "onFailure");
                data="shibai";
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ZDLW","onResponse");
                data="chenggong";
            }
        });
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void CancleClient(){

    }


    public static String XHttpTest(){
        HttpUtils utils=new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET,
                "http://www.lidroid.com",
                new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        data= (String) responseInfo.result;

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }



}

