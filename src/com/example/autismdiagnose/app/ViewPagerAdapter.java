package com.example.autismdiagnose.app;
 
import com.example.autismdiagnose.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
 
public class ViewPagerAdapter extends PagerAdapter {
    LayoutInflater inflater;
    Context context;
    Activity activity;
    int images[];
    SharedPreferences Prefs;
 
    public ViewPagerAdapter(Context context, Activity activity, int[] images) {
    	this.context = context;
    	this.images = images;
    	this.activity = activity;
        Log.v("HEllO", "In contstructor");
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }
 
    @Override
    public Object instantiateItem(ViewGroup container, int position) {    	
        // Declare Variables
        ImageView imgflag;
 
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);
    	// Locate the ImageView in viewpager_item.xml
        imgflag = (ImageView) itemView.findViewById(R.id.flag);
        // Capture position and set to the ImageView
        imgflag.setImageResource(images[position]);
        
        if (position == images.length - 1) {	       
        	// Make the done button visible
        	Button done = (Button) itemView.findViewById(R.id.done);
        	done.setVisibility(View.VISIBLE);
        	done.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					switchToVideoAndDestroy();
				}
			});
        }
 
        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);
 
        return itemView;
    }
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((FrameLayout) object);
 
    }

	@Override
	public int getCount() {
		// Returns the number of views available
		return images.length;
	}
	
	// When 'Done' Button is clicked, switch to the video
	public void switchToVideoAndDestroy() {
    	Prefs = activity.getSharedPreferences(
				  "com.example.autismdiagnose", 
				  Context.MODE_PRIVATE);
    	
	    if (Prefs != null) {
			Prefs.edit().putBoolean("COMPLETED", true).commit();
		}
		
		Intent videoUI = new Intent(activity, VideoUI.class);
		Intent fromIntent = activity.getIntent();
		
		if (fromIntent.getBooleanExtra("HELP", false)) {
			videoUI.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		}
		activity.startActivity(videoUI);
		activity.finish();
    }
}