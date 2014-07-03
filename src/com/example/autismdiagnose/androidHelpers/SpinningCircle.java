package com.example.autismdiagnose.androidHelpers;

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
		
		//Log.v("ArcWid", String.valueOf(arcwidth));
		
		Paint red = new Paint();
		Paint heavyred = new Paint();
		heavyred.setColor(Color.parseColor("#CC0099"));
		red.setColor(Color.parseColor("#FF0099"));
		
		red.setStyle(Paint.Style.STROKE);
		heavyred.setStyle(Paint.Style.STROKE);
		heavyred.setStrokeWidth(5);
		red.setStrokeWidth(20);
		
		canvas.drawArc(new RectF(15, 15, canvas.getWidth()-16, canvas.getHeight()-16), 270, arcwidth, false, red);		
		arcwidth -= 1.15;
	}
}
