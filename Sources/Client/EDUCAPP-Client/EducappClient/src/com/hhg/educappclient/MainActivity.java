package com.hhg.educappclient;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hhg.educappclient.service.NetworkOperationsService;
import com.hhg.educappclient.service.ServiceConstants;
import com.hhg.educappclient.userinterfaces.CreditsFragment;
import com.hhg.educappclient.userinterfaces.DrawerAdapter;
import com.hhg.educappclient.userinterfaces.DrawerItem;
import com.hhg.educappclient.userinterfaces.NotificationListFragment;
import com.hhg.educappclient.userinterfaces.ProfileViewerFragment;


public class MainActivity extends Activity {
	private final static int LOGIN_INTENT_CODE = 3004;
	public final static String NOTIFICATION_LIST_ACTION = "-3141";
	private final String TAG = getClass().getName();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
	protected Messenger serviceMessenger;
	protected boolean isBound;
	private boolean isLoggedIn = false;
	private String[] authStrings; //Here for convenience.
	private String userName;//Here for convenience.
	private Fragment currentDisplayedFragment;
	/**
	 * Messenger used to send back messages from the
	 * service to this activity.
	 */
	final Messenger localMessenger = new Messenger(new IncomingHandler());
    
    
    private ServiceConnection serviceConnection= new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			Log.d(getClass().getSimpleName(), "Service connected!");
			serviceMessenger = new Messenger(binder);
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.d(getClass().getSimpleName(), "Service disconnected!");
			isBound = false;
		}
		
	};
	
	
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	setContentView(R.layout.main_layout_with_drawer);
        Intent callingIntent = getIntent();
        if(NOTIFICATION_LIST_ACTION.equals(callingIntent.getAction())){
        	//We force the drawer initial generation. We will enqueue its
        	//actual proper building by asking for the current user auths.
    		switchFragment(new NotificationListFragment(this, callingIntent
    				.getStringArrayExtra(ServiceConstants.NOTIFICATION_EXTRA)));
        	isLoggedIn = true;
        	
        }else if(isLoggedIn == false){
            //If there is no user logged in through the service,
            //we will show the login/signup screen. Otherwise
            //we will TODO: load the current user configuration.
        	Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, LOGIN_INTENT_CODE);
        }
    }

	private void initDrawer(String[] authArray){
    	if(mDrawerLayout == null)
    		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    	
    	if(authArray == null || authArray.length == 0){
    		return;
    	}
    	//We populate the header.
    	//TODO: Images, like always...
    	((TextView)findViewById(R.id.drawer_head_text)).setText(userName);
    	
    	mDrawerList = (ListView) findViewById(R.id.left_drawer);
    	
    	DrawerAdapter adapter = new DrawerAdapter(this);
    	adapter.populateDrawerAdapterList(authArray);
    	mDrawerList.setAdapter(adapter);
    	mDrawerList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				DrawerItem item = (DrawerItem) mDrawerList.getAdapter()
									.getItem(position);
				currentDisplayedFragment = item.getFragment(MainActivity.this);
				MainActivity.this.switchFragment(currentDisplayedFragment);
				mDrawerLayout.closeDrawers();
			}
    	});
    	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == LOGIN_INTENT_CODE &&resultCode == Activity.RESULT_OK){
			authStrings = data.getStringArrayExtra("PRIVILEGES");
			userName = data.getStringExtra(ServiceConstants.USER_EXTRA);
			Log.d(TAG, "ONACTIVITYRESULT IN MAINACTIVITY with "+(authStrings.length));
			initDrawer(authStrings);
			isLoggedIn = true;
			switchFragment(new ProfileViewerFragment(getApplicationContext(), this));
		}else{
			finish(); //Exit on back press from login.
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
        Intent intent = new Intent(this, NetworkOperationsService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onBackPressed() {
	    if (getFragmentManager().getBackStackEntryCount() > 1) {
	        getFragmentManager().popBackStack();
	    } else if(getFragmentManager().getBackStackEntryCount() <= 1
	    		&& !(currentDisplayedFragment instanceof CreditsFragment)){
	    	currentDisplayedFragment = new CreditsFragment();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
	        tx.replace(R.id.content_frame,currentDisplayedFragment);
	        tx.commit();
		}else{
			finish();
	    }
	}
	
	@Override
	public void onStop(){
		unbindService(serviceConnection);
		super.onStop();
	}

	public Messenger getServiceMessenger() {
		Log.e(getClass().getSimpleName(), "1 GetServiceMessenger is null..."+(serviceMessenger == null));
		if(serviceMessenger == null){
			Intent intent = new Intent(this, NetworkOperationsService.class);
	        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		}
		
		Log.e(getClass().getSimpleName(), "1 GetServiceMessenger is null..."+(serviceMessenger == null));
		return serviceMessenger;
	}

	public boolean isBound() {
		return isBound;
	}
	
	
	
	public String[] getAuthStrings() {
		return authStrings;
	}

	public void setAuthStrings(String[] authStrings) {
		this.authStrings = authStrings;
	}

	public void switchFragment(Fragment fragment){
		currentDisplayedFragment = fragment;
		FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame,fragment);
        tx.addToBackStack(null); //So it can be reversed on "back button" override
        tx.commit();
	}
	
	/**
	 * Handler of incoming messages from clients.
	 */
	class IncomingHandler extends Handler {
		/**
		 * In this class, the only notification we can receive
		 * which will trigger an update is the GET_AUTH_LIST
		 * one, required when we want to load the notification
		 * list from an alert message. So... dirty hack.
		 */
		@Override
		public void handleMessage(Message msg) {
			if(msg.what != ServiceConstants.RequestTypes.GET_AUTH_LIST){
				//If this doesn't come from a known request,
				//it is of no use to this activity. It'd be a
				//sign of a bug.
				Log.e(TAG, "Unknown message received with code: "+msg.what);
				super.handleMessage(msg);
				return;
			}else{
				Bundle data = msg.getData();
				String operationResult = data.getString(
						ServiceConstants.ExtraCodes.OPERATION_RESULT);
				if(ServiceConstants.ExtraCodes.STATUS_OK.equals(operationResult)){
					authStrings = data.getStringArray(
							ServiceConstants.ExtraCodes.EXTRA_PRIVILEGES_LIST);
					initDrawer(authStrings);
				}
			}
		}
	}
}


