package com.example.autismdiagnose.android_helpers;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import com.example.autismdiagnose.R;

public class PromptAnimations {

	Context context;
	
	public PromptAnimations(Context context) {
		this.context = context;
	}
	
	public void animateNotifyStart(final TextView notify, final boolean shouldSlideDown) {
		// Animate "Please say your child's name" to visible
		notify.setVisibility(View.VISIBLE);
		
		final Animation SlideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
			SlideDown.setFillAfter(true);
		Animation SlideIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
			SlideIn.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation){}
				@Override
				public void onAnimationRepeat(Animation animation){}					
				@Override
				public void onAnimationEnd(Animation animation) {					
					if (shouldSlideDown) {
						notify.startAnimation(SlideDown);
					}
				}
			});
			SlideIn.setFillAfter(true);
		notify.startAnimation(SlideIn);		
	}
	
	public void dismissNotify(final TextView notify, boolean shouldFadeOut) {
		Animation notifyDismiss;
		if (!shouldFadeOut) {
			notifyDismiss = AnimationUtils.loadAnimation(context, R.anim.notify_hide);
			notify.startAnimation(notifyDismiss);
			notify.setVisibility(View.INVISIBLE);
		}
		else {
			notifyDismiss = AnimationUtils.loadAnimation(context, R.anim.fade_out);
		}
		notify.startAnimation(notifyDismiss);
		notify.setVisibility(View.INVISIBLE);
	}
}
