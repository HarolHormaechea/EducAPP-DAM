package com.hhg.educappclient.userinterfaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hhg.educappclient.R;

/**
 * Adapter implementation for the DrawerLayout
 * view the user can navigate to access his/her
 * authorized operations.
 * 
 * @author Harold Hormaechea Garcia
 *
 */
public class DrawerAdapter extends BaseAdapter {
	//We use an array because once logged in,
	//the number of "allowed" operations for
	//the user will not change. If the users
	//privileges are added from the server side,
	//it will not be updated until the next
	//login, but an error message should be shown.
	//If more privileges are added server-side to
	//that user, we will not update it.
	private DrawerItem[] drawerItemList = null;
	
	//Context reference will be required to inflate
	//element layouts and to generate Intents for
	//each allowed operation.
	private Context context;
	
	public DrawerAdapter(Context context){
		this.context = context;
	}
	
	/**
	 * Forces the generation/regeneration of this adapter
	 * list of items.
	 * 
	 * @param authList
	 */
	public void populateDrawerAdapterList(String[] authList){
		this.drawerItemList = DrawerItem.Builder(context, authList);
	}
	
	@Override
	public int getCount() {
		return drawerItemList.length;
	}

	@Override
	public Object getItem(int arg0) {
		return drawerItemList[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return (drawerItemList[arg0]).hashCode();
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
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.drawer_item, null);
			
			//And we set a ViewHolder for it (to improve
			//responsiveness the next time the user scrolls
			//through this)
			viewHolder = new ViewHolderItem();
			viewHolder.ItemText = (TextView) view.findViewById(R.id.drawer_item_text);
			view.setTag(viewHolder);
		}
		
		//Step2: Initialize all the values on this view.
		DrawerItem drawerItem = this.drawerItemList[index];
		viewHolder.ItemText.setText(drawerItem.toString());
		
		
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
		TextView ItemText;
	}
}
