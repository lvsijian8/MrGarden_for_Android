package com.lvsijian8.flowerpot.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;


/**
 * Created by Administrator on 2016/11/12.
 */
public class Relistview extends ListView implements AdapterView.OnItemClickListener,AbsListView.OnScrollListener{
    private View headview,footview;
    private TextView msg;
    private TextView time;
    private ImageView img;
    private ProgressBar pbr;
    private int headheight,footheight;
    private static final int STATE_PULL_REFRESH=0;//下拉刷新
    private static final int STATE_RELFASE_REFRESH=1;//松开刷新
    private static final int STATE_REFRESHING=2;//正在刷新
    private static int mCurrentState=STATE_PULL_REFRESH;//默认下拉刷新
    private int starty,endy,intervaly;
    private int paddingy;
    private boolean ismoreloading=false;
    private RotateAnimation upani,downani;

    public Relistview(Context context) {
        super(context);
        initview();
    }

    public Relistview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview();
    }

    public Relistview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview();
    }


    private void initview(){
        animationsig();
        headview=View.inflate(getContext(), R.layout.layout_head,null);
        this.addHeaderView(headview);
        footview=View.inflate(getContext(),R.layout.layout_footview,null);
        this.addFooterView(footview);

        msg= (TextView) headview.findViewById(R.id.text_refre_title);
        time= (TextView) headview.findViewById(R.id.text_refre_time);
        pbr= (ProgressBar) headview.findViewById(R.id.progress_refreslayout);
        img= (ImageView) headview.findViewById(R.id.img_refresh);

        headview.measure(0, 0);
        headheight=headview.getMeasuredHeight();
        headview.setPadding(0, -headheight, 0, 0);

        footview.measure(0, 0);
        footheight=footview.getMeasuredHeight();
        footview.setPadding(0,-footheight,0,0);

        this.setOnScrollListener(this);
    }

    private void animationsig(){
        downani=new RotateAnimation(0,180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        downani.setDuration(300);
        downani.setFillAfter(true);
        upani=new RotateAnimation(-180,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        upani.setDuration(300);
        upani.setFillAfter(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                starty= (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (mCurrentState==STATE_REFRESHING){
                    break;
                }
                endy= (int) ev.getRawY();
                intervaly=endy-starty;//y轴移动的距离
                if (intervaly>0&&getFirstVisiblePosition()==0){
                    paddingy=intervaly-headheight;
                    headview.setPadding(0,paddingy,0,0);
                    if (paddingy>0&&mCurrentState!=STATE_RELFASE_REFRESH){//拉动距离>刷新框，且不是正在松开刷新
                        mCurrentState=STATE_RELFASE_REFRESH;
                        caozuo(mCurrentState);//判定一：触摸状态时滑动距离大于隐藏headview的高度，动画开始
                    }else if(paddingy<0&&mCurrentState!=STATE_PULL_REFRESH){//拉动距离<刷新框，且不是正在下拉刷新
                        mCurrentState=STATE_PULL_REFRESH;
                        caozuo(mCurrentState);//判定二：触摸状态时滑动距离小于隐藏headview的高度，动画开始
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mCurrentState==STATE_RELFASE_REFRESH){
                    mCurrentState=STATE_REFRESHING;//进入正在刷新状态
                    headview.setPadding(0,0,0,0);
                    caozuo(mCurrentState);//判定三：进入正在刷新状态，开始加载数据
                }else if (mCurrentState==STATE_PULL_REFRESH){
                    headview.setPadding(0,-headheight,0,0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void caozuo(int mCurrentState){
        switch (mCurrentState){
            case STATE_RELFASE_REFRESH:
                msg.setText("松开刷新");
                img.setVisibility(VISIBLE);
                pbr.setVisibility(GONE);
                img.startAnimation(downani);
                break;
            case STATE_PULL_REFRESH:
                msg.setText("下拉刷新");
                img.setVisibility(VISIBLE);
                pbr.setVisibility(GONE);
                img.startAnimation(upani);
                break;
            case STATE_REFRESHING:
                msg.setText("正在刷新");
                img.clearAnimation();
                img.setVisibility(GONE);
                pbr.setVisibility(VISIBLE);
                if (Rlistener!=null){
                    Rlistener.onReresh();
                }
                break;
        }
    }

    public interface OnRefreshListener{
        void onloding();
        void onReresh();
    }
    private OnRefreshListener Rlistener;
    public void setOnRershListener(OnRefreshListener r){
        Rlistener=r;
    }

    /**
     * 关闭刷新界面的操作：加载完毕后，再度隐藏headview。
     */
    public void CloseRersh(){
        headview.setPadding(0,-headheight,0,0);
        mCurrentState=STATE_PULL_REFRESH;
        img.setVisibility(INVISIBLE);
        img.startAnimation(upani);
        pbr.setVisibility(GONE);
        msg.setText("下拉刷新");
    }


    public void Closeloading(){
        ismoreloading=false;
        footview.setPadding(0,-footheight,0,0);
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState==SCROLL_STATE_IDLE||scrollState==SCROLL_STATE_FLING){//快速移动或静止
            if (getLastVisiblePosition()==getCount()-1&&!ismoreloading){//当前位置在ListView最后
                ismoreloading=true;
                footview.setPadding(0,0,0,0);
                setSelection(getCount()-1);
                if (Rlistener!=null){
                    Rlistener.onloding();//加载更多操作
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
