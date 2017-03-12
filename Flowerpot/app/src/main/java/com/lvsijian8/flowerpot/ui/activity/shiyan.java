package com.lvsijian8.flowerpot.ui.activity;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.ui.view.Relistview;

import java.util.ArrayList;
import java.util.Random;

public class shiyan extends AppCompatActivity {
    private Relistview relistview;
    private ArrayList<String> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiyan);
        initui();
        initdata();
    }

    private void initui() {
        relistview= (Relistview) findViewById(R.id.relist_shiyan);
        relistview.setOnRershListener(new Relistview.OnRefreshListener() {
            @Override
            public void onloding() {

            }

            @Override
            public void onReresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(new Random().nextInt(1000)+500);
                        mhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                relistview.CloseRersh();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void initdata() {
        data=new ArrayList<>();
        for (int i=0;i<100;i++){
            data.add("data"+i);
        }
        relistview.setAdapter(new MyAdapter());
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView=new TextView(getApplicationContext());
            textView.setText((String)getItem(position));
            return textView;
        }
    }

    private Handler mhandler =new Handler();
}
