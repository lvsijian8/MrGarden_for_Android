package com.lvsijian8.flowerpot.domin;

import java.util.ArrayList;

/**花盆的数据
 * Created by Administrator on 2017/3/28.
 */
public class Flowerpot {
    public ArrayList<FlowerData> data;
    public class FlowerData{
        public ArrayList<Integer> humidity;//湿度
        public ArrayList<Integer> inhumidity;//土壤湿度
        public ArrayList<Integer> sunshine;//光照
        public ArrayList<Integer> temperature;//温度
        public ArrayList<Integer> days;//日期
        public String month;//X轴字符
        public int id;
        public int warn;
        public String name;
        public String msg;
    }
}
