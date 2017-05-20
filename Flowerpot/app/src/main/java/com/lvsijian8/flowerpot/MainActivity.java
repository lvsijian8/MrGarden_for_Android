package com.lvsijian8.flowerpot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.activity.AppendActivity;
import com.lvsijian8.flowerpot.ui.activity.ContainActivity;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String VIDEO_NAME = "welcome_video.mp4";

    private VideoView mVideoView;
    private InputType inputType = InputType.NONE;
    private Button buttonLeft, buttonRight;
    private FormView formView;
    private FormViewSU formViewSU;
    private TextView appName;
    static private SharedPreferences preferences;
    static private String prename;
    private String user_name;
    private String user_pwd;
    private int user_id;

    ClientThread clientThread;
    static Handler handler;
    MediaPlayer mediaPlayerchange;
    private HttpHelper httpHelper;
    private HashMap<String, Object> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        Const.selector=0;
        preferences=getSharedPreferences("user",MODE_PRIVATE);
        int mark=0;
        if(0< UIUtils.getSpInt(Const.USER_ID)){
            Intent intent = new Intent(MainActivity.this, ContainActivity.class);//已登录,跳过登录界面
            startActivity(intent);
            mark=1;
        }
//        Intent intentf = new Intent(MainActivity.this, FirstPicture.class);
//        startActivity(intentf);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        findView();

        initView();

        File videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists()) {
            videoFile = copyVideoFile();
        }

        playVideo(videoFile);

        playAnim();
