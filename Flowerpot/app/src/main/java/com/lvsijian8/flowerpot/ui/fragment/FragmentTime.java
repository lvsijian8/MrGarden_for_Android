package com.lvsijian8.flowerpot.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.RemotePot;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.activity.AppendActivity;
import com.lvsijian8.flowerpot.ui.activity.BatchActivity;
import com.lvsijian8.flowerpot.ui.activity.ContainActivity;
import com.lvsijian8.flowerpot.ui.activity.HistoryActivity;
import com.lvsijian8.flowerpot.ui.activity.RemoteActivity;
import com.lvsijian8.flowerpot.ui.view.CircleImageView;
import com.lvsijian8.flowerpot.utils.UIUtils;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentTime extends Fragment  {
    private ImageView ibtn_edit;
    private PopupWindow mPopupWindow;
    private LinearLayout contain;
    private ListView lv_item;
    private HttpHelper httpHelper;
    private ArrayList<RemotePot.Pot> potdata;//用户所有花盆的数据
    private MyAdapter adapter;
    private View rootview;
    private LinearLayout ll_error;
    private Button btn_error;
    private ProgressBar pb_loading;
    private HashMap<String, Object> params;
    private Gson gson;
    private static int delete_position;//标识删除的Item的position
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private Button btn_append;
    private CircleImageView iv_logo;
    private ContainActivity mContainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Const.MENU_STATE_CURRENT= Const.MENU_STATE_TIME;
        if (rootview ==null){
            rootview = inflater.inflate(R.layout.fragment_time, container, false);
        }
        ViewGroup parent= (ViewGroup) rootview.getParent();
        if (parent!=null){
            parent.removeView(rootview);
        }
        initui();
        initdata();
        return rootview;
    }



    private void initui(){
        mContainActivity = (ContainActivity) getActivity();
        fragmentManager=getActivity().getSupportFragmentManager();
        contain = (LinearLayout) rootview.findViewById(R.id.layout_time_contain);
        ibtn_edit= (ImageView) rootview.findViewById(R.id.ibtn_time_edit);
        iv_logo = (CircleImageView) rootview.findViewById(R.id.circle_time_icon);
        lv_item= (ListView) rootview.findViewById(R.id.lv_time_item);
        ll_error = (LinearLayout) rootview.findViewById(R.id.layout_time_error);
        btn_error = (Button) rootview.findViewById(R.id.btn_time_error);
        btn_append = (Button) rootview.findViewById(R.id.btn_time_append);
        pb_loading = (ProgressBar) rootview.findViewById(R.id.progress_time_loading);
        adapter=new MyAdapter();
        potdata =new ArrayList<>();
        gson = new Gson();
        httpHelper = HttpHelper.getInstances();

    }

    private void initdata() {
        initHttp();
        initMenu();
        View popup=View.inflate(UIUtils.getContext(),R.layout.equip_popup,null);
        setPopup(popup);
        ibtn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                if (mPopupWindow != null) {
                //                    if (!mPopupWindow.isShowing()) {
                //                        mPopupWindow.showAtLocation(contain, Gravity.BOTTOM, 0, 0);
                //                    } else {
                //                        mPopupWindow.dismiss();
                //                    }
                //                }
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
            }
        });
        lv_item.setVerticalScrollBarEnabled(false);//滑动时隐藏滚动轴
        lv_item.setAdapter(adapter);
        btn_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_loading.setVisibility(View.VISIBLE);
                ll_error.setVisibility(View.GONE);//隐藏错误View
                lv_item.setVisibility(View.GONE);//隐藏显示View
                initHttp();
            }
        });
        btn_append.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加组
                Const.APPEND_INTSTATE=Const.APPEND_POT;
                getActivity().startActivity(new Intent(getActivity(), AppendActivity.class));
            }
        });
        iv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContainActivity.OpenMenu();
            }
        });
    }

    /**
     * ContextMenu菜单
     */
    private void initMenu() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize(UIUtils.dip2px(56));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View clickedView, int position) {
                switch (position) {
                    case 1:
                        //进入添加花盆的界面
                        Const.APPEND_INTSTATE = Const.APPEND_POT;
                        getActivity().startActivity(new Intent(getActivity(), AppendActivity.class));
                        break;
                    case 2:
                        UIUtils.makeText("长按花盆可删除");
                        break;
                    case 3:
                        getActivity().startActivity(new Intent(getActivity(), BatchActivity.class));
                        break;
                }
            }
        });
