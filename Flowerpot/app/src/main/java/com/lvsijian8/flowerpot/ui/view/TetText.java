package com.lvsijian8.flowerpot.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;

/**
 * Created by Administrator on 2017/5/9.
 */
public class TetText extends RelativeLayout {

    private TextView tv_msg;
    private EditText et_msg;

    public TetText(Context context) {
        super(context);
        init(context);
    }

    public TetText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.TetText);
        String msg=typedArray.getString(R.styleable.TetText_msg);
        String defaule_hint=typedArray.getString(R.styleable.TetText_defaulthint);
        //设置输入类型
        if (!TextUtils.isEmpty(msg)){
            tv_msg.setText(msg);
        }else {
            tv_msg.setText("");
        }
        //设置输入类型的提示
        if (!TextUtils.isEmpty(defaule_hint)){
            et_msg.setHint(defaule_hint);
        }else {
            et_msg.setHint("");
        }

    }

    public TetText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View.inflate(context, R.layout.view_tettext,this);
        tv_msg = (TextView) findViewById(R.id.tv_view_tet_msg);
        et_msg = (EditText) findViewById(R.id.et_view_tet_hint);

    }

    public TextView getTextView(){
        return tv_msg;
    }

    public EditText getEditText(){
        return et_msg;
    }
    //返回输入框的文本
    public String getEditTextMsg(){
        String text=et_msg.getText().toString().trim();
        return text;
    }
    //设置EditText的文本
    public void setEtitText(String text){
        if (!TextUtils.isEmpty(text)){
            et_msg.setText(text);
        }
    }

    public void setInputType(int type){
        et_msg.setInputType(type);
    }


}
