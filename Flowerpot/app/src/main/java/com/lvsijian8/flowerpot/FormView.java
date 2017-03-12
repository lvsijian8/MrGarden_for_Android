package com.lvsijian8.flowerpot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;



public class FormView extends LinearLayout {

    private EditText edit1, edit2;

    public FormView(Context context) {
        super(context);
        loadView();
    }

    public FormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadView();
    }

    public FormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadView();
    }

    private void loadView(){
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.form_view, this);
        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        edit1.setFocusable(focusable);
        edit2.setFocusable(focusable);
    }
    public String getEdit1(){
        return edit1.getText().toString();
    }
    public String getEdit2(){
        return edit2.getText().toString();
    }
    public void setEdit1(String text){
        edit1.setText(text);
    }
    public void setEdit2(String text){
        edit2.setText(text);
    }
    public void setfocus1(){
        edit1.setFocusable(true);
        //edit1.setFocusableInTouchMode(true);
        edit1.requestFocus();
        //edit1.requestFocusFromTouch();

    }
    public void setfocus2(){
        edit2.setFocusable(true);
        //edit1.setFocusableInTouchMode(true);
        edit2.requestFocus();
        //edit1.requestFocusFromTouch();

    }
}
