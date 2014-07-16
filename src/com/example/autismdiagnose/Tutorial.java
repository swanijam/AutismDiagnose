package com.example.autismdiagnose;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

public class Tutorial extends Activity {

	private String [] urls = {"https://www.youtube.com/watch?v=7JjiesNmADo", 
							  "https://www.youtube.com/watch?v=qctQR0tZtW0"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tutorial);
		
		ListView lview = (ListView) findViewById(R.id.Tutorials);
		String[] tuts = {"Default Tutorial", "Another Tutorial"};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, tuts);
		
		lview.setAdapter(adapter);
		
		lview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int element,
					long arg3) {
				 SharedPreferences prefs = getSharedPreferences("com.example.autismdiagnose", Context.MODE_PRIVATE);
					prefs.edit().putBoolean("COMPLETED", true).commit();
				 
				switchToVideo(element);
			}
			
		});		
	}
	
	public void switchToVideo(int vidID) {
		if (isInstalled("com.google.android.youtube")) {
			
			Intent video = new Intent(this, TutorialVideo.class);
			video.putExtra("VIDEO_ID", urls[vidID]);
			startActivity(video);
		 }
		 else {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urls[vidID])));
		 }
	}
	
	private boolean isInstalled(String uri) {
		PackageManager pm = getPackageManager();
		boolean isInstalled = false;
		
		try{
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			isInstalled = true;
			Log.v("Youtube", "EXISTS");
		}
		catch (Exception e) {}
		
		return isInstalled;
	}
	
	@Override
	public void onBackPressed() {
		Log.v("Back send", "Here");
		Intent videoUI = new Intent(this, VideoUI.class);
		videoUI.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(videoUI);
		finish();
	}	
}
