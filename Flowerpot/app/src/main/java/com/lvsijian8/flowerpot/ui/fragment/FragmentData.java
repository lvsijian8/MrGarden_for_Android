package com.lvsijian8.flowerpot.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.Section_data;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.activity.AppendActivity;
import com.lvsijian8.flowerpot.ui.activity.ContainActivity;
import com.lvsijian8.flowerpot.ui.view.CircleImageView;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class FragmentData extends Fragment {

	private ContainActivity containActivity;
	private View rootview;
	private ArrayList<Section_data.Data> data;
	private HttpHelper httpHelper;
	private Gson gson;
	private HashMap<String, Object> parms;
	private LinearLayout mLl_contain;
	private Button mBtn_append;
	private CircleImageView mIv_logo;
	private ScrollView mSl_sc;
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case Const.RECEIVE_DATA_SUCCESS:
					mLl_contain.removeAllViews();//清除全部View
					String stringdata= (String) msg.obj;
					Section_data datas=gson.fromJson(stringdata,Section_data.class);
					data=datas.data;
					for (int i=0;i<data.size();i++){
						View view=UIUtils.inflate(R.layout.item_section);
						TextView tv_name= (TextView) view.findViewById(R.id.tv_section_name);
						tv_name.setText("温度  "+data.get(i).name);
						LineChartView lineChartView= (LineChartView) view.findViewById(R.id.chart_section_temperature);
						setLineView(lineChartView, data.get(i).temperature, data.get(i).days, R.color.orange, data.get(i).month, 42);
						LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2px(200));
						params.bottomMargin=15;
						mLl_contain.addView(view,params);
						Log.e("ZDLW", data.get(i).name);
					}
					mBtn_append.setVisibility(View.GONE);
					mSl_sc.setVisibility(View.VISIBLE);
				break;
				case Const.RECEIVE_DATA_NUll:
					mBtn_append.setVisibility(View.VISIBLE);
					mSl_sc.setVisibility(View.GONE);
					break;
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gson = UIUtils.getGson();
		httpHelper = HttpHelper.getInstances();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Const.MENU_STATE_CURRENT= Const.MENU_STATE_DATA;
		if (rootview ==null){
			rootview = inflater.inflate(R.layout.activity_section, container, false);
		}
		ViewGroup parent= (ViewGroup) rootview.getParent();
		if (parent!=null){
			parent.removeView(rootview);
		}
		initui();
		return rootview;
	}

	private void initui(){
		containActivity= (ContainActivity) getActivity();
		mSl_sc = (ScrollView) rootview.findViewById(R.id.sl_section_sc);
		mLl_contain = (LinearLayout) rootview.findViewById(R.id.ll_section_contain);
		mBtn_append= (Button) rootview.findViewById(R.id.btn_section_append);
		mIv_logo = (CircleImageView) rootview.findViewById(R.id.circle_section_icon);
		httpHelper=HttpHelper.getInstances();
		gson=new Gson();
		parms=new HashMap<>();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initdata();
	}



	private void initdata() {

		httpHelper.setOnDetailgetData(new HttpHelper.OnDetailgetData() {
			@Override
			public void successGet(String data) {
				Message message=new Message();
				if (!TextUtils.isEmpty(data)){
					Log.e("ZDLW",data);
					message.what=Const.RECEIVE_DATA_SUCCESS;
					message.obj=data;
					mHandler.sendMessage(message);
				}else {
					message.what=Const.RECEIVE_DATA_NUll;
					mHandler.sendMessage(message);
					Log.e("ZDLW","data为空 value="+data);
				}
			}

			@Override
			public void failGet() {

			}
		});
		mBtn_append.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Const.APPEND_INTSTATE = Const.APPEND_POT;
				getActivity().startActivity(new Intent(getActivity(), AppendActivity.class));
			}
		});
		mIv_logo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				containActivity.OpenMenu();
			}
		});
		parms.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");
		httpHelper.getDetailData(Const.URL_NEW_DATA, parms);
	}

	public void ConnectionHttp(int group_id){
		parms.put("group_id", group_id + "");
		Log.e("ZDLW","group_id="+group_id );
		httpHelper.getDetailData(Const.URL_NEW_DATA, parms);
		if (data.size()>0){
			//组内没有花盆数据，通知用户添加花盆
			mBtn_append.setVisibility(View.GONE);
		}else {
			mBtn_append.setVisibility(View.VISIBLE);
		}
	}



	/**
	 * 设置LineChart
	 */
	private void setLineView(LineChartView lineView,ArrayList<Integer> data,ArrayList<Integer> day,int color,String month,int top){
		lineView.setInteractive(true);//是否与用户交互
		lineView.setZoomEnabled(false);//是否可以缩放
		lineView.setValueSelectionEnabled(true);//是否有点击动画
		List<Line> lines=new ArrayList<>();
		for (int i=0;i<1;i++){
			List<PointValue> pointValues=new ArrayList<>();
			ArrayList<AxisValue> axisValueX=new ArrayList<>();
//			ArrayList<AxisValue> axisValueY=new ArrayList<>();
			for (int j=0;j<data.size();j++){
				pointValues.add(new PointValue(j,data.get(j)));
				axisValueX.add(new AxisValue(j).setLabel(day.get(j)+""));//设置X轴
//				axisValueY.add(new AxisValue(j * 5).setLabel(j * 5 + ""));//设置Y轴
			}
			Axis axis=new Axis();//X轴
			Axis ayis=new Axis();//Y轴
			axis.setName(month);
			ayis.setName("");
			axis.setValues(axisValueX);
//			ayis.setValues(axisValueY);
			ayis.setTextSize(12);
			axis.setTextColor(Color.BLACK);
			ayis.setTextColor(Color.BLACK);
			/*===============设置曲线=================*/
			Line line=new Line(pointValues);
			line.setColor(getResources().getColor(color));//设置线的颜色
			line.setFilled(true);//设置折线覆盖区域
			line.setCubic(true);//设置曲线是否圆滑
			line.setPointRadius(3);//设置圆点的半径
			line.setShape(ValueShape.CIRCLE);//节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形
			line.setPointColor(getResources().getColor(R.color.colorAccent));//设置节点颜色
			line.setStrokeWidth(2);//线的宽度
			line.setHasPoints(true);//是否显示节点
			line.setHasLabels(false);//是否显示节点数据
			line.setHasLines(true);//是否显示折线
			line.setHasLabelsOnlyForSelected(true);//隐藏数据，触摸可以显示
			lines.add(line);

			/*================设置轴======================*/
			LineChartData lineChartData = new LineChartData(lines);
			lineChartData.setAxisXBottom(axis);
			lineChartData.setAxisYLeft(ayis);
			lineChartData.setBaseValue(0);//覆盖区域以什么为标准覆盖
			lineChartData.setValueLabelBackgroundAuto(true);//设置数据背景是否跟随节点颜色
			lineChartData.setValueLabelBackgroundColor(getResources().getColor(color));//设置数据背景颜色
			lineChartData.setValueLabelBackgroundEnabled(false);//点击后是字体是否背景色
			lineChartData.setValueLabelsTextColor(Color.BLACK);//点击后的字体颜色
			lineView.setLineChartData(lineChartData);
			resetViewport(lineView,top);
		}
	}




	/**
	 * 注意，以下必须设置，v是整个坐标最大有多大，v.top=50,最高就是50.
	 */
	private void resetViewport(LineChartView lineTemperature,int top) {
		final Viewport v = new Viewport(lineTemperature.getMaximumViewport());
		v.bottom = 0;
		v.top = top;
//		v.left = 3;
//		v.right = f.length-1;
		lineTemperature.setCurrentViewportWithAnimation(v);
		lineTemperature.setMaximumViewport(v);
		lineTemperature.setCurrentViewport(v);
	}



	@Override
	public void onResume() {
		super.onResume();
		if (Const.isResume){
			httpHelper.setOnDetailgetData(new HttpHelper.OnDetailgetData() {
				@Override
				public void successGet(String data) {
					Message message = new Message();
					if (!TextUtils.isEmpty(data)) {
						Log.e("ZDLW", data);
						message.what = Const.RECEIVE_DATA_SUCCESS;
						message.obj = data;
						mHandler.sendMessage(message);
					} else {
						message.what = Const.RECEIVE_DATA_NUll;
						mHandler.sendMessage(message);
					}
				}

				@Override
				public void failGet() {

				}
			});
			httpHelper.getDetailData(Const.URL_NEW_DATA, parms);
			Const.isResume=false;
		}

	}


}
