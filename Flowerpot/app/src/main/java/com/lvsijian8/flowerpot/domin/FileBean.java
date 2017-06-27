package com.lvsijian8.flowerpot.domin;

import com.zhy.tree.bean.Node;
import com.zhy.tree.bean.TreeNodeId;
import com.zhy.tree.bean.TreeNodeLabel;
import com.zhy.tree.bean.TreeNodePid;

/**
 * Created by Administrator on 2017/6/6.
 */
public class FileBean extends Node {
    @TreeNodeId
    public int _id;
    @TreeNodePid
    public int parentId;
    @TreeNodeLabel
    public String names;
    public int state;
    public int it_id;



    public FileBean(int id,int pid,String name){
        this._id = id;
        this.parentId = pid;
        this.names = name;

    }
    public String getName() {
        return names;
    }

    public void setName(String name) {
        this.names = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }




}
