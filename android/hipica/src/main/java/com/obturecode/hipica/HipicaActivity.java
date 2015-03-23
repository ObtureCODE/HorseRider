package com.obturecode.hipica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class HipicaActivity extends Activity 
{
	
	ViewFlipper flip_page;
	private int position = 0;
	
	Animation animFlipInForeward;
	Animation animFlipOutForeward;
	Animation animFlipInBackward;
	Animation animFlipOutBackward;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);

        Button buttonArre = (Button)findViewById(R.id.ButtonArre);

        buttonArre.setOnClickListener(new View.OnClickListener() 
        {    
            @Override
            public void onClick(View v) 
            {
            	Intent intent = new Intent(HipicaActivity.this, NameActivity.class);
            	startActivity(intent);
            }
        });
        
        animFlipInForeward = AnimationUtils.loadAnimation(this, R.anim.flipin);
        animFlipOutForeward = AnimationUtils.loadAnimation(this, R.anim.flipout);
        animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.flipin_reverse);
        animFlipOutBackward = AnimationUtils.loadAnimation(this, R.anim.flipout_reverse);
        
        flip_page = (ViewFlipper)findViewById(R.id.ViewFlipper);
    }
    
    private void SwipeRight()
    {
 	   if(this.position > 0)
 	   {
 		   flip_page.setInAnimation(animFlipInBackward);
 		   flip_page.setOutAnimation(animFlipOutBackward);
 		   flip_page.showPrevious();
 		   
 		   this.position--;
 	   }
    }
   
    private void SwipeLeft()
    {
 	   if(this.position < (flip_page.getChildCount() - 1))
 	   {
 		   flip_page.setInAnimation(animFlipInForeward);
 		   flip_page.setOutAnimation(animFlipOutForeward);
 		   flip_page.showNext();
 		   
 		   this.position++;
 	   }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
     return gestureDetector.onTouchEvent(event);
    }

    SimpleOnGestureListener simpleOnGestureListener
    = new SimpleOnGestureListener(){

 	   @Override
 	   public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
 			   float velocityY) {

 		   float sensitvity = 100;
 		   
 		   if(e1 != null && e2 != null){
 			   if((e1.getX() - e2.getX()) > sensitvity){
 				   SwipeLeft();
 			   }else if((e2.getX() - e1.getX()) > sensitvity){
 				   SwipeRight();
 			   }
 		   }
  
 		   return true;
 	   }
    
    };
   
    GestureDetector gestureDetector
    = new GestureDetector(simpleOnGestureListener);
}