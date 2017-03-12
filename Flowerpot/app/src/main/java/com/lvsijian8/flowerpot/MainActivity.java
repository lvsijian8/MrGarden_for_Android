package com.lvsijian8.flowerpot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.lvsijian8.flowerpot.ui.activity.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    ClientThread clientThread;
    Handler handler;
    MediaPlayer mediaPlayerchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        preferences=getSharedPreferences("user",MODE_PRIVATE);
        int mark=0;
        if(0<preferences.getInt("user_id",-1)){
            Intent intent = new Intent(MainActivity.this, Main.class);//已登录,跳过登录界面
            startActivity(intent);
            mark=1;
        }
        Intent intentf = new Intent(MainActivity.this, FirstPicture.class);
        startActivity(intentf);
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
        handler = new Handler()                                     //等待服务器返回
        {
            @Override
            public void handleMessage(Message msg)
            {
                // 如果消息来自于子线程
                //if (msg.what == 0x123)
                if (msg.what == 0x888)
                {
                    String content=msg.obj.toString();
                    switch((content.charAt(0)-'0')){
                        case 0:{
                            if((content.charAt(1)-'0')>0)
                            {
                                Toast.makeText(MainActivity.this,"密码正确。",Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString("name",prename);
                                editor.putInt("user_id",(content.charAt(1)-'0'));
                                editor.commit();
                                Intent intent = new Intent(MainActivity.this, Main.class);
                                startActivity(intent);//以登陆跳转界面
                                finish();
                                break;
                            }
                            else {
                                Toast.makeText(MainActivity.this, "输入有误，请重新输入。", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                        case 1:{
                            if((content.charAt(1)-'0')==-1)
                            {
                                Toast.makeText(MainActivity.this,"该用户用户已存在，请登录",Toast.LENGTH_LONG).show();break;
                            }
                            else if((content.charAt(1)-'0')>0)
                            {
                                Toast.makeText(MainActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString("name",prename);
                                editor.putInt("user_id",(content.charAt(1)-'0'));
                                editor.commit();
                                Intent intent = new Intent(MainActivity.this, SignUpFirst.class);//跳转初始化界面
                                startActivity(intent);
                                finish();
                                break;
                            }
                            else {
                                Toast.makeText(MainActivity.this, "网络连接失败，请重新注册", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                }
                /*if (msg.what == 0x888)//登陆进程返回值
                {




                    Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                }*/
            }
        };
        //ClientThread.handler=handler;
        GetPostUtil.handler=handler;
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
                int delta = formView.getTop()+formView.getHeight();
                formView.setTranslationY(-1 * delta);
            }
        });
        formViewSU.post(new Runnable() {
            @Override
            public void run() {
                int deltaSU = formViewSU.getTop()+formViewSU.getHeight();
                formViewSU.setTranslationY(-1 * deltaSU);
            }
        });
    }

    private void initView() {

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

                if (view == buttonLeft) {
                    if(validate()){
                        new Thread(new GetPostUtil("MyServer","switchMode=userLogin&username="+formView.getEdit1()+"&userpass="+formView.getEdit2())).start();
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
                        /*Message msg = new Message();
                        msg.what = 0x346;
                        Bundle bundle = new Bundle();
                        bundle.putString("name",formViewSU.getName());
                        bundle.putString("pass",formViewSU.getPass1());
                        bundle.putString("phone",formViewSU.getPhone());
                        msg.setData(bundle);
                        clientThread.revHandler.sendMessage(msg);//发送注册信息至线程*/


                        new Thread(new GetPostUtil("MyServer","switchMode=userSign_up&username="+formViewSU.getName()+"&userpass="+formViewSU.getPass1()+"&userphone"+formViewSU.getPhone())).start();


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
        NONE, LOGIN, SIGN_UP;
    }
}
