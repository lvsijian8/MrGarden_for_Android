package com.lvsijian8.flowerpot.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.Detail_data;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.view.DetailItem;
import com.lvsijian8.flowerpot.utils.BitmapHelper;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    private ImageView iv_back;//返回按钮
    private ImageView iv_plant;//植物图片
    private BitmapUtils bitmapUtils;
    private TextView tv_plant;
    private TextView tv_hot;//最高温度
    private TextView tv_cool;//最低温度
    private TextView tv_brief;//简介
    private TextView tv_text;//全文
    private DetailItem item_water;//浇水适应量
    private DetailItem item_light;//光照适应量
    private DetailItem item_yinyang;//营养液适应量
    private HttpHelper httpHelper;
    private HashMap<String, Object> map;
    private Gson gson;
    private String namec;
    private String pic;
    private int fid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().hide();

        initui();
        initdata();
    }

    private void initui(){
        iv_plant= (ImageView) findViewById(R.id.iv_detail_main);
        iv_back= (ImageView) findViewById(R.id.iv_detail_back);
        tv_plant = (TextView) findViewById(R.id.tv_detail_namec);
        tv_hot = (TextView) findViewById(R.id.tv_detail_hot);
        tv_cool = (TextView) findViewById(R.id.tv_detail_cool);
        tv_brief = (TextView) findViewById(R.id.tv_detail_brief);
        tv_text = (TextView) findViewById(R.id.tv_detail_text);
        tv_plant.setTypeface(UIUtils.getTypeFace());
        item_water = (DetailItem) findViewById(R.id.item_detail_water);
        item_light = (DetailItem) findViewById(R.id.item_detail_light);
        item_yinyang = (DetailItem) findViewById(R.id.item_detail_yinyang);

        Bundle bundle=getIntent().getExtras();
        pic = bundle.getString("pic");
        fid = bundle.getInt("fid");
        namec = bundle.getString("namec");
        httpHelper = HttpHelper.getInstances();
        bitmapUtils = BitmapHelper.getBitmapUtils();
        gson=new Gson();
        map = new HashMap<>();
        bitmapUtils.display(iv_plant, Const.URL_PATH + "sql_image" + pic);
        map.put("fid", fid + "");
        httpHelper.getDetailData(Const.URL_DETAIL, map);
        httpHelper.setOnDetailgetData(new HttpHelper.OnDetailgetData() {
            @Override
            public void successGet(String data) {
                if (data != null) {
                    Detail_data detail = gson.fromJson(data, Detail_data.class);
                    final Detail_data.Ddata ddata = detail.data.get(0);
                    Message message=new Message();
                    message.what=Const.RECEIVE_DATA_SUCCESS;
                    message.obj=ddata;
                    mhandle.sendMessage(message);
                }
            }

            @Override
            public void failGet() {

            }
        });




    }
    private void initdata(){




        tv_plant.setText(namec);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UIUtils.currentapiVersion >= 21) {
                    ActivityCompat.finishAfterTransition(DetailActivity.this);
                } else {
                    finish();
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK==keyCode){
            if(UIUtils.currentapiVersion>=21){
                ActivityCompat.finishAfterTransition(DetailActivity.this);
            }else {
                finish();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private Handler mhandle =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Const.RECEIVE_DATA_SUCCESS:
                    Detail_data.Ddata ddata= (Detail_data.Ddata) msg.obj;
                    bitmapUtils.display(item_water.getImage(), Const.URL_PIC + ddata.watering);
                    bitmapUtils.display(item_light.getImage(), Const.URL_PIC + ddata.sunshine);
                    bitmapUtils.display(item_yinyang.getImage(), Const.URL_PIC + ddata.fertilizer);
                    tv_hot.setText(ddata.temperature_max + "℃");
                    tv_cool.setText(ddata.temperature_min + "℃");
                    Log.e("ZDLW",ddata.text);
                    tv_brief.setText(ddata.brief);
                    tv_text.setText(ddata.text);
                    break;
            }
        }
    };
}
