package com.example.autismdiagnose;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_screen);
		Bundle bundle = getIntent().getExtras();
		
		// If we are starting on this activity
		if (bundle == null) {
			switchToVideoAndDestroy();
		}
		else if (bundle.getBoolean("HELP") == true) {
			View back = (View) findViewById(R.id.back);
			back.setVisibility(View.VISIBLE);
			
			Button backbtn = (Button) findViewById(R.id.backButton);
				backbtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						switchToVideoAndDestroy();
					}
				});
			
			Button tutorial = (Button) findViewById(R.id.tutButton);
				tutorial.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						switchToTutorials();
					}
				});
		}
		
	}

	public void switchToVideoAndDestroy() {
		Intent videoUI = new Intent(this, VideoUI.class);
		videoUI.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(videoUI);
		finish();
	}
	
	public void switchToTutorials() {
		Intent videoUI = new Intent(this, Tutorial.class);
		startActivity(videoUI);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

}
