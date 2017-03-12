package com.lvsijian8.flowerpot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;


public class FormViewSU extends LinearLayout {

    private EditText name,pass1,pass2,phone;

    public FormViewSU(Context context) {
        super(context);
        loadView();
    }

    public FormViewSU(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadView();
    }

    public FormViewSU(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadView();
    }

    private void loadView(){
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.form_viewsu, this);
        name = (EditText) findViewById(R.id.name);
        pass1 = (EditText) findViewById(R.id.pass1);
        pass2 = (EditText) findViewById(R.id.pass2);
        phone = (EditText) findViewById(R.id.phone);
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        name.setFocusable(focusable);
        pass1.setFocusable(focusable);
        pass2.setFocusable(focusable);
        phone.setFocusable(focusable);
    }

    public String getName(){
        return name.getText().toString();
    }
    public String getPhone(){
        return phone.getText().toString();
    }
    public void setName(String text){
        name.setText(text);
    }
    public void setPhone(String text){
        phone.setText(text);
    }
    public void setfocusname(){
        name.setFocusable(true);
        name.requestFocus();
    }
    public void setfocusphone(){
        phone.setFocusable(true);
        phone.requestFocus();
    }

    public String getPass1(){
        return pass1.getText().toString();
    }
    public String getPass2(){
        return pass2.getText().toString();
    }
    public void setPass1(String text){
        pass1.setText(text);
    }
    public void setPass2(String text){
        pass2.setText(text);
    }
    public void setfocuspass1(){
        pass1.setFocusable(true);
        pass1.requestFocus();
    }
    public void setfocuspass2(){
        pass2.setFocusable(true);
        pass2.requestFocus();
    }

}
