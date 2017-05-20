package com.lvsijian8.flowerpot.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lvsijian8.flowerpot.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String data=TestHttp.getTestData("http://www.baidu.com",null);
//        String data=TestHttp.XHttpTest();
        Log.e("ZDLW",data);
    }
}
