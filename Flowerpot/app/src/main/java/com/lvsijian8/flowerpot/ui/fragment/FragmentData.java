package com.lvsijian8.flowerpot.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.ui.view.ProgressArc;
import com.lvsijian8.flowerpot.ui.view.TasksCompletedView;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class FragmentData extends Fragment {
	private LinearLayout layout_contain;
	private LineChartView lineTemperature;
	private PopupWindow mPopupWindow;
	private ImageView btn_edit;
//	private LineChartData lineChartData;
	private float[] f={22.2f,30.0f,30.5f,19.4f,25.0f,24.0f,22.5f,28.6f,19.2f,25.0f,26.4f,23.7f};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_data, container, false);
		initui(view);
		setPopup();
		Log.e("ZDLW","W");

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTemperature(lineTemperature, f);
	}

	private void initui(View view){
		layout_contain= (LinearLayout) view.findViewById(R.id.layout_fdata_contain);
		lineTemperature= (LineChartView) view.findViewById(R.id.chart_data_temperature);
		TasksCompletedView conview= (TasksCompletedView) view.findViewById(R.id.tasks_view);
		conview.setProgress(30);
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
	}

	/**
	 * 设置温度LineChart
	 */
	private void setTemperature(LineChartView lineTemperature,float[] data){
		lineTemperature.setInteractive(true);//是否与用户交互
		lineTemperature.setZoomEnabled(true);//是否可以缩放
		lineTemperature.setValueSelectionEnabled(true);//是否有点击动画
		List<Line> lines=new ArrayList<>();
		for (int i=0;i<1;i++){
			List<PointValue> pointValues=new ArrayList<>();
			ArrayList<AxisValue> axisValueX=new ArrayList<>();
			ArrayList<AxisValue> axisValueY=new ArrayList<>();
			Axis axis=new Axis();//X轴
			Axis ayis=new Axis();//Y轴
			axis.setValues(axisValueX);
			ayis.setValues(axisValueY);
			axis.setName("天数");
			ayis.setName("");
			for (int j=0;j<data.length;j++){
				pointValues.add(new PointValue(j,data[j]));
				axisValueX.add(new AxisValue(j).setLabel(j+""));
				axisValueY.add(new AxisValue(j*5).setLabel(j*5+""));
			}
//			axis.setHasLines(true);
//			ayis.setHasLines(true);
			ayis.setTextSize(12);
			axis.setTextColor(Color.BLACK);
			ayis.setTextColor(Color.BLACK);
			/*===============设置曲线=================*/
			Line line=new Line(pointValues);
			line.setColor(R.color.violet);
			line.setFilled(true);//设置折线覆盖区域
			line.setCubic(true);//设置曲线是否圆滑
			line.setPointRadius(2);//设置圆点的半径
			line.setShape(ValueShape.CIRCLE);//节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形
			line.setPointColor(Color.RED);//设置节点颜色
			line.setStrokeWidth(1);//线的宽度
			line.setHasPoints(true);//是否显示节点
			line.setHasLabels(false);//是否显示节点数据
			line.setHasLines(true);//是否显示折线
			line.setHasLabelsOnlyForSelected(true);////隐藏数据，触摸可以显示
			lines.add(line);

			/*================设置轴======================*/
			LineChartData lineChartData = new LineChartData(lines);
			lineChartData.setAxisXBottom(axis);
			lineChartData.setAxisYLeft(ayis);
			lineChartData.setBaseValue(0);//覆盖区域以什么为标准覆盖
			lineChartData.setValueLabelBackgroundAuto(false);//设置数据背景是否跟随节点颜色
			lineChartData.setValueLabelBackgroundColor(R.color.violet);//设置数据背景颜色
			lineChartData.setValueLabelBackgroundEnabled(false);//点击后是字体是否背景色
			lineChartData.setValueLabelsTextColor(Color.BLACK);//点击后的字体颜色
			lineTemperature.setLineChartData(lineChartData);
			resetViewport();
		}



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
	}

	/**
	 * 注意，以下必须设置，v是整个坐标最大有多大，v.top=50,最高就是50.
	 */
	private void resetViewport() {
		final Viewport v = new Viewport(lineTemperature.getMaximumViewport());
		v.bottom = 0;
		v.top = 52;
		v.left = 0;
		v.right = f.length - 1;
		lineTemperature.setCurrentViewportWithAnimation(v);
		lineTemperature.setMaximumViewport(v);
		lineTemperature.setCurrentViewport(v);
	}

}
