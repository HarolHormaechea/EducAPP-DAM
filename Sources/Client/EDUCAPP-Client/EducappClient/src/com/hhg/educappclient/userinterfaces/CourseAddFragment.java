package com.hhg.educappclient.userinterfaces;

import java.util.Calendar;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.service.ServiceConstants;
import com.hhg.educappclient.service.ServiceConstants.ExtraCodes;
import com.hhg.educappclient.service.ServiceMessageBuilder;
import com.hhg.educappclient.utilities.CustomDateDeserializerSerializer;

public class CourseAddFragment extends Fragment {
	private String TAG = getClass().getSimpleName();
	private Context context;
	private MainActivity activity;
	
	/**
	 * Messenger used to send back messages with results
	 * to this activity.
	 */
	final Messenger localMessenger = new Messenger(new IncomingHandler());
	
	private EditText tag, name, description, maxStudents;
	DatePicker endDate;
	DatePicker beginDate;
	private Button okButton;
	
	
	public CourseAddFragment(Context context, MainActivity activity){
		this.context = context;
		this.activity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("CourseAddFragment", "onCreateView()");
		
		//We report to Android this fragment
		//will use a custom menu.
		setHasOptionsMenu(true); 
		View v = inflater.inflate(R.layout.course_add_layout, null);
		okButton = (Button) v.findViewById(R.id.course_add_confirm_button);
		tag = (EditText) v.findViewById(R.id.course_add_tag);
		name = (EditText) v.findViewById(R.id.course_add_name);
		description = (EditText) v.findViewById(R.id.course_add_description);
		maxStudents = (EditText) v.findViewById(R.id.course_add_max_students);
		beginDate = (DatePicker) v.findViewById(R.id.course_add_begin_date);
		endDate = (DatePicker) v.findViewById(R.id.course_add_end_date);
		
		okButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Calendar date0 = Calendar.getInstance();
				date0.set(	beginDate.getYear(),
						  	beginDate.getMonth(),
						  	beginDate.getDayOfMonth());
				Calendar date1 = Calendar.getInstance();
				date1.set(	endDate.getYear(),
							endDate.getMonth(),
							endDate.getDayOfMonth());
				
				Message outMessage = ServiceMessageBuilder
						.createAddNewCourseMessage(
								localMessenger,
								tag.getText().toString(),
								name.getText().toString(),
								description.getText().toString(),
								Integer.valueOf(maxStudents.getText().toString()),
								CustomDateDeserializerSerializer
									.dateFormatter.format(date0.getTime()),
								CustomDateDeserializerSerializer
									.dateFormatter.format(date1.getTime()));
				try {
					activity.getServiceMessenger().send(outMessage);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (NumberFormatException a){
					Toast.makeText(context,
							activity.getResources().getString(R.string.course_add_number_error),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		return v;
	}
	/**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {

		@Override
        public void handleMessage(Message msg) {
        	Log.e(TAG, "Handling message in add course: "+msg.what);
        	if(msg.what == ServiceConstants.RequestTypes.COURSE_ADD){
        		String result = msg.getData()
        				.getString(ExtraCodes.OPERATION_RESULT);
        		if(ExtraCodes.STATUS_OK.equals(result)){
        			result = getResources().getString(R.string.results_ok);
        			activity.switchFragment(new CourseListFragment(context, activity));
        		}else{
        			Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        		}
        		Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
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
