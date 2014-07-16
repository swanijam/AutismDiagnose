package com.example.autismdiagnose.tutorial;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.example.autismdiagnose.R;

public class TimerTutorialListener implements  OnShowcaseEventListener {
	Activity activity;
	
	public TimerTutorialListener(Activity ac) {
		activity = ac;
	}
	
	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {}

	@Override
	public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
		TextView sayName = (TextView) activity.findViewById(R.id.notify);
		sayName.setVisibility(View.VISIBLE);
	}

	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {}

}
