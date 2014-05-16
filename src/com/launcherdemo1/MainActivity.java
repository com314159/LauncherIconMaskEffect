package com.launcherdemo1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

public class MainActivity extends Activity {
	private AppHelper mAppHelper;
	
	private GridView mAppGridView;
	private Button mButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		findViews();
		setEventListeners();
	}

	@Override
	protected void onResume() {
		mAppHelper.loadApps();
		super.onResume();
	}

	private void init() {
		mAppHelper = new AppHelper(this);
	}
	
	private void findViews() {
		mAppGridView = (GridView) findViewById(R.id.app_gridview);
		mButton = (Button) findViewById(R.id.toandroid);
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory("android.intent.category.HOME");
				MainActivity.this.startActivity(intent);
			}
		});
	}
	
	private void setEventListeners() {
		mAppGridView.setAdapter(mAppHelper.getAppAdapter());
		mAppGridView.setOnItemClickListener(mAppHelper.getAppOnItemClickListener());
	}
}
