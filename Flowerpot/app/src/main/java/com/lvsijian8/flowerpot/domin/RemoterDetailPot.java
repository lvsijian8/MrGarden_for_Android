package com.lvsijian8.flowerpot.domin;

import java.util.ArrayList;

/**Remoter界面的数据
 * Created by Administrator on 2017/4/15.
 */
public class RemoterDetailPot {
    public ArrayList<RemoterDetailPot.Pot> data;
    public class Pot{
        public String num_water_day;
        public String num_water_time;
        public String num_water_ml;
        public String num_bottle_day;
        public String num_bottle_time;
        public String num_bottle_ml;
        public String light;//光照
        public String power;//电量
        public String temperature;//温度
        public String humidity;//湿度
    }
}