//        handler = new Handler()                                     //等待服务器返回
//        {
//            @Override
//            public void handleMessage(Message msg)
//            {
//                // 如果消息来自于子线程
//                //if (msg.what == 0x123)
//                if (msg.what == 0x888)
//                {
//                    String content=msg.obj.toString();
//                    switch((content.charAt(0)-'0')){
//                        case 0:{
//                            if((content.charAt(1)-'0')>0)
//                            {
//                                Toast.makeText(MainActivity.this,"密码正确。",Toast.LENGTH_LONG).show();
//                                SharedPreferences.Editor editor=preferences.edit();
//                                editor.putString("name",prename);
//                                editor.putInt("user_id",(content.charAt(1)-'0'));
//                                editor.commit();
//
//
//
//                                Intent intent = new Intent(MainActivity.this, ContainActivity.class);
//                                startActivity(intent);//以登陆跳转界面
//                                finish();
//                                break;
//                            }
//                            else {
//                                Toast.makeText(MainActivity.this, "输入有误，请重新输入。", Toast.LENGTH_LONG).show();
//                                break;
//                            }
//                        }
//                        case 1:{
//                            if((content.charAt(1)-'0')==-1)
//                            {
//                                Toast.makeText(MainActivity.this,"该用户用户已存在，请登录",Toast.LENGTH_LONG).show();break;
//                            }
//                            else if((content.charAt(1)-'0')>0)
//                            {
//                                Toast.makeText(MainActivity.this,"注册成功",Toast.LENGTH_LONG).show();
//                                SharedPreferences.Editor editor=preferences.edit();
//                                editor.putString("name",prename);
//                                editor.putInt("user_id",(content.charAt(1)-'0'));
//                                editor.commit();
//                                Intent intent = new Intent(MainActivity.this, SignUpFirst.class);//跳转初始化界面
//                                startActivity(intent);
//                                finish();
//                                break;
//                            }
//                            else {
//                                Toast.makeText(MainActivity.this, "网络连接失败，请重新注册", Toast.LENGTH_LONG).show();
//                                break;
//                            }
//                        }
//                    }
//                }
//                /*if (msg.what == 0x888)//登陆进程返回值
//                {
//
//
//
//
//                    Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
//                }*/
//            }
//        };
        //ClientThread.handler=handler;

       /* clientThread = new ClientThread();
        Thread thread=new Thread(clientThread);
        thread.start();*/
        if(mark==1)//已开启主界面,关闭此登录界面
            finish();
    }

    private void findView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        buttonLeft = (Button) findViewById(R.id.buttonLeft);
        buttonRight = (Button) findViewById(R.id.buttonRight);
        formView = (FormView) findViewById(R.id.formView);
        formViewSU = (FormViewSU) findViewById(R.id.formViewSU);
        appName = (TextView) findViewById(R.id.appName);
        formView.post(new Runnable() {
            @Override
            public void run() {
                int delta = formView.getTop() + formView.getHeight();
                formView.setTranslationY(-1 * delta);
            }
        });
        formViewSU.post(new Runnable() {
            @Override
            public void run() {
                int deltaSU = formViewSU.getTop() + formViewSU.getHeight();
                formViewSU.setTranslationY(-1 * deltaSU);
            }
        });
    }

    private void initView() {
        httpHelper = HttpHelper.getInstances();
        hashMap = new HashMap<>();
        httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                Message message=new Message();
                if (data!=null){
                    Log.e("ZDLW",""+data);
                    int code=Integer.parseInt(data.toString().trim());
                    if (code==-1){
                        message.what=Const.SIGNUP_STATE_REPEAT;
                    }else if (code==-2){
                        message.what=Const.LOGIN_STATE_UNEXIST;
                    }else if (code==-3){
                        message.what=Const.LOGIN_STATE_PWDFAIL;
                    }else {
                        switch (inputType){
                            case LOGIN:
                                message.what=Const.LOGIN_STATE_SUCCESS;
                                message.obj=code;
                                break;
                            case SIGN_UP:
                                message.what=Const.SIGNUP_STATE_SUCCESS;
                                message.obj=code;
                                break;
                        }
                    }
                    mhandler.sendMessage(message);
                }


            }

            @Override
            public void failConnect() {

            }
        });
        buttonRight.setOnClickListener(this);
        buttonLeft.setOnClickListener(this);
    }

    private void playVideo(File videoFile) {
        mVideoView.setVideoPath(videoFile.getPath());
        mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayerchange=mediaPlayer;
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });
    }

    private void playAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(appName, "alpha", 0,1);
        anim.setDuration(5000);
        anim.setRepeatCount(1);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                appName.setVisibility(View.INVISIBLE);
            }
        });
    }

    @NonNull
    private File copyVideoFile() {
        File videoFile;
        try {
            FileOutputStream fos = openFileOutput(VIDEO_NAME, MODE_PRIVATE);
            InputStream in = getResources().openRawResource(R.raw.welcome_video);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists())
            throw new RuntimeException("没找到welcome_video.mp4。。。");
        return videoFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    @Override
    public void onClick(View view) {
        int delta = formView.getTop()+formView.getHeight();
        int deltaSU = formViewSU.getTop()+formViewSU.getHeight();

        switch (inputType) {
            case NONE:

                if (view == buttonLeft) {
                    formView.animate().translationY(0).alpha(1).setDuration(500).start();
                    inputType = InputType.LOGIN;
                    buttonLeft.setText(R.string.button_confirm_login);
                    buttonRight.setText(R.string.button_cancel_login);
                    mediaPlayerchange.pause();
                } else if (view == buttonRight) {
                    formViewSU.animate().translationY(-250).alpha(1).setDuration(500).start();
                    inputType = InputType.SIGN_UP;
                    buttonLeft.setText(R.string.button_confirm_signup);
                    buttonRight.setText(R.string.button_cancel_signup);
                    mediaPlayerchange.pause();
                }

                break;
            case LOGIN:
                //登录操作
                if (view == buttonLeft) {
                    if(validate()){
                        //okhttp方法：
                        hashMap.clear();
                        user_name=formView.getEdit1();
                        user_pwd=formView.getEdit2();
                        if (!TextUtils.isEmpty(user_name)&&!TextUtils.isEmpty(user_pwd)){
                            try {
                                hashMap.put(Const.USER_NAME, URLEncoder.encode(user_name,"UTF-8"));
                                hashMap.put(Const.USER_PASSWODR,URLEncoder.encode(user_pwd,"UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        httpHelper.getJsonData(Const.URL_LOGIN, hashMap);
//                        new Thread(new GetPostUtil("MyServer","switchMode=userLogin&username="+formView.getEdit1()+"&userpass="+formView.getEdit2())).start();
                        prename=formView.getEdit1();
                        formView.setEdit1("");
                        formView.setEdit2("");
                    }
                } else if (view == buttonRight) {
                    formView.animate().translationY(-1 * delta).alpha(0).setDuration(500).start();
                    mediaPlayerchange.start();
                    inputType = InputType.NONE;
                    buttonLeft.setText(R.string.button_login);
                    buttonRight.setText(R.string.button_signup);
                }

                break;
            case SIGN_UP:

                if (view == buttonLeft) {
                    if(validateSU()){
                        //注册联网操作
                        /*Message msg = new Message();
                        msg.what = 0x346;
                        Bundle bundle = new Bundle();
                        bundle.putString("name",formViewSU.getName());
                        bundle.putString("pass",formViewSU.getPass1());
                        bundle.putString("phone",formViewSU.getPhone());
                        msg.setData(bundle);
                        clientThread.revHandler.sendMessage(msg);//发送注册信息至线程*/
                        hashMap.clear();
                        String user_name=formViewSU.getName();
                        String user_pwd=formViewSU.getPass1();
                        String user_phone=formViewSU.getPhone();
                        try {
                            hashMap.put(Const.USER_NAME,user_name);
                            hashMap.put(Const.USER_PASSWODR,URLEncoder.encode(user_pwd,"utf-8"));
                            hashMap.put(Const.USER_PHONE,URLEncoder.encode(user_phone,"utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        httpHelper.getJsonData(Const.URL_SIGNUP,hashMap);


//                        new Thread(new GetPostUtil("MyServer","switchMode=userSign_up&username="+formViewSU.getName()+"&userpass="+formViewSU.getPass1()+"&userphone"+formViewSU.getPhone())).start();


                        prename=formViewSU.getName();
                        formViewSU.setName("");
                        formViewSU.setPass1("");
                        formViewSU.setPass2("");
                        formViewSU.setPhone("");
                    }
                } else if (view == buttonRight) {
                    formViewSU.animate().translationY(-1 * deltaSU).alpha(0).setDuration(500).start();
                    mediaPlayerchange.start();
                    inputType = InputType.NONE;
                    buttonLeft.setText(R.string.button_login);
                    buttonRight.setText(R.string.button_signup);
                }
                break;
        }
    }
    //规范输入
    private boolean validate() {
        String username = formView.getEdit1();
        if (username.equals("")) {
            Toast.makeText(MainActivity.this,"还没填用户名呢。。",Toast.LENGTH_LONG).show();
            formView.setfocus1();//改变焦点
            SoftInput();
            return false;
        }
        if (username.indexOf("|")!=-1) {
            Toast.makeText(MainActivity.this,"用户名中不能输入特殊字符 | 哦",Toast.LENGTH_LONG).show();
            formView.setfocus1();//改变焦点
            SoftInput();
            return false;
        }
        String pwd = formView.getEdit2();
        if (pwd.equals("")) {
            Toast.makeText(MainActivity.this,"还没填写密码呢。。",Toast.LENGTH_LONG).show();
            formView.setfocus2();//改变焦点
            SoftInput();
            return false;
        }
        if (pwd.indexOf("|")!=-1) {
            Toast.makeText(MainActivity.this, "密码中不能输入特殊字符 | 哦", Toast.LENGTH_LONG).show();
            formView.setfocus2();//改变焦点
            SoftInput();
        }
        return true;
    }
    private boolean validateSU(){
        String username = formViewSU.getName();
        if (username.equals("")) {
            Toast.makeText(MainActivity.this,"还没填用户名呢。。",Toast.LENGTH_LONG).show();
            formViewSU.setfocusname();//改变焦点
            SoftInput();
            return false;
        }
        if (username.indexOf("|")!=-1) {
            Toast.makeText(MainActivity.this, "用户名中不能输入特殊字符 | 哦", Toast.LENGTH_LONG).show();
            formViewSU.setfocusname();//改变焦点
            SoftInput();
        }
        String pass1 = formViewSU.getPass1();
        if (pass1.equals("")) {
            Toast.makeText(MainActivity.this,"还没填密码呢。。",Toast.LENGTH_LONG).show();
            formViewSU.setfocuspass1();//改变焦点
            SoftInput();
            return false;
        }
        if (pass1.indexOf("|")!=-1) {
            Toast.makeText(MainActivity.this, "密码中不能输入特殊字符 | 哦", Toast.LENGTH_LONG).show();
            formViewSU.setfocuspass1();//改变焦点
            SoftInput();
        }
        String pass2 = formViewSU.getPass2();
        if (pass2.equals("")) {
            Toast.makeText(MainActivity.this,"还要输入一次密码呢。。",Toast.LENGTH_LONG).show();
            formViewSU.setfocuspass2();//改变焦点
            SoftInput();
            return false;
        }
        if (pass2.indexOf("|")!=-1) {
            Toast.makeText(MainActivity.this, "密码中不能输入特殊字符 | 哦", Toast.LENGTH_LONG).show();
            formViewSU.setfocuspass2();//改变焦点
            SoftInput();
        }
        String phone = formViewSU.getPhone();
        if (phone.equals("")) {
            Toast.makeText(MainActivity.this,"还没填手机号呢。。",Toast.LENGTH_LONG).show();
            formViewSU.setfocusphone();//改变焦点
            SoftInput();
            return false;
        }
        if(!pass1.equals(pass2)){
            Toast.makeText(MainActivity.this,"两次输入的密码要一样哦",Toast.LENGTH_LONG).show();
            formViewSU.setfocuspass1();//改变焦点
            formViewSU.setPass1("");
            formViewSU.setPass2("");
            SoftInput();
            return false;
        }
        if(phone.length()!=11){
            Toast.makeText(MainActivity.this,"手机号码要是11位的呢。。",Toast.LENGTH_LONG).show();
            formViewSU.setfocusphone();//改变焦点
            SoftInput();
            return false;
        }
        return true;
    }
    //开启输入法
    private void SoftInput(){
        InputMethodManager m=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    static public SharedPreferences getpreferences(){
        return preferences;
    }

    enum InputType {
        NONE, LOGIN, SIGN_UP
    }

    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case Const.LOGIN_STATE_UNEXIST:
                   Toast.makeText(UIUtils.getContext(),"账号不存在",Toast.LENGTH_SHORT).show();
                   formView.setEdit1("");//清空账号输入
                   formView.setEdit2("");//清空密码输入
                   break;
               case Const.LOGIN_STATE_PWDFAIL:
                   Toast.makeText(UIUtils.getContext(),"密码错误，请重新输入",Toast.LENGTH_SHORT).show();
                   formView.setEdit1("");//清空账号输入
                   formView.setEdit2("");//清空密码输入
                   break;
               case Const.LOGIN_STATE_SUCCESS:
                   UIUtils.setSpNumInt(Const.USER_ID,(int)msg.obj);//设置ID号保存
                   UIUtils.setSpString(Const.USER_NAME, user_name);//设置用户名保存
                   Toast.makeText(UIUtils.getContext(),"成功登录",Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(MainActivity.this,ContainActivity.class));
                   finish();
                   break;
               case Const.SIGNUP_STATE_REPEAT:
                   Toast.makeText(UIUtils.getContext(),"账号已存在",Toast.LENGTH_SHORT).show();
                   //清空输入项
                   formViewSU.setName("");
                   formViewSU.setPass1("");
                   formViewSU.setPass2("");
                   formViewSU.setPhone("");
                   break;
               case Const.SIGNUP_STATE_SUCCESS:
                   UIUtils.setSpNumInt(Const.USER_ID,(int)msg.obj);//设置ID号保存
                   UIUtils.setSpString(Const.USER_NAME, user_name);//设置用户名保存
                   Toast.makeText(UIUtils.getContext(),"注册成功",Toast.LENGTH_SHORT).show();
                   Const.APPEND_INTSTATE=Const.APPEND_REGISTER;
                   startActivity(new Intent(MainActivity.this, AppendActivity.class));
                   finish();
                   break;
           }
        }
    };
    private long back_time;
    //双击返回的逻辑处理
    @Override
    public void onBackPressed() {
        long progress= System.currentTimeMillis();
        if (progress-back_time>2000){
            Toast.makeText(UIUtils.getContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
            back_time=progress;
        }else {
            finish();
        }
    }


}
