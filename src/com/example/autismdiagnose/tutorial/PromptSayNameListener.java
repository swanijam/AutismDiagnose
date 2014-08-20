package com.example.autismdiagnose.tutorial;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.autismdiagnose.R;
import com.example.autismdiagnose.android_helpers.PromptAnimations;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class PromptSayNameListener implements  OnShowcaseEventListener {

	Activity activity;
	
	public PromptSayNameListener(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
		TextView notify = (TextView) activity.findViewById(R.id.notify);
		PromptAnimations pm = new PromptAnimations(activity.getApplicationContext());
		pm.dismissNotify(notify, true);
		
	
		// Hide spinning circle
		activity.findViewById(R.id.spinningcircle).setVisibility(View.GONE);
		
		// layout to place the button
		RelativeLayout.LayoutParams lps = new 
				RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        
		TutorialHelper th = new TutorialHelper(activity, lps);
		
	}

	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {
		// TODO Auto-generated method stub
		
	}

}
