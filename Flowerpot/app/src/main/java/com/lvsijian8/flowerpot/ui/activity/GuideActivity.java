package com.lvsijian8.flowerpot.ui.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    private int[] resours={R.drawable.guide_a,R.drawable.guide_b,R.drawable.guide_c,R.drawable.guide_d,R.drawable.guide_e,};
    private ArrayList<ImageView> list_guide;
    private ViewPager vp_guide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_guide);
        initui();
        initdata();

    }

    private void initui() {
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        list_guide=new ArrayList<>();
    }

    private void initdata() {

        for (int i=0;i<5;i++){
            ImageView imageView=new ImageView(UIUtils.getContext());
            imageView.setBackgroundResource(resours[i]);
            list_guide.add(imageView);
        }
        vp_guide.setAdapter(new MyAdapter());

    }

    class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return list_guide.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list_guide.get(position));
            return list_guide.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
