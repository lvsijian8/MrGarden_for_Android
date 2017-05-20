package com.lvsijian8.flowerpot.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.fragment.FragmentData;
import com.lvsijian8.flowerpot.ui.fragment.FragmentFind;
import com.lvsijian8.flowerpot.ui.fragment.FragmentMe;
import com.lvsijian8.flowerpot.ui.fragment.FragmentTime;
import com.lvsijian8.flowerpot.ui.view.MyIndicator_data;
import com.lvsijian8.flowerpot.utils.UIUtils;

import static android.widget.ImageView.ScaleType.CENTER_INSIDE;
import static android.widget.ImageView.ScaleType.FIT_CENTER;
import static android.widget.ImageView.ScaleType.MATRIX;

public class ContainActivity extends FragmentActivity {
    private long back_time;
    private FragmentTabHost tabHost;
    private final String[] TAB_id={"item_data","item_find","item_time","item_me"};
    private final int[] TAB_img={R.drawable.main_tab_item_data,R.drawable.main_tab_item_find,R.drawable.main_tab_item_time,R.drawable.main_tab_item_me};
    private final String[] TAB_name={"消息","发现","设备","我"};
    private Class TAB_fragment[]={FragmentData.class,FragmentFind.class,FragmentTime.class,FragmentMe.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        initView();
        //判断是否是第一次进入APP，是的话在桌面生成快捷方式
        if (!UIUtils.getSpBoolean("Second_Enter")){
            initShortcut();
            UIUtils.setSpBoolean("Second_Enter",true);
        }
    }

    private void initView() {
        //初始化Tabhost
        tabHost= (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.activity_home_container);
        //新建并添加TabSpec 区域中的小方块
        for (int i=0;i<TAB_id.length;i++){
            TabHost.TabSpec spec=tabHost.newTabSpec(TAB_id[i]).setIndicator(getTabItem(i));
            tabHost.addTab(spec,TAB_fragment[i],null);
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);

        }
        tabHost.setCurrentTabByTag(TAB_id[0]);
        tabHost.getTabWidget().setDividerDrawable(null);

    }



    /**
     * 传一个View去给TabHost加载
     * @param i
     * @return
     */
    private View getTabItem(int i){
        View view=View.inflate(this,R.layout.tab_data,null);
        TextView tv= (TextView) view.findViewById(R.id.tv_tab);
        ImageView img= (ImageView) view.findViewById(R.id.img_tab);
        tv.setText(TAB_name[i]);
        img.setImageResource(TAB_img[i]);
        return view;
    }
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

    private void initShortcut() {
        //1,给intent维护图标,名称
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //维护图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Mr.Flower");
        //2,点击快捷方式后跳转到的activity
        //2.1维护开启的意图对象
        Intent shortCutIntent = new Intent("android.intent.action.HOME");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        //3,发送广播
        sendBroadcast(intent);
        Log.d("ZDLW","ZDLW");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        FragmentFind find= (FragmentFind) getSupportFragmentManager().findFragmentByTag(TAB_id[1]);
//        if (find!=null){
//            Log.e("ZDLW","CLOSE");
//            find.CloseRelistview();
//        }
    }
}
