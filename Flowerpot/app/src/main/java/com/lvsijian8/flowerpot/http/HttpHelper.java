package com.lvsijian8.flowerpot.http;

import android.util.Log;

import com.lvsijian8.flowerpot.global.Const;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/11.
 */
public class HttpHelper {



    private static HttpHelper helper=null;

    private HttpHelper(){
        client=new OkHttpClient();
    }

    public static HttpHelper getInstances(){
        if(helper==null){
            synchronized (HttpHelper.class){
                if (helper==null){
                    helper=new HttpHelper();
                }
            }
        }
        return helper;
    }

    public static String getGsondata(HashMap<String,Object> paramMap){
        HttpURLConnection connection = null;
        PrintWriter printWriter=null;
        StringBuilder text = new StringBuilder();
        for(Map.Entry<String,Object> entry : paramMap.entrySet()) { //在for循环中拼接报文，上传文本数据
            text.append( entry.getKey());
            text.append("=");
            text.append(entry.getValue());
            text.append("&");
        }
        if (text.length()>0){
            text.deleteCharAt(text.length()-1);
        }
        try {
            URL url=new URL("");
            connection= (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestMethod("POST");
            printWriter = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            printWriter.write(text.toString());
            // flush输出流的缓冲
            printWriter.flush();
            int code=connection.getResponseCode();
            if (code==200){
                String  data=readStream(connection.getInputStream());
                return data;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            connection.disconnect();
        }
        return null;
    }

    private static String readStream(InputStream ips){
        ByteArrayOutputStream bops=new ByteArrayOutputStream();
        int len=-1;
        byte b[]=new byte[1024];
        try {
            while ((len=ips.read(b))!=-1){
                bops.write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data=bops.toString();
        try {
            bops.flush();
            bops.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 联网
     * @param path
     * @param paramMap
     */
    public void getJsonData(String path,HashMap<String,Object> paramMap){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder=new FormBody.Builder();
        if (paramMap!=null&&paramMap.size()>0){
            for (Map.Entry<String,Object> entry : paramMap.entrySet()){
                builder.add(entry.getKey(), entry.getValue()+"");
            }
        }
        Request request=new Request.Builder().url(path).post(builder.build()).build();
        Call call= client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ZDLW", "onFailure");
                if (listener != null) {
                    listener.failConnect();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ZDLW", "onResponse");
                String data = response.body().string().trim();
                if (listener != null) {
                    listener.successConnect(data);
                }
            }
        });
    }

    /**
     * find界面获取数据
     * @param path
     * @param paramMap
     */
    public void getFindData(String path,HashMap<String,Object> paramMap){
        OkHttpClient client = new OkHttpClient();
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

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ZDLW", "onResponse");
                String data = response.body().string();
                if (findListener != null) {
                    findListener.successGet(data);
                }
            }
        });
    }




    /**
     * 浇水管理
     * @param path
     * @param paramMap
     */
    public void WaterManager(String path,HashMap<String,Object> paramMap){
        OkHttpClient client=new OkHttpClient();
        FormBody.Builder builder=new FormBody.Builder();
        if (paramMap!=null&&paramMap.size()>0){
            for (Map.Entry<String,Object> entry : paramMap.entrySet()){
                builder.add(entry.getKey(), (String) entry.getValue());
            }
        }
        Request request=new Request.Builder().url(path).post(builder.build()).build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ZDLW","onFailure");
                if (Wmanagerlistener!=null){
                    Wmanagerlistener.failW();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ZDLW","onResponse");
                String data=response.body().string();
                if (Wmanagerlistener!=null){
                    Wmanagerlistener.successW(data);
                }
            }
        });
    }

    /**
     * 施肥管理
     * @param path
     * @param paramMap
     */
    public void BottleManager(String path,HashMap<String,Object> paramMap){
        OkHttpClient client=new OkHttpClient();
        FormBody.Builder builder=new FormBody.Builder();
        if (paramMap!=null&&paramMap.size()>0){
            for (Map.Entry<String,Object> entry : paramMap.entrySet()){
                builder.add(entry.getKey(), (String) entry.getValue());
            }
        }
        Request request=new Request.Builder().url(path).post(builder.build()).build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ZDLW","onFailure");
                if (Bmanagerlistener!=null){
                    Bmanagerlistener.failB();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ZDLW","onResponse");
                String data=response.body().string();
                if (Bmanagerlistener!=null){
                    Bmanagerlistener.successB(data);
                }
            }
        });
    }

    /**
     * 施肥
     */
    public void Bottle(){
        Request request=new Request.Builder().url(Const.URL_PATH +Const.URL_BOTTLE).build();
        OkHttpClient client=new OkHttpClient();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (Bottlelistener!=null){
                    Bottlelistener.failBottlet();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (Bottlelistener!=null){
                    Bottlelistener.successBottle(response.body().string());
                }
            }
        });
    }

    /**
     * 浇水
     */
    public void Watering(){
        Request request=new Request.Builder().url(Const.URL_PATH +Const.URL_WATER).build();
        OkHttpClient client=new OkHttpClient();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (Waterlistener!=null){
                    Waterlistener.failWater();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (Waterlistener!=null){
                    Waterlistener.successWater(response.body().string());
                }
            }
        });
    }

    /**
     * 联网接口
     */
    public interface OnConnectionListener{
        void successConnect(String data);
        void failConnect();
    }
    private OnConnectionListener listener=null;
    public void setOnConnectionListener(OnConnectionListener listener){
        this.listener=listener;
    }

    /**
     * 细节数据接口
     */
    public interface OnDetailgetData{
        void successGet(String data);
        void failGet();
    }
    private OnDetailgetData detailListener=null;
    public void setOnDetailgetData(OnDetailgetData detailListener){this.detailListener=detailListener;}

    //Find界面
    public interface OnFindListener{
        void successGet(String data);
    }
    private OnFindListener findListener=null;
    public void setOnFindtData(OnFindListener findListener){this.findListener=findListener;}

    /**
     * 施肥接口
     */
    public interface OnBottleListener{
        void successBottle(String data);
        void failBottlet();
    }
    private OnBottleListener Bottlelistener=null;
    public void setOnBottleListener(OnBottleListener listener){
        this.Bottlelistener=listener;
    }

    /**
     * 浇水接口
     */
    public interface OnWaterListener{
        void successWater(String data);
        void failWater();
    }
    private OnWaterListener Waterlistener=null;
    public void setOnWaterListener(OnWaterListener listener){
        this.Waterlistener=listener;
    }

    /**
     * 浇水管理接口
     */
    public interface OnWaterManagerListener{
        void successW(String data);
        void failW();
    }
    private OnWaterManagerListener Wmanagerlistener=null;
    public void setOnWaterManagerListener(OnWaterManagerListener listener){
        this.Wmanagerlistener=listener;
    }

    /**
     * 施肥管理接口
     */
    public interface OnBottleManagerListener{
        void successB(String data);
        void failB();
    }
    private OnBottleManagerListener Bmanagerlistener=null;
    public void setOnBottleListener(OnBottleManagerListener listener){
        this.Bmanagerlistener=listener;
    }


    public void getDetailData(String path,HashMap<String,Object> paramMap){
        OkHttpClient client = new OkHttpClient();
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
                Log.e("ZDLW","onFailure");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ZDLW","onResponse");
                String data=response.body().string().trim();
                if (detailListener!=null){
                    detailListener.successGet(data);
                }
            }
        });
    }


    private static OkHttpClient client=null;
    public static void  OkClient(String path,HashMap<String,Object> paramMap){
        FormBody.Builder builder=new FormBody.Builder();
        if (paramMap!=null&&paramMap.size()>0){
            for (Map.Entry<String,Object> entry : paramMap.entrySet()){
                builder.add(entry.getKey(), (String) entry.getValue());
            }
        }
        Request request=new Request.Builder().url(path).post(builder.build()).build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ZDLW","onFailure");
                if (clientListener!=null){
                    clientListener.onFailure();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ZDLW","onResponse");
                String data=response.body().string().trim();
                if (clientListener!=null){
                    clientListener.onResponse(data);
                }
            }
        });
    }
    public interface OkClientListener{
        void onFailure();
        void onResponse(String data);
    }

    private static OkClientListener clientListener=null;
    public void setOkClientListener(OkClientListener clientListener){
        HttpHelper.clientListener =clientListener;
    }

    /**
     * Time用
     * @param path
     * @param paramMap
     */
    public void getTimeData(String path,HashMap<String,Object> paramMap){
        OkHttpClient client = new OkHttpClient();
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
                Log.e("ZDLW","onFailure");
                if (TimeListener!=null){
                    TimeListener.failGet();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ZDLW","onResponse");
                String data=response.body().string().trim();
                if (TimeListener!=null){
                    TimeListener.successGet(data);
                }
            }
        });
    }
    public interface OnTimegetData{
        void successGet(String data);
        void failGet();
    }
    private OnTimegetData TimeListener=null;
    public void setOnTimegetData(OnTimegetData TimeListener){this.TimeListener=TimeListener;}

}
