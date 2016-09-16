package com.hhg.educappclient.userinterfaces;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hhg.educappclient.MainActivity;
import com.hhg.educappclient.R;
import com.hhg.educappclient.rest.EducappAPI;
import com.hhg.educappclient.utilities.Constants.PRIVILEGES_DEFINITIONS;

/**
 * Item which holds all the data for every drawer item
 * the user can see in the drawerlayout.
 * 
 * @author Harold Hormaechea Garcia
 *
 */
public class DrawerItem{
	private String stringResource;	
	private Fragment fragment;
	private WeakReference<Context> context;
	private PRIVILEGES_DEFINITIONS intentTargetValue;
	
	private DrawerItem(Intent intent, String readableString,
			PRIVILEGES_DEFINITIONS intentTargetValue, Context context){
		this.stringResource = readableString;
		this.context = new WeakReference<Context>(context);
		this.intentTargetValue = intentTargetValue;
	}
	
	@Override
	public String toString(){
		return stringResource;
	}
	
	/**
	 * Generates or retrieves (if already existing)
	 * the associated fragment for this item.
	 * @return
	 */
	public Fragment getFragment(MainActivity activity){
		if (fragment==null){
			switch(intentTargetValue){
			case AUTH_ALTER_SELF_PROFILE:
				fragment = new ProfileViewerFragment(context.get(), activity);
				break;
			case AUTH_COURSES_LIST:
				fragment = new CourseListFragment(context.get(), activity);
				break;
			case AUTH_USERS_LIST:
				fragment = new UserListFragment(context.get(), activity);
				break;
			case AUTH_RETRIEVE_SELF_ALERTS:
				fragment = new NotificationListFragment(activity);
				break;
			default:
				fragment = null;
				break;
			}
		}		
		return fragment;
	}
	
	/**
	 * Generates a DrawerItem list so the listAdapter for these objects
	 * can handle them.
	 * 
	 * DrawerItems will only be created for those operations directly 
	 * accessible through the drawer layout.
	 * 
	 * @param context
	 * @param stringAuthDefinition
	 * @return
	 */
	public static DrawerItem[] Builder(Context context, String[] stringAuthDefinition){
		ArrayList<DrawerItem> list = new ArrayList<DrawerItem>();
		for(String auth : stringAuthDefinition){
			DrawerItem item = DrawerItem.generateDrawerItem(context, auth);
			if(item != null){
				list.add(item);
			}
		}
		return list.toArray(new DrawerItem[0]);
	}
	
	private static DrawerItem generateDrawerItem(Context context, String stringAuthDefinition){
		Log.i(DrawerItem.class.getSimpleName(), "generateDrawerItem()");
		DrawerItem item = null;
		Intent intent = null;
		PRIVILEGES_DEFINITIONS intentTargetValue = null;
		String readableResource = null;

		Log.d(DrawerItem.class.getSimpleName(), "Attempting to generate draweritem for: "
												+stringAuthDefinition);
		switch(stringAuthDefinition){
		case EducappAPI.AUTH_COURSES_LIST:
			readableResource = context.getResources().
			getString(R.string.drawer_access_courses_list);
			intentTargetValue = PRIVILEGES_DEFINITIONS.valueOf(stringAuthDefinition);
			break;
		case EducappAPI.AUTH_ALTER_SELF_PROFILE:
			readableResource = context.getResources().
			getString(R.string.drawer_access_profile);
			intentTargetValue = PRIVILEGES_DEFINITIONS.valueOf(stringAuthDefinition);
			break;
		case EducappAPI.AUTH_USERS_LIST:
			readableResource = context.getResources().
			getString(R.string.drawer_access_users_list);
			intentTargetValue = PRIVILEGES_DEFINITIONS.valueOf(stringAuthDefinition);
			break;
		case EducappAPI.AUTH_RETRIEVE_SELF_ALERTS:
			readableResource = context.getResources().
			getString(R.string.drawer_access_notifications);
			intentTargetValue = PRIVILEGES_DEFINITIONS.valueOf(stringAuthDefinition);
			break;
		default:
			break;
		}
		if(intentTargetValue != null){
			item = new DrawerItem(intent, readableResource, intentTargetValue, context);
		}
		return item;
	}
}
