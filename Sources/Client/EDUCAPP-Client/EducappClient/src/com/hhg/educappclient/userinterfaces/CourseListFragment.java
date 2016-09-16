package com.hhg.educappclient.userinterfaces;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.models.Course;
import com.hhg.educappclient.rest.EducappAPI;

public class CourseListFragment extends Fragment {
	private Context context;
	private MainActivity activity;
	private Menu menu;
	private CourseListAdapter adapter;
	private View headerView;
	private ListView list;
	
	public CourseListFragment(Context context, MainActivity activity){
		this.context = context;
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("CourseListFragment", "onCreateView()");
		
		
		setRetainInstance(true);
		View v = inflater.inflate(R.layout.course_list_layout, null);
        list = (ListView)v;
        this.adapter = new CourseListAdapter(context, getServiceMessenger(), this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
			}
        });
        
        //We report to Android this fragment
  		//will use a custom menu.
  		setHasOptionsMenu(true); 
  		registerForContextMenu(v);
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		this.menu = menu;
	    inflater.inflate(R.menu.course_list_menu, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	        case R.id.menu_courselist_add_course:
	        	CourseAddFragment frag = new CourseAddFragment(context, activity);
	        	activity.switchFragment(frag);
	            break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
		Log.d(getClass().getSimpleName(), "Requested context menu for: "+v.getId());
		if (v.getId() == R.id.course_list_layout) {
			MenuInflater inflater = activity.getMenuInflater();
			inflater.inflate(R.menu.course_list_context_menu, menu);
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(((Course) adapter.getItem(info.position))
					.getCourseTag());
			Log.d(getClass().getSimpleName(), "Generating menu with "
					+menu.size()+"elements inside.");
			for(int i = 0; i < menu.size(); i++){
				menu.getItem(i).setEnabled(false);
				menu.getItem(i).setVisible(false);
			}
			for(String s : adapter.getUserAuthValues()){
				switch(s){
				case EducappAPI.AUTH_CREATE_ASSISTANCE_CONTROL:
					Log.d(getClass().getSimpleName(), "case EducappAPI.AUTH_CREATE_ASSISTANCE_CONTROL");
					MenuItem item = menu.findItem(R.id.course_list_context_assistance);
					item.setEnabled(true);
					item.setVisible(true);
					break;
					
				}
			}
		}
	}
	
	/**
	 * Called when an item from a contextual menu is clicked
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	      switch(item.getItemId()) {
	         case R.id.course_list_context_assistance:
	        	AssistanceFragment frag = new AssistanceFragment(context,
	        			activity, (Course) adapter.getItem(info.position));
	        	activity.switchFragment(frag);
	            return true;
	         case R.id.course_list_context_join:
	        	 return true;
	          default:
                return super.onContextItemSelected(item);
	      }
	}

	/**
	 * Disables all menu items for operations not allowed
	 * for this user, and then adds the ones availables.
	 * 
	 * @param userAuthValues
	 */
	public void setUpView(String[] userAuthValues){
		if (menu != null) {
			for (int i = 0; i < menu.size(); i++) {
				menu.getItem(i).setEnabled(false);
				menu.getItem(i).setVisible(false);
			}

			for (String s : userAuthValues) {
				switch (s) {
				case EducappAPI.AUTH_COURSES_CREATE:
					MenuItem item = menu
							.findItem(R.id.menu_courselist_add_course);
					item.setEnabled(true);
					item.setVisible(true);
					break;
				default:
					break;
				}

			}
		}
			
	}
	
	public Messenger getServiceMessenger(){
        return activity.getServiceMessenger();
	}
	
	public String[] getAuthStrings(){
		return activity.getAuthStrings();
	}

	public void hasData(boolean data) {
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
