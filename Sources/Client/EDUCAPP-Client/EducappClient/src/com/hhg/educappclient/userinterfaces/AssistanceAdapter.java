package com.hhg.educappclient.userinterfaces;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.models.AssistanceControl;
import com.hhg.educappclient.models.Course;
import com.hhg.educappclient.service.ServiceConstants;
import com.hhg.educappclient.service.ServiceConstants.ExtraCodes;
import com.hhg.educappclient.service.ServiceMessageBuilder;

public class AssistanceAdapter extends BaseAdapter {
	
	private ArrayList<AssistanceControl> dataList = new ArrayList<AssistanceControl>();
	private String TAG = getClass().getSimpleName();
	protected Messenger serviceMessenger;
	protected boolean isBound;
	private Context context;
	private MainActivity activity;
	private Course course;
	private WeakReference<AssistanceResultsInterface> listener
		= new WeakReference<AssistanceResultsInterface>(null);
	
	/**
	 * Messenger used to send back messages with results
	 * to this activity.
	 */
	final Messenger localMessenger = new Messenger(new IncomingHandler());
	
	public AssistanceAdapter(Context context, Messenger serviceMessenger, Course course){
		this.context = context;
		this.serviceMessenger = serviceMessenger;
		this.course = course;
		init();
	}
	
	public void init(){
		beginAdapterListDataPopulation();
	}
	
	/**
	 * Forces the generation/regeneration of this adapter
	 * list of items.
	 * 
	 * @param authList
	 */
	public void beginAdapterListDataPopulation(){
		Date date = Calendar.getInstance().getTime();
		
		Message outgoingMessage = ServiceMessageBuilder
				.CreateRequestNewAssistanceControl(localMessenger,
				course, date);
		try {
			serviceMessenger.send(outgoingMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return dataList.get(position).hashCode();
	}
	
	
	public void setPresenceStatus(int position, boolean isPresent){
		((AssistanceControl)getItem(position)).setIsPresent(isPresent);
	}

	@Override
	public View getView(final int index, View view, ViewGroup parent) {
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
			view = inflater.inflate(R.layout.assistance_list_element, null);
			
			//And we set a ViewHolder for it (to improve
			//responsiveness the next time the user scrolls
			//through this)
			viewHolder = new ViewHolderItem();
			viewHolder.fullName = (TextView) view.findViewById(
					R.id.assistance_list_element_fullname);
			viewHolder.nif = (TextView) view.findViewById(
					R.id.assistance_list_element_niffield);
			viewHolder.enrollment = (TextView) view.findViewById(
					R.id.assistance_list_element_enrollmentid);
			view.setTag(viewHolder);
			
			//Don't forget to link the toggle button!
			viewHolder.toggle = (Switch) view.findViewById(
					R.id.assistance_list_element_absentswitch);
			viewHolder.toggle.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					setPresenceStatus(index, isChecked);
					Log.i(TAG, "Presence status for "+index+" is: "+isChecked );
				}
				
			});
		}
		
		//Step2: Initialize all the values on this view.
		AssistanceControl control = this.dataList.get(index);
		viewHolder.fullName.setText(control.getEnrollmentReference().getStudent().getFirstName()
								+" "+control.getEnrollmentReference().getStudent().getLastName());
		viewHolder.nif.setText(control.getEnrollmentReference().getStudent().getNif());
		viewHolder.enrollment.setText(String.valueOf(control.getEnrollmentReference().getId()));
		viewHolder.toggle.setChecked(control.getIsPresent());
		
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
		TextView fullName;
		TextView nif;
		TextView enrollment;
		Switch toggle;
	}
	
	
	/**
	 * Will post the assistance control through service.
	 */
	public void submitData(){
		Message outgoingMessage = ServiceMessageBuilder
				.CreatePostAssistanceControlMessage(localMessenger,
				dataList);
		try {
			serviceMessenger.send(outgoingMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void addListener(AssistanceResultsInterface listener){
		this.listener = new WeakReference<AssistanceResultsInterface>(listener);
	}
	
	/**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {

		@Override
        public void handleMessage(Message msg) {
        	Log.e(TAG, "Handling message in assistance list adapter: "+msg.what);
        	if(msg.what == ServiceConstants.RequestTypes.GET_NEW_ASSISTANCE_CONTROL){
        		String[] jsonData = msg.getData()
        				.getStringArray(ServiceConstants.ASSISTANCE_CONTROL_EXTRA);
        		if(jsonData == null){
        			Toast.makeText(context, "This course has no students assigned.",
        					Toast.LENGTH_SHORT).show();
        			dataList.clear();
        			
        			AssistanceAdapter.this.notifyDataSetChanged();
            		Log.i(TAG, "Added assistance verification for empty course with no students");
            		return;
        		}
        		AssistanceControl[] controls = new AssistanceControl[jsonData.length];
        		for(int i = 0; i<jsonData.length; i++){
        			controls[i] = new Gson().fromJson(jsonData[i], AssistanceControl.class);
        		}
        		dataList = Lists.newArrayList(controls);
        		AssistanceAdapter.this.notifyDataSetChanged();
        		Log.i(TAG, "Added assistance verification for "+dataList.size()+" students.");
        	}else if(msg.what == ServiceConstants.RequestTypes.POST_ASSISTANCE_CONTROL){
        		AssistanceResultsInterface listening = listener.get();
        		if(listening != null){
        			Log.i(TAG, "post assistance op result in adapter: "
        					+msg.getData().getString(ExtraCodes.OPERATION_RESULT));
        			if(msg.getData().getString(ExtraCodes.OPERATION_RESULT).equals(
        					ExtraCodes.STATUS_OK)){
            			listening.onPostedSuccesfully();
            		}else{
            			listening.onError(
            					msg.getData().getString(ExtraCodes.OPERATION_RESULT));
            		}
        		}
        		
        	}else{
        		//If this doesn't come from a login request,
        		//it is of no use to this activity. It'd be a
        		//sign of a bug or desync.
        		super.handleMessage(msg);
        		return;
        	}
        }
		
		
    }
}
