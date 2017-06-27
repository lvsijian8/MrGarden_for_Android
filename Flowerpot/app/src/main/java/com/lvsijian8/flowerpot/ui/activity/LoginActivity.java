package com.lvsijian8.flowerpot.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {


    private HttpHelper httpHelper;
    private Gson gson;
    private HashMap<String,Object> params;
    private EditText et_name;
    private EditText et_pwd;
    private ImageView iv_delete;
    private ImageView iv_visibili;
    private boolean isVisibili;
    private Button btn_login;
    private TextView tv_froget;
    private TextView tv_register;
    private String username;
    private ScrollView sc_contai;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Const.LOGIN_STATE_UNEXIST:
                    Toast.makeText(UIUtils.getContext(),"账号不存在",Toast.LENGTH_SHORT).show();
                    et_name.setText("");//清空账号输入
                    et_pwd.setText("");//清空密码输入
                    break;
                case Const.LOGIN_STATE_PWDFAIL:
                    Toast.makeText(UIUtils.getContext(),"密码错误，请重新输入",Toast.LENGTH_SHORT).show();
                    et_name.setText("");//清空账号输入
                    et_pwd.setText("");//清空密码输入
                    break;
                case Const.LOGIN_STATE_SUCCESS:
                    UIUtils.setSpNumInt(Const.USER_ID,(int)msg.obj);//设置ID号保存
                    UIUtils.setSpString(Const.USER_NAME, username);//设置用户名保存
                    Toast.makeText(UIUtils.getContext(),"成功登录",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,ContainActivity.class));
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        if (UIUtils.getSpInt(Const.USER_ID)!=-1){
            startActivity(new Intent(this,ContainActivity.class));
            finish();
        }
        initui();
        initdata();
    }

    private void initui() {
        gson = new Gson();
        httpHelper = HttpHelper.getInstances();
        params=new HashMap<>();
        RelativeLayout rl_main= (RelativeLayout) findViewById(R.id.rl_shiyan_main);
        sc_contai = (ScrollView) findViewById(R.id.sc_shiyan_contain);
        et_name = (EditText) findViewById(R.id.et_shiyan_name);
        et_pwd = (EditText) findViewById(R.id.et_shiyan_pwd);
        iv_delete = (ImageView) findViewById(R.id.iv_shiyan_delete);
        iv_visibili = (ImageView) findViewById(R.id.iv_shiyan_visibili);
        btn_login = (Button) findViewById(R.id.btn_shiyan_login);
        tv_froget= (TextView) findViewById(R.id.tv_shiyan_forget);
        tv_register= (TextView) findViewById(R.id.tv_shiyan_register);
    }

    private void initdata() {
        httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                Message message=new Message();
                if (data!=null){
                    int code=Integer.parseInt(data.toString().trim());
                    if (code==-2){
                        message.what= Const.LOGIN_STATE_UNEXIST;
                    }else if (code==-3){
                        message.what=Const.LOGIN_STATE_PWDFAIL;
                    } else {
                        message.what=Const.LOGIN_STATE_SUCCESS;
                        message.obj=code;
                    }
                    mhandler.sendMessage(message);
                }


            }

            @Override
            public void failConnect() {

            }
        });
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    iv_delete.setVisibility(View.VISIBLE);
                } else {
                    iv_delete.setVisibility(View.GONE);
                }
            }
        });

        et_pwd.setImeOptions(EditorInfo.IME_ACTION_DONE);//将回车改为完成

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_name.setText("");
            }
        });

        iv_visibili.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisibili){
                    et_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                            InputType.TYPE_CLASS_TEXT);
                    iv_visibili.setImageResource(R.drawable.icon_invisible);
                    isVisibili=false;
                }else {
                    et_pwd.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iv_visibili.setImageResource(R.drawable.icon_visible);
                    isVisibili=true;
                }
                et_pwd.setSelection(et_pwd.length());
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = et_name.getText().toString().trim();
                String pwd=et_pwd.getText().toString().trim();
                if (judgeResult(username,pwd)){
                    params.put(Const.USER_NAME,username);
                    params.put(Const.USER_PASSWODR,pwd);
                    httpHelper.getJsonData(Const.URL_LOGIN,params);
                }
            }
        });

        //进入注册页面
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        //进入找回密码界面
        tv_froget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPwdActivity.class));
            }
        });
    }

    private long back_time;
    //双击返回的逻辑处理
    @Override
    public void onBackPressed() {
        long progress= System.currentTimeMillis();
        if (progress-back_time>2000){
            Toast.makeText(UIUtils.getContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            back_time=progress;
        }else {
            finish();
        }

    }

    /**
     * 用户名和密码的非空验证
     * @param username
     * @param pwd
     * @return
     */
    private boolean judgeResult(String username,String pwd){
        int i=0;
        if (!TextUtils.isEmpty(username)){
            i++;
        }else {
            UIUtils.makeText("请输入用户名");
        }
        if (!TextUtils.isEmpty(pwd)){
            i++;
        }else {
            UIUtils.makeText("请输入密码");
        }
        return i == 2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (httpHelper!=null){
            httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
                @Override
                public void successConnect(String data) {
                    Message message=new Message();
                    if (data!=null){
                        int code=Integer.parseInt(data.toString().trim());
                        if (code==-2){
                            message.what= Const.LOGIN_STATE_UNEXIST;
                        }else if (code==-3){
                            message.what=Const.LOGIN_STATE_PWDFAIL;
                        } else {
                            message.what=Const.LOGIN_STATE_SUCCESS;
                            message.obj=code;
                        }
                        mhandler.sendMessage(message);
                    }


                }

                @Override
                public void failConnect() {

                }
            });
        }
    }
}
