package com.example.autismdiagnose.androidHelpers;

import com.example.autismdiagnose.R;
import android.os.CountDownTimer;

public class DiagnoseTimer {
	private CountDownTimer timer;
	
	public void start(long DELAY, long TICK) {
		timer = new CountDownTimer(DELAY, TICK) {
			@Override
			public void onTick(long mills) {
				long seconds = (mills) / 1000;
				
				if (seconds == 4) {
					
				}
			}
			@Override
			public void onFinish() {
			}
		};
		
		timer.start();
	}
	
	public void stop() {
		timer.cancel();
	}
}
