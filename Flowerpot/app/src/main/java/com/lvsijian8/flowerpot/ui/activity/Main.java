package com.lvsijian8.flowerpot.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.ui.fragment.FragmentData;
import com.lvsijian8.flowerpot.ui.fragment.FragmentFind;
import com.lvsijian8.flowerpot.ui.fragment.FragmentMe;
import com.lvsijian8.flowerpot.ui.fragment.FragmentTime;
import com.lvsijian8.flowerpot.ui.view.MyIndicator_data;

import static android.widget.ImageView.ScaleType.CENTER_INSIDE;
import static android.widget.ImageView.ScaleType.FIT_CENTER;
import static android.widget.ImageView.ScaleType.MATRIX;

public class Main extends FragmentActivity {
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

    private View getTabItem(int i){
        View view=View.inflate(this,R.layout.tab_data,null);
        TextView tv= (TextView) view.findViewById(R.id.tv_tab);
        ImageView img= (ImageView) view.findViewById(R.id.img_tab);
        tv.setText(TAB_name[i]);
        img.setImageResource(TAB_img[i]);
        return view;
    }


}
