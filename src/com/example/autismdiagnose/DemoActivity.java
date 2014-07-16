package com.example.autismdiagnose;

import com.example.autismdiagnose.androidHelpers.SpinningCircle;
import com.example.autismdiagnose.tutorial.TimerTutorialListener;
import com.example.autismdiagnose.tutorial.TutorialHelper;
import com.example.autismdiagnoses.videoHelper.VideoPreviewActivity;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DemoActivity extends VideoPreviewActivity implements 
SurfaceHolder.Callback, OnShowcaseEventListener {

	ShowcaseView sv;
	TutorialHelper tutorial;
	Button start;
	SpinningCircle sc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		setUpVideoView(this);
		
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

	public void onClick(View view) {
		if (view.getId() == start.getId()){
			tutorial.hideHelp();
			start.setVisibility(View.GONE);
			sc.setVisibility(View.VISIBLE);
			
			ViewTarget target = new ViewTarget(R.id.spinningcircle, this);
			tutorial.createTimerHelp(target, new TimerTutorialListener(this));
			tutorial.showHelp();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		startCamera(this);
	}
	
	@Override 
	public void onPause() {
		super.onPause();
		handlePause();
	}
	
	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {}
	@Override
	public void onShowcaseViewDidHide(ShowcaseView showcaseView) {}
	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {}
	@Override
	public void start() {}
	@Override
	public void stop() {}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}

}
