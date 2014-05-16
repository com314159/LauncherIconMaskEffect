package com.launcherdemo1;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AppHelper {
	private Context mContext;

	private List<ResolveInfo> mAppResolveInfoList = new ArrayList<ResolveInfo>();

	public AppHelper(Context context) {
		mContext = context;
	}

	public void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mAppResolveInfoList = new ArrayList<ResolveInfo>();
		List<ResolveInfo> list = mContext.getPackageManager()
				.queryIntentActivities(mainIntent, 0);
		for (int i = 0; i < list.size(); ++i) {
			if (!mAppResolveInfoList.contains(list.get(i))) {
				mAppResolveInfoList.add(list.get(i));
			}
		}
		// mAppResolveInfoList =
		// mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
		mAppAdapter.notifyDataSetChanged();
	}

	public BaseAdapter getAppAdapter() {
		return mAppAdapter;
	}

	public OnItemClickListener getAppOnItemClickListener() {
		return mAppOnItemClickListener;
	}

	public void openAppAt(int position) {
		ResolveInfo appInfo = mAppResolveInfoList.get(position);
		ComponentName component = new ComponentName(
				appInfo.activityInfo.packageName, appInfo.activityInfo.name);
		Intent intent = new Intent();
		intent.setComponent(component);
		mContext.startActivity(intent);
	}

	private BaseAdapter mAppAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return mAppResolveInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAppResolveInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View appItemLayout;
			if (convertView == null) {
				appItemLayout = ((LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.gridview_item, null);
			} else {
				appItemLayout = convertView;
			}

			ResolveInfo appInfo = mAppResolveInfoList.get(position);
			Drawable appIcon =  appInfo.activityInfo
					.loadIcon(mContext.getPackageManager());

			String appName = appInfo.loadLabel(mContext.getPackageManager())
					.toString();

			IconImageView appIconImageView = (IconImageView) appItemLayout
					.findViewById(R.id.appicon_imageview);
			TextView appNameTextView = (TextView) appItemLayout
					.findViewById(R.id.appname_textview);

			appIconImageView.setImageDrawable(appIcon);
			appNameTextView.setText(appName);
			return appItemLayout;
		}
	};

	private OnItemClickListener mAppOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			openAppAt(position);
		}
	};
	
}
