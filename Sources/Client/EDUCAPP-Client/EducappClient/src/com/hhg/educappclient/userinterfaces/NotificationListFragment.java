package com.hhg.educappclient.userinterfaces;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;

public class NotificationListFragment extends Fragment {
	private NotificationListAdapter adapter;
	private ListView list;
	private View headerView;
	
	/**
	 * To be used if we already have the data to show.
	 * 
	 * @param context
	 * @param activity
	 * @param jsonData
	 */
	public NotificationListFragment(Context context, String[] jsonData){
		this.adapter = new NotificationListAdapter(context,
				jsonData,
				this);
	}
	
	public NotificationListFragment(Context context){

		this.adapter = new NotificationListAdapter(context,
				this);
		adapter.initData(context);
	}
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("NotificationListFragment", "onCreateView()");
		View v = inflater.inflate(R.layout.course_list_layout, null);
        list = (ListView)v;
        list.setAdapter(adapter);
        return v;
	}

	

	@Override
	public void onResume() {
		super.onResume();
	}

	public void hasData(boolean data){
		Log.d(getClass().getSimpleName(), "hasData="+data);
		if(!data){
			headerView = getActivity().getLayoutInflater()
					.inflate(R.layout.base_list_header, null);

			TextView txt = (TextView)headerView.findViewById(R.id.base_list_header_title);
			txt.setText(getActivity().getResources().getString(R.string.notification_list_empty));
			
			list.addHeaderView(headerView);
		}else{
			list.removeHeaderView(headerView);
		}
	}

	public Messenger getServiceMessenger() {
		return ((MainActivity)getActivity()).getServiceMessenger();
	}
}
