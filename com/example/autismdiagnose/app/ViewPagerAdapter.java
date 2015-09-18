package com.example.autismdiagnose.app;
 
import com.example.autismdiagnose.R;
import com.example.autismdiagnose.video_helper.Response;
import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import com.viewpagerindicator.CirclePageIndicator;

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
import android.widget.TextView;
 
public class ViewPagerAdapter extends PagerAdapter {
    LayoutInflater inflater;
    Context context;
    Activity activity;
    int images[];
    SharedPreferences Prefs;
    CirclePageIndicator indicator;
 
    public ViewPagerAdapter(Context context, Activity activity, CirclePageIndicator ind, int[] images) {
    	this.context = context;
    	this.images = images;
    	this.activity = activity;
    	indicator = ind;
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
        
        Button goToTrial = (Button) itemView.findViewById(R.id.goToTrial);
        ImageView swipe = (ImageView) itemView.findViewById(R.id.swipePic);
        ImageView titlePic = (ImageView) itemView.findViewById(R.id.titlePic);
        ImageView smallTitle = (ImageView) itemView.findViewById(R.id.smalll_title);
        ImageView bottom = (ImageView) itemView.findViewById(R.id.bottom);
        TextView dateDisplay = (TextView) itemView.findViewById(R.id.lastTrial);
        TextView swipeText = (TextView) itemView.findViewById(R.id.text);
        
        if (position > 0) {
        	goToTrial.setVisibility(View.GONE);
        	titlePic.setVisibility(View.GONE);
        	smallTitle.setVisibility(View.GONE);
        	swipe.setVisibility(View.GONE);
        	swipeText.setVisibility(View.GONE);
        	bottom.setVisibility(View.GONE);
    		dateDisplay.setVisibility(View.GONE);
        }
        else {
        	goToTrial.setVisibility(View.VISIBLE);
        	titlePic.setVisibility(View.VISIBLE);
        	smallTitle.setVisibility(View.GONE);
        	swipe.setVisibility(View.VISIBLE);
        	swipeText.setVisibility(View.VISIBLE);
        	bottom.setVisibility(View.VISIBLE);
        	goToTrial.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					switchToVideoAndDestroy();
				}
			});
        	String lastDate = Response.getDateofLastTrial(activity);
        	if(lastDate.length() > 0) {
        		titlePic.setVisibility(View.GONE);
        		smallTitle.setVisibility(View.VISIBLE);
        		dateDisplay.setVisibility(View.VISIBLE);
        		dateDisplay.setText("Last Trial\n"+lastDate);
        	}
        }
     
        if (position > 1) {
        	indicator.setVisibility(View.VISIBLE);
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