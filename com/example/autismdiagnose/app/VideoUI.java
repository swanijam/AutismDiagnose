package com.example.autismdiagnose.app;

import java.io.File;
import java.util.ArrayList;

import org.joda.time.DateTime;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.example.autismdiagnose.R;
import com.example.autismdiagnose.android_helpers.SpinningCircle;
import com.example.autismdiagnose.android_helpers.Util;
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
SurfaceHolder.Callback, android.view.View.OnClickListener, OnInfoListener, OnErrorListener {
		
		// "Please Say Your Child's Name" message
	TextView Notify;
	
	private Button Help;
	private Button Start;
	private ProgressBar progress;
	
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
		
	private static final int RECORDINGLIMIT = 3000;
	
	// Every new trial prompts the user 3 times to call the childs name
	// and 3 times to input if he/she responded or not. This variable is
	// reset at the beginning of a new trial.
	private static int TrialNumber=0;
	
	// A User may upload several videos. This variable keeps track of
	// how many videos the user has uploaded so far in order to properly
	// name the videos uploaded to the server.
	private int VideoNumber;
	private TransferManager tm;
	
	private boolean FinishedTrial = false;
	
	// Stores start time and response time information as well as the
	// location of the video file for each trial.
	private static Response videoData;

	private static final String CLASSTAG = "VideoUI";
	private String PhoneNumber;
	private boolean Wifi;
	private int VideoQuality;
	private boolean isUploading;
	private boolean isRecording=false;
	private String EnteredResponse = null;
	
	
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
		TimerController.setTextView("restart", restart);
		
		AlertDialog response = setResponseDialogListener();
		AlertDialog waitout = setWaitOutDialogListener();
		TimerController.setDialog("response", response);
		TimerController.setDialog("waitout", waitout);
		
		// Get the progressbar
		progress = (ProgressBar) findViewById(R.id.progressbar);
	}
	
	public void onClick(View view) {
		if(view.getId() == Start.getId())
			start();
		else if (view.getId() == Help.getId()) {
			Intent tutscreen = new Intent(this, PagerTutorialActivity.class);
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
		String outputFile = getFilesDir() + "/TRIAL-VIDEO.mp4";
		videoData = new Response(outputFile, this);
		// Add the start time
		videoData.addTime("NONE");
		
		PreviewRecorder.record(this, Holder, 
							   outputFile, 
							   MediaRecorder.AudioSource.DEFAULT, 
							   MediaRecorder.VideoSource.CAMERA, 
							   VideoQuality);
		
		TimerController.startCountDownResponseTimer(TrialNumber);
		spinningcircle.setVisibility(View.VISIBLE);
		isRecording = true;
	}
	
	// Method for Killing the application completely. Done if the Internet connection is not on.
	public void killApp() {
		this.finish();
		android.os.Process.killProcess( android.os.Process.myPid()); 
	}
	
	public void showErrorDialog() {
		Builder failed = new Builder(this);
		failed.setMessage("Failed to Initialize Camera. Please check that your camera is working." +
						  "Try restarting your phone.");
		failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				killApp();
			}
		});
		failed.show();
		failed.setCancelable(false);
	}
	
	public void stop() {
		PreviewRecorder.stopRecorder();
		TimerController.stopTimer();
		spinningcircle.setVisibility(View.GONE);
		// Reset the arcwidth
		spinningcircle.arcwidth = 360;
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
		catch (Exception e) {
			showErrorDialog();
		}
	}
	
	public void Pause() {
		Notify.setVisibility(View.GONE);
		
		// When paused, release the recorder and delete the video file
		if (!PreviewRecorder.isNull()) {
			stop();
			PreviewRecorder.releaseRecorder();
			PreviewRecorder.releaseCamera();
			Response.DeleteFile(getFilesDir() + "/TRIAL-VIDEO.mp4");
		}
		else {
			PreviewRecorder.releaseRecorder();
			PreviewRecorder.releaseCamera();
		}
		
		if (isRecording) {
			Start.setVisibility(View.VISIBLE);
			Help.setVisibility(View.VISIBLE);
			findViewById(R.id.restartMessage).setVisibility(View.VISIBLE);
			isRecording = false;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.v(CLASSTAG, "APP PAUSED");
		Pause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.v(CLASSTAG, "APP RESUMED");
		// Always Check that user is connected to the Internet
		networkCheck();
		try {
			PreviewRecorder.initializeCamera(this);
			PreviewRecorder.startCameraPreview(this, Holder);
			Log.i(CLASSTAG, "App Resumed Sucessfully");
		}
		catch(Exception e) {
			showErrorDialog();
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
			Response.DeleteFile(getFilesDir() + "/TRIAL-VIDEO.mp4");
			videoData = new Response(null, null);
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
					FinishedTrial = true;
					EnteredResponse = "YES";
					Log.i(CLASSTAG, "Clicked Positive");
				}
				else if (which == Dialog.BUTTON_NEGATIVE) {
					if (TrialNumber < 3){
						spinningcircle.setVisibility(View.VISIBLE);
						TrialNumber += 1;
						TimerController.startCountDownResponseTimer(TrialNumber);
						Log.i(CLASSTAG, "Clicked Negative");
					}
				}
				else if(which == Dialog.BUTTON_NEUTRAL) {
					stop();
					Help.setVisibility(View.VISIBLE);
					Start.setVisibility(View.VISIBLE);
					
					TimerController.stopTimer();
					TimerController.showRestartMessage();
					EnteredResponse = "NO";
					Log.i(CLASSTAG, "Clicked Neutral");
				}
				
				if (TrialNumber < 3) {
					videoData.addTime(EnteredResponse);
				}
				else {
					FinishedTrial = true;
				}
				
				if (FinishedTrial) {
					videoData.addTime(EnteredResponse);
					spinningcircle.setVisibility(View.GONE);
					PreviewRecorder.addVideoData(videoData);
					EnteredResponse = null;
					
					isRecording = false;
					stop();
							
					videoData.storeDateOfTrial();
					SharedPreferences shp = getSharedPreferences("com.example.autismdiagnose", 
																 Context.MODE_PRIVATE);
					
					// The unique identifier of the video
					String videoName = shp.getString("EMAIL_ADDRESS", "anonymous@unknown.com");
					// The number of videos the usler records is stored in this variable in
					// SharedPrefrences.
					VideoNumber = shp.getInt("VIDEONUMBER", 0);
					VideoNumber ++;
					shp.edit().putInt("VIDEONUMBER", VideoNumber).commit();
					
					String path_txt = videoData.writeTimes();
					findViewById(R.id.UploadMessage).setVisibility(View.VISIBLE);
					
					new UploadFile().execute(videoName + "_" + VideoNumber, videoData.getPath(), path_txt);
				}
			}
		};
		
		// Show the dialog and determine if there was a response
		ResponseBuilder.setMessage("Was there a response?")
			.setPositiveButton("Yes", responseListener)
			.setNegativeButton("No", responseListener)
			.setNeutralButton("Cancel", responseListener);
	
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
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
		else {
			ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			
			if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				Wifi = true;
				getApplicationContext();
				// Uploading with wifi. Change video quality depending on speed.
				// Set the video quality depending on the speed of wifi/service
				WifiManager wifiManger = (WifiManager) 
						getApplicationContext().getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManger.getConnectionInfo();
				
				final int FAST_WIFI_SPEED = 54; //Mbps
				
				if (wifiInfo.getLinkSpeed() < FAST_WIFI_SPEED) {
					VideoQuality = CamcorderProfile.QUALITY_LOW;
					Log.v("VIDEOQUALITY_LOW", "" + wifiInfo.getLinkSpeed());
					//spinningcircle.Subtract = 2.10;
				} 
				else {
					VideoQuality = CamcorderProfile.QUALITY_720P;
					Log.v("VIDEOQUALITY_HIGH", "" + wifiInfo.getLinkSpeed());
					//spinningcircle.Subtract = 2.15;
				}
			}
			else {
				// Uploading video with 4G/3G data. We do not want to record in high quality.
				Wifi = false;
				VideoQuality = CamcorderProfile.QUALITY_LOW;
				//spinningcircle.Subtract = 2.10;
			}
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
	
	private class UploadFile extends AsyncTask<String, Integer, String[]> {
	
	     protected String[] doInBackground(String ... params) {
			 try {
			 	tm = new TransferManager(Util.getCredProvider(VideoUI.this));		 
			 }
			 catch (Exception e) {
				failedUpload();
			 }
	    	return params;
	     }
	
	     protected void onPostExecute(String [] result) {
	    	// Make the progress bar visible
		     progress.setVisibility(View.VISIBLE);
	    	 new CompleteUpload().execute(result);
	     }
	}
	
	private class CompleteUpload extends AsyncTask<String, Integer, String[]> {

		@Override
		protected String[] doInBackground(String... result) {
			try {
				String fileName = result[0];
				String filePath = result[1];
				String file_txt = result[2];
			
				File file = new File(filePath);
				File txt_file = new File(file_txt);
				file.createNewFile();
				txt_file.createNewFile();
				
				ArrayList <File> twoFiles = new ArrayList<File>();
				twoFiles.add(file);
				twoFiles.add(txt_file);
				
				MultipleFileUpload mUpload = tm.uploadFileList(
												   Constants.BUCKET_NAME, 
												   fileName, 
												   VideoUI.this.getFilesDir(), 
												   twoFiles);
				
				isUploading = true;
				while (mUpload.isDone() == false && isNetworkAvailable()) {
					progress.setProgress((int) mUpload.getProgress().getPercentTransferred());
					Log.d("Uploading", String.valueOf(mUpload.getProgress().getPercentTransferred()) + "%");
				}				
			}
			catch (Exception e) {
				failedUpload();	
			}
			return result;
	    }
	
		protected void onPostExecute(String [] some) {
			isUploading = false;
			findViewById(R.id.UploadMessage).setVisibility(View.GONE);
			Start.setVisibility(View.VISIBLE);
			Help.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			
			Builder done = new Builder(VideoUI.this);
			done.setMessage("This Trial is done.\nVideo upload was successful.");
			done.setPositiveButton("Ok!", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DemoActivity.switchToPagerTutorial(VideoUI.this);					

				}
			});
			done.setCancelable(false);
			done.show();
		}
	}
	
	protected void failedUpload() {
		 Builder failed = new Builder(VideoUI.this);
		 failed.setMessage("Upload Failed! Please check your Internet connection and retry the trial!");
		 failed.setPositiveButton("Ok", null);
		 Start.setVisibility(View.VISIBLE);
		 progress.setVisibility(View.GONE);
    }
}
