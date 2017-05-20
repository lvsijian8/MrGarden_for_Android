package com.lvsijian8.flowerpot.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;

/**
 * Created by Administrator on 2017/3/9.
 */
public class MyIndicator_data extends LinearLayout {
    private TextView tv_title;
    private ImageView img_top;
    public MyIndicator_data(Context context) {
        super(context, null);

    }

    public MyIndicator_data(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.tab_data, this);
        tv_title= (TextView) this.findViewById(R.id.tv_tab);
        img_top= (ImageView) this.findViewById(R.id.img_tab);
    }


    public void setText(String text){
        tv_title.setText(text);
    }

    public void setImg(int res){
        img_top.setImageResource(res);
    }


}
