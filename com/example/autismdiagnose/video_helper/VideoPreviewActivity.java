package com.example.autismdiagnose.video_helper;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;
import android.widget.VideoView;

import com.example.autismdiagnose.R;

/**
 * @author Sheik Hassan
 * @version 0.0.1
 *
 * This Class is allows an activity with extends it to quickly initialize a video preview view.
 */

public abstract class VideoPreviewActivity extends Activity {

	private VideoView videoView;
	private SurfaceHolder holder;
	private Camera cam;
	private MediaRecorder recorder;
	private AndroidPreviewRecorder previewRecorder;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video_ui);
 	}

	public abstract void start();
	
	public abstract void stop();
	
	
	public void setUpVideoView(SurfaceHolder.Callback callback) {
		videoView = (VideoView) findViewById(R.id.videoView1);
		// Set up the videoView
		holder = videoView.getHolder();
 		holder.addCallback(callback);
 		previewRecorder = new AndroidPreviewRecorder(cam, recorder);
	}
	
	public void startCamera(Activity activity) {
		previewRecorder.initializeCamera(activity);
		Log.v("Hello", "Started Camera");
	}
	
	public void startPreview() {
		try {
			previewRecorder.startCameraPreview(this, holder);
		} catch(Exception e) {}
	}
	
	
	public void handlePause() {
		// When paused, release the recorder and delete the video file
		if (!previewRecorder.isNull()) {
			previewRecorder.stopRecorder();
		}
		else previewRecorder.releaseRecorder();		
	}
}

