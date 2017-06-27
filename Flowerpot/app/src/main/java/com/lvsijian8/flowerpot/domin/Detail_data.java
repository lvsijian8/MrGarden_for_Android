package com.lvsijian8.flowerpot.domin;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/27.
 */
public class Detail_data {
    public ArrayList<Ddata> data;
    public class Ddata{
        public String chinese_name;
        public String english_name;
        public String watering;
        public String sunshine;
        public String fertilizer;
        public int temperature_min;
        public int temperature_max;
        public String brief;
        public String text;
    }
}
