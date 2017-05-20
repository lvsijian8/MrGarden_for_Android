package com.lvsijian8.flowerpot.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;

/**
 * Created by Administrator on 2017/3/22.
 */
public class MySeekBar extends RelativeLayout {

    private TextView tv_title;
    private TextView tv_max;
    private TextView tv_min;
    private TextView tv_num;
    private SeekBar seekBar;

    public MySeekBar(Context context) {
        super(context);
        init(context);
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initdata(context, attrs);

    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        View.inflate(context, R.layout.layout_myseek, this);
        tv_title = (TextView) this.findViewById(R.id.tv_MySeekBar_title);
        tv_max = (TextView) this.findViewById(R.id.tv_MySeekBar_max);
        tv_min = (TextView) this.findViewById(R.id.tv_MySeekBar_min);
        tv_num = (TextView) this.findViewById(R.id.tv_MySeekBar_num);
        seekBar = (SeekBar) this.findViewById(R.id.seekbar_MySeekBar);
    }

    private void initdata(Context context, AttributeSet attrs){
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.MySeekBar);
        String num_title=typedArray.getString(R.styleable.MySeekBar_stitle);
        String num_max=typedArray.getString(R.styleable.MySeekBar_max);
        String num_min=typedArray.getString(R.styleable.MySeekBar_min);
        int num_chidu=typedArray.getInteger(R.styleable.MySeekBar_num,0);
        tv_max.setText(num_max);//设置最大尺度
        tv_min.setText(num_min);//设置最小尺度
        tv_title.setText(num_title);//设置标题
        seekBar.setMax(num_chidu);
        seekBar.setProgress(0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_num.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public String getProgress(){
        return tv_num.getText().toString().trim();
    }

    public void setTitle(String title){
        if (title!=null){
            tv_title.setText(title);
        }
    }

    public void setProgress(int num){
        seekBar.setProgress(num);
    }

}
