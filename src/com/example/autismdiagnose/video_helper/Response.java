package com.example.autismdiagnose.video_helper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Response {

	private String path;
	private boolean NO_RESPONSE = false;
	private DateTime start_time;
	private DateTime trial_1_time;
	private DateTime trial_2_time;
	private DateTime trial_3_time;
	
	public Response(String path) {
		this.path = path;
		start_time = getCurrentTime();
	}

	public DateTime getTrial_1_time() {
		return trial_1_time;
	}

	public void setTrial_1_time() {
		this.trial_1_time = getCurrentTime();
	}

	public DateTime getTrial_2_time() {
		return trial_2_time;
	}

	public void setTrial_2_time() {
		this.trial_2_time = getCurrentTime();
	}

	public DateTime getTrial_3_time() {
		return trial_3_time;
	}

	public void setTrial_3_time() {
		this.trial_3_time = getCurrentTime();
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
}
