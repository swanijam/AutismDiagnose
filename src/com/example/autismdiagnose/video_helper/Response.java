package com.example.autismdiagnose.video_helper;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.Activity;
import android.content.Context;

public class Response {

	private String path;
	private boolean NO_RESPONSE = false;
	private ArrayList<DateTime> trial_times;
	private Activity activity;
	
	public Response(String path, Activity activity) {
		this.path = path;
		trial_times = new ArrayList<DateTime>();
		this.activity = activity;
	}

	public String getPath() {
		return path;
	}
	
	public void addTime() {
		trial_times.add(getCurrentTime());
	}
	
	public void getTime(int index) {
		trial_times.get(index);
	}
	
	public void setNoResponse(boolean resp) {
		this.NO_RESPONSE = resp;
	}

	public DateTime getCurrentTime(){
		DateTime datetime = new DateTime(DateTimeZone.UTC);
		return datetime;
	}
	
	public void writeTimes() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(activity.openFileOutput(path, Context.MODE_PRIVATE)));
			
			String JSON = "";
			JSON += "{";
			for (int i = 0; i < trial_times.size(); i++) {
				JSON += "{";
				JSON += "Hour_of_Day:" + trial_times.get(i).getHourOfDay() + ",";
				JSON += "Minute_of_Hour:" + trial_times.get(i).getMinuteOfHour() + ",";
				JSON += "Second_of_Minute:" + trial_times.get(i).getSecondOfMinute();
				JSON += "},";
			}
			
			// Remove the last comma
			JSON = JSON.substring(0, JSON.length() - 2);
			JSON += "}";
			
			// Write the JSON to the file
			writer.write(JSON);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}   
	}
}
