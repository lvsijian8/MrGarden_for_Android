package com.lvsijian8.flowerpot.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.ui.activity.AboutActivity;
import com.lvsijian8.flowerpot.ui.activity.AlertActivity;
import com.lvsijian8.flowerpot.ui.activity.FeedbackActivity;
import com.lvsijian8.flowerpot.ui.activity.GuideActivity;
import com.lvsijian8.flowerpot.ui.activity.LoginActivity;
import com.lvsijian8.flowerpot.utils.UIUtils;

public class FragmentMe extends Fragment implements View.OnClickListener {
	private RelativeLayout layout_help;//帮助
	private RelativeLayout layout_guide;//指导
	private RelativeLayout layout_about;//关于
	private RelativeLayout layout_feedback;//反馈
	private TextView tv_unlogin;//注销
	private TextView tv_user;//用户名
	private View rootview;
	private ImageView iv_alert;
	private RelativeLayout rl_alert;

	@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		if (rootview==null){
			rootview = inflater.inflate(R.layout.fragment_me, container, false);
		}
		initui();
		return rootview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initdata();

	}

	private void initui(){
		layout_help= (RelativeLayout) rootview.findViewById(R.id.rlayout_me_help);
		layout_about= (RelativeLayout) rootview.findViewById(R.id.rlayout_me_about);
		layout_guide= (RelativeLayout) rootview.findViewById(R.id.rlayout_me_guide);
		layout_feedback= (RelativeLayout) rootview.findViewById(R.id.rlayout_me_feedback);
		tv_unlogin= (TextView) rootview.findViewById(R.id.tv_me_unlogin);
		tv_user= (TextView) rootview.findViewById(R.id.tv_me_user);
		iv_alert= (ImageView) rootview.findViewById(R.id.iv_me_alert);
		rl_alert = (RelativeLayout) rootview.findViewById(R.id.rl_me_alert);
	}

	private void initdata() {
		layout_help.setOnClickListener(this);
		layout_about.setOnClickListener(this);
		layout_guide.setOnClickListener(this);
		layout_feedback.setOnClickListener(this);
		tv_unlogin.setOnClickListener(this);
		rl_alert.setOnClickListener(this);
		if (UIUtils.getSpString(Const.USER_NAME)!=null){
			tv_user.setText(UIUtils.getSpString(Const.USER_NAME));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.rlayout_me_help:
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse("https://lvsijian.cn/MrGarden/");
				intent.setData(content_url);
				startActivity(intent);
				break;
			case R.id.rlayout_me_guide:
				startActivity(new Intent(getContext(), GuideActivity.class));
				getActivity().overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
				break;
			case R.id.rlayout_me_about:
				startActivity(new Intent(getContext(), AboutActivity.class));
				getActivity().overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
				break;
			case R.id.rlayout_me_feedback:
				startActivity(new Intent(getContext(), FeedbackActivity.class));
				getActivity().overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
				break;
			case R.id.tv_me_unlogin:
				AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
				builder.setTitle("您要退出登录吗？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						UIUtils.setSpNumInt(Const.USER_ID, -1);
						UIUtils.setSpString(Const.USER_NAME, null);
						getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
						getActivity().finish();
					}
				});
				builder.setNegativeButton("取消", null);
				AlertDialog dialog=builder.create();
				dialog.show();
				break;
			case R.id.rl_me_alert:
				startActivity(new Intent(getContext(), AlertActivity.class));
				getActivity().overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (UIUtils.getSpString(Const.USER_NAME)!=null){
			tv_user.setText(UIUtils.getSpString(Const.USER_NAME));
		}
	}
}
