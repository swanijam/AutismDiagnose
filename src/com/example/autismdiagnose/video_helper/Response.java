package com.example.autismdiagnose.video_helper;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Response {

	private String path;
	private boolean NO_RESPONSE = false;
	private DateTime start_time;
	private ArrayList<DateTime> trial_times;
	
	public Response(String path) {
		this.path = path;
		start_time = getCurrentTime();
		trial_times = new ArrayList<DateTime>();
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

	public DateTime getStart_time() {
		return this.start_time;
	}
	
	public DateTime getCurrentTime(){
		DateTime datetime = new DateTime(DateTimeZone.UTC);
		return datetime;
	}
	
	public void writeTimes() {
		
	}
}
