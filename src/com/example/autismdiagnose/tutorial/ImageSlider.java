package com.example.autismdiagnose.tutorial;

import java.util.TimerTask;
import java.util.Vector;

import com.example.autismdiagnose.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class ImageSlider{

	public int [] images;
	public int slideNum = 0;
	public Activity activity;

	public ImageSlider(Activity activity) {
		this.activity = activity;
		images = new int [] {R.id.slide_2, R.id.slide_3, R.id.slide_4, R.id.slide_5, 
				 R.id.slide_6, R.id.slide_7, R.id.slide_8, R.id.slide_9, R.id.slide_10, R.id.slide_1};
	}
	
	public void switchImage(final Context context, final ImageView fromView, final ImageView toView) {
		
		CountDownTimer timer = new CountDownTimer(2000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {}
			
			// Fade out the current picture and fade in the next slide
			@Override
			public void onFinish() {
				Animation FadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
				fromView.startAnimation(FadeOut);
				fromView.setVisibility(View.GONE);
				
				Animation FadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
				toView.startAnimation(FadeIn);
				toView.setVisibility(View.VISIBLE);
				
				// Recursion to continue the slide show
				if (slideNum + 1 < images.length) {
					ImageView fromView = (ImageView) activity.findViewById(images[slideNum]);
					ImageView toView = (ImageView) activity.findViewById(images[slideNum + 1]);
					
					slideNum ++;
					switchImage(context, fromView, toView);
				}
				else {
					Button finish = (Button) activity.findViewById(R.id.startTut);
					finish.setVisibility(View.VISIBLE);
					finish.setText("Replay");
					activity.findViewById(R.id.finish).setVisibility(View.VISIBLE); 
				}
			}
		};
		
		timer.start();
	}
	
	public void reset() {
		slideNum = 0;
		// hide the # 2 slide that is not included in the array.
		for (int i=0; i< images.length - 1; i++) {
			activity.findViewById(images[i]).setVisibility(View.GONE);
		}
	}
}
