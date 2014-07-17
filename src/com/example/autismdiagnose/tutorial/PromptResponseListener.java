package com.example.autismdiagnose.tutorial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.autismdiagnose.app.VideoUI;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;

public class PromptResponseListener implements OnShowcaseEventListener {
	Activity activity;
	
	public PromptResponseListener(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
		// TODO Auto-generated method stub
		Bundle bundle = activity.getIntent().getExtras();
		boolean fromHelp = false;
		
		// Preference to verify user has completed this tutorial
		SharedPreferences prefs = activity.getSharedPreferences(
				  "com.example.autismdiagnose", 
				  Context.MODE_PRIVATE);
		prefs.edit().putBoolean("COMPLETED", true).commit();
		
		if (bundle != null) {
			fromHelp = bundle.getBoolean("HELP");
		}
		
		Intent videoUI = new Intent(activity, VideoUI.class);
		
		if (fromHelp) {
			videoUI.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		}
		
		activity.startActivity(videoUI);
		activity.finish();
	}

	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {
		// TODO Auto-generated method stub
		
	}

}
