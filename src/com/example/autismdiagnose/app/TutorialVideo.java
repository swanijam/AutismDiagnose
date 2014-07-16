package com.example.autismdiagnose.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.autismdiagnose.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;


public class TutorialVideo extends YouTubeBaseActivity implements
YouTubePlayer.OnInitializedListener {

	private String API_KEY="AIzaSyAcUvDYxrgIGGYBJWUnY49cGKyB7DnHpI4";
	private String uri=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tutorial_video);
		
		Bundle bundle = getIntent().getExtras();
		uri = bundle.getString("VIDEO_ID").replace("https://www.youtube.com/watch?v=", "");
		
		Log.v("VideoId", uri);
		
		YouTubePlayerView youtube = (YouTubePlayerView)findViewById(R.id.youtubeplayerview);
		
		youtube.initialize(API_KEY, this);
		
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitializationSuccess(Provider arg0, YouTubePlayer player,
			boolean arg2) {
		// TODO Auto-generated method stub
		  if (!arg2) {
			  	player.setFullscreen(true);
		        player.loadVideo(uri);
		  }
	}
}
