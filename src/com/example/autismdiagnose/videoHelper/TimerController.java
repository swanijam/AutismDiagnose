package com.example.autismdiagnose.videoHelper;

import java.util.HashMap;
import com.example.autismdiagnose.R;
import com.example.autismdiagnose.androidHelpers.PromptAnimations;
import com.example.autismdiagnose.androidHelpers.SpinningCircle;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Sheik Hassan
 * This class controls the various timers that are used throughout the app.
 * It displays dialogs and animates TextViews depending on the timer it is asked
 * to run.
 */


public class TimerController {
	
	private Context context;
	private CountDownTimer currentTimer;
	private CountDownTimer delayTimer;
	private SpinningCircle spinningcircle;
	private PromptAnimations animateprompt;
	
	private Button start;
	
	private int TRIAL_NUMBER;
	private int RECORDING_LIMIT;
	private final int DIALOG_DISMISS_DELAY = 7000;
	// Dictionary to store the several textViews that are used.
	private HashMap<String, TextView> TEXTVIEWS = new HashMap<String, TextView>();
	
	// Dictionary to store the several alert dialogs that are used.
	private HashMap<String, AlertDialog> DIALOGS = new HashMap<String, AlertDialog>();
	
	// Public constructor requires all the components that will be used
	public TimerController(Context context,
			      SpinningCircle TimerAnimation, int RECORDING_LIMIT, 
			      Button start) {
		
		this.context = context;
		this.spinningcircle = TimerAnimation;
		this.start = start;
		this.RECORDING_LIMIT = RECORDING_LIMIT;
		this.animateprompt = new PromptAnimations(context);
		
		
	}
	
	private void dismissDialog() {
		delayTimer = new CountDownTimer(DIALOG_DISMISS_DELAY, 1000) {
			@Override
			public void onTick(long arg0){}
			@Override
			public void onFinish() {
				DIALOGS.get("response").getButton(DialogInterface.BUTTON_NEUTRAL).performClick();
				DIALOGS.get("waitout").show();
			}
		};
		delayTimer.start();
	}
	
	public void cancelDelayTimer() {
		if (delayTimer != null)
			delayTimer.cancel();
	}
	
	
	/**
	 * NOTE: You must set a 'response' dialog, and "notify" textView for this method to function.
	 * 
	 * @param trialNumber (NOTE: You must set a 'response' dialog for this method to function)
	 * @return true if the timer was successfully started.
	 */
	public boolean startCountDownResponseTimer(int trialNumber) {
		cancelDelayTimer();

		TRIAL_NUMBER = trialNumber;
		if (TRIAL_NUMBER == 3) {
			return false;
		}
		
		// Start a new timer everytime this method is called.
		if (currentTimer != null) {
			Log.v("canceled Timer", "true");
			currentTimer.cancel();
		}
		
		animateprompt.animateNotifyStart(TEXTVIEWS.get("notify"));
		
		currentTimer = new CountDownTimer(RECORDING_LIMIT, 1) {
			@Override
			public void onTick(long mills) {
				spinningcircle.invalidate();
			}
			@Override
			public void onFinish() {
				try {
					animateprompt.dismissNotify(TEXTVIEWS.get("notify"));
					
					// Show prompt and reset the spinning circle animation
					DIALOGS.get("response").show();
					dismissDialog();
					SpinningCircle.arcwidth = 360;
					spinningcircle.setVisibility(View.INVISIBLE);
				}
				catch(Exception e) {}
			}
		}.start();
		
		return true;
	}

	/**
	 * Method to disable the 'Start Trial' Button for 10 seconds after
	 * has successfully completed the trial.
	 * NOTE: textView "disableMessage" must be set to use this method.
	 */
	public void showDelayAfterFinishTimer() {
		start.setEnabled(false);
		TEXTVIEWS.get("disableMessage").setVisibility(View.VISIBLE);
		Animation SlideIn = AnimationUtils.loadAnimation(context, R.anim.slide_left);
		TEXTVIEWS.get("disableMessage").startAnimation(SlideIn);
		
		if (currentTimer != null) {
			currentTimer.cancel();
		}
		
		currentTimer = new CountDownTimer(10000, 1000) {
			@Override
			public void onTick(long mills) {
			}
			@Override
			public void onFinish() {
				start.setEnabled(true);
				
				Animation FadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
				TEXTVIEWS.get("disableMessage").startAnimation(FadeOut);
				TEXTVIEWS.get("disableMessage").setVisibility(View.GONE);
			}
		}.start();
	}// end showDelay
	
	public void showRestartMessage() {
		if (currentTimer != null) {
			currentTimer.cancel();
		}
		
		TEXTVIEWS.get("restart").setVisibility(View.VISIBLE);
		Animation FadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
		TEXTVIEWS.get("restart").startAnimation(FadeIn);
		start.setEnabled(false);
		currentTimer = new CountDownTimer(1500, 1000) {
			@Override
			public void onTick(long mills) {
			}
			@Override
			public void onFinish() {				
				Animation FadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
				TEXTVIEWS.get("restart").startAnimation(FadeOut);
				TEXTVIEWS.get("restart").setVisibility(View.GONE);
				start.setEnabled(true);
			}
		}.start();
	}
	
	public void stopTimer() {
		if (currentTimer != null)
			currentTimer.cancel();
		cancelDelayTimer();
	}
	
	public void hideAllTextView() {
		for (String view: TEXTVIEWS.keySet()) {
			TEXTVIEWS.get(view).setVisibility(View.GONE);
		}
	}
	
	
	public void setDialog(String name, AlertDialog dialog) {
		DIALOGS.put(name, dialog);
	}
	
	public void setTextView(String name, TextView view) {
		TEXTVIEWS.put(name, view);
	}
	
	public AlertDialog getDialog(String name) {
		return DIALOGS.get(name);
	}
	
	public TextView getTextView(String name) {
		return TEXTVIEWS.get(name);
	}	
}