//        mMenuDialogFragment.setItemLongClickListener(this);
    }

    /**
     * ContextMenu的菜单按钮样式
     * @return
     */
    private List<MenuObject> getMenuObjects(){
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject menu_close = new MenuObject();
        menu_close.setResource(R.drawable.icon_close);

        MenuObject menu_add = new MenuObject("添加");
        menu_add.setResource(R.drawable.icon_add);

        MenuObject menu_delete = new MenuObject("删除");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icon_delete);
        menu_delete.setBitmap(b);

        MenuObject menu_batch = new MenuObject("批量操作");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_batch));
        menu_batch.setDrawable(bd);

        menuObjects.add(menu_close);
        menuObjects.add(menu_add);
        menuObjects.add(menu_delete);
        menuObjects.add(menu_batch);
        return menuObjects;
    }


    private void initHttp() {
        //获取数据
        params = new HashMap<>();
        params.clear();
        int USER_ID=UIUtils.getSpInt(Const.USER_ID);
        if (USER_ID!=-1){
            params.put(Const.USER_ID, USER_ID + "");

        }
        httpHelper.setOnTimegetData(new HttpHelper.OnTimegetData() {
            @Override
            public void successGet(String data) {
                Message message = new Message();
                Log.e("ZDLW", "data: " + data + "");
                if (!TextUtils.isEmpty(data)) {
                    message.obj = data;
                    message.what = Const.RECEIVE_DATA_SUCCESS;
                } else {
                    message.what = Const.RECEIVE_DATA_NUll;
                }
                mhandler.sendMessage(message);
            }

            @Override
            public void failGet() {
                Message message = new Message();
                message.what = Const.RECEIVE_DATA_FAIL;
                mhandler.sendMessage(message);
            }
        });
        httpHelper.setOkClientListener(new HttpHelper.OkClientListener() {
            @Override
            public void onFailure() {
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UIUtils.getContext(), "删除失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(String data) {
                if (!TextUtils.isEmpty(data)) {
                    final int code = Integer.parseInt(data);
                    UIUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 0) {
                                Toast.makeText(UIUtils.getContext(), "删除失败，请重试", Toast.LENGTH_SHORT).show();
                            } else if (code == 1) {
                                Toast.makeText(UIUtils.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                potdata.remove(delete_position);//移除Item
                                adapter.notifyDataSetChanged();//刷新listview
                            }
                        }
                    });
                }
            }
        });
        httpHelper.getTimeData(Const.URL_DEVICE, params);
    }

    public void MenuTimeData(HashMap<String,Object> map){
        httpHelper.getTimeData(Const.URL_NEW_TIME, map);
    }



    private void setPopup(View popupView){
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);//加载弹进弹出风格
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.getContentView().setFocusable(true);
        TextView tv_add= (TextView) popupView.findViewById(R.id.tv_time_add);
        TextView tv_batch= (TextView) popupView.findViewById(R.id.tv_time_batch);
        //进入添加花盆界面
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Const.APPEND_INTSTATE=Const.APPEND_POT;
                getActivity().startActivity(new Intent(getActivity(), AppendActivity.class));
            }
        });
        //进入批量操作界面
        tv_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), BatchActivity.class));
            }
        });


    }

    /**
     * 进入远程控制界面
     */
    public void EnterRemoter(int pot_id,int state){
        Intent intent=new Intent(getActivity(), RemoteActivity.class);
        intent.putExtra("POTID", pot_id);
        intent.putExtra("state", state);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }



    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return potdata.size();
        }

        @Override
        public Object getItem(int position) {
            return potdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder=null;
            if (convertView==null){
                convertView=View.inflate(UIUtils.getContext(),R.layout.equipment_item,null);
                holder=new ViewHolder();
                holder.name= (TextView) convertView.findViewById(R.id.tv_equip_name);
                holder.bottle= (TextView) convertView.findViewById(R.id.tv_equip_electric);
                holder.tv_state= (TextView) convertView.findViewById(R.id.tv_equip_state);
                holder.iv_state= (ImageView) convertView.findViewById(R.id.iv_equip_state);
                holder.WarningState= (TextView) convertView.findViewById(R.id.tv_equip_warning);
                holder.remote= (TextView) convertView.findViewById(R.id.tv_equip_manager);
                holder.history= (TextView) convertView.findViewById(R.id.tv_equip_history);
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }



            final RemotePot.Pot potdata= (RemotePot.Pot) getItem(position);
            holder.WarningState.setText(potdata.warning);
            holder.name.setText(potdata.name);
            if (potdata.state==0){
                holder.iv_state.setImageResource(R.drawable.state_off);
                holder.tv_state.setText("离线");
                holder.tv_state.setTextColor(getResources().getColor(R.color.darkgray));
            }else {
                holder.iv_state.setImageResource(R.drawable.state_on);
                holder.tv_state.setText("在线");
                holder.tv_state.setTextColor(getResources().getColor(R.color.black));
            }
            holder.bottle.setText("电量:"+potdata.power+"%");
            //远程控制的点击
            holder.remote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EnterRemoter(potdata.pot_id, potdata.state);
                }
            });
            //刷新当前数据
            holder.history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(), HistoryActivity.class);
                    intent.putExtra("POTID",potdata.pot_id);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("是否删除该花盆");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete_position=position;//需要删除的花盆的位置索引

                            //联网删除
                            params.clear();
                            params.put("pot_id", potdata.pot_id+"");
                            params.put(Const.USER_ID,UIUtils.getSpInt(Const.USER_ID)+"");
                            HttpHelper.OkClient(Const.URL_DELETE, params);
                        }
                    });
                    builder.setNegativeButton("否",null);
                    builder.show();
                    return true;
                }
            });
            return convertView;
        }
        class ViewHolder{
            TextView name;
            TextView bottle;
            TextView tv_state;
            ImageView iv_state;
            TextView WarningState;
            TextView remote;
            TextView history;
        }
    }

    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Const.RECEIVE_DATA_SUCCESS:
                    //获取数据成功，显示数据View，隐藏重试View,隐藏读取View
                    pb_loading.setVisibility(View.GONE);
                    lv_item.setVisibility(View.VISIBLE);
                    ll_error.setVisibility(View.GONE);
                    btn_append.setVisibility(View.GONE);
                    String dataString= (String) msg.obj;
                    Log.e("ZDLW",dataString);
                    RemotePot remotePot=gson.fromJson(dataString, RemotePot.class);
                    potdata =remotePot.data;//获取数据
                    adapter.notifyDataSetChanged();
                    break;
                case Const.RECEIVE_DATA_NUll:
                    pb_loading.setVisibility(View.GONE);
                    lv_item.setVisibility(View.GONE);
                    ll_error.setVisibility(View.GONE);
                    btn_append.setVisibility(View.VISIBLE);
                    UIUtils.makeText("未添加花盆，请添加");
                    break;
                case Const.RECEIVE_DATA_FAIL:
                    //获取数据失败，隐藏数据View，显示藏重试View,隐藏读取View
                    Toast.makeText(UIUtils.getContext(),"网络异常，请点击重试",Toast.LENGTH_SHORT).show();
                    pb_loading.setVisibility(View.GONE);
                    lv_item.setVisibility(View.GONE);
                    ll_error.setVisibility(View.VISIBLE);
                    btn_append.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (Const.APPEND_INTSTATE==Const.APPEND_POT){
            Const.APPEND_INTSTATE=Const.APPEMD_NULL;
            params.clear();
            int USER_ID=UIUtils.getSpInt(Const.USER_ID);
            if (USER_ID!=-1){
                params.put(Const.USER_ID, USER_ID + "");

            }
            httpHelper.setOnTimegetData(new HttpHelper.OnTimegetData() {
                @Override
                public void successGet(String data) {
                    if (!TextUtils.isEmpty(data)){
                        Message message = new Message();
                        message.obj=data;
                        message.what = Const.RECEIVE_DATA_SUCCESS;
                        mhandler.sendMessage(message);
                    }else {

                    }
                }

                @Override
                public void failGet() {
                    Message message = new Message();
                    message.what = Const.RECEIVE_DATA_FAIL;
                    mhandler.sendMessage(message);
                }
            });
            httpHelper.getTimeData(Const.URL_DEVICE, params);
        }
    }
}
