package com.example.autismdiagnose;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.autismdiagnose.R;
import com.example.autismdiagnose.androidHelpers.AndroidPreviewRecorder;
import com.example.autismdiagnose.androidHelpers.Response;
import com.example.autismdiagnose.androidHelpers.SpinningCircle;

/**
 * @author Sheik Hassan
 * @version 0.0.1
 *
 * This Class is main screen that a user will see when starting the App after completing the
 * tutorials. It provides the functionality to record a trial while guiding the user through
 * the various steps (prompting to call the child's name, asking for the response etc).
 * 
 * After the conclusion of the trial, the video is uploaded to a aws server for analysis.
 */

public class VideoUI extends Activity implements
SurfaceHolder.Callback, OnClickListener, OnInfoListener, OnErrorListener {
	
	// "Please Say Your Child's Name" message
	private TextView notify;
	// Message displayed when "Start Trial" button is disabled
	private TextView disableMessage;
	
	private Button help;
	private Button start;

	private VideoView videoView;
	private SurfaceHolder holder;
	private Camera cam;
	private MediaRecorder recorder;
	
	// Animation that shows how much time is remaining before the next notify message
	private SpinningCircle spinningcircle;
	private static CountDownTimer timer;
	
	private AndroidPreviewRecorder previewRecorder;
	
	// Response to ask the user if the child responded (Yes, No)
	private Builder response;
		
	private static final int RECORDINGLIMIT = 5000;
	
	// Every new trial prompts the user 3 times to call the childs name
	// and 3 times to input if he/she responded or not. This variable is
	// reset at the beginning of a new trial.
	private static int TRIAL_NUMBER=0;
	// Stores start time and response time information as well as the
	// location of the video file for each trial.
	private static Response videoData;
	
	private static final String CLASSTAG = "VideoUI";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video_ui);
     
		// Retrieve UI elements
		notify = (TextView) findViewById(R.id.notify);
		disableMessage = (TextView) findViewById(R.id.disableMessage);
		help = (Button) findViewById(R.id.help);
		start = (Button) findViewById(R.id.startTrial);
		videoView = (VideoView) findViewById(R.id.videoView1);
 		
		// Set up the videoView
		holder = videoView.getHolder();
 		holder.addCallback(this);
 		previewRecorder = new AndroidPreviewRecorder(cam, recorder);
 		
 		response = new Builder(this);
 		setDialogListeners();
 		spinningcircle = (SpinningCircle) findViewById(R.id.spinningcircle);
 	}
	
	public void onClick(View view) {
		
		if(view.getId() == start.getId())
			start();
		else if (view.getId() == help.getId()) {
			Intent tutscreen = new Intent(this, MainScreen.class);
			tutscreen.putExtra("HELP", true);
			startActivity(tutscreen);
		}
	}
	
	public void start() {
		start.setVisibility(View.GONE);
		help.setVisibility(View.GONE);
	
		TRIAL_NUMBER = 0;
		
		// Start a new recording session
		String outputFile = getFilesDir() + "/trials0.mp4";
		videoData = new Response(outputFile);
		
		previewRecorder.record(this, holder, 
							   outputFile, 
							   MediaRecorder.AudioSource.DEFAULT, 
							   MediaRecorder.VideoSource.CAMERA, 
							   CamcorderProfile.QUALITY_LOW);
		startTimer();
		spinningcircle.setVisibility(View.VISIBLE);
	}
	
	public void stop() {
		previewRecorder.stopRecorder();
		timer.cancel();
		
		start.setVisibility(View.VISIBLE);
		help.setVisibility(View.VISIBLE);
		spinningcircle.setVisibility(View.GONE);
	}
	
	/** 
	 * Starts the timer that shows a prompt.
	 * When the user clicks 'Start Trial', every 5 seconds,
	 * they are prompted to enter if the child responded or not.
	*/
	
	public boolean startTimer() {
		
		if (TRIAL_NUMBER == 3) {
			return false;
		}
		
		// Start a new timer everytime this method is called.
		if (timer != null) {
			Log.v("canceled Timer", "true");
			timer.cancel();
		}
		
		// Animate "Please say your child's name" to visible
		notify.setVisibility(View.VISIBLE);
		Animation FadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
		notify.startAnimation(FadeIn);
		
		timer = new CountDownTimer(RECORDINGLIMIT, 1) {
			@Override
			public void onTick(long mills) {
				spinningcircle.invalidate();
			}
			@Override
			public void onFinish() {
				try {
					Animation FadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
					notify.startAnimation(FadeOut);
					notify.setVisibility(View.INVISIBLE);
					
					// Show prompt and reset the spinning circle animation
					response.show();
					spinningcircle.arcwidth = 360;
					spinningcircle.setVisibility(View.INVISIBLE);
				}
				catch(Exception e) {}
			}
		}.start();
		
		return true;
	}
	
	
	/**
	 * This method sets the camera preview on the surface holder
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(CLASSTAG, "Got here");
		// Once the holder is set up, we can plop the camera preview
		// onto it.
		try {
			previewRecorder.startCameraPreview(this, holder);
		}
		catch (IOException e) {
			Log.v(CLASSTAG, "Could not start the preview");
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}

	@Override
	public void onPause() {
		super.onPause();
		notify.setVisibility(View.GONE);

		// When paused, release the recorder and delete the video file
		if (!previewRecorder.isNull()) {
			stop();
		}
		else previewRecorder.releaseRecorder();
		
		try {
			File f = new File(getFilesDir() + "/trials0.mp4");
			f.delete();
		}catch(Exception e){}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//stop.setEnabled(false);
		previewRecorder.initializeCamera(this);
		Log.v(CLASSTAG, "In Resumed");
	}
	
	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub
		Log.v(CLASSTAG, "errored out!");
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			previewRecorder.stopRecorder();
		}		
		Log.v(CLASSTAG, "recording!!");
	}
	
	/**
	 * Sets the listeners for the prompt and handles the responses from the
	 * user.
	 * Question: Did your child respond?
	 * Possible Answers:
	 * YES ---> Stops the recording and concludes the trial(s).
	 * RESTART ---> Stops the recording and discards the current trial.
	 * NO ---> Continues with a new trial.
	 */
	public void setDialogListeners() {
		DialogInterface.OnClickListener responseListener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (which == Dialog.BUTTON_POSITIVE) {
					stop();
					timer.cancel();
					TRIAL_NUMBER += 1;
					previewRecorder.addVideoData(videoData);
					showDelay();
				}
				else if (which == Dialog.BUTTON_NEGATIVE) {
					if (TRIAL_NUMBER < 3){
						spinningcircle.setVisibility(View.VISIBLE);
						TRIAL_NUMBER += 1;
						startTimer();
					}
				}
				else if(which == Dialog.BUTTON_NEUTRAL) {
					stop();
					timer.cancel();
				}
				
				if (TRIAL_NUMBER == 1) {
					videoData.setTrial_1_time();
				}
				else if (TRIAL_NUMBER == 2) {
					videoData.setTrial_2_time();
				}
				else if (TRIAL_NUMBER == 3) {
					spinningcircle.setVisibility(View.GONE);
					videoData.setTrial_3_time();
					previewRecorder.addVideoData(videoData);
					stop();
					showDelay();
				}
				
				Log.v("Click", Integer.toString(which));
			}
		};
		
		// Show the dialog and determine if there was a response
		response.setMessage("Was there a response?").setPositiveButton("Yes", responseListener);
		response.setNegativeButton("No", responseListener);
		response.setNeutralButton("Restart", responseListener);
		
	}
	
	/**
	 * Method to disable the 'Start Trial' Button for 10 seconds after
	 * has successfully completed the trial. 
	 */
	public void showDelay() {
		start.setEnabled(false);
		disableMessage.setVisibility(View.VISIBLE);
		Animation FadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
		disableMessage.startAnimation(FadeIn);
		
		if (timer != null) {
			timer.cancel();
		}
		
		timer = new CountDownTimer(10000, 1000) {
			@Override
			public void onTick(long mills) {
			}
			
			@Override
			public void onFinish() {
				start.setEnabled(true);
				
				Animation FadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
				disableMessage.startAnimation(FadeOut);
				disableMessage.setVisibility(View.GONE);
			}
		}.start();
	}// end showDelay
}

