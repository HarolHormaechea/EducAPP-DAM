package com.hhg.educappclient.userinterfaces;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.models.Course;

public class AssistanceFragment extends Fragment implements AssistanceResultsInterface{
	private String TAG = getClass().getSimpleName();
	private Context context;
	private MainActivity activity;
	private Course course;
	private AssistanceAdapter adapter;
	private Menu menu;
	
	
	public AssistanceFragment(Context context,
			MainActivity activity, Course course){
		this.context = context;
		this.activity = activity;
		this.course = course;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("AssistanceFragment", "onCreateView()");
		
		//We report to Android this fragment
		//will use a custom menu.
		//setHasOptionsMenu(true); 
		View v = inflater.inflate(R.layout.assistance_list_layout, null);
        ListView list = (ListView)v;
        adapter = new AssistanceAdapter(context, 
        		activity.getServiceMessenger(), course);
        adapter.addListener(this);
        list.setAdapter(adapter);
        
        //We report to Android this fragment
  		//will use a custom menu.
  		setHasOptionsMenu(true); 
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		this.menu = menu;
	    inflater.inflate(R.menu.assistance_control_menu, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	        case R.id.menu_assistance_submit:
	        	onSubmitRequested();
	            break;
	        case R.id.menu_assistance_go_back:
	        	returnToCourseList();
	            break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Will pop up a warning with the number of students which
	 * have been declared as absent and a decision element to
	 * ask the user if definitely submit or not the report.
	 */
	private void onSubmitRequested(){
		Log.i(TAG, "onSubmitRequested()");
		adapter.submitData();
	}
	
	public void returnToCourseList(){
		Log.i(TAG, "returnToCourseList()");
		activity.switchFragment(new CourseListFragment(context, activity));
	}
	@Override
	public void onPostedSuccesfully() {
		Toast.makeText(context, R.string.results_ok, Toast.LENGTH_SHORT).show();
		returnToCourseList();
	}
	
	@Override
	public void onError(String value){
		Toast.makeText(context, "Error://"+value, Toast.LENGTH_SHORT).show();
	}
	
}
