package hu.trio.taskmanager;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;

public class SwipeTouchListener implements OnTouchListener{

	private static final int SWIPE_DURATION = 250;
	
	private int frontViewId;
	private int mSwipeSlop = -1;
	private float mDownX;
	private float frontTranslation;
	private boolean mSwiping = false;
	private boolean isSwipeOf = false;
	private SwipeListener swipeLis = null;
	private float screenWidth=0;
	
	public SwipeTouchListener(Context context, int frontViewId){
		if (mSwipeSlop  < 0) {
            mSwipeSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }
		this.frontViewId=frontViewId;
		Point size = new Point();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
		screenWidth = size.x;
	}
	
	public SwipeTouchListener(Context context, int frontViewId, SwipeListener swipeListener){
		this(context, frontViewId);
		this.swipeLis = swipeListener;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		View front = v.findViewById(frontViewId);
		switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mDownX = event.getX();
            frontTranslation = front.getTranslationX();
            if(frontTranslation > 50){
            	isSwipeOf = false;
            }else isSwipeOf = true;
            break;
        case MotionEvent.ACTION_CANCEL:
            front.animate().translationX(0);
            break;
        case MotionEvent.ACTION_MOVE:
            {
                float deltaX = event.getX() - mDownX;
                float deltaXAbs = Math.abs(deltaX);
                if (!mSwiping) {
                    if (deltaXAbs > mSwipeSlop) {
                        mSwiping = true;
                    }
                }
                if (mSwiping) {
                	if(isSwipeOf){
	                    front.setTranslationX((event.getX() - mDownX));
                	}else{
                		front.setTranslationX((event.getX() - mDownX) + frontTranslation);
                	}
                	if(front.getTranslationX() < 0)front.setTranslationX(0);
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            {
                float deltaX = event.getX() - mDownX;
                float deltaXAbs = Math.abs(deltaX);
                float fractionCovered = deltaXAbs / v.getWidth();
                float endX;
                if(isSwipeOf) endX = deltaX < 0 ? 0 : v.getWidth();
                else endX = deltaX <= 0 ? 0 : v.getWidth();
                long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                
                front.animate().setDuration(duration).translationX(endX);
                mSwiping = false;
        		v.performClick();
                if(swipeLis != null){
                	if(isSwipeOf) {
                		swipeLis.onSwipeOff(v);
                		swipeLis.onFirstClick(v);
                	} else {
                		swipeLis.onSwipeIn(v);
                		swipeLis.onSecondClick(v);
                	}
                }
            }
            break;
        }
		return true;
	}

	public void swipeIn(View v){
		View front = v.findViewById(frontViewId);
		if(front == null) return;
		if(front.getTranslationX() > 50){
			front.animate().setDuration(SWIPE_DURATION).translationX(0);
			if(swipeLis != null) swipeLis.onSwipeIn(v);
		}
	}
	
	public void swipeOff(View v){
		View front = v.findViewById(frontViewId);
		if(front == null) return;
		if(front.getTranslationX() < 50){
			front.animate().setDuration(SWIPE_DURATION).translationX(v.getWidth() == 0 ? screenWidth : v.getWidth());
			if(swipeLis != null) swipeLis.onSwipeOff(v);
		}
	}
	
	public void setSwipe(View v, boolean toSwipeOff){
		View front = v.findViewById(frontViewId);
		if(front == null) return;
		if(toSwipeOff){
			front.setTranslationX(v.getWidth() == 0 ? screenWidth : v.getWidth());
			if(swipeLis != null) swipeLis.onSwipeOff(v);
		}else{
			front.setTranslationX(0);
			if(swipeLis != null) swipeLis.onSwipeIn(v);
		}
	}
}
