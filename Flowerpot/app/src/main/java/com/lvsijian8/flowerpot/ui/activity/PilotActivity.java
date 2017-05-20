package com.lvsijian8.flowerpot.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lvsijian8.flowerpot.MainActivity;
import com.lvsijian8.flowerpot.R;

public class PilotActivity extends AppCompatActivity {
    private Handler mhandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            startActivity(new Intent(PilotActivity.this, MainActivity.class));
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilot);
        getSupportActionBar().hide();
        mhandle.sendMessageDelayed(new Message(),2000);
    }
}
