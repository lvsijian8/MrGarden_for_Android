package com.lvsijian8.flowerpot.ui.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.Flower_Find;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;

import java.util.HashMap;

public class shiyan extends AppCompatActivity {


    private HttpHelper httpHelper;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiyan);
        initui();
        initdata();
    }

    private void initui() {
        gson = new Gson();
        httpHelper = HttpHelper.getInstances();
    }

    private void initdata() {
        HashMap<String,Object> parms=new HashMap<>();
        parms.put("fid",0+"");
        httpHelper.getJsonData(Const.URL_FIND,parms);
        httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                Log.e("ZDLW", data);
                Flower_Find c = gson.fromJson(data, Flower_Find.class);
                if (c != null) {
                    Flower_Find.flower f = c.data.get(0);
                    Log.e("ZDLW", f.namec);
                }
            }

            @Override
            public void failConnect() {

            }
        });
    }

    private Handler mhandler =new Handler();
}
