package com.example.autismdiagnose.app;

import com.example.autismdiagnose.R;
import com.example.autismdiagnose.android_helpers.SpinningCircle;
import com.example.autismdiagnose.tutorial.ImageSlider;
import com.example.autismdiagnose.tutorial.TimerTutorialListener;
import com.example.autismdiagnose.tutorial.TutorialHelper;
import com.example.autismdiagnose.video_helper.VideoPreviewActivity;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author Sheperd
 *
 */
public class DemoActivity extends Activity {

	ShowcaseView sv;
	TutorialHelper tutorial;
	Button start;
	SpinningCircle sc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_demo);
		//setUpVideoView(this);
		
		/*Bundle bundle = getIntent().getExtras();
		SharedPreferences prefs = this.getSharedPreferences(
				  "com.example.autismdiagnose", 
				  Context.MODE_PRIVATE);

		boolean completedTutorial = prefs.getBoolean("COMPLETED", false);
		
		if (completedTutorial && bundle == null) {
			switchToVideoAndDestroy();
		}
		else {
		}
		*/
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

	public void startSlides(View v) {
		v.setVisibility(View.GONE);
		ImageSlider is = new ImageSlider(this);
		is.reset();
		is.switchImage(getApplicationContext(), 
				(ImageView) findViewById(R.id.slide_1), (ImageView) findViewById(R.id.slide_2));
	}
	
	public void onClick(View view) {
		if (view.getId() == start.getId()) {
			tutorial.hideHelp();
			start.setVisibility(View.GONE);
			sc.setVisibility(View.VISIBLE);
			
			ViewTarget target = new ViewTarget(R.id.spinningcircle, this);
			tutorial.createTimerHelp(target, new TimerTutorialListener(this));
			tutorial.showHelp();
		}
	}

	public void switchToVideoAndDestroy() {
		Intent videoUI = new Intent(this, VideoUI.class);
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
