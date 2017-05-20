package com.lvsijian8.flowerpot.domin;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/21.
 */
public class Chart_data {
    public ArrayList<cdata> data;
    class cdata{
        float[] chart_Temperature;//温度表格数据
        float[] chart_HumidityView;//湿度表格数据
        float[] chart_InHumidityView;//土壤湿度表格数据
        float[] chart_Light;//光照强度表格数据
    }
}
