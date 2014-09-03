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
	
	public static float arcwidth = 360;
	
	public SpinningCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
	}	

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		Paint FillColor = new Paint();
		FillColor.setColor(Color.parseColor("#00FF66"));		
		FillColor.setStyle(Paint.Style.STROKE);
		FillColor.setStrokeWidth(8);
	
		canvas.drawArc(new RectF(10, 10, canvas.getWidth()-10, canvas.getHeight()-10), 270, -arcwidth, false, FillColor);
		
		
		arcwidth -= 2.10;
	}
}
