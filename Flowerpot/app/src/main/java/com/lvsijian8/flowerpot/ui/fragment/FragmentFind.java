package com.lvsijian8.flowerpot.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lvsijian8.flowerpot.R;
import com.lvsijian8.flowerpot.ui.view.Relistview;

import java.util.ArrayList;

public class FragmentFind extends Fragment {
	private ImageView ibtn_search;
	private Relistview relistview;
	private ArrayList<String> listdata;
	private MyAdapter myAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		View find_view=inflater.inflate(R.layout.fragment_find, container, false);
		initUui(find_view);
		initData();
		return find_view;

	}

	private void initUui(View view) {
		ibtn_search= (ImageView) view.findViewById(R.id.ibtn_find_search);
		relistview= (Relistview) view.findViewById(R.id.lv_find_flower);
		listdata=new ArrayList();
		for (int i=0;i<23;i++){
			listdata.add("蝴蝶"+i);
		}
		myAdapter = new MyAdapter();
		relistview.setAdapter(myAdapter);
	}

	private void initData(){

	}

	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return listdata.size();
		}

		@Override
		public Object getItem(int position) {
			return listdata.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

				View view=View.inflate(getContext(),R.layout.find_item,null);

			return view;
		}
	}
}
