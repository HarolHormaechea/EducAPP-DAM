package com.hhg.educappclient.service;

import java.lang.ref.WeakReference;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.service.ServiceConstants.ExtraCodes;
import com.hhg.educappclient.service.ServiceConstants.RequestTypes;

/**
 * Broadcast receiver implementation designed to check
 * for new notifications for the logged in user when
 * alarms trigger for that.
 * 
 * @author Harold Hormaechea Garcia
 *
 */
public class NotificationCheckAlarm extends BroadcastReceiver {
	private Messenger serviceMessenger;
	private WeakReference<Context> context;
	
	/**
	 * Messenger used to send back messages with login results
	 * to this object.
	 */
	final Messenger localMessenger = new Messenger(new IncomingHandler());
	
	public NotificationCheckAlarm(){
		Log.i(getClass().getSimpleName(), "Notification alarm receiver created.");
	}

	@Override
	public void onReceive(Context context, Intent receivedIntent) {
		Log.i(getClass().getName(),"Broadcast received");
		Intent intent = new Intent(context, NetworkOperationsService.class);
        IBinder binder = peekService(context, intent);
        if(binder == null)//If service is not already running.{
        {
        	Log.e(getClass().getName(), "Service not running!");
        	return;
        }else{
        	Log.e(getClass().getName(), "Service running. Requesting notifications.");
        	serviceMessenger = new Messenger(binder);
    		Message outMessage = ServiceMessageBuilder
    				.createRequestNotificationsMessage(localMessenger);
    		try {
    			serviceMessenger.send(outMessage);
    			this.context = new WeakReference<Context>(context);
    		} catch (RemoteException e) {
    			e.printStackTrace();
    		}
        }
	}
	
	
	
	/**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	Log.i(getClass().getSimpleName(), "NotificationCheckAlarm: "
    				+msg.what+" --- "
        			+msg.getData().getString(ExtraCodes.OPERATION_RESULT));
        		if(msg.what == RequestTypes.NOTIFICATION_REQUEST
        			&& ExtraCodes.STATUS_OK.equals(
        					msg.getData().getString(ExtraCodes.OPERATION_RESULT)))
        		{
        			String[] jsonData = msg.getData()
        					.getStringArray(ServiceConstants.NOTIFICATION_EXTRA);
        			if(jsonData != null && jsonData.length > 0)
        				createNotification(context.get(), jsonData);
        			context = null;
        			
        		}
        	}
    }
    
    /**
     * Notification creator for when there is something to present the user.
     * 
     * @param context
     */
    private void createNotification(Context context, String[] jsonData){
    	Log.i(getClass().getSimpleName(), "Generating Android notification: "
				+jsonData.length+" educapp notifications incoming.");
    	Intent intent = new Intent(context, MainActivity.class);
    	intent.setAction(MainActivity.NOTIFICATION_LIST_ACTION);
    	intent.putExtra(ServiceConstants.NOTIFICATION_EXTRA, jsonData);
		PendingIntent pIntent = PendingIntent.getActivity(
				context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification noti = new Notification.Builder(context)
	        .setContentTitle("You got "+jsonData.length+" notifications.")
	        .setContentText("Click to read.")
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        .build();
		
	    NotificationManager notificationManager = 
	    		(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    // hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;
	
	    notificationManager.notify(0, noti);
	}
    
}
