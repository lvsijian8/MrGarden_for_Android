package com.lvsijian8.flowerpot.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;

/**
 * Created by Administrator on 2017/3/19.
 */
public class DetailItem extends RelativeLayout {

    private TextView tv_name;
    private ImageView img;

    public DetailItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DetailItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.DetailItem);
        String name=typedArray.getString(R.styleable.DetailItem_name);
        tv_name.setText("" + name);
    }

    public DetailItem(Context context) {
        super(context);
        init(context);
    }
    private void init(Context context){
        View.inflate(context, R.layout.detail_item,this);
        tv_name = (TextView) this.findViewById(R.id.tv_detailItem_name);
        img = (ImageView) this.findViewById(R.id.iv_detailItem_plant);
    }

    public ImageView getImage(){
        return img;
    }
}
