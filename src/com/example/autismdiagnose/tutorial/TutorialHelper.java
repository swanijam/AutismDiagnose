package com.example.autismdiagnose.tutorial;

import android.app.Activity;
import android.graphics.Color;
import android.widget.RelativeLayout;

import com.example.autismdiagnose.R;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class TutorialHelper {
	ShowcaseView sv;
	Activity a;
	RelativeLayout.LayoutParams lps;
	
	public TutorialHelper(Activity activity,
			RelativeLayout.LayoutParams layout) {
		a = activity;
		lps = layout;
	}
	
	public void createStartButtonHelp(ViewTarget target, OnShowcaseEventListener listen) {
		
		sv = new ShowcaseView.Builder(a, true)
	        .setTarget(target)
	        .setStyle(R.style.AppBaseTheme)
	        .setContentTitle("Starting A Trial")
	        .setContentText("Click this button when you are ready to start a new " +
	        				"Trial. Aim the camera at your child and then click the button.\n\n" +
	        			    "Try clicking the button now!")
	        .setShowcaseEventListener(listen)
        .build();
		
		sv.setButtonPosition(lps);
		sv.hideButton();
	}
	
	public void createTimerHelp(ViewTarget target, OnShowcaseEventListener listen) {
		sv = new ShowcaseView.Builder(a, true)
	        .setTarget(target)
	        .setStyle(R.style.AppBaseTheme)
	        .setContentTitle("The Timer")
	        .setContentText("After you click 'Start Trial' you will be prompted to " +
	        				"say your child's name. When this timer finishes winding down \n " +
	        			    "you will be prompted to enter if your child responded or not. \n\n" +
	        				"Click OK to continue with the Tutorial.")
	        .setShowcaseEventListener(listen)
        .build();
		
		sv.setButtonPosition(lps);
	}
	
	public void createNotifyHelp(ViewTarget target, OnShowcaseEventListener listen) {
		sv = new ShowcaseView.Builder(a, true)
	        .setTarget(target)
	        .setStyle(R.style.AppBaseTheme)
	        .setContentTitle("Notifications")
	        .setContentText("When you see this notification, say your child's name ONCE. \n" +
	        				"Then wait for a response from your child and enter if he/she responded " + 
	        				"in the next prompt that will be shown. \n\n" +
	        				"Click OK to continue with the Tutorial.")
	        .setShowcaseEventListener(listen)
        .build();
	
		sv.setButtonPosition(lps);
	}
	
	public void createResponsePromptHelp(ViewTarget target, OnShowcaseEventListener listen) {
		sv = new ShowcaseView.Builder(a, true)
	        .setTarget(target)
	        .setStyle(R.style.AppBaseTheme)
	        .setContentTitle("Did Your Child Respond")
	        .setContentText("After you have finished saying your child's name and the Timer winds down," +
	        				"this prompt will ask you to enter if your child responded or not. \n" + 
	        				"Press 'YES' if your child responded.\n" +
	        				"Press 'NO' if your child did not respond. \n" +
	        				"Press 'DISCARD' if you want to quit the trial and/or retry. \n\n\n" +
	        				"Click OK to finish with the Tutorial and record a trial.")
	        .setShowcaseEventListener(listen)
        .build();
		
		sv.setButtonPosition(lps);
		sv.setButtonText("Finish");
	}
	
	
	public void showHelp() {
		sv.show();
	}
	
	public void hideHelp() {
		sv.hide();
	}
	
	
}
