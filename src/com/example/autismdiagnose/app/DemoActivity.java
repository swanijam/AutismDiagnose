package com.example.autismdiagnose.app;

import com.example.autismdiagnose.R;
import com.example.autismdiagnose.android_helpers.SpinningCircle;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 @author Sheik Hassan
 * @version 0.0.1
 *
 * This Class is concerned with showing a tutorial of the app using several pictures.
 */
public class DemoActivity extends Activity {

	Button Start;
	SpinningCircle Sc;
	SharedPreferences Prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_demo);
		//setUpVideoView(this);
		
		Bundle bundle = getIntent().getExtras();
		Prefs = this.getSharedPreferences(
				  "com.example.autismdiagnose", 
				  Context.MODE_PRIVATE);

		boolean completedTutorial = Prefs.getBoolean("COMPLETED", false);
		
		if (completedTutorial && bundle == null) {
			switchToPagerTutorial(DemoActivity.this);
		}
		// If the user is using the app for the first time
		else {
			final EditText emailAddress = (EditText) findViewById(R.id.email_address);
			final Button confirm = (Button) findViewById(R.id.confirm);
			confirm.setEnabled(false);
			
			// Upon clicking confirm, the user is sent to the tutorial
			confirm.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					Prefs.edit().putString("EMAIL_ADDRESS", emailAddress.getText().toString()).commit();
					switchToPagerTutorial(DemoActivity.this);
				}
			});
			
			// Verify the email when the user types it in. If the email is valid
			// then the confirm button is enabled. Else, the button cannot be clicked.
			emailAddress.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable finalText) {
					if (isValidEmail(finalText.toString())) {
						confirm.setEnabled(true);
						Log.v("Button", "enabled");
					}
					else {
						Log.v("Button", "disabled");
						confirm.setEnabled(false);
					}	
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {}
				@Override
				public void onTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {}
			});
		}	
	}

	public boolean isValidEmail(String target) {
	    if (target == null) {
	    	return false;
	    } else {
	    	return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	} 
	
	public static void switchToPagerTutorial(Activity activity) {
		Intent pagerTutorial = new Intent(activity, PagerTutorialActivity.class);
		activity.startActivity(pagerTutorial);
		activity.finish();
	}

	public void switchToVideoAndDestroy() {
		if (Prefs != null) {
			Prefs.edit().putBoolean("COMPLETED", true).commit();
		}
		
		Intent videoUI = new Intent(this, VideoUI.class);
		Intent fromIntent = getIntent();
		
		if (fromIntent.getBooleanExtra("HELP", false)) {
			videoUI.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		}
		startActivity(videoUI);
		finish();
	}
}
