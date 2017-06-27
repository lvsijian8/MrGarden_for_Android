package com.lvsijian8.flowerpot.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.School_data;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.view.Relistview;
import com.lvsijian8.flowerpot.utils.BitmapHelper;
import com.lvsijian8.flowerpot.utils.ThreadManager;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SchoolActivity extends AppCompatActivity {

    private Gson gson;
    private HttpHelper mHttpHelper;
    private BitmapUtils bitmapUtils;
    private ArrayList<School_data.data> data;
    private Relistview listview;
    private HashMap<String, Object> params;
    private static final int STATE_LOADING=0;
    private static final int STATE_REFRESH=1;
    private int mCurrent=STATE_REFRESH;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Const.RESHRE_FIND_DATA:
                    Toast.makeText(UIUtils.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    listview.Closeloading();
                    listview.setSelection(listview.getCount()-1);
                    break;
                case Const.LOADING_FIND_DATA:
                    School_data sdata=gson.fromJson((String)msg.obj,School_data.class);
                    ArrayList<School_data.data> data=sdata.data;
                    if (mCurrent==STATE_REFRESH){
                        //初次进入界面的情况
                        SchoolActivity.this.data=data;
                    }else if (mCurrent==STATE_LOADING){
                        //加载更多数据的情况
                        for (School_data.data s:data){
                            data.add(s);
                        }
                        listview.Closeloading();
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private SchoolAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);
        initui();
        initdata();
    }

    private void initui() {
        gson = new Gson();
        params = new HashMap<>();
        mHttpHelper = HttpHelper.getInstances();
        bitmapUtils = BitmapHelper.getBitmapUtils();
        listview = (Relistview) findViewById(R.id.lv_school_all);
        data=new ArrayList<>();
        mAdapter = new SchoolAdapter();
        listview.setAdapter(mAdapter);
    }

    private void initdata() {
        params.put("sid", 0 + "");
        mHttpHelper.getJsonData(Const.URL_SCHOOL, params);
        mHttpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                Message message = new Message();
                if (!TextUtils.isEmpty(data)) {
                    message.what = Const.LOADING_FIND_DATA;
                    message.obj = data;
                } else {
                    message.what = Const.RESHRE_FIND_DATA;//当没有数据时，会返回该参数
                }
                mHandler.sendMessage(message);
            }

            @Override
            public void failConnect() {

            }
        });
        listview.setOnRershListener(new Relistview.OnRefreshListener() {
            @Override
            public void onloding() {
                mCurrent = STATE_LOADING;
                ThreadManager.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500 + new Random().nextInt(500));
                            params.clear();
                            params.put("sid", "" + data.size());
                            //加载更多数据,根据当前的Item数量，获取后6条数据
                            mHttpHelper.getJsonData(Const.URL_FIND, params);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onReresh() {
                mCurrent = STATE_REFRESH;
                UIUtils.makeText("已刷新数据");
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                School_data.data detailData=data.get(position);

            }
        });
    }

    class SchoolAdapter extends BaseAdapter {

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
            if (convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(SchoolActivity.this,R.layout.find_item,null);
                holder.arrow= (ImageView) convertView.findViewById(R.id.imageView3);
                holder.img= (ImageView) convertView.findViewById(R.id.iv_item_find);
                holder.nameC= (TextView) convertView.findViewById(R.id.tv_item_find_c);
                holder.nameE= (TextView) convertView.findViewById(R.id.tv_item_find_e);
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            holder.arrow.setVisibility(View.GONE);
            School_data.data data= (School_data.data) getItem(position);
            holder.nameC.setText(data.namec);
            holder.nameE.setText(data.namee);
            bitmapUtils.display(holder.img, Const.URL_PATH + "sql_image" + data.pic);
            return convertView;
        }
        class ViewHolder{
            ImageView img;
            ImageView arrow;
            TextView nameC;
            TextView nameE;
        }
    }
}
