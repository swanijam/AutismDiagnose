package com.example.autismdiagnose.app;

import com.example.autismdiagnose.R;
import com.example.autismdiagnose.android_helpers.SpinningCircle;
import com.example.autismdiagnose.tutorial.ImageSlider;
import com.example.autismdiagnose.tutorial.TimerTutorialListener;
import com.example.autismdiagnose.tutorial.TutorialHelper;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

/**
 @author Sheik Hassan
 * @version 0.0.1
 *
 * This Class is concerned with showing a tutorial of the app using several pictures.
 */
public class DemoActivity extends Activity {

	ShowcaseView Sv;
	TutorialHelper Tutorial;
	Button Start;
	SpinningCircle Sc;
	SharedPreferences Prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_demo);
		//setUpVideoView(this);
		
		Bundle bundle = getIntent().getExtras();
		Prefs = this.getSharedPreferences(
				  "com.example.autismdiagnose", 
				  Context.MODE_PRIVATE);

		boolean completedTutorial = Prefs.getBoolean("COMPLETED", false);
		
		if (completedTutorial && bundle == null) {
			switchToVideoAndDestroy();
		}
		else {
			Button startTut = (Button) findViewById(R.id.startTut);
			Button finishTut = (Button) findViewById(R.id.finish);
			
			startTut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startSlides(v);
				}
			});
			
			finishTut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					switchToVideoAndDestroy();
				}
			});
		}
	}

	public void startSlides(View v) {
		v.setVisibility(View.GONE);
		findViewById(R.id.finish).setVisibility(View.GONE);
		ImageSlider is = new ImageSlider(this);
		is.reset();
		is.switchImage(getApplicationContext(), 
				(ImageView) findViewById(R.id.slide_1), (ImageView) findViewById(R.id.slide_2));
	}
	
	public void onClick(View view) {
		if (view.getId() == Start.getId()) {
			Tutorial.hideHelp();
			Start.setVisibility(View.GONE);
			Sc.setVisibility(View.VISIBLE);
			
			ViewTarget target = new ViewTarget(R.id.spinningcircle, this);
			Tutorial.createTimerHelp(target, new TimerTutorialListener(this));
			Tutorial.showHelp();
		}
	}

	public void switchToVideoAndDestroy() {
		if (Prefs != null) {
			Prefs.edit().putBoolean("COMPLETED", true).commit();
		}
		
		Intent videoUI = new Intent(this, VideoUI.class);
		Intent fromIntent = getIntent();
		
		if (fromIntent.getBooleanExtra("HELP", false)) {
			videoUI.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		}
		startActivity(videoUI);
		finish();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//startCamera(this);
	}
	
	@Override 
	public void onPause() {
		super.onPause();
		//handlePause();
	}
}
