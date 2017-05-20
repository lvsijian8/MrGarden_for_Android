package com.lvsijian8.flowerpot.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {

    private ImageView iv_back;//返回按钮
    private EditText et_msg;//反馈的信息
    private EditText et_phone;//联系电话
    private Button btn_sumbit;//提交按钮
    private TextView tv_num;
    private HttpHelper httpHelper;
    private HashMap<String, Object> params;

    private final int STATE_MIN=0;
    private final int STATE_SEND=1;
    private final int STATE_MAX=2;
    private int STATE_CURRENT=STATE_MIN;
    private Handler mhandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar().hide();
        initui();
        initdata();
    }

    private void initui() {
        iv_back = (ImageView) findViewById(R.id.iv_feedback_back);
        et_msg = (EditText) findViewById(R.id.et_feedback_msg);
        tv_num = (TextView) findViewById(R.id.tv_feedback_num);
        et_phone = (EditText) findViewById(R.id.et_feedback_phone);
        btn_sumbit = (Button) findViewById(R.id.btn_feedback_sumbit);
        httpHelper = HttpHelper.getInstances();
        params = new HashMap<>();
        httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                final String d=data;
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UIUtils.getContext(),"发送成功，谢谢您的反馈",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void failConnect() {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UIUtils.getContext(),"发送失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initdata() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
        btn_sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送反馈信息
                switch (STATE_CURRENT){
                    case STATE_MIN:
                        Toast.makeText(getApplicationContext(),"您还没输入反馈信息",Toast.LENGTH_SHORT).show();
                        break;
                    case STATE_SEND:
                        if (!TextUtils.isEmpty(et_phone.getText().toString())){
                            params.put(Const.USER_ID,UIUtils.getSpInt(Const.USER_ID)+"");
                            params.put("opinion",et_msg.getText().toString());
                            params.put("phone", et_phone.getText().toString()+"");
                            httpHelper.getJsonData(Const.URL_FEEDBACK, params);
                        }else {
                            Toast.makeText(getApplicationContext(),"请填写您的联系电话",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case STATE_MAX:
                        Toast.makeText(getApplicationContext(),"超出字数限制",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        //监听反馈信息的输入，字数限制200
        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int l=s.toString().length();
                tv_num.setText(l+"/200");
                if (l>0){
                    STATE_CURRENT=STATE_SEND;
                }else if (l==0){
                    STATE_CURRENT=STATE_MIN;
                }else if (l>200){
                    STATE_CURRENT=STATE_MAX;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}
