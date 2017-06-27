package com.lvsijian8.flowerpot.ui.activity;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.RemoterDetailPot;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.service.RemoterService;
import com.lvsijian8.flowerpot.ui.view.MySeekBar;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class RemoteActivity extends AppCompatActivity {
    private ImageView iv_sun;//太阳
    private ImageView iv_bottle;//营养液
    private ImageView iv_water;//花洒
    private ImageView iv_back;//返回按钮
    private int downx,downy,currentx,currenty,movex,movey;
    private PopupWindow p_bottle,p_water;
    private View view_bottle,view_water;
    private TextView tv_temperature;//温度
    private TextView tv_humidity;//湿度
    private TextView tv_battery;//电量
    private TextView tv_light;//光强
    private TextView bottle_add;//施肥
    private TextView bottle_manager;//管理施肥
    private TextView water_add;//浇水
    private TextView water_manager;//浇水管理

    private String num_water_day="0",num_water_time="0",num_water_ml="0";//决定浇水的数值
    private String num_bottle_day="0",num_bottle_time="0",num_bottle_ml="0";//决定施肥的数值
    private MySeekBar seekBar_day;//浇水间隔的拖动条
    private MySeekBar seekBar_time;//浇水时间的拖动条
    private MySeekBar seekBar_ml;//浇水量的拖动条
    private static final int isWaterManager=0;
    private static final int isBottleManager=1;
    private static  int mCurrent=isWaterManager;


    private boolean isBottle;//为false时，当前没有进行施肥操作，可进行施肥，过程中为true，施肥结束后或联网失败时，重新设为false
    private boolean isWater;//为false时，当前没有进行浇水操作，可进行浇水，过程中为true，浇水结束后或联网失败时，重新设为false
    private long startTime_bottle,startTime_water;

    private HttpHelper httpHelper;
    private Gson gson;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private HashMap<String, Object> params;


    private int STATE_CURRENT;
    private final int STATE_SENDWATER=33;
    private final int STATE_SENDBOTTLE=44;
    private final int STATE_SETWATER=55;
    private final int STATE_SETBOTTLE=66;
    private int PotID;//花盆的ID
    private int pot_state;

    private Messenger ActivityMessenger;
    private Messenger ServicerMessenger;
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServicerMessenger=new Messenger(service);
            starConnection();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        getSupportActionBar().hide();
        PotID = getIntent().getIntExtra("POTID",-1);
        pot_state = getIntent().getIntExtra("state",0);
        initui();
        initdata();
    }

    private void initui() {
        Const.REMOTE_STATE=Const.REMOTE_CONNECTION;
        httpHelper = HttpHelper.getInstances();
        gson=new Gson();
        ActivityMessenger=new Messenger(mHandler);
        tv_temperature= (TextView) findViewById(R.id.tv_remote_temperature);
        tv_battery= (TextView) findViewById(R.id.tv_remote_battery);
        tv_humidity= (TextView) findViewById(R.id.tv_remote_humidity);
        tv_light= (TextView) findViewById(R.id.tv_remote_light);

        iv_sun= (ImageView) findViewById(R.id.img_sun);
        iv_bottle= (ImageView) findViewById(R.id.iv_remote_bottle);
        iv_water= (ImageView) findViewById(R.id.iv_remote_water);
        iv_back= (ImageView) findViewById(R.id.iv_remote_back);


        view_bottle=View.inflate(UIUtils.getContext(),R.layout.remote_popup_bottle,null);
        view_water=View.inflate(UIUtils.getContext(),R.layout.remote_popup_water,null);
        p_bottle=new PopupWindow(view_bottle, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        p_water=new PopupWindow(view_water, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        bottle_add= (TextView) view_bottle.findViewById(R.id.tv_bottle_add);
        bottle_manager= (TextView) view_bottle.findViewById(R.id.tv_bottle_manager);
        water_add= (TextView) view_water.findViewById(R.id.tv_water_add);
        water_manager= (TextView) view_water.findViewById(R.id.tv_water_manager);
        //管理的View
        View view_manager=View.inflate(this,R.layout.layout_manager,null);
        seekBar_day = (MySeekBar) view_manager.findViewById(R.id.myseekbar_day);
        seekBar_time = (MySeekBar) view_manager.findViewById(R.id.myseekbar_time);
        seekBar_ml = (MySeekBar) view_manager.findViewById(R.id.myseekbar_ml);

        builder = new AlertDialog.Builder(this);
        builder.setView(view_manager);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (mCurrent) {
                    //设置浇水数据
                    case isWaterManager:
                        if (pot_state == 0) {
                            Toast.makeText(UIUtils.getContext(), "当前设备已离线", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //CURRENT设置为浇水管理
                        Const.REMOTE_STATE = Const.REMOTE_WATER_MANAGER;

                        num_water_day = seekBar_day.getProgress() + "";
                        num_water_time = seekBar_time.getProgress() + "";
                        num_water_ml = seekBar_ml.getProgress() + "";

                        params.clear();
                        params.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");//用户ID
                        params.put("pot_id", PotID + "");//花盆ID
                        params.put("num_water_day", num_water_day);//浇水的数据
                        params.put("num_water_time", num_water_time);
                        params.put("num_water_ml", num_water_ml);
                        //设置新的浇水数据
                        HttpHelper.OkClient(Const.URL_SETWATER, params);
                        break;

                    //设置施肥数据
                    case isBottleManager:
                        if (pot_state == 0) {
                            Toast.makeText(UIUtils.getContext(), "当前设备已离线", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //CURRENT设置为施肥管理
                        Const.REMOTE_STATE = Const.REMOTE_BOTTLE_MANAGER;

                        num_bottle_day = seekBar_day.getProgress() + "";
                        num_bottle_time = seekBar_time.getProgress() + "";
                        num_bottle_ml = seekBar_ml.getProgress() + "";

                        params.clear();
                        params.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");//用户ID
                        params.put("pot_id", PotID + "");//花盆ID
                        params.put("num_bottle_day", num_bottle_day);//施肥的数据
                        params.put("num_bottle_time", num_bottle_time);
                        params.put("num_bottle_ml", num_bottle_ml);
                        //设置新的施肥数据
                        HttpHelper.OkClient(Const.URL_SETBOTTLE, params);
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        //绑定服务
        Intent intent=new Intent(this, RemoterService.class);
        bindService(intent,mConnection,Service.BIND_AUTO_CREATE);

    }

    private void initdata() {
        setHttp();
        SunAnimation();
        setPopupBottle();
        setPopupWater();
        BottleSettting();
        WaterSetting();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    /**
     * 判断设备是否在线，在线才能操作
     */
    private void judgeState(){
        if (pot_state==0){
            Toast.makeText(UIUtils.getContext(),"当前设备已离线",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 网络相关设置
     */
    private void setHttp() {

        params=new HashMap<>();
        params.put("pot_id", PotID + "");
        //联网获取该花盆的信息
        initTest();
        HttpHelper.OkClient(Const.URL_REMOTE, params);
    }

    private void setPopupBottle() {
        p_bottle.setTouchable(true);
        p_bottle.setOutsideTouchable(true);
        p_bottle.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        p_bottle.getContentView().setFocusableInTouchMode(true);
        p_bottle.getContentView().setFocusable(true);
        p_bottle.setAnimationStyle(R.style.anim_menu_remote);
        iv_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!p_water.isShowing()) {
                    p_water.showAsDropDown(iv_water, -50, 0);
                } else {
                    p_water.dismiss();
                }
            }
        });
    }

    private void setPopupWater() {
        p_water.setTouchable(true);
        p_water.setOutsideTouchable(true);
        p_water.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        p_water.getContentView().setFocusableInTouchMode(true);
        p_water.getContentView().setFocusable(true);
        p_water.setAnimationStyle(R.style.anim_menu_remote);
        iv_bottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!p_bottle.isShowing()) {
                    p_bottle.showAsDropDown(iv_bottle, -50, 0);
                } else {
                    p_bottle.dismiss();
                }
            }
        });
    }

    /**
     * 花洒的处理
     */
    private void WaterSetting() {
        final AnimationSet animationSet=new AnimationSet(true);
        animationSet.setDuration(1000);
        animationSet.setFillAfter(false);
        TranslateAnimation translateAnimation=new TranslateAnimation(0,-100,0,-100);
        RotateAnimation rotateAnimation=new RotateAnimation(0f,-70f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(rotateAnimation);
        water_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pot_state == 0) {
                    Toast.makeText(UIUtils.getContext(), "当前设备已离线", Toast.LENGTH_SHORT).show();
                    return;
                }
                //等待10秒后，再次浇水
                if (System.currentTimeMillis() > startTime_water) {
                    if (!isWater) {
                        Const.REMOTE_STATE = Const.REMOTE_WATER_ADD;
                        isWater = true;
                        iv_water.startAnimation(animationSet);
                        //联网浇水
                        params.clear();
                        params.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");//用户ID
                        params.put("pot_id", PotID + "");//花盆ID
                        HttpHelper.OkClient(Const.URL_ADDWATER, params);

                    }
                } else {
                    Toast.makeText(UIUtils.getContext(), "请勿频繁浇水,等待10秒！", Toast.LENGTH_SHORT).show();
                }
                startTime_water = System.currentTimeMillis() + 10 * 1000;
            }
        });
            //浇水管理-拖动条,点击时状态变化为浇水管理
        water_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrent=isWaterManager;
                builder.setTitle("设置浇水管理");
                seekBar_day.setTitle("请设置浇水间隔(天)");
                seekBar_time.setTitle("请设置浇水时间(24小时制)");
                seekBar_ml.setTitle("请设置浇水量(ml)");
                seekBar_day.setProgress(Integer.parseInt(num_water_day));
                seekBar_time.setProgress(Integer.parseInt(num_water_time));
                seekBar_ml.setProgress(Integer.parseInt(num_water_ml));
                alertDialog.show();
            }
        });

    }

    /**
     * 营养液的处理
     */
    private void BottleSettting() {
        final AnimationSet animationSet=new AnimationSet(true);
        animationSet.setDuration(1000);
        animationSet.setFillAfter(false);
        TranslateAnimation translateAnimation=new TranslateAnimation(0,100,0,-100);
        RotateAnimation rotateAnimation=new RotateAnimation(0f,70f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(rotateAnimation);

        bottle_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pot_state == 0) {
                    Toast.makeText(UIUtils.getContext(), "当前设备已离线", Toast.LENGTH_SHORT).show();
                    return;
                }
                //等待10秒后才可再次进行施肥操作
                if (System.currentTimeMillis() > startTime_bottle) {
                    if (!isBottle) {
                        Const.REMOTE_STATE = Const.REMOTE_BOTTLE_ADD;
                        isBottle = true;
                        iv_bottle.startAnimation(animationSet);

                        //联网施肥
                        params.clear();
                        params.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");//用户ID
                        params.put("pot_id", PotID + "");//花盆ID
                        HttpHelper.OkClient(Const.URL_ADDBOTTLE, params);

                    }
                } else {
                    Toast.makeText(UIUtils.getContext(), "请勿频繁施肥,等待10秒！", Toast.LENGTH_SHORT).show();
                }
                startTime_bottle = System.currentTimeMillis() + 10 * 1000;
            }
        });
            //施肥的管理-拖动条,点击时状态变化为施肥管理
        bottle_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrent = isBottleManager;
                builder.setTitle("设置施肥管理");
                seekBar_day.setTitle("请设置施肥间隔(天)");
                seekBar_time.setTitle("请设置施肥时间(24小时制)");
                seekBar_ml.setTitle("请设置施肥量(ml)");
                seekBar_day.setProgress(Integer.parseInt(num_bottle_day));
                seekBar_time.setProgress(Integer.parseInt(num_bottle_time));
                seekBar_ml.setProgress(Integer.parseInt(num_bottle_ml));
                alertDialog.show();
            }
        });
    }


    /**
     * 太阳的动画及拖动效果
     */
    private void SunAnimation() {
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.sun_rotate);
        animation.setInterpolator(new LinearInterpolator());
        iv_sun.startAnimation(animation);
        iv_sun.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downx = (int) event.getRawX();
                        downy = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        movex = (int) event.getRawX();
                        movey = (int) event.getRawY();
                        currentx = movex - downx;
                        currenty = movey - downy;
                        int left = iv_sun.getLeft() + currentx;
                        int right = iv_sun.getRight() + currentx;
                        int top = iv_sun.getTop() + currenty;
                        int bottom = iv_sun.getBottom() + currenty;

                        iv_sun.layout(left, top, right, bottom);
                        downx = movex;
                        downy = movey;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 用后台服务执行心跳操作，每隔三秒访问网络获取数据。
     */
    private void starConnection(){
        Message message=new Message();
        message.what=Const.SERVICE_REMOTER;
        message.arg1=PotID;
        message.replyTo=ActivityMessenger;
        try {
            ServicerMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int code=msg.what;
            String dataString= (String) msg.obj;
            switch (code){
                //联网成功
                case Const.REMOTE_CONNECTION_SUCCESS:
                    if (!TextUtils.isEmpty(dataString)){
                        RemoterDetailPot pot=gson.fromJson(dataString,RemoterDetailPot.class);
                        ArrayList<RemoterDetailPot.Pot> data=pot.data;
                        if (data.size()>0&&data!=null){
                            num_water_day=data.get(0).num_water_day;
                            num_water_time=data.get(0).num_water_time;
                            num_water_ml=data.get(0).num_water_ml;
                            num_bottle_day=data.get(0).num_bottle_day;
                            num_bottle_time=data.get(0).num_bottle_time;
                            num_bottle_ml=data.get(0).num_bottle_ml;
                            tv_light.setText(data.get(0).light);
                            tv_battery.setText(data.get(0).power+"%");
                            tv_humidity.setText(data.get(0).humidity);
                            tv_temperature.setText(data.get(0).temperature);
                        }
                    }
                    break;
                //联网失败
                case Const.REMOTE_CONNECTION_FAIL:
                    Toast.makeText(UIUtils.getContext(),"获取花盆数据失败，请检查网络",Toast.LENGTH_SHORT).show();
                    break;
                //施肥成功
                case Const.REMOTE_BOTTLE_ADD_SUCCESS:
                    int wd_code=Integer.parseInt(dataString);
                    if (wd_code==1){
                        Toast.makeText(UIUtils.getContext(),"施肥成功",Toast.LENGTH_SHORT).show();
                    }else if (wd_code==0){
                        Toast.makeText(UIUtils.getContext(),"施肥失败，请重试",Toast.LENGTH_SHORT).show();
                    }else if (wd_code==-1){
                        Toast.makeText(UIUtils.getContext(),"肥料不够，请补充",Toast.LENGTH_SHORT).show();
                    }
                    isBottle=false;
                    break;
                //施肥失败
                case Const.REMOTE_BOTTLE_ADD_FAIL:
                    Toast.makeText(UIUtils.getContext(),"施肥失败，请重试",Toast.LENGTH_SHORT).show();
                    isBottle=false;
                    break;
                //浇水成功
                case Const.REMOTE_WATER_ADD_SUCCESS:
                    int bd_code=Integer.parseInt(dataString);
                    if (bd_code==1){
                        Toast.makeText(UIUtils.getContext(),"浇水成功",Toast.LENGTH_SHORT).show();
                    }else if (bd_code==0){
                        Toast.makeText(UIUtils.getContext(),"浇水失败，请重试",Toast.LENGTH_SHORT).show();
                    }else if (bd_code==-1){
                        Toast.makeText(UIUtils.getContext(),"水量不够，请补充",Toast.LENGTH_SHORT).show();
                    }
                    isWater=false;
                    break;
                //浇水失败
                case Const.REMOTE_WATER_ADD_FAIL:
                    Toast.makeText(UIUtils.getContext(),"浇水失败，请重试",Toast.LENGTH_SHORT).show();
                    isWater=false;
                    break;
                //施肥设置成功
                case Const.REMOTE_BOTTLE_MANAGER_SUCCESS:
                    int bm_code=Integer.parseInt(dataString);
                    if (bm_code==1){
                        Toast.makeText(UIUtils.getContext(),"设置成功",Toast.LENGTH_SHORT).show();
                    }else if (bm_code==0){
                        Toast.makeText(UIUtils.getContext(),"设置失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                    break;
                //施肥设置失败
                case Const.REMOTE_BOTTLE_MANAGER_FAIL:
                    Toast.makeText(UIUtils.getContext(),"设置失败，请重试",Toast.LENGTH_SHORT).show();
                    break;
                //浇水设置成功
                case Const.REMOTE_WATER_MANAGER_SUCCESS:
                    int wm_code=Integer.parseInt(dataString);
                    if (wm_code==1){
                        Toast.makeText(UIUtils.getContext(),"设置成功",Toast.LENGTH_SHORT).show();
                    }else if (wm_code==0){
                        Toast.makeText(UIUtils.getContext(),"设置失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                    break;
                //浇水设置失败
                case Const.REMOTE_WATER_MANAGER_FAIL:
                    Toast.makeText(UIUtils.getContext(),"设置失败，请重试",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK==keyCode){
            finish();
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 联网操作
     */
    private void initTest(){
        httpHelper.setOkClientListener(new HttpHelper.OkClientListener() {
            @Override
            public void onFailure() {
                switch (Const.REMOTE_STATE){
                    case Const.REMOTE_CONNECTION:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_CONNECTION_FAIL;
                        break;
                    case Const.REMOTE_BOTTLE_ADD:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_BOTTLE_ADD_FAIL;
                        break;
                    case Const.REMOTE_WATER_ADD:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_WATER_ADD_FAIL;
                        break;
                    case Const.REMOTE_BOTTLE_MANAGER:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_BOTTLE_MANAGER_FAIL;
                        break;
                    case Const.REMOTE_WATER_MANAGER:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_WATER_MANAGER_FAIL;
                        break;
                }
                Message message=new Message();
                message.what=Const.REMOTE_STATE_SUCCESS;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(String data) {
                switch (Const.REMOTE_STATE){
                    case Const.REMOTE_CONNECTION:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_CONNECTION_SUCCESS;
                        break;
                    case Const.REMOTE_BOTTLE_ADD:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_BOTTLE_ADD_SUCCESS;
                        break;
                    case Const.REMOTE_WATER_ADD:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_WATER_ADD_SUCCESS;
                        break;
                    case Const.REMOTE_BOTTLE_MANAGER:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_BOTTLE_MANAGER_SUCCESS;
                        break;
                    case Const.REMOTE_WATER_MANAGER:
                        Const.REMOTE_STATE_SUCCESS= Const.REMOTE_WATER_MANAGER_SUCCESS;
                        break;
                }
                Message message=new Message();
                message.what=Const.REMOTE_STATE_SUCCESS;
                message.obj=data;
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}