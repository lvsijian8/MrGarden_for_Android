package com.lvsijian8.flowerpot.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.view.TetText;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;

public class AlertActivity extends AppCompatActivity {

    private TetText tet_username;//用户名
    private TetText tet_newpsw;//新密码
    private TetText tet_oldpsw;//老密码
    private TetText tet_checkpsw;//新密码的确认
    private TetText tet_phone;//联系电话
    private Button btn_sumbit;//提交按钮
    private ImageView iv_back;
    private Gson gson;
    private HttpHelper httpHelper;
    private static final int STATE_ALERT_FIRST=0X021;
    private static final int STATE_ALERT_SUMBIT=0X022;
    private int STATE_ALERT_CURRENT=STATE_ALERT_FIRST;
    private HashMap<String, Object> params;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case STATE_ALERT_FIRST:
                    String data=(String)(msg.obj);
                    String[] datas=data.split("\\|");
                    tet_username.setEtitText(datas[0]);
                    tet_phone.setEtitText(datas[1]);
                    break;
                case STATE_ALERT_SUMBIT:
                    int code=Integer.parseInt((String)(msg.obj));
                    if (code==-1){
                        Toast.makeText(UIUtils.getContext(),"原密码错误，请重新填写",Toast.LENGTH_SHORT).show();
                    }else if (code==-3){
                        Toast.makeText(UIUtils.getContext(),"用户名已存在",Toast.LENGTH_SHORT).show();
                    }else if (code==-4){
                        Toast.makeText(UIUtils.getContext(),"网络错误，请重试",Toast.LENGTH_SHORT).show();
                    }else if(code>0){
                        Toast.makeText(UIUtils.getContext(),"修改成功",Toast.LENGTH_SHORT).show();
                        if (!TextUtils.isEmpty(text_username)){
                            UIUtils.setSpString(Const.USER_NAME,text_username);
                        }
//                        finish();
                    }
                    break;
            }
        }
    };
    private String text_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        getSupportActionBar().hide();
        initui();
        initdata();
    }

    private void initui() {
        gson = new Gson();
        params = new HashMap<>();
        httpHelper = HttpHelper.getInstances();
        iv_back= (ImageView) findViewById(R.id.iv_alert_back);
        tet_username = (TetText) findViewById(R.id.tet_alert_username);
        tet_newpsw = (TetText) findViewById(R.id.tet_alert_newpsw);
        tet_oldpsw = (TetText) findViewById(R.id.tet_alert_oldpsw);
        tet_checkpsw = (TetText) findViewById(R.id.tet_alert_check);
        tet_phone = (TetText) findViewById(R.id.tet_alert_phone);
        btn_sumbit = (Button) findViewById(R.id.btn_alert_sumbit);

    }

    private void initdata() {
        //设置输入框的输入类型
        tet_phone.setInputType(InputType.TYPE_CLASS_PHONE);//设置输入模式为电话号码
        tet_oldpsw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//当前密码输入类型为可见密码
        tet_newpsw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        tet_checkpsw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        params.clear();
        params.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");
        httpHelper.getDetailData(Const.URL_GETINFO, params);
        httpHelper.setOnDetailgetData(new HttpHelper.OnDetailgetData() {
            @Override
            public void successGet(String data) {
                if (!TextUtils.isEmpty(data)) {
                    Log.e("ZDLW", data);
                    Message message = new Message();
                    switch (STATE_ALERT_CURRENT) {
                        //获取用户名和电话
                        case STATE_ALERT_FIRST:
                            message.what = STATE_ALERT_FIRST;
                            message.obj = data;
                            mhandler.sendMessage(message);
                            break;
                        //提交修改后的资料
                        case STATE_ALERT_SUMBIT:
                            message.what = STATE_ALERT_SUMBIT;
                            message.obj = data;
                            mhandler.sendMessage(message);
                            break;
                    }
                }
            }

            @Override
            public void failGet() {

            }
        });
        btn_sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogiSumbit();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
    }

    /**
     * 上传修改后的个人资料
     */
    private void LogiSumbit(){
        int checkCode=1;
        text_username = tet_username.getEditTextMsg();
        String text_phone=tet_phone.getEditTextMsg();
        String text_newpsw=tet_newpsw.getEditTextMsg();
        String text_oldpsw=tet_oldpsw.getEditTextMsg();
        String text_check=tet_checkpsw.getEditTextMsg();
        params.clear();
        if (!TextUtils.isEmpty(text_username)&&!TextUtils.isEmpty(text_oldpsw)){//用户名、旧密码必须存在
            params.put("username", text_username);//1
            params.put("oldpsw",text_oldpsw);//2
            //对新密码和验证密码的填写处理
            if (TextUtils.isEmpty(text_newpsw)){
                //新密码未填写
                if (!TextUtils.isEmpty(text_check)){
                    //校验密码已填写
                    Toast.makeText(UIUtils.getContext(),"密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
                    checkCode=-1;
                }else {
                    //校验密码未填写
                    params.put("newpsw","");//3
                    checkCode=1;
                }
            }else {
                //新密码已填写
                if (!TextUtils.isEmpty(text_check)){
                    //检验密码已填写
                    if (!text_newpsw.equals(text_check)){
                        Toast.makeText(UIUtils.getContext(),"密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
                        checkCode=-1;
                    }else {
                        params.put("newpsw",text_newpsw);//3
                        checkCode=1;
                    }
                }else {
                    //校验密码未填写
                    Toast.makeText(UIUtils.getContext(),"密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
                    checkCode=-1;
                }
            }
            /*---------------------------------------------------------------------------------------------------*/
            if (!TextUtils.isEmpty(text_phone)){
                params.put("phone",text_phone+"");//4
            }else {
                params.put("phone","");//4
            }
        }else {
            Toast.makeText(UIUtils.getContext(),"用户名、密码必须填写",Toast.LENGTH_SHORT).show();
        }
        if (checkCode==1){
            for (Map.Entry<String,Object> entry:params.entrySet()){
                Log.e("ZDLW","Key："+entry.getKey()+"  Value："+entry.getValue());
            }
            params.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID)+"");//5
            STATE_ALERT_CURRENT=STATE_ALERT_SUMBIT;
            httpHelper.getDetailData(Const.URL_ALERTINFO, params);
        }


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }
}
