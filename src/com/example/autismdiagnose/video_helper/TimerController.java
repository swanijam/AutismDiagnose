package com.example.autismdiagnose.video_helper;

import java.util.HashMap;
import com.example.autismdiagnose.R;
import com.example.autismdiagnose.android_helpers.PromptAnimations;
import com.example.autismdiagnose.android_helpers.SpinningCircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
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
	
	private Context Context;
	private CountDownTimer CurrentTimer;
	private CountDownTimer DelayTimer;
	private SpinningCircle SpinningCircle;
	private PromptAnimations AnimatePrompt;
	
	private Button start;

	private int TrialNumber;
	private int RecordingLimit;
	private final int DIALOG_DISMISS_DELAY = 7000;
	// Dictionary to store the several textViews that are used.
	private HashMap<String, TextView> TEXTVIEWS = new HashMap<String, TextView>();
	
	// Dictionary to store the several alert dialogs that are used.
	private HashMap<String, AlertDialog> DIALOGS = new HashMap<String, AlertDialog>();
	
	// Public constructor requires all the components that will be used
	public TimerController(Context Context,
			      SpinningCircle TimerAnimation, int RecordingLimit, 
			      Button start) {
		
		this.Context = Context;
		this.SpinningCircle = TimerAnimation;
		this.start = start;
		this.RecordingLimit = RecordingLimit;
		this.AnimatePrompt = new PromptAnimations(Context);
		
		
	}
	
	private void dismissDialog() {
		DelayTimer = new CountDownTimer(DIALOG_DISMISS_DELAY, 1000) {
			@Override
			public void onTick(long arg0){}
			@Override
			public void onFinish() {
				DIALOGS.get("response").
					getButton(DialogInterface.BUTTON_NEUTRAL).performClick();
				DIALOGS.get("waitout").show();
			}
		};
		DelayTimer.start();
	}
	
	public void cancelDelayTimer() {
		if (DelayTimer != null)
			DelayTimer.cancel();
	}
	
	
	/**
	 * NOTE: You must set a response dialog, and "notify" textView for this method to function.
	 * 
	 * @param trialNumber (NOTE: You must set a 'response' dialog for this method to function)
	 * @return true if the timer was successfully started.
	 */
	public boolean startCountDownResponseTimer(int currentTrialNumber) {
		cancelDelayTimer();
		hideAllTextView();

		TrialNumber = currentTrialNumber;
		if (TrialNumber == 3) {
			return false;
		}
		
		// Start a new timer everytime this method is called.
		if (CurrentTimer != null) {
			Log.v("canceled Timer", "true");
			CurrentTimer.cancel();
		}
		
		// The notification slides in, waits for 2 seconds, then
		// leaves the screen to reveal the spinning circle.
		
		AnimatePrompt.animateNotifyStart(TEXTVIEWS.get("notify"));
		CurrentTimer = new CountDownTimer(2000, 1000) {		
			@Override
			public void onTick(long millisUntilFinished) {}		
			@Override
			public void onFinish() {
				AnimatePrompt.dismissNotify(TEXTVIEWS.get("notify"), false);
				Log.v("I Got", "Now I'm GOING AWAY SHIT");
				startResponseTimer();
			}
		}.start();
		
		return true;
	}
	
	public void startResponseTimer() {
		CurrentTimer.cancel();
		CurrentTimer = new CountDownTimer(RecordingLimit, 1) {
			@Override
			public void onTick(long mills) {
				SpinningCircle.invalidate();
			}
			@Override
			public void onFinish() {
				try {
					// Show prompt and reset the spinning circle animation
					DIALOGS.get("response").setCanceledOnTouchOutside(false);
					DIALOGS.get("response").show();
					dismissDialog();
					SpinningCircle.arcwidth = 360;
					SpinningCircle.setVisibility(View.INVISIBLE);
				}
				catch(Exception e) {}
			}
		}.start();
		
	}
	
	public void dismissDelayAfterFinish() {
		start.setEnabled(true);
		
		Animation FadeOut = AnimationUtils.loadAnimation(Context, R.anim.fade_out);
		TEXTVIEWS.get("disableMessage").startAnimation(FadeOut);
		TEXTVIEWS.get("disableMessage").setVisibility(View.GONE);
	}
	
	public void showRestartMessage() {
		if (CurrentTimer != null) {
			CurrentTimer.cancel();
		}
		
		TEXTVIEWS.get("restart").setVisibility(View.VISIBLE);
		Animation FadeIn = AnimationUtils.loadAnimation(Context, R.anim.fade_in);
		TEXTVIEWS.get("restart").startAnimation(FadeIn);
		start.setEnabled(false);
		CurrentTimer = new CountDownTimer(5000, 1000) {
			@Override
			public void onTick(long mills) {
			}
			@Override
			public void onFinish() {				
				Animation FadeOut = AnimationUtils.loadAnimation(Context, R.anim.fade_out);
				TEXTVIEWS.get("restart").startAnimation(FadeOut);
				TEXTVIEWS.get("restart").setVisibility(View.GONE);
				TEXTVIEWS.get("startTrialMessage").setVisibility(View.VISIBLE);
				start.setEnabled(true);
			}
		}.start();
	}
	
	public void stopTimer() {
		if (CurrentTimer != null)
			CurrentTimer.cancel();
		cancelDelayTimer();
	}
	
	public void hideAllTextView() {
		TEXTVIEWS.get("startTrialMessage").setVisibility(View.GONE);
		for (String view: TEXTVIEWS.keySet()) {
			TEXTVIEWS.get(view).setVisibility(View.GONE);
			Log.v("view", view);
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
