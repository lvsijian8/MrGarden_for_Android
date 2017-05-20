package com.lvsijian8.flowerpot.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.lvsijian8.flowerpot.domin.Flower_Find;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.view.Relistview;
import com.lvsijian8.flowerpot.utils.BitmapHelper;
import com.lvsijian8.flowerpot.utils.ThreadManager;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PlantActivity extends AppCompatActivity {
    private Relistview listView;
    private ArrayList<Flower_Find.flower> fdata;
    private MyAdapter myAdapter;
    private BitmapUtils bitmapUtils;
    private Gson gson;
    private static int State_REFRESH=0;
    private static int State_LOADING=1;
    private static int CURRENT_STATE=State_REFRESH;
    private HttpHelper httpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);
        getSupportActionBar().hide();
        initui();
        initdata();
    }

    private void initui() {
        httpHelper = HttpHelper.getInstances();
        gson = new Gson();
        bitmapUtils = BitmapHelper.getBitmapUtils();
        listView= (Relistview) findViewById(R.id.lv_plant_all);
        fdata=new ArrayList<>();
        myAdapter=new MyAdapter();
        listView.setAdapter(myAdapter);

    }
    private void initdata() {
        HashMap<String,Object> map=new HashMap<String, Object>();
        map.put("fid", 0 + "");
        httpHelper.getJsonData(Const.URL_FIND, map);


        httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //选定植物数据，回传图片地址，植物中文名，英文名
                Intent intent = new Intent(PlantActivity.this, AppendActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("pic", fdata.get(position - 1).pic);
                bundle.putInt("fid", fdata.get(position - 1).fid);
                bundle.putString("namec", fdata.get(position - 1).namec);
                bundle.putString("namee", fdata.get(position - 1).namee);
                intent.putExtras(bundle);
                setResult(-1, intent);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        listView.setOnRershListener(new Relistview.OnRefreshListener() {
            @Override
            public void onloding() {
                CURRENT_STATE=State_LOADING;
                ThreadManager.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500 + new Random().nextInt(500));
                            HashMap<String, Object> parmsmap = new HashMap<String, Object>();
                            parmsmap.put("fid", "" + fdata.size());
                            //加载更多数据,根据当前的Item数量，获取后6条数据
                            httpHelper.getJsonData(Const.URL_FIND, parmsmap);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onReresh() {
                listView.CloseRersh();
                Toast.makeText(PlantActivity.this,"已刷新数据",Toast.LENGTH_SHORT).show();
            }
        });
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return fdata.size();
        }

        @Override
        public Object getItem(int position) {
            return fdata.get(position);
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
                convertView=View.inflate(PlantActivity.this,R.layout.find_item,null);
                holder.arrow= (ImageView) convertView.findViewById(R.id.imageView3);
                holder.img= (ImageView) convertView.findViewById(R.id.iv_item_find);
                holder.nameC= (TextView) convertView.findViewById(R.id.tv_item_find_c);
                holder.nameE= (TextView) convertView.findViewById(R.id.tv_item_find_e);
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            holder.arrow.setVisibility(View.GONE);
            Flower_Find.flower data= (Flower_Find.flower) getItem(position);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlantActivity.this, AppendActivity.class);
        setResult(1,intent);
        finish();
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Const.RESHRE_FIND_DATA:
                    Toast.makeText(UIUtils.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    listView.Closeloading();
                    listView.setSelection(listView.getCount()-1);
                    break;
                case Const.LOADING_FIND_DATA:
                    Flower_Find ceshi=gson.fromJson((String)msg.obj,Flower_Find.class);
                    ArrayList<Flower_Find.flower> data=ceshi.data;
                    if (CURRENT_STATE==State_REFRESH){
                        //进入发现页的情况
                        fdata=data;
                    }else if (CURRENT_STATE==State_LOADING){
                        //加载更多数据的情况
                        for (Flower_Find.flower f:data){
                            fdata.add(f);
                            listView.Closeloading();
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
}
