package com.lvsijian8.flowerpot.ui.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout rl_main;
    private EditText[] et_array;//输入框的集合
    private ImageView[] iv_array;//图案的集合
    private Button btn_register;
    private boolean[] isHide;
    private HttpHelper httpHelper;
    private HashMap<String, Object> params;
    private String name;//用户名
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case Const.SIGNUP_STATE_REPEAT:
                   Toast.makeText(UIUtils.getContext(), "账号已存在", Toast.LENGTH_SHORT).show();
                   //清空输入项
                   et_array[0].setText("");
                   break;
               case Const.SIGNUP_STATE_SUCCESS:
                   UIUtils.setSpNumInt(Const.USER_ID,(int)msg.obj);//设置ID号保存
                   UIUtils.setSpString(Const.USER_NAME, name);//设置用户名保存
                   Toast.makeText(UIUtils.getContext(),"注册成功",Toast.LENGTH_SHORT).show();
                   //以下两行为Mr。Flower从注册到花盆添加界面的处理
//                   Const.APPEND_INTSTATE=Const.APPEND_REGISTER;
//                   startActivity(new Intent(RegisterActivity.this, AppendActivity.class));

                   finish();
                   break;
           }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        initui();
        initdata();
    }

    private void initui() {
        httpHelper = HttpHelper.getInstances();
        params = new HashMap<>();
        rl_main = (RelativeLayout) findViewById(R.id.rl_register_main);
        isHide=new boolean[4];
        et_array=new EditText[4];
        et_array[0]= (EditText) findViewById(R.id.et_register_name);
        et_array[1]= (EditText) findViewById(R.id.et_register_phone);
        et_array[2]= (EditText) findViewById(R.id.et_register_pwd);
        et_array[3]= (EditText) findViewById(R.id.et_register_check);
        iv_array=new ImageView[4];
        iv_array[0]= (ImageView) findViewById(R.id.iv_register_name);
        iv_array[1]= (ImageView) findViewById(R.id.iv_register_phone);
        iv_array[2]= (ImageView) findViewById(R.id.iv_register_pwd);
        iv_array[3]= (ImageView) findViewById(R.id.iv_register_check);
        btn_register = (Button) findViewById(R.id.btn_register_register);
        controlKeyboardLayout(rl_main, btn_register);
    }

    private void initdata() {
        iv_array[0].setOnClickListener(this);
        iv_array[1].setOnClickListener(this);
        iv_array[2].setOnClickListener(this);
        iv_array[3].setOnClickListener(this);
        btn_register.setOnClickListener(this);
        judgeLength(et_array[0], iv_array[0]);
        judgeLength(et_array[1], iv_array[1]);
        //密码初始不可见
        et_array[2].setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                InputType.TYPE_CLASS_TEXT);
        et_array[3].setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                InputType.TYPE_CLASS_TEXT);



        //根据返回的code判断状态
        httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                Message message = new Message();
                if (data != null) {
                    int code = Integer.parseInt(data.toString().trim());
                    if (code == -1) {
                        message.what = Const.SIGNUP_STATE_REPEAT;
                    } else if (code>0){
                        message.what = Const.SIGNUP_STATE_SUCCESS;
                        message.obj = code;
                    }
                    mhandler.sendMessage(message);
                }
            }

            @Override
            public void failConnect() {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UIUtils.makeText("网络异常");
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_register_name:
                et_array[0].setText("");
                break;
            case R.id.iv_register_phone:
                et_array[1].setText("");
                break;
            case R.id.iv_register_pwd:
                visibilityPwd(2);
                break;
            case R.id.iv_register_check:
                visibilityPwd(3);
                break;
            case R.id.btn_register_register:
                connectHttp();
                break;
        }
    }

    /**
     * 输入密码可见与不可见的处理
     * position为数组中密码的index
     */
    private void visibilityPwd(int position){
        if (isHide[position]){
            //切换为不可见
            et_array[position].setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                    InputType.TYPE_CLASS_TEXT);
            iv_array[position].setImageResource(R.drawable.icon_invisible);
            isHide[position]=false;
        }else {
            //切换为可见
            et_array[position].setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            iv_array[position].setImageResource(R.drawable.icon_visible);
            isHide[position]=true;
        }
        et_array[position].setSelection(et_array[position].length());
    }

    /**
     * 输入长度>0时，可清空输入框
     * @param et
     * @param iv
     */
    private void judgeLength(EditText et, final ImageView iv){
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

    private void connectHttp(){
        name = et_array[0].getText().toString().trim();
        String phone = et_array[1].getText().toString().trim();
        String pwd= et_array[2].getText().toString().trim();
        String check = et_array[3].getText().toString().trim();
        if (judgeisNull(et_array)){
            //11位手机号码验证
            if (phone.length()==11){
                //判断两次输入的密码是否一致
                if (!pwd.equals(check)){
                    UIUtils.makeText("密码不一致，请重新输入");
                }else {
                   if (UIUtils.checkCellphone(phone)){
                       params.put(Const.USER_NAME,name);
                       params.put(Const.USER_PASSWODR,pwd);
                       params.put(Const.USER_PHONE,phone);
                       httpHelper.getJsonData(Const.URL_SIGNUP,params);
                   }else {
                       UIUtils.makeText("手机号码格式错误");
                       et_array[1].setFocusable(true);
                       et_array[1].requestFocus();
                   }
                }
            }else {
                UIUtils.makeText("手机号码长度有误，请检查");
                et_array[1].setFocusable(true);
                et_array[1].requestFocus();
            }

        }
    }


    /**
     * 对提交的表单进行非空验证，如果为空，焦点移动到该项并输出警告
     * @param etits
     * @return
     */
    private boolean judgeisNull(EditText[] etits){
        for (int i=0;i<etits.length;i++){
            if (TextUtils.isEmpty(etits[i].getText().toString().trim())){
                switch (i){
                    case 0:
                        UIUtils.makeText("手机号码不能为空");
                        break;
                    case 1:
                        UIUtils.makeText("联系方式不能为空");
                        break;
                    case 2:
                        UIUtils.makeText("密码不能为空");
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
     * 解决软键盘遮挡按钮
     * @param root
     * @param scrollToView
     */
    private void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    int[] location = new int[2];
                    //获取scrollToView在窗体的坐标
                    scrollToView.getLocationInWindow(location);
                    //计算root滚动高度，使scrollToView在可见区域
                    int srollHeight = (location[1] + scrollToView.getHeight()) - rect.bottom;
                    if (srollHeight != 0) {
                        root.scrollTo(0, srollHeight);
                    }
                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0);
                }
            }
        });
    }


}
