package com.example.autismdiagnose.video_helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.VideoView;

public class AndroidPreviewRecorder implements 
OnInfoListener, OnErrorListener {

	private Camera camera;
	private MediaRecorder recorder;
	private ArrayList<Response> VideoList = new ArrayList<Response>();
	
	public static boolean isRecording = false;
	private final String CLASSTAG = "AndroidPreviewRecorder";
	
	public AndroidPreviewRecorder(Camera camera, MediaRecorder recorder) {
		this.camera = camera;
		this.recorder = recorder;
	}
	
	/**
	 * @param activity: the activity you are calling this method in
	 * @param videoView: the videoview you are using
	 * @return boolean (True if camera was initialized and vice versa)
	 * 
	 * Use this method to access the phone camera, lock it to the activity
	 * and then set the surface holder to the videoView's holder.
	 */
	public boolean initializeCamera(Activity activity) {
		if (camera != null) return true;
		try {
			camera = Camera.open();
			Camera.Parameters camParams = camera.getParameters();
			camera.lock();
			Log.v("Started", "CAMERA AGAIN");
		}
		catch(RuntimeException re){
			Log.v(CLASSTAG, "Failed to initialize camera");
			re.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void releaseCamera() {
		if(camera != null) {
			try {
				camera.reconnect();
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
		camera.release();
		camera = null;
	}
	
	public void startCameraPreview(Activity activity, SurfaceHolder holder) throws Exception {
		camera.setPreviewDisplay(holder);
		setCameraDisplayOrientation(activity, Camera.CameraInfo.CAMERA_FACING_FRONT, camera);
		camera.startPreview();
	}
	
	public void initializeRecorder(Activity activity, SurfaceHolder holder, 
			String outputPath, int AUDIOSOURCE, int VIDEOSOURCE, int QUALITY) {
		
		if(recorder != null) return;
		
		Log.v(CLASSTAG, outputPath);
		
		File outputFile = new File(outputPath);
		
		if (outputFile.exists())
			outputFile.delete();
		
		try {
			// If a preview is going on, stop it and unlock the camera
			camera.stopPreview();
			Parameters params = camera.getParameters();
			camera.setParameters(params);
			camera.unlock();
			
			// Create a new recorder and set its source as the phone camera.
			recorder = new MediaRecorder();
			recorder.setCamera(camera);
			
			recorder.setAudioSource(AUDIOSOURCE);
			recorder.setVideoSource(VIDEOSOURCE);
			
			// Set the video quality
			CamcorderProfile profile = CamcorderProfile.get(QUALITY);
			recorder.setProfile(profile);
			recorder.setOutputFile(outputPath);
			prepareRecorder(holder);
		}
		catch(Exception e) {
			Log.v(CLASSTAG, "Failed to initialize Recorder");
			e.printStackTrace();
		}	
	
	}
	
	private void prepareRecorder(SurfaceHolder holder) {
        recorder.setPreviewDisplay(holder.getSurface());
        try {
            recorder.prepare();
			Log.v(CLASSTAG, "Recorder Initialized!");
        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
	
	public void startRecorder() throws IllegalStateException {
		recorder.setOnInfoListener(this);
		recorder.setOnErrorListener(this);
		recorder.start();
	}
	
	/**
	 * @param activity
	 * @param camera
	 * @param holder
	 * @param outputPath
	 * @param AUDIOSOURCE
	 * @param VIDEOSOURCE
	 * @param QUALITY
	 * Initializes and starts the recorder
	 */
	public void record(Activity activity, SurfaceHolder holder, 
			String outputPath, int AUDIOSOURCE, int VIDEOSOURCE, int QUALITY) {
		
		initializeRecorder(activity, holder, outputPath, AUDIOSOURCE, VIDEOSOURCE, QUALITY);
		startRecorder();
		isRecording = true;
	}
	
	public void stopRecorder() {
		if(recorder != null) {
			try {
				recorder.stop();
				recorder.reset();
				releaseRecorder();
			}
			catch(IllegalStateException e) {
				Log.e(CLASSTAG, "Recorder BADDDDDD");
			}	
		}
		isRecording = false;
	}
	
	public void releaseRecorder() {
		if(recorder != null) {
			recorder.release();
			recorder = null;
		}
		isRecording = false;
	}
	
	// TODO add a feature to allow the recorder to wait a certain amount
	// of time before recording again.
	public boolean isNull() {
		if(recorder == null)
			return true;
		return false;
	}

	
	public void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
		 Log.v("Changed Orientation", "SUCCESSFUL!");
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
	

	@Override
	public void onError(MediaRecorder arg0, int what, int extras) {}

	@Override
	public void onInfo(MediaRecorder arg0, int what, int extra) {}

	public Response getVideoData(int index) {
		return VideoList.get(index);
	}
	
	public ArrayList<Response> getVideoData() {
		return VideoList;
	}

	public void addVideoData(Response videoData) {
		VideoList.add(videoData);
	}
}