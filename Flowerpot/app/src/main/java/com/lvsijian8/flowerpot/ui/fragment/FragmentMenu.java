package com.lvsijian8.flowerpot.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.FileBean;
import com.lvsijian8.flowerpot.domin.Left_data;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpOkHelper;
import com.lvsijian8.flowerpot.ui.activity.ContainActivity;
import com.lvsijian8.flowerpot.ui.activity.SectionActivity;
import com.lvsijian8.flowerpot.utils.UIUtils;
import com.zhy.tree.bean.Node;
import com.zhy.tree.bean.TreeHelper;
import com.zhy.tree.bean.TreeListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */
public class FragmentMenu extends Fragment{
    private static ContainActivity mActivity;
    private HttpOkHelper mHelper;
    private ArrayList<FileBean> mData;
    private HashMap<String, Object> mParams;
    private Gson mGson;
    private ListView mLv_contain;
    private boolean isFirst=true;
    private DrawAdapter mAdapter;
    private static final int STATE_SUCCESS=0X101;
    private static final int STATE_FAIL=0X102;
    private static final int STATE_ONCLICK=0X103;
    private static final int STATE_INITIALIZA=0X104;
    private static int MENU_STATE_CURRENT=STATE_INITIALIZA;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case STATE_SUCCESS:
                    if (MENU_STATE_CURRENT==STATE_INITIALIZA){
                        try {
                            mAdapter = new DrawAdapter(mLv_contain,mActivity,mData,0);
                            mLv_contain.setAdapter(mAdapter);
                            initAdapter();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else if (MENU_STATE_CURRENT==STATE_ONCLICK){

                    }
                    break;
                case STATE_FAIL:
                    break;
            }
        }
    };


    public static final FragmentMenu newInstance(Activity activity)
    {
        FragmentMenu fragment = new FragmentMenu();
        if (activity instanceof ContainActivity){
            mActivity= (ContainActivity) activity;
        }
        return fragment ;
    }



    public FragmentMenu(){

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= UIUtils.inflate(R.layout.fragment_drawer);
        mLv_contain = (ListView) view.findViewById(R.id.lv_drawer_contain);
        initview();
        initdata();
        mParams.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID));
        mHelper.ConnectHttp(Const.URL_GET_MENU, mParams);
        return view;
    }

    private void initview() {
        mHelper = HttpOkHelper.getInstances();
        mParams = new HashMap<>();
        mGson = new Gson();
        mData=new ArrayList<>();

    }


    public void initdata(){
        mHelper.setOnConnectMenuListener(new HttpOkHelper.OnConnectMenuListener() {
            @Override
            public void onSuccess(String data) {
                Message message = new Message();
                if (!TextUtils.isEmpty(data)) {
                    if (MENU_STATE_CURRENT == STATE_INITIALIZA) {
                        //初始化侧边栏
                        Left_data parent = mGson.fromJson(data, Left_data.class);
                        mData = parent.data;
                        message.what = STATE_SUCCESS;
                        mHandler.sendMessage(message);
                    } else if (MENU_STATE_CURRENT == STATE_ONCLICK) {
                        //点击联网获取数据，用于点击组的Item改变第一界面Data。
                    }
                }
            }

            @Override
            public void onFail() {

            }
        });


    }




    private void initAdapter()
    {

//        // id , pid , label , 其他属性
//        mData.add(new FileBean(1, 0, "花盆A"));
//        mData.add(new FileBean(2, 0, "花盆B"));
//        mData.add(new FileBean(3, 0, "花盆C"));
//        mData.add(new FileBean(4, 0, "花盆D"));
//        mData.add(new FileBean(5, 1, "A-01"));
//        mData.add(new FileBean(6, 1, "A-02"));
//        mData.add(new FileBean(7, 1, "A-03"));
//        mData.add(new FileBean(8, 1, "A-04"));
//        mData.add(new FileBean(9, 2, "B-01"));
//        mData.add(new FileBean(10,2, "B-02"));
//        mData.add(new FileBean(11,2, "B-03"));
//        mData.add(new FileBean(12,2, "B-04"));
//        mData.add(new FileBean(13,3, "C-01"));
//        mData.add(new FileBean(14,3, "C-02"));
//        mData.add(new FileBean(15,3, "C-03"));
//        mData.add(new FileBean(16,3, "C-04"));
//        mData.add(new FileBean(17,4, "D-01"));
//        mData.add(new FileBean(18,4, "D-02"));
//        mData.add(new FileBean(19,4, "D-03"));
//        mData.add(new FileBean(20,4, "D-04"));



    }


    class DrawAdapter<FileBean> extends TreeListViewAdapter<FileBean>{
        private int defaultExpandLevel;

        public DrawAdapter(ListView mTree, Context context, List<FileBean> datas, int defaultExpandLevel) throws IllegalArgumentException, IllegalAccessException {
            super(mTree, context, datas, defaultExpandLevel);
            this.defaultExpandLevel=defaultExpandLevel;
        }

        @Override
        public View getConvertView(final Node node, final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder=null;
            if (view==null){
                view=UIUtils.inflate(R.layout.item_tree);
                holder=new ViewHolder();
                holder.iv_icon= (ImageView) view.findViewById(R.id.id_treenode_icon);
                holder.tv_label= (TextView) view.findViewById(R.id.id_treenode_label);
                view.setTag(holder);
            }else {
                holder= (ViewHolder) view.getTag();
            }
            //设置箭头
            if (node.getIcon()==-1){
                holder.iv_icon.setVisibility(View.INVISIBLE);
            }else {
                holder.iv_icon.setVisibility(View.VISIBLE);
                if (node.isExpand()){
                    node.setIcon(R.drawable.tree_ex);
                }else {
                    node.setIcon(R.drawable.tree_ec);
                }
                holder.iv_icon.setImageResource(node.getIcon());
            }
            holder.tv_label.setText(node.getName());//设置文字
            //点击父节点时，data界面改变，点击子节点时，跳转Activity
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (node.isRoot()){
//                        UIUtils.makeText("父节点");
                        UIUtils.Log_e(FragmentMenu.class,node.getName());

                        if (Const.MENU_STATE_CURRENT== Const.MENU_STATE_DATA){
                            for (com.lvsijian8.flowerpot.domin.FileBean bean:mData){
                                if (bean.get_id()==node.getId()&&bean.getName().equals(node.getName())){
                                    HashMap<String,Object> map=new HashMap<String, Object>();
                                    mActivity.setFlowerData(mData.get(i).it_id);
                                }
                            }

                        }else if (Const.MENU_STATE_CURRENT==Const.MENU_STATE_TIME){
                            //第三界面组别切换
                            for (com.lvsijian8.flowerpot.domin.FileBean bean:mData){
                                if (bean.get_id()==node.getId()&&bean.getName().equals(node.getName())){
                                    HashMap<String,Object> map=new HashMap<String, Object>();
                                    Log.e("ZDLW", mData.get(i).getName());
                                    map.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");
                                    map.put("group_id", mData.get(i).it_id + "");
                                    Log.e("ZDLW", mData.get(i).it_id + "");
                                    mActivity.setFlowerTime(map);
                                }
                            }
                        }
                    }else {
                        //子节点
                        if (Const.MENU_STATE_CURRENT== Const.MENU_STATE_DATA){
                            for (com.lvsijian8.flowerpot.domin.FileBean bean:mData){
                                if (bean.get_id()==node.getId()&&bean.getName().equals(node.getName())){
                                    Intent intent=new Intent(mActivity, SectionActivity.class);
                                    intent.putExtra("group_id",bean.it_id);
                                    intent.putExtra("name",bean.names);
                                    Log.e("ZDLW", bean.names + " " + bean.it_id);
                                    startActivity(intent);
                                    mActivity.CloseMenu();
                                }
                            }
                        }else if (Const.MENU_STATE_CURRENT==Const.MENU_STATE_TIME){
                            //第三界面跳转处理
                            for (com.lvsijian8.flowerpot.domin.FileBean bean:mData){
                                if (bean.get_id()==node.getId()&&bean.getName().equals(node.getName())){
                                    mActivity.EnterRemoter(mData.get(i).it_id, mData.get(i).state);
                                    mActivity.CloseMenu();
                                }
                            }
                        }
                    }
                }
            });
            //点击箭头展开子节点
            if (node.isRoot()){
                holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        node.setExpand(!node.isExpand());
                        mNodes = TreeHelper.filterVisibleNode(mAllNodes);
                        notifyDataSetChanged();// 刷新视图
                    }
                });
            }
            return view;
        }

        public synchronized void OnclickChildNode(){}

        public synchronized void OnclickFatherNode(){}

        class ViewHolder{
            ImageView iv_icon;
            TextView tv_label;
        }

//        @Override
//        public void notifyDataSetChanged() {
//            if (!isFirst){
//                for (int i=0;i<mAllNodes.size();i++){
//                    if (mAllNodes.get(i).getId()==mData.get(i).get_id()&&
//                            mAllNodes.get(i).getpId()== mData.get(i).getParentId()){
//                        mAllNodes.get(i).setName(mData.get(i).getName());
//                    }
//                }
//            }else {
//                isFirst=false;
//            }
//            super.notifyDataSetChanged();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mHelper.ConnectHttp(Const.URL_GET_MENU, mParams);
        if (Const.isResume){
            Log.e("ZDLW", "WWW");
            mActivity.getDataFragment().onResume();
        }
    }
}
