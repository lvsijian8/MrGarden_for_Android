package com.lvsijian8.flowerpot.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.Flowerpot;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.activity.AppendActivity;
import com.lvsijian8.flowerpot.ui.view.CircleImageView;
import com.lvsijian8.flowerpot.ui.view.MyCheckBox;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class FragmentData extends Fragment {
	private LinearLayout layout_contain;
	private LineChartView lineTemperature;//温度
	private ColumnChartView ColumnHumidityView;//湿度
	private LineChartView lineLight;//光照
	private ColumnChartView ColumnInHumidityView;//土壤湿度
	private TextView tv_warning;//警告-文字
	private ImageView iv_warning;//警告-图标
	private LinearLayout layout_light;
	private LinearLayout layout_inhumidity;
	private ImageView btn_edit;//展示PopupWindow
	private Button btn_append;//添加花盆按钮
	private ScrollView scrollView;//数据界面的containView
	private boolean isLight=false;
	private boolean isInHumidity=false;
	private PopupWindow mPopupWindow;

	private ArrayList<Flowerpot.FlowerData> data;
	private ArrayList<Integer> humidity;//湿度数据数据
	private ArrayList<Integer> inhumidity;//土壤湿度数据
	private ArrayList<Integer> sunshine;//光照强度数据
	private ArrayList<Integer> temperature;//温度数据


	private ArrayList<Integer> days;//天数数据-往后推7天
	private CircleImageView circle_icon;
	private RadioGroup group;
	private Column column;
	private AlertDialog alertDialog;

	private HttpHelper httpHelper;
	private View rootview;

	private View choice_view;
	private AlertDialog.Builder builder;
	private Gson gson;
	private HashMap<String, Object> parms;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gson = UIUtils.getGson();
		data = new ArrayList<>();
		days=new ArrayList<>();
		humidity=new ArrayList<>();
		inhumidity=new ArrayList<>();
		sunshine=new ArrayList<>();
		temperature=new ArrayList<>();
		httpHelper = HttpHelper.getInstances();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootview ==null){
			rootview = inflater.inflate(R.layout.fragment_data, container, false);
		}
		ViewGroup parent= (ViewGroup) rootview.getParent();
		if (parent!=null){
			parent.removeView(rootview);
		}
		initui(rootview);
		return rootview;
	}

	private void initui(View view){

		layout_contain= (LinearLayout) view.findViewById(R.id.layout_fdata_contain);
		layout_light= (LinearLayout) view.findViewById(R.id.layout_hide_light);
		layout_inhumidity= (LinearLayout) view.findViewById(R.id.layout_hide_inhumidity);
		circle_icon = (CircleImageView) view.findViewById(R.id.circle_fdata_icon);

		tv_warning= (TextView) rootview.findViewById(R.id.tv_data_warning);
		iv_warning= (ImageView) rootview.findViewById(R.id.iv_data_warning);
		scrollView = (ScrollView) rootview.findViewById(R.id.layout_data_scroll);
		btn_append= (Button) rootview.findViewById(R.id.btn_data_append);
		lineTemperature= (LineChartView) view.findViewById(R.id.chart_data_temperature);
		ColumnHumidityView= (ColumnChartView) view.findViewById(R.id.chart_data_humidity);
		lineLight= (LineChartView) view.findViewById(R.id.chart_data_light);
		ColumnInHumidityView= (ColumnChartView) view.findViewById(R.id.chart_data_inhumidity);
		btn_append.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Const.APPEND_INTSTATE=Const.APPEND_DATA;
				getActivity().startActivityForResult(new Intent(getActivity(), AppendActivity.class), Const.APPEND_DATA);
			}
		});
		btn_edit= (ImageView) view.findViewById(R.id.ibtn_data_edit);
		btn_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPopupWindow != null) {
					if (!mPopupWindow.isShowing()) {
						mPopupWindow.showAtLocation(layout_contain, Gravity.BOTTOM, 0, 0);
					} else {
						mPopupWindow.dismiss();
					}
				}
			}
		});
		//以下是PopupWindow的内容设置
		circle_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.show();
			}
		});//点击左上角弹出Alert
		//选择花盆,弹出Alert选择花盆
		choice_view = View.inflate(UIUtils.getContext(), R.layout.choice_flower, null);
		group= (RadioGroup) choice_view.findViewById(R.id.rg_choice);
		builder = new AlertDialog.Builder(getContext());

	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initdata();
		setLineView(lineTemperature, temperature, days, R.color.orange,"1月",42);
		setLineView(lineLight, sunshine, days, R.color.orange,"1月",110);
		setColummView(ColumnHumidityView, humidity, days, R.color.skyblue,"1月");
		setColummView(ColumnInHumidityView, inhumidity, days, R.color.skyblue, "1月");
		setPopup();
		setAlert();

	}



	private void initdata() {
		parms = new HashMap<>();
		parms.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");
		httpHelper.getJsonData(Const.URL_DATA, parms);
		httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
			@Override
			public void successConnect(String data) {

				Message message = new Message();
				message.obj = data;
				message.what = Const.RECEIVE_DATA_SUCCESS;
				mHandler.sendMessage(message);

			}

			@Override
			public void failConnect() {
				Message message = new Message();
				message.what = Const.RECEIVE_DATA_FAIL;
				mHandler.sendMessage(message);
			}
		});
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
				axisValueX.add(new AxisValue(j).setLabel(days.get(j)+""));//设置X轴
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
	 * 设置ColumChart
	 */
	private void setColummView(ColumnChartView columnView,ArrayList<Integer> data,ArrayList<Integer> days,int color,String month){
		columnView.setInteractive(true);
		columnView.setZoomEnabled(false);
		List<Column> columns=new ArrayList<>();
		List<SubcolumnValue> values;
		List<AxisValue> axisValuesX=new ArrayList<>();
		List<AxisValue> axisValuesY=new ArrayList<>();
		for (int i=0;i<data.size();i++){
			values=new ArrayList<>();
			for (int j=0;j<1;j++){
				values.add(new SubcolumnValue(data.get(i), getResources().getColor(color)));
			}
			//*===========设置柱体属性=============*//
			column = new Column(values);
			column.setHasLabels(false);//是否显示节点数据
			column.setHasLabelsOnlyForSelected(true);
//			ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter(2);
//			column.setFormatter(chartValueFormatter);

			columns.add(column);
			axisValuesX.add(new AxisValue(i).setLabel(days.get(i)+""));//设置X轴

		}
		for (int i=0;i<=10;i++){
			axisValuesY.add(new AxisValue(i*10).setLabel(i*10+""));//设置Y轴
		}

		ColumnChartData columnChartData=new ColumnChartData(columns);
		columnChartData.setStacked(false);
		/*===========坐标轴相关设置=============*/
		Axis axisX=new Axis();
		Axis axisY=new Axis();
		axisX.setValues(axisValuesX);
		axisY.setValues(axisValuesY);

		axisY.setTextSize(12);
		axisX.setTextColor(Color.BLACK);
		axisY.setTextColor(Color.BLACK);
		axisX.setName(month);
		axisY.setName("");
		columnChartData.setAxisYLeft(axisY);
		columnChartData.setAxisXBottom(axisX);
		columnChartData.setValueLabelBackgroundAuto(true);
		columnChartData.setValueLabelsTextColor(R.color.black);
		columnChartData.setValueLabelTextSize(16);
		columnChartData.setValueLabelBackgroundColor(getResources().getColor(R.color.transparent));
		columnView.setColumnChartData(columnChartData);
		resetViewportColumn(columnView);
	}

	/**
	 * 设置Popupwindow
	 */
	private void setPopup(){
		View popupView= View.inflate(getContext(),R.layout.layout_popup,null);
		mPopupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
		mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);//加载弹进弹出风格
		mPopupWindow.getContentView().setFocusableInTouchMode(true);
		mPopupWindow.getContentView().setFocusable(true);
		setOnclickPopup(popupView);
	}

	/**
	 * 设置Popup的点击事件
	 */
	private void setOnclickPopup(View popupView) {
		final MyCheckBox box_light= (MyCheckBox) popupView.findViewById(R.id.box_popup_light);
		final MyCheckBox box_humidity= (MyCheckBox) popupView.findViewById(R.id.box_popup_humidity);
		//当回到消息界面时，判断隐藏的View是否没隐藏，如果是，则把勾选保持着
		if (layout_light.getVisibility()==View.VISIBLE){
			box_light.setCheckState(true);
		}
		if (layout_inhumidity.getVisibility()==View.VISIBLE){
			box_humidity.setCheckState(true);
		}
		box_light.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (box_light.getCheckState()) {
					box_light.setCheckState(false);
					layout_light.setVisibility(View.GONE);
				} else {
					box_light.setCheckState(true);
					layout_light.setVisibility(View.VISIBLE);
				}
			}
		});
		box_humidity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (box_humidity.getCheckState()) {
					box_humidity.setCheckState(false);
					layout_inhumidity.setVisibility(View.GONE);
				} else {
					box_humidity.setCheckState(true);
					layout_inhumidity.setVisibility(View.VISIBLE);
				}
			}
		});

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

	private void resetViewportColumn(ColumnChartView ColumnChartView) {
		final Viewport v = new Viewport(ColumnChartView.getMaximumViewport());
//		v.bottom = 0;
		v.top = 100;
//		v.left = 0;
//		v.right = f.length;
		ColumnChartView.setCurrentViewportWithAnimation(v);
		ColumnChartView.setMaximumViewport(v);
		ColumnChartView.setCurrentViewport(v);
	}

	/**
	 * 用于选择需要检测数据的花盆
	 */
	private void setAlert(){
		builder.setView(choice_view);
		RadioButton button= (RadioButton) group.getChildAt(Const.selector);
		if (button!=null){
			button.setChecked(true);
		}
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//获取点击的RadioButton的按钮是第几个
				for (int i = 0; i < group.getChildCount(); i++) {
					RadioButton radioButton = (RadioButton) group.getChildAt(i);
					if (radioButton.isChecked()) {
//						data.get(i);//被选择的花盆的数据
						//将数据更换为被选中的花盆的数据
						Const.selector = i;
						break;
					}
				}
				setFlowerot(data.get(Const.selector));

			}
		});
		alertDialog = builder.create();
	}

	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case Const.RECEIVE_DATA_SUCCESS:
					//设置花盆数据
					String dataString= (String) msg.obj;
					if (!TextUtils.isEmpty(dataString)){
						Flowerpot flowerpot = gson.fromJson(dataString, Flowerpot.class);
						data=flowerpot.data;
						scrollView.setVisibility(View.VISIBLE);
						for (int i=0;i<data.size();i++){
							//为花盆添加RadioButton
							RadioButton radioButton=new RadioButton(UIUtils.getContext());
							radioButton.setTextColor(getResources().getColor(R.color.black));
							radioButton.setText(data.get(i).name + "");
							radioButton.setTextSize(UIUtils.dip2px(6));
							group.addView(radioButton);
						}
						RadioButton ra= (RadioButton) group.getChildAt(Const.selector);
						ra.setChecked(true);
						if (data.get(0).warn==-1){
							iv_warning.setVisibility(View.GONE);
						}else {
							iv_warning.setVisibility(View.VISIBLE);
						}
						tv_warning.setText(data.get(0).msg);
						//设置显示第几个花盆
						setFlowerot(data.get(Const.selector));
					} else {
						btn_append.setVisibility(View.VISIBLE);
						scrollView.setVisibility(View.GONE);
						Toast.makeText(UIUtils.getContext(),"您还没添加花盆",Toast.LENGTH_SHORT).show();
						Log.e("ZDLW","花盆未添加");
					}
					break;
				case Const.RECEIVE_DATA_FAIL:
					Toast.makeText(UIUtils.getContext(),"网络异常",Toast.LENGTH_SHORT).show();
					Log.e("ZDLW","data网络异常");
					break;
			}
		}
	};

	public void setFlowerot(Flowerpot.FlowerData data){
		temperature=data.temperature;
		sunshine=data.sunshine;
		humidity=data.humidity;
		inhumidity=data.inhumidity;
		days=data.days;
		tv_warning.setText(data.msg);
		if (data.warn==-1){
			iv_warning.setVisibility(View.GONE);
		}else {
			iv_warning.setVisibility(View.VISIBLE);
		}


		setLineView(lineTemperature, temperature, days, R.color.orange, data.month,42);
		setLineView(lineLight, sunshine,days, R.color.orange,data.month,110);
		setColummView(ColumnHumidityView, humidity,days, R.color.skyblue,data.month);
		setColummView(ColumnInHumidityView, inhumidity,days, R.color.skyblue,data.month);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Const.APPEND_INTSTATE== Const.APPEND_DATA){
			Const.APPEND_INTSTATE= Const.APPEMD_NULL;//返回原始状态，防止再次进入
			parms.clear();//清除发送请求的数据
			parms.put(Const.USER_ID, UIUtils.getSpInt(Const.USER_ID) + "");
			httpHelper.getJsonData(Const.URL_DATA, parms);
			httpHelper.setOnConnectionListener(new HttpHelper.OnConnectionListener() {
				@Override
				public void successConnect(String data) {
					Message message = new Message();
					message.obj = data;
					message.what = Const.RECEIVE_DATA_SUCCESS;
					mHandler.sendMessage(message);
				}

				@Override
				public void failConnect() {
					Message message = new Message();
					message.what = Const.RECEIVE_DATA_FAIL;
					mHandler.sendMessage(message);
				}
			});
			btn_append.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);
		}
	}
}
