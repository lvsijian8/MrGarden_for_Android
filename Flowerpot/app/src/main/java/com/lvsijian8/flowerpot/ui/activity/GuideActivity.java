package com.lvsijian8.flowerpot.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
        //获取屏幕宽高
        int ScreenWidth=getWindowManager().getDefaultDisplay().getWidth();
        int ScreenHeight=getWindowManager().getDefaultDisplay().getHeight();
        for (int i=0;i<resours.length;i++){
            ImageView imageView=new ImageView(UIUtils.getContext());

            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds=true;//不去真实解析bitmap，而是查询宽高信息
            BitmapFactory.decodeResource(getResources(), resours[i],options);
            //获取图片宽高
            int scale=1;
//            int picWidth=options.outWidth;
//            int picHeight=options.outHeight;
//            int dx=picWidth/ScreenWidth;
//            int dy=picHeight/ScreenHeight;
//            if (dx>=dy&&dy>1){
//                scale=dx;
//            }
//            if (dy>=dx&&dx>1){
//                scale=dy;
//            }
//            Log.e("ZDLW","picWidth: "+picWidth+"  picHeight: "+picHeight+" ScreenWidth: "+ScreenWidth+" ScreenHeight: "+ScreenHeight);
            scale=2;
            options.inSampleSize=scale;
            options.inJustDecodeBounds=false;
            Bitmap bitmap=BitmapFactory.decodeResource(getResources(), resours[i], options);
            imageView.setImageBitmap(bitmap);
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
