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
	
	
	public void showHelp() {
		sv.show();
	}
	
	public void hideHelp() {
		sv.hide();
	}
	
	
}
