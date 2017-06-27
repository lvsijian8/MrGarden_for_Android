package com.lvsijian8.flowerpot;


import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.activity.ContainActivity;
import com.lvsijian8.flowerpot.utils.UIUtils;


public class SignUpFirst extends AppCompatActivity {
    private int sex;
    EditText potnamein;
    private HttpHelper httpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.signupfirst_view);
        getSupportActionBar().hide();
        httpHelper=HttpHelper.getInstances();
        httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                if (data != null) {
                    int code = Integer.parseInt(data);
                    if (code == 0) {
                        Toast.makeText(UIUtils.getContext(),"账号不存在，请重新输入",Toast.LENGTH_SHORT).show();

                    } else if (code == 1) {

                    } else if (code == 2) {

                    }
                }
            }

            @Override
            public void failConnect() {

            }
        });

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sex = checkedId == R.id.boy ? 1 : 0;
            }
        });
        potnamein = (EditText) findViewById(R.id.potname);
        Button next = (Button) findViewById(R.id.nextbutton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Message msg = new Message();
//                msg.what = 0x347;
//                Bundle bundle = new Bundle();
//                String potname = potnamein.getText().toString();
//                String name=MainActivity.getpreferences().getString("name","name");
//                bundle.putString("name",name);
//                bundle.putInt("sex", sex);
//                bundle.putString("potname", potname);
//                msg.setData(bundle);
//                ClientThread.revHandler.sendMessage(msg);
                Intent intent = new Intent(SignUpFirst.this, ContainActivity.class);
                startActivity(intent);//以登陆跳转界面
                finish();
            }
        });
    }
}
