package com.hhg.educappclient.userinterfaces;

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
import android.widget.TextView;

import com.google.gson.Gson;
import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.models.Notification;
import com.hhg.educappclient.service.ServiceConstants;
import com.hhg.educappclient.service.ServiceConstants.ExtraCodes;
import com.hhg.educappclient.service.ServiceConstants.RequestTypes;
import com.hhg.educappclient.service.ServiceMessageBuilder;
import com.hhg.educappclient.utilities.CustomDateDeserializerSerializer;

public class NotificationListAdapter extends BaseAdapter {
	private Notification[] notifList = new Notification[0];
	private Context context;
	private Messenger serviceMessenger;
	private NotificationListFragment fragment;
	final Messenger localMessenger = new Messenger(new IncomingHandler());

	/**
	 * To be used if we already have notification data.
	 * 
	 * @param context
	 * @param jsonData
	 */
	public NotificationListAdapter(Context context,
			String[] jsonData,
			NotificationListFragment fragment){
		this.context = context;
		this.fragment = fragment;
		initData(jsonData);
	}
	
	/**
	 * To be used if we don't yet have any notifications
	 * downloaded and require this adapter to handle their
	 * retrieval.
	 * 
	 * @param context
	 */
	public NotificationListAdapter(Context context,
			NotificationListFragment fragment){
		this.context = context;
		this.fragment = fragment;
	}
	
	private void initData(String[] jsonData){
		Log.d(getClass().getSimpleName(), "initingDataFromAdapter");
		if(jsonData != null){
			notifList = new Notification[jsonData.length];
			
			for(int i = 0; i < jsonData.length; i++){
				notifList[i] = new Gson().fromJson(jsonData[i], Notification.class);
			}	
		}else{
			//Dummy object. Just in case. Which happens.
			Log.i(getClass().getSimpleName(),
					"Warning: Empty notification list received.");
			notifList = new Notification[0];
		}
		notifyDataSetChanged();
	}
	
	public void initData(Context context){

		Log.d(getClass().getSimpleName(), "initingDataFromAdapter");
		Message outgoingMessage = ServiceMessageBuilder
				.createRequestNotificationsMessage(localMessenger);
		try {
			Messenger sm = ((MainActivity)context).getServiceMessenger();
			//TODO: CHECK FOR NULL
			if(sm != null)
				sm.send(outgoingMessage);
			else
				Log.e(getClass().getSimpleName(), "Null service messenger!");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public int getCount() {
		return notifList.length;
	}

	@Override
	public Object getItem(int arg0) {
		return notifList[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return notifList[arg0].hashCode();
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
			view = inflater.inflate(R.layout.notification_element, null);
			
			//And we set a ViewHolder for it (to improve
			//responsiveness the next time the user scrolls
			//through this)
			viewHolder = new ViewHolderItem();
			viewHolder.notificationAuthor = (TextView) view.findViewById(
					R.id.notification_author);
			viewHolder.notificationDate = (TextView) view.findViewById(
					R.id.notification_date);
			viewHolder.notificationStudent = (TextView) view.findViewById(
					R.id.notification_about_data);
			viewHolder.notificationText = (TextView) view.findViewById(
					R.id.notification_text_details);
			view.setTag(viewHolder);
		}
		
		//Step2: Initialize all the values on this view.
		Notification notification = (Notification) getItem(index);
		viewHolder.notificationAuthor.setText(notification.getCreatedBy().getFirstName()
				+" "+notification.getCreatedBy().getLastName());
		viewHolder.notificationStudent.setText(notification.getCausingUser().getFirstName()
				+" "+notification.getCausingUser().getLastName());
		viewHolder.notificationDate.setText(
				CustomDateDeserializerSerializer
				.dateFormatter.format(notification.getDate().getTime()));
		viewHolder.notificationText.setText(notification.getText());
		
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
		TextView notificationDate;
		TextView notificationText;
		TextView notificationAuthor;
		TextView notificationStudent;
	}
	
	/**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {

		@Override
        public void handleMessage(Message msg) {
			Log.d(getClass().getSimpleName(), "handleMessage()");
        	if(msg.what == RequestTypes.NOTIFICATION_REQUEST
        			&& ExtraCodes.STATUS_OK.equals(
        					msg.getData().getString(ExtraCodes.OPERATION_RESULT)))
    		{
        			String[] jsonData = msg.getData()
        					.getStringArray(ServiceConstants.NOTIFICATION_EXTRA);
        			if(jsonData != null && jsonData.length > 0){
        				initData(jsonData);
        				fragment.hasData(true);
        			}else{
        				fragment.hasData(false);
        			}
        			notifyDataSetChanged();
        			
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
