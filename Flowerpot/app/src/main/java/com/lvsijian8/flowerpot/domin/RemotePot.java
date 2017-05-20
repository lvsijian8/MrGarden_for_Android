package com.lvsijian8.flowerpot.domin;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/14.
 */
public class RemotePot {
    public ArrayList<Pot> data;
    public class Pot{
        public int pot_id;
        public String name;
        public int state;
        public String power;
        public String warning;
    }
}
