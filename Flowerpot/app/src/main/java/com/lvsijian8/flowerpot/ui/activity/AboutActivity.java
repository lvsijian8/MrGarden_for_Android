package com.lvsijian8.flowerpot.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.view.TasksCompletedView;
import com.lvsijian8.flowerpot.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_back;//返回按钮
    private TextView tv_version;//版本信息
    private LinearLayout ll_function;//功能介绍
    private LinearLayout ll_updata;//检查更新
    private HttpHelper httpHelper;
    private String versionNmae;
    private String target;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Const.RECEIVE_DATA_SUCCESS:
                    String result= (String) msg.obj;
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        final String code=jsonObject.getString("versioncode");
                        final String apkurl=jsonObject.getString("apkUrl");
                        target=jsonObject.getString("fileName");
                        Log.e("ZDLW",code+"  "+apkurl+" "+target);
                        showUpdata(code, apkurl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private TasksCompletedView tasksview;
    private File apkfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
        initui();
        initdata();
    }

    private void initui() {
        iv_back = (ImageView) findViewById(R.id.iv_about_back);
        tv_version = (TextView) findViewById(R.id.tv_about_version);
        ll_function = (LinearLayout) findViewById(R.id.ll_about__function);
        ll_updata = (LinearLayout) findViewById(R.id.ll_about_updata);
        tasksview = (TasksCompletedView) findViewById(R.id.tasks_about);
        httpHelper = HttpHelper.getInstances();
        versionNmae = UIUtils.getAppVersionName();
    }

    private void initdata() {
        tv_version.setText("版本:"+versionNmae);
        iv_back.setOnClickListener(this);
        ll_function.setOnClickListener(this);
        ll_updata.setOnClickListener(this);
        httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
            @Override
            public void successConnect(String data) {
                Message message = new Message();
                message.what = Const.RECEIVE_DATA_SUCCESS;
                message.obj = data.toString().trim();
                mhandler.sendMessage(message);
            }

            @Override
            public void failConnect() {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_about_back:
                finish();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                break;
            case R.id.ll_about__function:
                startActivity(new Intent(AboutActivity.this,IntroductionActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.ll_about_updata:

                httpHelper.getJsonData(Const.URL_UPDATA,null);//进行版本更新判断
                break;
        }
    }

    /**
     * 更新版本
     * @param code
     * @param apkurl
     */
    public void showUpdata(String code, final String apkurl){
        if (!versionNmae.equals(code)){
            AlertDialog.Builder builder=new AlertDialog.Builder(AboutActivity.this);
            builder.setTitle("检测到版本更新");
            builder.setMessage("是否更新");
            AlertDialog.Builder builder1 = builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    apkfile = new File("mnt/sdcard/",target);
                    Log.e("ZDLW",apkfile.getAbsolutePath());
                    HttpUtils httpUtils = new HttpUtils();
                    HttpHandler handler = httpUtils.download(apkurl, apkfile.getAbsolutePath(), true, false, new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            Toast.makeText(UIUtils.getContext(), "下载成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setDataAndType(Uri.fromFile(apkfile),
                                    "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
//                            android.os.Process.killProcess(android.os.Process.myPid());//关闭当前程序的进程
                            tasksview.setVisibility(View.GONE);//下载结束关闭进度条
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(UIUtils.getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            tasksview.setVisibility(View.VISIBLE);
                            int pro= (int) (current/(total/100));
                            Log.e("ZDLW", "pro: "+pro);
                            tasksview.setProgress(pro);
                        }
                    });

                }

            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();

        }else {
            Toast.makeText(UIUtils.getContext(),"当前已是最新版本",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
