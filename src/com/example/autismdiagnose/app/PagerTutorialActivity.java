package com.example.autismdiagnose.app;
 
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
 
import com.example.autismdiagnose.R;
import com.viewpagerindicator.CirclePageIndicator;
 
public class PagerTutorialActivity extends Activity {
 
    // Declare Variables
    ViewPager viewPager;
    PagerAdapter adapter;
    int[] images;
    CirclePageIndicator mIndicator;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.viewpager_main);
        
        images = new int[] { R.drawable.tutorial_title, R.drawable.tutorial_begin, 
        		  R.drawable.tutorial_click_start, R.drawable.tutorial_say_name,R.drawable.tutorial_responded_no,
                  R.drawable.tutorial_say_name, R.drawable.tutorial_responded_yes, R.drawable.discard,
                  R.drawable.tutorial_done};
       
        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapter(getApplicationContext(), PagerTutorialActivity.this, images);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
 
        // ViewPager Indicator
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);       
    }
}