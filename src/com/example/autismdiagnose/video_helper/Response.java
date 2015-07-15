package com.example.autismdiagnose.video_helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Response {

	private String path;
	private boolean NO_RESPONSE = false;
	private ArrayList<DateTime> trial_times;
	private ArrayList<String> responses;
	private Activity activity;
	private String [] months = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "June",
			"July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};
	
	public Response(String path, Activity activity) {
		this.path = path;
		trial_times = new ArrayList<DateTime>();
		responses = new ArrayList<String>();
		this.activity = activity;
	}

	public String getPath() {
		return path;
	}
	
	public void addTime(String response) {
		trial_times.add(getCurrentTime());
		responses.add(response);
	}
	
	public void getTime(int index) {
		trial_times.get(index);
	}
	
	public void setNoResponse(boolean resp) {
		this.NO_RESPONSE = resp;
	}

	public DateTime getCurrentTime(){
		DateTime datetime = new DateTime();
		return datetime;
	}
	
	// Methods dealing with storing the last video date
	public String getDateAsString () {
		DateTime dateObj = getCurrentTime();
		return months[dateObj.getMonthOfYear() - 1] + " " + dateObj.getDayOfMonth();
	}
	
	public static String getDateofLastTrial(Activity activity) {
		SharedPreferences shp = activity.getSharedPreferences("com.example.autismdiagnose", 
				 Context.MODE_PRIVATE);
		
		return shp.getString("LASTDATE", "");
	}
	
	public void storeDateOfTrial() {
		SharedPreferences shp = activity.getSharedPreferences("com.example.autismdiagnose", 
				 Context.MODE_PRIVATE);
		shp.edit().putString("LASTDATE", getDateAsString()).commit();
	}
	//
	
	
	// Outside method used for deleting left over files
	public static void DeleteFile(String name) {
		try {
			File f = new File(name);
			f.delete();
		}catch(Exception e){}
	}
	
	/**
	 * Description: This method writs the information about the video file in JSON format into a
	 * 				txt file, then it returns the path to the file.
	 * @return txtPath (The path of the written text file)
	 */
	public String writeTimes() {		
		File outfile = new File(activity.getFilesDir() + "TRIALJSON.txt");
		if (outfile.exists()) {
			outfile.delete();
		}
		
		try {
			outfile.createNewFile();
			FileWriter writer = new FileWriter(outfile, true);
			BufferedWriter out = new BufferedWriter(writer);			
			String JSON = "";
			JSON += "[";
			for (int i = 0; i < trial_times.size(); i++) {
				JSON += "{";
				JSON += "\"Year\":" + trial_times.get(i).getYear() + ",";
				JSON += "\"Month_of_Year\":" + trial_times.get(i).getMonthOfYear() + ",";
				JSON += "\"Day_of_Month\":" + trial_times.get(i).getDayOfMonth() + ",";
				JSON += "\"Hour_of_Day\":" + trial_times.get(i).getHourOfDay() + ",";
				JSON += "\"Minute_of_Hour\":" + trial_times.get(i).getMinuteOfHour() + ",";
				JSON += "\"Second_of_Minute\":" + trial_times.get(i).getSecondOfMinute() + ",";
				JSON += "\"Response\":" + responses.get(i);
				JSON += "},";
			}
			
			// Remove the last comma
			JSON = JSON.substring(0, JSON.length() - 1);
			JSON += "]";
			
			Log.v("FILE", JSON);
			// Write the JSON to the file
			out.write(JSON);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outfile.getAbsolutePath();
	}	
}
