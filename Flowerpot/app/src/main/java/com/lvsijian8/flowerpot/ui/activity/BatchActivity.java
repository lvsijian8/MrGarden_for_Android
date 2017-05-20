package com.lvsijian8.flowerpot.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.BatchContain;
import com.lvsijian8.flowerpot.domin.Batch_data;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BatchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_all;//全选按钮
    private Button btn_notall;//反选按钮
    private Button btn_waters;//浇水按钮
    private Button btn_bottle;//施肥按钮
    private ListView lv_contain;
    private MyAdapter myAdapter;
    private ArrayList<Batch_data> data;//数据
    private StringBuilder sb;
    private static final int CONNECT_SUCCESS=1;
    private static final int CONNECT_FAIL=2;
    private static final int STATE_FIRST=0X24;
    private static final int STATE_WATER=0X25;
    private static final int STATE_BOTTLE=0X26;
    private int STATE_CURRENT=STATE_FIRST;
    private HttpHelper httpHelper;
    private HashMap<String, Object> params;
    private Gson gson;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONNECT_SUCCESS:
                    String dataString= (String) msg.obj;
                    String[] dataArray=null;
                    switch (STATE_CURRENT){
                        case STATE_FIRST:
                            BatchContain contain=gson.fromJson(dataString, BatchContain.class);
                            data=contain.data;
                            myAdapter.notifyDataSetChanged();
                            break;
                        case STATE_WATER:
                            dataArray=dataString.split("&");
                            if ("success".equalsIgnoreCase(dataArray[0])){
                                Toast.makeText(UIUtils.getContext(),"浇水成功",Toast.LENGTH_SHORT).show();
                            }else {
                                for (int i=1;i<dataArray.length;i++){
                                    for (Batch_data d:data){
                                        if (d.pot_id==Integer.parseInt(dataArray[i])){
                                            Toast.makeText(UIUtils.getContext(),d.getName()+"浇水失败",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                            break;
                        case STATE_BOTTLE:
                            dataArray=dataString.split("&");
                            if ("success".equalsIgnoreCase(dataArray[0])){
                                Toast.makeText(UIUtils.getContext(),"施肥成功",Toast.LENGTH_SHORT).show();
                            }else {
                                for (int i=1;i<dataArray.length;i++){
                                    for (Batch_data d:data){
                                        if (d.pot_id==Integer.parseInt(dataArray[i])){
                                            Toast.makeText(UIUtils.getContext(),d.getName()+"施肥失败",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case CONNECT_FAIL:
                    switch (STATE_CURRENT){
                        case STATE_FIRST:
                            Toast.makeText(UIUtils.getContext(),"获取花盆数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                            break;
                        case STATE_WATER:
                            Toast.makeText(UIUtils.getContext(),"浇水失败，请检查网络",Toast.LENGTH_SHORT).show();
                            break;
                        case STATE_BOTTLE:
                            Toast.makeText(UIUtils.getContext(),"施肥失败，请检查网络",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch);
        getSupportActionBar().hide();
        initui();
        initdata();
    }

    private void initui() {
        httpHelper = HttpHelper.getInstances();
        params = new HashMap<>();
        gson = new Gson();
        btn_all = (Button) findViewById(R.id.btn_batch_all);
        btn_notall = (Button) findViewById(R.id.btn_batch_notall);
        btn_waters = (Button) findViewById(R.id.btn_batch_waters);
        btn_bottle = (Button) findViewById(R.id.btn_batch_bottle);
        lv_contain = (ListView) findViewById(R.id.lv_batch_contain);
        sb = new StringBuilder();
        myAdapter = new MyAdapter();
        data = new ArrayList<>();
    }

    private void initdata() {
        lv_contain.setAdapter(myAdapter);
        btn_all.setOnClickListener(this);
        btn_notall.setOnClickListener(this);
        btn_waters.setOnClickListener(this);
        btn_bottle.setOnClickListener(this);
        params.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID));
        httpHelper.getJsonData(Const.URL_BATCH_POT, params);
        httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                if (!TextUtils.isEmpty(data)){
                    Log.e("ZDLW","数据:"+data);
                    Message message=new Message();
                    message.obj=data;
                    message.what=CONNECT_SUCCESS;
                    mhandler.sendMessage(message);

                }
            }

            @Override
            public void failConnect() {
                Message message=new Message();
                message.what=CONNECT_FAIL;
                mhandler.sendMessage(message);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_batch_all:
                for (Batch_data d:data){
                    //遍历，对在线的花盆进行全选操作
                    if (d.getState()==1){
                        if (!d.isCheck()){
                            d.setCheck(true);
                        }
                    }
                }
                myAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_batch_notall:
                for (Batch_data d:data){
                    if (d.getState()==1){
                        //遍历，对在线的花盆进行反选操作
                        if (!d.isCheck()){
                            d.setCheck(true);
                        }else {
                            d.setCheck(false);
                        }
                    }
                }
                myAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_batch_waters:
                sb=new StringBuilder();
                for (int i=0;i<data.size();i++){
                    Batch_data d=data.get(i);
                    if (d.getState()==1){
                        if (d.isCheck()){
                            sb.append(d.pot_id + "&");
                        }
                    }
                }
                if (sb.length()!=0){
                    sb.setLength(sb.length() - 1);
                    //批量浇水操作
                    STATE_CURRENT=STATE_WATER;
                    params.put("pot_ids", sb.toString());
                    httpHelper.getJsonData(Const.URL_BATCH_WATER, params);
//                    httpHelper.getJsonData("http://172.16.60.25:8080/MrFlower/waterAllAndroid", params);
                }else {
                    Toast.makeText(UIUtils.getContext(),"您当前未选择任何花盆",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_batch_bottle:
                sb=new StringBuilder();
                for (int i=0;i<data.size();i++){
                    Batch_data d=data.get(i);
                    if (d.isCheck()){
                        sb.append(d.pot_id+"&");
                    }
                }
                if (sb.length()!=0){
                    sb.setLength(sb.length() - 1);
                    //批量施肥操作
                    STATE_CURRENT=STATE_BOTTLE;
                    params.put("pot_ids",sb.toString());
                    httpHelper.getJsonData(Const.URL_BATCH_BOTTLE, params);
//                    httpHelper.getJsonData("http://172.16.60.25:8080/MrFlower/bottleAllAndroid", params);
                }else {
                    Toast.makeText(UIUtils.getContext(),"您当前未选择任何花盆",Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
            ViewHolder holder=null;
            final Batch_data check_item= (Batch_data) getItem(position);
            if (convertView==null){
                convertView= UIUtils.inflate(R.layout.item_batch);
                holder=new ViewHolder();
                holder.name= (TextView) convertView.findViewById(R.id.tv_batch_name);
                holder.state= (TextView) convertView.findViewById(R.id.tv_batch_state);
                holder.check= (CheckBox) convertView.findViewById(R.id.cb_batch_check);
                holder.water= (TextView) convertView.findViewById(R.id.tv_batch_water);
                holder.bottle= (TextView) convertView.findViewById(R.id.tv_batch_bottle);
                holder.iv_state= (ImageView) convertView.findViewById(R.id.iv_batch_state);
                convertView.setTag(holder);

            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            //设置Item的点击-用于checkBox的点击
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //只有在线才能响应点击
                    if (check_item.getState() == 1) {
                        if (check_item.isCheck()) {
                            check_item.setCheck(false);
                            notifyDataSetChanged();
                        } else {
                            check_item.setCheck(true);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
            //设置花盆数据
            holder.name.setText(check_item.getName());//设置名字
            holder.water.setText(check_item.pot_waters+"%");//设置水剩余量
            holder.bottle.setText(check_item.pot_bottles+"%");//设置营养液剩余量
            //设置勾选框-默认为false
            if (check_item.isCheck()){
                holder.check.setChecked(true);
            }else {
                holder.check.setChecked(false);
            }
            //设置是否在线
            if (check_item.getState()==0){
                holder.state.setText("离线");
                holder.iv_state.setImageResource(R.drawable.state_off);
            }else {
                holder.state.setText("在线");
                holder.iv_state.setImageResource(R.drawable.state_on);
            }

            return convertView;
        }

         class ViewHolder{
             CheckBox check;
             TextView name;
             TextView state;
             TextView water;
             TextView bottle;
             ImageView iv_state;
        }
    }


}
