package com.lvsijian8.flowerpot.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.Flower_Find;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.utils.BitmapHelper;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ImageView iv_back;
    private EditText ed_search;
    private TextView tv_cancle;
    private ListView lv_search;
    private MyAdapter myAdapter;
    private BitmapUtils bitmapUtils;
    private List<Flower_Find.flower> fdata;
    private HttpHelper httpHelper;
    private Gson gson;
    private HashMap<String, Object> params;
    private Handler mhandler=new Handler();
    private final static int state_null=0;
    private final static int state_min=1;
    private  int state=state_null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        initui();
        initdata();
    }

    private void initui() {
        bitmapUtils = BitmapHelper.getBitmapUtils();
        httpHelper = HttpHelper.getInstances();
        params = new HashMap<>();
        gson = new Gson();
        iv_back= (ImageView) findViewById(R.id.iv_search_back);
        ed_search= (EditText) findViewById(R.id.ed_search);
        tv_cancle= (TextView) findViewById(R.id.tv_search_cancle);
        lv_search= (ListView) findViewById(R.id.lv_search);
        fdata=new ArrayList<>();
        myAdapter=new MyAdapter();
        lv_search.setAdapter(myAdapter);
    }

    private void initdata() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_search.setText("");
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        httpHelper.setOnDetailgetData(new HttpHelper.OnDetailgetData() {
            @Override
            public void successGet(String data) {
                if (!TextUtils.isEmpty(data)) {
                    Flower_Find flower = gson.fromJson(data, Flower_Find.class);
                    if (flower instanceof Flower_Find){
                        fdata = flower.data;
                        mhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                myAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
//                else {
//                    mhandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(UIUtils.getContext(),"查无结果",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
            }

            @Override
            public void failGet() {

            }
        });


        //检测输入框的输入
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    state=state_null;
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fdata.clear();//清除ListView数据
                            Log.e("ZDLW", fdata.size()+"");
                            myAdapter.notifyDataSetChanged();//通知更新
                        }
                    },1400);

                } else if (s.toString().length() > 0) {
                    state=state_min;
                    Log.e("ZDLW",s.toString());
                    params.clear();
                    params.put(Const.FLOWER_NAME, s.toString());
                    httpHelper.getDetailData(Const.URL_SEARCH, params);
                }
            }
        });

        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("pic",fdata.get(position).pic);
                bundle.putInt("fid", fdata.get(position ).fid);
                bundle.putString("namec",fdata.get(position).namec);
                intent.putExtras(bundle);
                //进行版本号判断，版本号低于21，即低于Android5.0；
                if (UIUtils.currentapiVersion >= 21) {
                    ImageView img = (ImageView) view.findViewById(R.id.iv_item_find);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(SearchActivity.this, img, "robot");
                    ActivityCompat.startActivity(SearchActivity.this, intent, options.toBundle());
                } else {
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
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
                convertView=View.inflate(SearchActivity.this,R.layout.find_item,null);
                holder.img= (ImageView) convertView.findViewById(R.id.iv_item_find);
                holder.nameC= (TextView) convertView.findViewById(R.id.tv_item_find_c);
                holder.nameE= (TextView) convertView.findViewById(R.id.tv_item_find_e);
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }


            Flower_Find.flower data= (Flower_Find.flower) getItem(position);
            bitmapUtils.display(holder.img, Const.URL_PIC + data.pic);
            holder.nameE.setText(data.namee);
            holder.nameC.setText(data.namec);
            return convertView;
        }
        class ViewHolder{
            ImageView img;
            TextView nameC;
            TextView nameE;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK==keyCode){
            finish();
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
