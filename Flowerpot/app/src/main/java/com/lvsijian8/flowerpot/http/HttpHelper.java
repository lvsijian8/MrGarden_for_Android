package com.lvsijian8.flowerpot.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/3/11.
 */
public class HttpHelper {
    private static final String URLpath="";

    public static String getGsondata(HashMap<String,Object> map){
        HttpURLConnection connection = null;
        try {
            URL url=new URL(URLpath);
            connection= (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10*1000);
            connection.connect();
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
}
