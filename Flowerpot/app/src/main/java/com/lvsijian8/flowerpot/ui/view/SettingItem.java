package com.lvsijian8.flowerpot.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.utils.UIUtils;

/**该自定义控件是TextView+横线+TextView的组合
 * Created by Administrator on 2017/3/20.
 */
public class SettingItem extends LinearLayout {

    private RelativeLayout layout_one;
    private RelativeLayout layout_two;
    private Typeface fontface= UIUtils.getTypeFace();

    public SettingItem(Context context) {
        super(context);
        init(context);
    }

    public SettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.SettingItem);
        String one = typedArray.getString(R.styleable.SettingItem_one);
        String two= typedArray.getString(R.styleable.SettingItem_two);
        TextView tv_one= (TextView) this.findViewById(R.id.tv_setting_one);
        TextView tv_two= (TextView) this.findViewById(R.id.tv_setting_two);
        tv_one.setText(one);
        tv_two.setText(two);
        layout_one = (RelativeLayout) this.findViewById(R.id.rlayout_setting_one);
        layout_two = (RelativeLayout) this.findViewById(R.id.rlayout_setting_two);

    }

    public SettingItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        View.inflate(context, R.layout.me_setting, this);
    }
    //获取第一个TextView所在的布局，以方便设置点击事件
    public RelativeLayout getLayout_one(){
        return layout_one;
    }
    //获取第二个TextView所在的布局，以方便设置点击事件
    public RelativeLayout getLayout_two(){
        return layout_two;
    }
}
