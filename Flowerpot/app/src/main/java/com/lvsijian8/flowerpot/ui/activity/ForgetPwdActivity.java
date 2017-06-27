package com.lvsijian8.flowerpot.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.HashMap;

public class ForgetPwdActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText[] mEditTexts;
    private ImageView[] micon;
    private HttpHelper mHttpHelper;
    private HashMap<String, Object> mParams;
    private Gson mGson;
    private Button mBtn_sumbit;
    private boolean ishide;
    private boolean isHide[];
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int code = Integer.parseInt((String) msg.obj);
            if (code == -4) {
                UIUtils.makeText("电话号码错误");
            } else if (code == -2) {
                UIUtils.makeText("用户名不存在");
            } else if (code > 0) {
                UIUtils.makeText("修改成功");
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        getSupportActionBar().hide();
        initui();
        initdata();
    }

    private void initui() {
        isHide = new boolean[4];
        mEditTexts = new EditText[4];
        mEditTexts[0] = (EditText) findViewById(R.id.et_forget_name);
        mEditTexts[1] = (EditText) findViewById(R.id.et_forget_phone);
        mEditTexts[2] = (EditText) findViewById(R.id.et_forget_pwd);
        mEditTexts[3] = (EditText) findViewById(R.id.et_forget_check);
        micon = new ImageView[4];
        micon[0] = (ImageView) findViewById(R.id.iv_forget_name);
        micon[1] = (ImageView) findViewById(R.id.iv_forget_phone);
        micon[2] = (ImageView) findViewById(R.id.iv_forget_pwd);
        micon[3] = (ImageView) findViewById(R.id.iv_forget_check);
        mBtn_sumbit = (Button) findViewById(R.id.btn_forget_sumbit);
        mHttpHelper = HttpHelper.getInstances();
        mParams = new HashMap<>();
        mGson = new Gson();
    }

    private void initdata() {
        micon[0].setOnClickListener(this);
        micon[1].setOnClickListener(this);
        micon[2].setOnClickListener(this);
        micon[3].setOnClickListener(this);
        mBtn_sumbit.setOnClickListener(this);
        //设置密码不可见
        mEditTexts[2].setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                InputType.TYPE_CLASS_TEXT);
        for (int i = 0; i < mEditTexts.length; i++) {
            judgeLength(mEditTexts[i], micon[i]);
        }
        mHttpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                Log.e("ZDLW", data);
                if (!TextUtils.isEmpty(data)) {
                    Message message = new Message();
                    message.obj = data;
                    mHandler.sendMessage(message);
                }
            }

            @Override
            public void failConnect() {
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIUtils.makeText("网络异常");
                    }
                });
            }
        });
    }


    /**
     * 对提交的表单进行非空验证，如果为空，焦点移动到该项并输出警告
     *
     * @param etits
     * @return
     */
    private boolean judgeisNull(EditText[] etits) {
        for (int i = 0; i < etits.length; i++) {
            if (TextUtils.isEmpty(etits[i].getText().toString().trim())) {
                switch (i) {
                    case 0:
                        UIUtils.makeText("用户名不能为空");
                        break;
                    case 1:
                        UIUtils.makeText("电话不能为空");
                        break;
                    case 2:
                        UIUtils.makeText("新密码不能为空");
                        break;
                    case 3:
                        UIUtils.makeText("确认密码不能为空");
                        break;
                }
                etits[i].setFocusable(true);
                etits[i].requestFocus();
                return false;
            }
        }
        return true;
    }

    /**
     * 输入长度>0时，可清空输入框
     *
     * @param et
     * @param iv
     */
    private void judgeLength(EditText et, final ImageView iv) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    iv.setVisibility(View.VISIBLE);
                } else {
                    iv.setVisibility(View.GONE);
                }
            }
        });
    }

    private void visibilityPwd(int position) {
        if (isHide[position]) {
            //切换为不可见
            mEditTexts[position].setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                    InputType.TYPE_CLASS_TEXT);
            micon[position].setImageResource(R.drawable.icon_invisible);
            isHide[position] = false;
        } else {
            //切换为可见
            mEditTexts[position].setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            micon[position].setImageResource(R.drawable.icon_visible);
            isHide[position] = true;
        }
        mEditTexts[position].setSelection(mEditTexts[position].length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_forget_name:
                judgeLength(mEditTexts[0], micon[0]);
                mEditTexts[0].setText("");
                break;
            case R.id.iv_forget_phone:
                judgeLength(mEditTexts[1], micon[1]);
                mEditTexts[1].setText("");
                break;
            case R.id.iv_forget_pwd:
                visibilityPwd(2);
                break;
            case R.id.iv_forget_check:
                visibilityPwd(3);
                break;
            case R.id.btn_forget_sumbit:
                if (judgeisNull(mEditTexts)) {
                    if (mEditTexts[2].getText().toString().equals(mEditTexts[3].getText().toString())) {
                        mParams.put("user_name", mEditTexts[0].getText().toString());
                        mParams.put("user_phone", mEditTexts[1].getText().toString());
                        mParams.put("user_pwd", mEditTexts[2].getText().toString());
                        mHttpHelper.getJsonData(Const.URL_FORGET, mParams);
                    }
                }
                break;
        }
    }
}
