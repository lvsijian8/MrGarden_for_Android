package com.lvsijian8.flowerpot.domin;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/14.
 */
public class Section_data {
    public ArrayList<Data> data;
    public class Data{
        public ArrayList<Integer> temperature;//温度
        public ArrayList<Integer> days;//日期
        public String month;//X轴字符
        public String name;
    }
}
