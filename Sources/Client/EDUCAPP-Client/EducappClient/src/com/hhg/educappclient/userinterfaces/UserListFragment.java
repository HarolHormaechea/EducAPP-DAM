package com.hhg.educappclient.userinterfaces;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.models.UserPublicProfile;

public class UserListFragment extends Fragment{
	private Context context;
	private MainActivity activity;
	private View headerView;
	private ListView list;
	
	public UserListFragment(Context context, MainActivity activity){
		this.context = context;
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("UserListFragment", "onCreateView()");
		
		//We report to Android this fragment
		//will use a custom menu.
		//setHasOptionsMenu(true); 
		View v = inflater.inflate(R.layout.course_list_layout, null);
        list = (ListView)v;
        final UserListAdapter adapter = new UserListAdapter(context,
        		getServiceMessenger(),
        		this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				activity.switchFragment(
						new ProfileViewerFragment(
								context,
								activity,
								(UserPublicProfile)adapter.getItem(position)));
			}
        });
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    //inflater.inflate(R.menu.course_list_menu, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

//	    switch (item.getItemId()) {
//	        case R.id.menu_courselist_add_course:
//	        	CourseAddFragment frag = new CourseAddFragment(context, activity);
//	        	activity.switchFragment(frag);
//	            break;
//	    }
	    return super.onOptionsItemSelected(item);
	}
	
	
	
	public Messenger getServiceMessenger(){
        return activity.getServiceMessenger();
	}
	
	public void hasData(boolean data){
		Log.d(getClass().getSimpleName(), "hasData="+data);
		if(!data){
			headerView = activity.getLayoutInflater()
					.inflate(R.layout.base_list_header, null);

			TextView txt = (TextView)headerView.findViewById(R.id.base_list_header_title);
			txt.setText(activity.getResources().getString(R.string.notification_list_empty));
			
			list.addHeaderView(headerView);
		}else{
			list.removeHeaderView(headerView);
		}
	}
}
