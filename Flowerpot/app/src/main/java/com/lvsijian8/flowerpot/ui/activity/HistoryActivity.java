package com.lvsijian8.flowerpot.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.History;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.view.Relistview;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {
    private ImageView iv_back;
    private HttpHelper httpHelper;
    private Relistview relistview;
    private ArrayList<History.HistoryData> datas;
    private Gson gson;
    private HashMap<String, Object> params;
    private int mcurrent=1;
    private static final int HISTORY_SUCCESS_REFRESH =0;
    private static final int HISTORY_SUCCESS_LOADING=1;
    private static final int HISTORY_FAIL=2;
    private int HISTORY_STATE=HISTORY_SUCCESS_REFRESH;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            History history=gson.fromJson((String)msg.obj,History.class);
            if (history==null){
                relistview.Closeloading();
                Toast.makeText(UIUtils.getContext(),"没有更多数据",Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<History.HistoryData> data=history.data;
            switch (msg.what){
                case HISTORY_SUCCESS_LOADING:
                    relistview.Closeloading();
                    datas.addAll(data);
                    myAdapter.notifyDataSetChanged();
                    Toast.makeText(UIUtils.getContext(),"加载成功",Toast.LENGTH_SHORT).show();
                    layout_error.setVisibility(View.GONE);//隐藏错误界面
                    pb_loading.setVisibility(View.GONE);//隐藏读取界面
                    relistview.setVisibility(View.VISIBLE);//显示结果界面
                    break;
                case HISTORY_SUCCESS_REFRESH:
                    relistview.CloseRersh();
                    datas=data;
                    mcurrent=1;//将索引指向第一页
                    myAdapter.notifyDataSetChanged();
                    Toast.makeText(UIUtils.getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
                    layout_error.setVisibility(View.GONE);//隐藏错误界面
                    pb_loading.setVisibility(View.GONE);//隐藏读取界面
                    relistview.setVisibility(View.VISIBLE);
                    break;
                case HISTORY_FAIL:
                    layout_error.setVisibility(View.VISIBLE);//显示错误界面
                    pb_loading.setVisibility(View.GONE);//隐藏读取界面
                    relistview.setVisibility(View.GONE);//隐藏结果界面
                    break;
            }

        }
    };
    private Message message;
    private MyAdapter myAdapter;
    private LinearLayout layout_error;
    private Button btn_error;
    private ProgressBar pb_loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().hide();
        initui();
        initdata();
    }

    private void initui() {
        httpHelper = HttpHelper.getInstances();
        gson=new Gson();
        params = new HashMap<>();
        datas=new ArrayList<>();
        iv_back= (ImageView) findViewById(R.id.iv_history_back);
        relistview = (Relistview) findViewById(R.id.rlv_history);
        layout_error = (LinearLayout) findViewById(R.id.layout_history_error);
        btn_error = (Button) findViewById(R.id.btn_history_error);
        pb_loading = (ProgressBar) findViewById(R.id.progress_history_loading);
        message = new Message();
        myAdapter = new MyAdapter();
        relistview.setAdapter(myAdapter);

    }
    private void initHttp(){
        httpHelper.setOkClientListener(new HttpHelper.OkClientListener() {
            @Override
            public void onFailure() {
                message=new Message();
                message.what=HISTORY_FAIL;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(String data) {
                Log.e("ZDLW",data);
                message=new Message();
                if (HISTORY_STATE==HISTORY_SUCCESS_LOADING){
                    message.what=HISTORY_SUCCESS_LOADING;
                }else if (HISTORY_STATE==HISTORY_SUCCESS_REFRESH){
                    message.what=HISTORY_SUCCESS_REFRESH;
                }
                message.obj=data;
                mHandler.sendMessage(message);

            }
        });
        params.clear();
        params.put("page", 1 + "");
        params.put(Const.USER_ID,UIUtils.getSpInt(Const.USER_ID)+"");
        HttpHelper.OkClient(Const.URL_HISTORY, params);
    }

    private void initdata() {
        initHttp();
        relistview.setOnRershListener(new Relistview.OnRefreshListener() {
            @Override
            public void onloding() {
                HISTORY_STATE = HISTORY_SUCCESS_LOADING;
                params.clear();
                mcurrent++;
                params.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");
                params.put("page", mcurrent + "");
                HttpHelper.OkClient(Const.URL_HISTORY, params);
            }

            @Override
            public void onReresh() {
                HISTORY_STATE = HISTORY_SUCCESS_REFRESH;
                params.clear();
                params.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");
                params.put("page", 1 + "");
                HttpHelper.OkClient(Const.URL_HISTORY, params);
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        btn_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initHttp();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Viewholder viewholder=null;
            if (convertView==null){
                convertView=View.inflate(UIUtils.getContext(),R.layout.item_history,null);
                viewholder=new Viewholder();
                viewholder.tv_time= (TextView) convertView.findViewById(R.id.tv_history_time);
                viewholder.tv_operate= (TextView) convertView.findViewById(R.id.tv_history_operate);
                convertView.setTag(viewholder);
            }else {
                viewholder= (Viewholder) convertView.getTag();
            }
            viewholder.tv_time.setText(datas.get(position).pot_time);
            viewholder.tv_operate.setText(datas.get(position).pot_detail);
            return convertView;
        }
        class Viewholder{
            TextView tv_time;
            TextView tv_operate;
        }
    }

}
