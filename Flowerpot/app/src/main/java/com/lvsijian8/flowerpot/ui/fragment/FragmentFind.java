package com.lvsijian8.flowerpot.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.domin.Flower_Find;
import com.lvsijian8.flowerpot.global.Const;
import com.lvsijian8.flowerpot.http.HttpHelper;
import com.lvsijian8.flowerpot.ui.activity.DetailActivity;
import com.lvsijian8.flowerpot.ui.activity.SearchActivity;
import com.lvsijian8.flowerpot.ui.view.Relistview;
import com.lvsijian8.flowerpot.utils.BitmapHelper;
import com.lvsijian8.flowerpot.utils.ThreadManager;
import com.lvsijian8.flowerpot.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class FragmentFind extends Fragment {
	private ImageView iv_search;//放大镜查找按钮
	private Relistview relistview;
	private ArrayList<Flower_Find.flower> flower_data;
	private MyAdapter myAdapter;
	private HttpHelper httpHelper;
	private BitmapUtils bitmapUtils;
	private Gson gson;
	private static int State_REFRESH=0;
	private static int State_LOADING=1;
	private static int CURRENT_STATE=State_REFRESH;
	private View rootview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gson = new Gson();
		flower_data=new ArrayList<>();
		bitmapUtils = BitmapHelper.getBitmapUtils();//获取单一bitmapUtils
		httpHelper = HttpHelper.getInstances();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {

		if (rootview==null){
			rootview = inflater.inflate(R.layout.fragment_find, container, false);
		}

		ViewGroup parent= (ViewGroup) rootview.getParent();
		if (parent!=null){
			parent.removeView(rootview);
		}
		initUui();
		relistview.CloseRersh();
		relistview.Closeloading();
		initData();
		return rootview;

	}





	private void initUui() {
		iv_search= (ImageView) rootview.findViewById(R.id.iv_find_search);
		relistview= (Relistview) rootview.findViewById(R.id.lv_find_flower);
		myAdapter = new MyAdapter();
		relistview.setAdapter(myAdapter);
//		relistview.setOnScrollListener(new PauseOnScrollListener(bitmapUtils, false, true)); //影响了加载更多的操作
		relistview.setOnRershListener(new Relistview.OnRefreshListener() {
			@Override
			public void onloding() {
				CURRENT_STATE=State_LOADING;
				ThreadManager.getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(500 + new Random().nextInt(500));
							//加载更多数据,根据当前的Item数量，获取后6条数据
							HashMap<String, Object> parmsmap = new HashMap<String, Object>();
							parmsmap.put("fid", "" + flower_data.size());
							httpHelper.getFindData(Const.URL_FIND, parmsmap);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});


			}

			@Override
			public void onReresh() {
				relistview.CloseRersh();
				Toast.makeText(getContext(),"已刷新数据",Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void initData() {
		HashMap<String,Object> map=new HashMap<String, Object>();
		map.put("fid", 0 + "");
		httpHelper.getFindData(Const.URL_FIND, map);
		httpHelper.setOnFindtData(new HttpHelper.OnFindListener() {
			@Override
			public void successGet(String data) {
				Message message = new Message();
				if (data != null && !data.equals("")) {
					message.what = Const.LOADING_FIND_DATA;
					Log.e("ZDLW_FIND", data);
					Flower_Find plantdata = gson.fromJson(data, Flower_Find.class);
					message.obj = plantdata;
				} else {
					message.what = Const.RESHRE_FIND_DATA;//当没有数据时，会返回该参数
				}
				mHandler.sendMessage(message);
			}


		});

		//先关掉切换到其他TabSpec时没关掉的头尾
		relistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				relistview.Closeloading();
				relistview.CloseRersh();

				Intent intent = new Intent(getActivity(), DetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("pic",flower_data.get(position-1).pic);
				bundle.putInt("fid", flower_data.get(position - 1).fid);
				bundle.putString("namec",flower_data.get(position-1).namec);
				intent.putExtras(bundle);
				//进行版本号判断，版本号低于21，即低于Android5.0；
				if (UIUtils.currentapiVersion >= 21) {
					ImageView img = (ImageView) view.findViewById(R.id.iv_item_find);
					ActivityOptionsCompat options = ActivityOptionsCompat
							.makeSceneTransitionAnimation(getActivity(), img, "robot");
					ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
				} else {
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			}
		});
		//放大镜按钮点击效果，跳转到搜寻界面
		iv_search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), SearchActivity.class));
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});
	}

	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return flower_data.size();
		}

		@Override
		public Object getItem(int position) {
			return flower_data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder=null;
			if (convertView==null){
				holder=new ViewHolder();
				convertView=View.inflate(getContext(),R.layout.find_item,null);
				holder.img= (ImageView) convertView.findViewById(R.id.iv_item_find);
				holder.nameC= (TextView) convertView.findViewById(R.id.tv_item_find_c);
				holder.nameE= (TextView) convertView.findViewById(R.id.tv_item_find_e);
				convertView.setTag(holder);
			}else {
				holder= (ViewHolder) convertView.getTag();
			}
			//设置数据
			Flower_Find.flower data= (Flower_Find.flower) getItem(position);
			bitmapUtils.display(holder.img, Const.URL_PIC + data.pic);
			holder.nameE.setText(data.namee);
			holder.nameC.setText(data.namec);


			return convertView;
		}
		class ViewHolder{
			ImageView img;
			TextView nameC;
			TextView nameE;
		}
	}
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case Const.RESHRE_FIND_DATA:
					Toast.makeText(UIUtils.getContext(),"没有更多数据了",Toast.LENGTH_SHORT).show();
					relistview.Closeloading();
					relistview.setSelection(relistview.getCount()-1);
					break;
				case Const.LOADING_FIND_DATA:
					Flower_Find plantdata= (Flower_Find) msg.obj;
					ArrayList<Flower_Find.flower> data=plantdata.data;
					if (CURRENT_STATE==State_REFRESH){
						//进入发现页的情况
						flower_data=data;
					}else if (CURRENT_STATE==State_LOADING){
						//加载更多数据的情况
						flower_data.addAll(data);
						relistview.Closeloading();
					}
					myAdapter.notifyDataSetChanged();
					break;
			}
		}
	};
	public void CloseRelistview(){
		if (relistview!=null){
			relistview.Closeloading();
			relistview.CloseRersh();
		}
	}
}
