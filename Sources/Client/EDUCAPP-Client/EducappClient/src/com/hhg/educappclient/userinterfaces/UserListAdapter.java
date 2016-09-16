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

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.models.UserPublicProfile;
import com.hhg.educappclient.service.ServiceConstants;
import com.hhg.educappclient.service.ServiceMessageBuilder;

public class UserListAdapter extends BaseAdapter {
	private ArrayList<UserPublicProfile> userList = new ArrayList<UserPublicProfile>();

	private String TAG = getClass().getSimpleName();
	
	protected Messenger serviceMessenger;

	protected boolean isBound;

	private Context context;

	private MainActivity activity;
	
	private String[] userAuthValues;
	
	/**
	 * Messenger used to send back messages with login results
	 * to this activity.
	 */
	final Messenger localMessenger = new Messenger(new IncomingHandler());

	private UserListFragment fragment;
	
	public UserListAdapter(Context context,
			Messenger serviceMessenger,
			UserListFragment fragment){
		this.context = context;
		this.serviceMessenger = serviceMessenger;
		this.fragment = fragment;
		init();
	}
	
	/**
	 * Retrieves the list of authorities for the
	 * currently logged in user, so we can present
	 * him/her with the corresponding options in the
	 * UI.
	 */
	public void retrieveUserAccessDetails(){
		Message outgoingMessage = ServiceMessageBuilder
				.createRequestLoggedUserAuthValuesMessage(localMessenger);
		try {
			serviceMessenger.send(outgoingMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	
	public void init(){
		retrieveUserAccessDetails();
		beginAdapterListDataPopulation();
	}
	
	/**
	 * Forces the generation/regeneration of this adapter
	 * list of items.
	 * 
	 * @param authList
	 */
	public void beginAdapterListDataPopulation(){
		Message outgoingMessage = ServiceMessageBuilder
				.CreateRequestUserListMessage(localMessenger);
		try {
			serviceMessenger.send(outgoingMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getCount() {
		return userList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return userList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return userList.get(position).hashCode();
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
			view = inflater.inflate(R.layout.user_list_element, null);
			
			//And we set a ViewHolder for it (to improve
			//responsiveness the next time the user scrolls
			//through this)
			viewHolder = new ViewHolderItem();
			viewHolder.name = (TextView) view.findViewById(
					R.id.user_list_element_name);
			view.setTag(viewHolder);
		}
		
		//Step2: Initialize all the values on this view.
		Log.d(TAG, "Attempting to find adapter course: "+index);
		UserPublicProfile profile = this.userList.get(index);
		viewHolder.name.setText(profile.getFirstName()+" "+
				profile.getLastName());
		
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
		TextView name;
	}

	/**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {

		@Override
        public void handleMessage(Message msg) {
        	Log.e(TAG, "Handling message in ser list adapter: "+msg.what);
        	if(msg.what == ServiceConstants.RequestTypes.GET_AUTH_LIST){
        		userAuthValues = msg.getData()
        				.getStringArray(ServiceConstants.AUTH_LIST_EXTRA);
        		Log.i(TAG, "Retrieved user auth values with lenght: "+userAuthValues.length);
        	}else if(msg.what == ServiceConstants.RequestTypes.GET_USER_LIST){
        		Bundle data = msg.getData();
            	String operationResult = data.getString(
            			ServiceConstants.ExtraCodes.OPERATION_RESULT);
            	
            	
            	if(ServiceConstants.ExtraCodes.STATUS_OK.equals(operationResult)){
            		String[] jsonData = data.getStringArray(ServiceConstants.ExtraCodes.EXTRA_USERS_LIST);
            		
            		if(jsonData != null && jsonData.length > 0){
        				fragment.hasData(true);
        			}else{
        				fragment.hasData(false);
        				return;
        			}
            		
            		UserPublicProfile[] profiles = new UserPublicProfile[jsonData.length];
            		for(int i = 0; i<jsonData.length; i++){
            			profiles[i] = new Gson().fromJson(jsonData[i], UserPublicProfile.class);
            			Log.i(TAG, "Converted user profile back to object data: "+profiles[i].getNif()+ " from "+jsonData[i]);
            		}
            		userList = Lists.newArrayList(profiles);
            		UserListAdapter.this.notifyDataSetChanged();
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
        		//sign of a bug or desync.
        		super.handleMessage(msg);
        		return;
        	}
        	
        	
        }
    }
}
