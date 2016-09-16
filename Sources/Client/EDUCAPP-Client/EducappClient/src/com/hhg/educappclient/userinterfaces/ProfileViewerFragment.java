package com.hhg.educappclient.userinterfaces;

import android.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.models.UserPublicProfile;
import com.hhg.educappclient.service.ServiceConstants;
import com.hhg.educappclient.service.ServiceConstants.ExtraCodes;
import com.hhg.educappclient.service.ServiceMessageBuilder;

/**
 * UI controller to show an user data.
 * 
 * This fragment should actually request the profile image from
 * the server, plus all the additional non-basic or bandwidth
 * intensive data, and allow the user to modify his/hers own profile.
 * 
 * @author Harold Hormaechea Garcia
 *
 */
public class ProfileViewerFragment extends Fragment {
	private String TAG = getClass().getSimpleName();
	private Context context;
	private MainActivity activity;
	private UserPublicProfile profile;
	private int profileId = -1;
	private View view;
	/**
	 * Messenger used to send back messages with results
	 * to this activity.
	 */
	final Messenger localMessenger = new Messenger(new IncomingHandler());
	
	public ProfileViewerFragment(){}
	
	/**
	 * Constructor used to see other people profiles.
	 * 
	 * @param context
	 * @param activity
	 * @param profile
	 * @param isSelfProfile
	 */
	public ProfileViewerFragment(Context context, MainActivity activity,
			UserPublicProfile profile){
		this.context = context;
		this.activity = activity;
		this.profile = profile;
	}
	
	/**
	 * Constructor used to see other people profiles.
	 * 
	 * @param context
	 * @param activity
	 * @param profile
	 * @param isSelfProfile
	 */
	public ProfileViewerFragment(Context context, MainActivity activity,
			int profileId){
		this.context = context;
		this.activity = activity;
		this.profileId = profileId;
	}
	
	/**
	 * Constructor used to see own profile.
	 * 
	 * @param context
	 * @param activity
	 * @param profile
	 * @param isSelfProfile
	 */
	public ProfileViewerFragment(Context context, MainActivity activity){
		this.context = context;
		this.activity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("ProfileViewerFragment", "onCreateView()");
		view = inflater.inflate(R.layout.profile_layout, null);
		setRetainInstance(true);
		populateView((MainActivity)getActivity());
		
		return view;
	}
	
	private void populateView(MainActivity activity){
		TextView firstName = (TextView) view.findViewById(R.id.profile_first_name);
		TextView lastName = (TextView) view.findViewById(R.id.profile_last_name);
		TextView idnumber = (TextView) view.findViewById(R.id.profile_id_number);
		TextView bioText = (TextView) view.findViewById(R.id.profile_bio_content);
		if(profile != null){
			//If we already have a profile object stored here,
			//we will access it.
			firstName.setText(profile.getFirstName());
			lastName.setText(profile.getLastName());
			idnumber.setText(profile.getNif());
			if(profile.getProfileDescription() != null &&
					profile.getProfileDescription().length() > 0){
				bioText.setText(profile.getProfileDescription());
			}
			
		}else if(profile == null && profileId < 0){
			//In this case, we will load the logged user profile,
			//as we are not given neither a valid profile ID nor
			//have a previously provided profile.
			try {
				activity.getServiceMessenger().send(
						ServiceMessageBuilder.CreateRetrieveUserProfileMessage(
								localMessenger));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}else if(profile == null && profileId >= 0){
			try {
				activity.getServiceMessenger().send(
						ServiceMessageBuilder.CreateRetrieveUserProfileMessage(
								localMessenger, profileId));
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
        	Log.e(TAG, "Handling message in profile fragment: "+msg.what);
        	if(msg.what == ServiceConstants.RequestTypes.PROFILE_REQUEST){
        		String result = msg.getData()
        				.getString(ExtraCodes.OPERATION_RESULT);
        		if(ExtraCodes.STATUS_OK.equals(result)){
        			String jsonData = msg.getData().getString(ServiceConstants.PROFILE_EXTRA);
        			profile = new Gson().fromJson(jsonData, UserPublicProfile.class);
        			populateView((MainActivity)getActivity());
        		}else{
        			Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
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
