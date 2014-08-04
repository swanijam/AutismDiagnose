package com.example.autismdiagnose.app;

import com.example.autismdiagnose.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
		SharedPreferences prefs = this.getSharedPreferences(
								  "com.example.autismdiagnose", 
								  Context.MODE_PRIVATE);
		
		boolean completedTutorial = prefs.getBoolean("COMPLETED", false);
		
		Button tutorial = (Button) findViewById(R.id.tutButton);
		tutorial.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// If we are starting on this activity
		if (completedTutorial == true) {
			switchToVideoAndDestroy();
		}
		
		boolean SWITCHED_FROM_HELP;
		if (bundle == null) {
			SWITCHED_FROM_HELP = false;
		}
		else {
			SWITCHED_FROM_HELP = bundle.getBoolean("HELP");
		}
		
	}

	public void switchToVideoAndDestroy() {
		Intent videoUI = new Intent(this, VideoUI.class);
		videoUI.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(videoUI);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

}
