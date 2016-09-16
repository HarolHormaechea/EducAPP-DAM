package com.hhg.educappclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hhg.educappclient.service.NetworkOperationsService;
import com.hhg.educappclient.service.ServiceConstants;
import com.hhg.educappclient.service.ServiceMessageBuilder;

public class LoginActivity extends Activity {
	private final String TAG = getClass().getSimpleName();
	private int reqValue;
	private Messenger serviceMessenger;
	private boolean isBound;
	private EditText username;
	private ServiceConnection serviceConnection= new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			serviceMessenger = new Messenger(binder);
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}
		
	};
	/**
	 * Messenger used to send back messages with login results
	 * to this activity.
	 */
	final Messenger activityMessenger = new Messenger(new IncomingHandler());
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        
        
        username = (EditText) findViewById(R.id.login_username);
        final EditText password = (EditText) findViewById(R.id.login_password);
        Button login = (Button) findViewById(R.id.login_login_button);
        login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String user = username.getText().toString();
				String pwd = password.getText().toString();
				if(		user != null && user.length() > 0 
						&& pwd != null && pwd.length() > 0){
					
					Message outgoingMessage = ServiceMessageBuilder
							.CreateLoginMessage(user, pwd, activityMessenger);
					try {
						serviceMessenger.send(outgoingMessage);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(LoginActivity.this,
							R.string.results_invalid_user,
							Toast.LENGTH_SHORT).show();
				}
			}
        });
    }
	
	@Override
	public void onStart(){
		super.onStart();
		Intent intent = new Intent(this, NetworkOperationsService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onStop(){
		unbindService(serviceConnection);
		super.onStop();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	public void loginOK(String[] authStrings){
		Log.d(TAG, "loginOK");
		Intent data = new Intent();
		data.putExtra("PRIVILEGES", authStrings);
		data.putExtra(ServiceConstants.USER_EXTRA, username.getText().toString());
        setResult(Activity.RESULT_OK, data);
        finish();
	}
	
	/**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	if(msg.what != ServiceConstants.RequestTypes.LOGIN){
        		//If this doesn't come from a login request,
        		//it is of no use to this activity. It'd be a
        		//sign of a bug.
        		Log.e(TAG, "Unknown message received with code: "+msg.what);
        		super.handleMessage(msg);
        		return;
        	}
        	
        	Bundle data = msg.getData();
        	String operationResult = data.getString(
        			ServiceConstants.ExtraCodes.OPERATION_RESULT);
        	if(ServiceConstants.ExtraCodes.STATUS_OK.equals(operationResult)){
        		Toast.makeText(LoginActivity.this,
						R.string.results_ok,
						Toast.LENGTH_SHORT).show();
				String[] authStrings = data.getStringArray(
						ServiceConstants.ExtraCodes.EXTRA_PRIVILEGES_LIST);
				for(String s : authStrings){
					Log.d(TAG, s);
				}
				loginOK(authStrings);
        	}else if(ServiceConstants.ExtraCodes.STATUS_BAD_CREDENTIALS.equals(operationResult)){
        		Toast.makeText(LoginActivity.this,
        				R.string.results_invalid_user,
        				Toast.LENGTH_SHORT).show();
        		Log.e(TAG, "Bad credentials during login attempt: "+operationResult);
        	}else if(ServiceConstants.ExtraCodes.STATUS_CONNECTION_ERROR.equals(operationResult)){
        		Toast.makeText(LoginActivity.this,
        				R.string.results_connection_error,
        				Toast.LENGTH_SHORT).show();
        		Log.e(TAG, "Connection error during login attempt: "+operationResult);
        	}else{
        		Toast.makeText(LoginActivity.this,
        				String.valueOf(operationResult),
        				Toast.LENGTH_SHORT).show();
        		Log.e(TAG, "Unknown error ocurred during login: "+operationResult);
        	}
        }
    }
		
        
}

