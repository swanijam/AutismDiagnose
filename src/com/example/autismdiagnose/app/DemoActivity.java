package com.example.autismdiagnose.app;

import com.example.autismdiagnose.R;
import com.example.autismdiagnose.android_helpers.SpinningCircle;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author Sheperd
 *
 */
public class DemoActivity extends Activity implements 
OnShowcaseEventListener {

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
		
		Bundle bundle = getIntent().getExtras();
		SharedPreferences prefs = this.getSharedPreferences(
				  "com.example.autismdiagnose", 
				  Context.MODE_PRIVATE);

		boolean completedTutorial = prefs.getBoolean("COMPLETED", false);
		
		if (completedTutorial && bundle == null) {
			switchToVideoAndDestroy();
		}
		else {
			RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	        int margin = ((Number) (getResources().getDisplayMetrics().density * 20)).intValue();
	        lps.setMargins(margin, margin, margin, margin);
			
			ViewTarget target = new ViewTarget(R.id.startTrial, this);
			tutorial = new TutorialHelper(this, lps);
			
			tutorial.createStartButtonHelp(target, this);
			tutorial.showHelp();
			
			start = (Button) findViewById(R.id.startTrial);
			sc = (SpinningCircle) findViewById(R.id.spinningcircle);
		}
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
	
	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {}
	@Override
	public void onShowcaseViewDidHide(ShowcaseView showcaseView) {}
	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {}
}
