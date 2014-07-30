package com.example.autismdiagnose.tutorial;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.example.autismdiagnose.R;
import com.example.autismdiagnose.android_helpers.PromptAnimations;

public class TimerTutorialListener implements  OnShowcaseEventListener {
	Activity activity;
	
	public TimerTutorialListener(Activity ac) {
		activity = ac;
	}
	
	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {}

	@Override
	public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
		TextView notify = (TextView) activity.findViewById(R.id.notify);
		PromptAnimations pm = new PromptAnimations(activity.getApplicationContext());
		pm.animateNotifyStart(notify);
		
		// Hide spinning circle
		activity.findViewById(R.id.spinningcircle).setVisibility(View.GONE);
		
		// layout to place the button
		RelativeLayout.LayoutParams lps = new 
				RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        
		TutorialHelper th = new TutorialHelper(activity, lps);
		ViewTarget target = new ViewTarget(R.id.notify, activity);
		th.createNotifyHelp(target, new PromptSayNameListener(activity));
	}

	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {}

}
