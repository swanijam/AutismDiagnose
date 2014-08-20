package com.example.autismdiagnose.app;

import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.provider.Settings.Secure;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.autismdiagnose.R;
import com.example.autismdiagnose.android_helpers.FileProcessor;
import com.example.autismdiagnose.android_helpers.SpinningCircle;
import com.example.autismdiagnose.video_helper.AndroidPreviewRecorder;
import com.example.autismdiagnose.video_helper.Response;
import com.example.autismdiagnose.video_helper.TimerController;



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
	TextView Notify;
	TextView StartTrialMessage;
	
	private Button Help;
	private Button Start;
	
	private VideoView VideoView;
	private SurfaceHolder Holder;
	private Camera Cam;
	private MediaRecorder Recorder;
	
	// Animation that shows how much time is remaining before the next notify message
	private SpinningCircle spinningcircle;
	private static TimerController TimerController;
	
	private AndroidPreviewRecorder PreviewRecorder;
	
	// Response to ask the user if the child responded (Yes, No)
	private Builder ResponseBuilder;
		
	private static final int RECORDINGLIMIT = 5000;
	
	// Every new trial prompts the user 3 times to call the childs name
	// and 3 times to input if he/she responded or not. This variable is
	// reset at the beginning of a new trial.
	private static int TrialNumber=0;
	
	// A User may upload several videos. This variable keeps track of
	// how many videos the user has uploaded so far in order to properly
	// name the videos uploaded to the server.
	private int VideoNumber;
	
	private boolean FinishedTrial = false;
	
	// Stores start time and response time information as well as the
	// location of the video file for each trial.
	private static Response videoData;

	private static final String CLASSTAG = "VideoUI";
	private String PhoneNumber;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video_ui);
		
		// The user must have a valid phone number to use this App.
		// They will provide the Phone number to the research co-ordinator
		// and the video trials will be identified by the unique phone number
		// of the participant.
		phoneNumberCheck();
		
		// The user must have the internet on. If not, quit the application
		networkCheck();
		
		// Retrieve UI elements
		// "Please Say Your Child's Name" message
		Notify = (TextView) findViewById(R.id.notify);
		
		// Help text for start trial button
		StartTrialMessage = (TextView) findViewById(R.id.startTrialMessage);
		StartTrialMessage.setVisibility(View.VISIBLE);
		
		// Message displayed when "Start Trial" button is disabled
		TextView disableMessage = (TextView) findViewById(R.id.disableMessage);
		
		TextView restart = (TextView) findViewById(R.id.restartMessage);
		
		Help = (Button) findViewById(R.id.help);
		Start = (Button) findViewById(R.id.startTrial);
		VideoView = (VideoView) findViewById(R.id.videoView1);
		
		// Set up the videoView
		Holder = VideoView.getHolder();
		Holder.addCallback(this);
		PreviewRecorder = new AndroidPreviewRecorder(Cam, Recorder);
		
		ResponseBuilder = new Builder(this);
		spinningcircle = (SpinningCircle) findViewById(R.id.spinningcircle);
		
		
		// Initialize the Timer that will control the various
		// of the trial process.
		TimerController = new TimerController(
						   getApplicationContext(), 
						   spinningcircle, 
						   RECORDINGLIMIT, Start);
		
		TimerController.setTextView("notify", Notify);
		TimerController.setTextView("startTrialMessage", StartTrialMessage);
		TimerController.setTextView("disableMessage", disableMessage);
		TimerController.setTextView("restart", restart);
		
		AlertDialog response = setResponseDialogListener();
		AlertDialog waitout = setWaitOutDialogListener();
		TimerController.setDialog("response", response);
		TimerController.setDialog("waitout", waitout);
	}
	
	public void onClick(View view) {
		
		if(view.getId() == Start.getId())
			start();
		else if (view.getId() == Help.getId()) {
			Intent tutscreen = new Intent(this, DemoActivity.class);
			tutscreen.putExtra("HELP", true);
			startActivity(tutscreen);
		}
	}
	
	public void start() {
		Start.setVisibility(View.GONE);
		Help.setVisibility(View.GONE);
		
		TrialNumber = 0;
		FinishedTrial = false;
		
		// Start a new recording session
		String outputFile = getFilesDir() + "/trials0.mp4";
		videoData = new Response(outputFile);
		
		PreviewRecorder.record(this, Holder, 
							   outputFile, 
							   MediaRecorder.AudioSource.DEFAULT, 
							   MediaRecorder.VideoSource.CAMERA, 
							   CamcorderProfile.QUALITY_LOW);
		
		TimerController.startCountDownResponseTimer(TrialNumber);
		spinningcircle.setVisibility(View.VISIBLE);
	}
	
	
	// Method for Killing the application completely. Done if the Internet connection is not on.
	public void killApp() {
		this.finish();
		android.os.Process.killProcess( android.os.Process.myPid() ); 
	}
	
	public void stop() {
		PreviewRecorder.stopRecorder();
		TimerController.stopTimer();
		
		Start.setVisibility(View.VISIBLE);
		Help.setVisibility(View.VISIBLE);
		spinningcircle.setVisibility(View.GONE);
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
			PreviewRecorder.startCameraPreview(this, holder);
		}
		catch (IOException e) {
			Log.v(CLASSTAG, "Could not start the preview");
				e.printStackTrace();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Notify.setVisibility(View.GONE);
	
		// When paused, release the recorder and delete the video file
		if (!PreviewRecorder.isNull()) {
			stop();
		}
		else PreviewRecorder.releaseRecorder();
		
		FileProcessor.DeleteFile(getFilesDir() + "/trials0.mp4");
			
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// Always Check that user is connected to the Internet
		networkCheck();
		
		try {
			PreviewRecorder.initializeCamera(this);
			Log.v(CLASSTAG, "In Resumed");
		}
		catch(Exception e) {
			Builder failed = new Builder(this);
			failed.setMessage("Failed to Initialize Camera. Please check that your camera is working!");
			failed.setPositiveButton("Ok", null);
			failed.show();
		}
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
			stop();
			FileProcessor.DeleteFile(getFilesDir() + "/trials0.mp4");
			videoData = new Response(null);
			videoData.setNoResponse(true);
			PreviewRecorder.addVideoData(videoData);
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
	private AlertDialog setResponseDialogListener() {
		DialogInterface.OnClickListener responseListener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (which == Dialog.BUTTON_POSITIVE) {
					TimerController.stopTimer();
					TrialNumber += 1;
					PreviewRecorder.addVideoData(videoData);
					TimerController.showDelayAfterFinishTimer();
					FinishedTrial = true;
				}
				else if (which == Dialog.BUTTON_NEGATIVE) {
					if (TrialNumber < 3){
						spinningcircle.setVisibility(View.VISIBLE);
						TrialNumber += 1;
						TimerController.startCountDownResponseTimer(TrialNumber);
					}
				}
				else if(which == Dialog.BUTTON_NEUTRAL) {
					stop();
					TimerController.stopTimer();
					TimerController.showRestartMessage();
					Log.v("NU", "NUTREAL");
				}
				
				if (TrialNumber == 1) {
					videoData.setTrial_1_time();
				}
				else if (TrialNumber == 2) {
					videoData.setTrial_2_time();
				}
				else if (TrialNumber == 3) {
					spinningcircle.setVisibility(View.GONE);
					videoData.setTrial_3_time();
					PreviewRecorder.addVideoData(videoData);
					FinishedTrial = true;
					TimerController.showDelayAfterFinishTimer();
				}
				
				if (FinishedTrial) {
					stop();
					
					SharedPreferences shp = getSharedPreferences("com.example.autismdiagnose.app", 
																 Context.MODE_PRIVATE);
					
					// The number of videos the usler records is stored in this variable in
					// SharedPrefrences.
					VideoNumber = shp.getInt("VIDEONUMBER", 0);
					VideoNumber ++;
					shp.edit().putInt("VIDEONUMBER", VideoNumber).commit();
					
					new UploadFile().execute(PhoneNumber + "_" + VideoNumber, videoData.getPath());
				}
		}
		};
		
		// Show the dialog and determine if there was a response
		ResponseBuilder.setMessage("Was there a response? \n\t(answer within 5 seconds)")
		.setPositiveButton("Yes", responseListener);
		ResponseBuilder.setNegativeButton("No", responseListener);
		ResponseBuilder.setNeutralButton("Discard", responseListener);
	
		return ResponseBuilder.create();
	}
	
	private AlertDialog setWaitOutDialogListener() {
		ResponseBuilder = new Builder(this);
		ResponseBuilder.setMessage("We're Sorry! You did not respond so we discarded your trial!")
		.setPositiveButton("Ok! Let's Retry", null);
			return ResponseBuilder.create();
		}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}
	
	// Method to check if the Internet is available
	// Taken from www.stackoverflow.com
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private void networkCheck() {
		// The user must have the internet on. If not, quit the application
		if (!isNetworkAvailable()) {
			Builder builder = new Builder(this);
			builder.setMessage("You are not connected to the Internet!");
			builder.setPositiveButton("Ok", null);
			
			AlertDialog error = builder.create();
			error.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(final DialogInterface dialog) {
					killApp();
				}
			});
			
			error.show();
		}
	}
	
	/**
	 * This Method attempts to retrieve the phoneNumber and handles the case
	 * in which a phoneNumber does not exist.
	 */
	private void phoneNumberCheck() {
		// Attempt to get the phone number of this user.
		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		PhoneNumber = tMgr.getLine1Number();
		
		if (PhoneNumber == null) {
			Builder builder = new Builder(this);
			builder.setMessage("A Valid Phone Number is required for you to use this app.\n" +
							   "This is how your Trials will be identified. Please obtain \n" +
								"a valid phone number.");
			builder.setPositiveButton("Ok", null);
			AlertDialog error = builder.create();
			error.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(final DialogInterface dialog) {
					killApp();
				}
			});
		}
	}
	
	private class UploadFile extends AsyncTask<String, Integer, String> {
	
	     protected String doInBackground(String ... params) {
			 String fileName = params[0];
			 String filePath = params[1];
			 Start.setText("Uploading ...");
			 
			 try {
				 FileProcessor.Upload(fileName, filePath);
			 } catch(Exception e) {
				 Start.setText("Start");
				 StartTrialMessage.setVisibility(View.VISIBLE);
				 Builder failed = new Builder(VideoUI.this.getBaseContext());
				 failed.setMessage("Your Upload could not go through! Make sure the Internet is on and please retry the Trial.");
				 failed.setPositiveButton("Ok", null);
				 failed.show();
			 }
			 
	    	 return filePath;
	     }

	     protected void onPostExecute(String filePath) {
	    	 Start.setText("Start");
	 		 StartTrialMessage.setVisibility(View.VISIBLE);
	    	 TimerController.dismissDelayAfterFinish();
	    	 
	    	 Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
	    	 // Delete the File from the device
	    	 try {
	    		 File file = new File(filePath);
	    		 file.delete();
	    	 } catch (Exception e) {}
	     }
	 }
}