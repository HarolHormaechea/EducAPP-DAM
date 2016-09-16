package com.hhg.educappclient.userinterfaces;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hhg.educappclient.R;
import com.hhg.educappclient.models.Course;
import com.hhg.educappclient.service.ServiceConstants;
import com.hhg.educappclient.service.ServiceMessageBuilder;

public class CourseListAdapter extends BaseAdapter{
	private ArrayList<Course> courseList = new ArrayList<Course>();

	private String TAG = getClass().getSimpleName();
	
	protected Messenger serviceMessenger;

	protected boolean isBound;

    private String[] userAuthValues;
	
	/**
	 * Messenger used to send back messages with login results
	 * to this activity.
	 */
	final Messenger localMessenger = new Messenger(new IncomingHandler());
	
	//Context reference will be required to inflate
	//fragments, etc
	private Context context;
	private CourseListFragment fragment;
	
	public CourseListAdapter(Context context, Messenger serviceMessenger,
			CourseListFragment userFragment){
		this.fragment = userFragment;
		this.context = context;
		this.serviceMessenger = serviceMessenger;
		init();
	}
	
	public void init(){
		retrieveUserAccessDetails();
		beginAdapterListDataPopulation();
	}
	
	/**
	 * Retrieves the list of authorities for the
	 * currently logged in user, so we can present
	 * him/her with the corresponding options in the
	 * UI.
	 */
	public void retrieveUserAccessDetails(){
		userAuthValues = fragment.getAuthStrings();
//		Message outgoingMessage = ServiceMessageBuilder
//				.createRequestLoggedUserAuthValuesMessage(localMessenger);
//		try {
//			serviceMessenger.send(outgoingMessage);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Forces the generation/regeneration of this adapter
	 * list of items.
	 * 
	 * @param authList
	 */
	public void beginAdapterListDataPopulation(){
		Message outgoingMessage = ServiceMessageBuilder
				.CreateRequestCourseListMessage(localMessenger);
		try {
			serviceMessenger.send(outgoingMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getCount() {
		return courseList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return courseList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return courseList.get(arg0).hashCode();
	}

	@Override
	public View getView(int index, View view, ViewGroup parent) {
		ViewHolderItem viewHolder;
		
		//Step 1: We check if there is an already created view
		//for this item in the layout.
		if(view != null){
			//If there is, we will use it.
			viewHolder = (ViewHolderItem) view.getTag();
		}else{
			//Otherwise, we will generate it anew
			LayoutInflater inflater = (LayoutInflater)context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.course_list_element, null);
			
			//And we set a ViewHolder for it (to improve
			//responsiveness the next time the user scrolls
			//through this)
			viewHolder = new ViewHolderItem();
			viewHolder.courseBeginDate = (TextView) view.findViewById(
					R.id.course_list_element_begin_date);
			viewHolder.courseEndDate = (TextView) view.findViewById(
					R.id.course_list_element_finish_date);
			viewHolder.courseTag = (TextView) view.findViewById(
					R.id.course_list_element_tag);
			viewHolder.courseFullName = (TextView) view.findViewById(
					R.id.course_list_element_full_name);
			view.setTag(viewHolder);
		}
		
		//Step2: Initialize all the values on this view.
		Log.d(TAG, "Attempting to find adapter course: "+index);
		Course course = this.courseList.get(index);
		viewHolder.courseFullName.setText(course.getCourseFullName());
		viewHolder.courseTag.setText(course.getCourseTag());
		viewHolder.courseBeginDate.setText(course.getBeginDate().getTime().toString());
		viewHolder.courseEndDate.setText(course.getEndDate().getTime().toString());
		
		
		
		return view;
	}

	/**
	 * ViewHolder pattern class to allow improved scrolling
	 * performance.
	 * 
	 * http://developer.android.com/training/improving-layouts/smooth-scrolling.html
	 * 
	 * @author Harold Hormaechea Garcia
	 *
	 */
	static class ViewHolderItem{
		TextView courseFullName;
		TextView courseTag;
		TextView courseBeginDate;
		TextView courseEndDate;
		TextView enrollmentStatus;
	}
	
	public String[] getUserAuthValues(){
		return userAuthValues;
	}
	
	/**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {

		@Override
        public void handleMessage(Message msg) {
        	Log.e(TAG, "Handling message in course list adapter: "+msg.what);
        	if(msg.what == ServiceConstants.RequestTypes.GET_AUTH_LIST){
        		userAuthValues = msg.getData()
        				.getStringArray(ServiceConstants.AUTH_LIST_EXTRA);
        		fragment.setUpView(userAuthValues);
        		Log.i(TAG, "Retrieved user auth values with lenght: "+userAuthValues.length);
        	}
        	else if(msg.what == ServiceConstants.RequestTypes.COURSE_LIST){
        		Bundle data = msg.getData();
            	String operationResult = data.getString(
            			ServiceConstants.ExtraCodes.OPERATION_RESULT);
            	if(ServiceConstants.ExtraCodes.STATUS_OK.equals(operationResult)){
            		Toast.makeText(context,
    						R.string.results_ok,
    						Toast.LENGTH_SHORT).show();
            		String[] jsonData = data.getStringArray(ServiceConstants.ExtraCodes.EXTRA_COURSE_LIST);
            		if(jsonData != null && jsonData.length > 0){
            			Course[] courses = new Course[jsonData.length];
                		for(int i = 0; i<jsonData.length; i++){
                			courses[i] = new Gson().fromJson(jsonData[i], Course.class);
                			Log.i(TAG, "Converted course back to object data: "+courses[i].getCourseTag());
                		}
                		courseList = Lists.newArrayList(courses);
        				fragment.hasData(true);
        			}else{
        				fragment.hasData(false);
        				return;
        			}
            		
            		CourseListAdapter.this.notifyDataSetChanged();
            	}else if(ServiceConstants.ExtraCodes.STATUS_BAD_CREDENTIALS.equals(operationResult)){
            		Log.e(TAG, "Bad credentials during course retrieval attempt: "+operationResult);
            	}else if(ServiceConstants.ExtraCodes.STATUS_CONNECTION_ERROR.equals(operationResult)){
            		Log.e(TAG, "Connection error during course retrieval attempt: "+operationResult);
            	}else{
            		Log.e(TAG, "Unknown error ocurred during operation: "+operationResult);
            	}
        	}else{
        		//If this doesn't come from a login request,
        		//it is of no use to this activity. It'd be a
        		//sign of a bug.
        		super.handleMessage(msg);
        		return;
        	}
        	
        	
        }
    }

}
