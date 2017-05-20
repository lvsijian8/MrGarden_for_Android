package com.lvsijian8.flowerpot.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;

/**
 * Created by Administrator on 2017/3/11.
 */
public class MyCheckBox extends RelativeLayout {

    private TextView tv;
    private CheckBox box;
    private String tv_title;

    public MyCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        tv_title=attrs.getAttributeValue("http://schemas.android.com/apk/com.lvsijian8.flowerpot","tvtitle");
        tv.setText(tv_title);
    }

    public MyCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public MyCheckBox(Context context) {
        super(context);
        init(context);
    }
    private void init(Context context){
        View.inflate(context, R.layout.layout_mycheck,this);
        tv= (TextView) this.findViewById(R.id.tv_check);
        box= (CheckBox) this.findViewById(R.id.box_check);
//        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (listener!=null){
//                    listener.StateChang(isChecked);
//                }
//            }
//        });
    }

    public void setCheckState(boolean ischeck){
        if (ischeck){
            box.setChecked(true);
        }else {
            box.setChecked(false);
        }
    }

    public boolean getCheckState(){
        return box.isChecked();
    }

//    public interface OnMyCheckBoxStateListener{
//        void StateChang(boolean isCheck);
//    }
//    private OnMyCheckBoxStateListener listener;
//    public void setOnMyCheckBoxStateListener(OnMyCheckBoxStateListener listener){
//        this.listener=listener;
//    }

}
