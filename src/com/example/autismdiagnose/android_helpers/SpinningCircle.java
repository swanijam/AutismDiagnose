package com.example.autismdiagnose.android_helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SpinningCircle extends View {
	
	public static double arcwidth = 360;
	public static double percent = 0;
	
	public SpinningCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
	}	

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		Paint FillColor = new Paint();
		FillColor.setColor(Color.parseColor("#33FFFF"));		
		FillColor.setStyle(Paint.Style.STROKE);
		FillColor.setStrokeWidth(8);
	
		canvas.drawArc(new RectF(10, 10, canvas.getWidth()-10, canvas.getHeight()-10), 270, (float) -arcwidth, false, FillColor);
		
		// Calculate what percentage of the arcwidth to subtract
		arcwidth = 360 - ((double) 360 * (double) percent); 
	}
}
