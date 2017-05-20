package com.lvsijian8.flowerpot.domin;

/**
 * Created by Administrator on 2017/4/26.
 */
public class Batch_data {
    public boolean check;
    public int pot_id;
    public String pot_names;
    public int pot_online;
    public int pot_waters;
    public int pot_bottles;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getName() {
        return pot_names;
    }

    public void setName(String name) {
        this.pot_names = name;
    }

    public int getState() {
        return pot_online;
    }

    public void setState(int state) {
        this.pot_online = state;
    }
}
