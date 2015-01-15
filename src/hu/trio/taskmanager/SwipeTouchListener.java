package hu.trio.taskmanager;

import static com.nineoldandroids.view.ViewHelper.setAlpha;
import static com.nineoldandroids.view.ViewHelper.setTranslationX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;

public class SwipeTouchListener implements OnTouchListener{

	private static final int SWIPE_DURATION = 250;
	
	private int frontViewId;
	private int mSwipeSlop = -1; //A swipe has to be bigger than this 
	private float mDownX;
	private float frontTranslation;
	private boolean mSwiping = false;
	private SwipeListener swipeLis = null;
	private float screenWidth=0;

	private List<PendingDismissData> mPendingDismisses = new ArrayList<PendingDismissData>();
    private int mDismissAnimationRefCount = 0;
    
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
//        	Log.d("erdekel", Float.toString(mDownX));
        	
//            mDownX = event.getX();
//            frontTranslation = front.getTranslationX();
//            if(frontTranslation > 50){
//            	isSwipeOf = false;
//            }else isSwipeOf = true;
            break;
        case MotionEvent.ACTION_CANCEL:
            front.animate().translationX(0);
            break;
        case MotionEvent.ACTION_MOVE:
            {
            	float deltaX = event.getX() - mDownX;
            	try{
//            	Log.d("erdekel", "Move to: " + Float.toString(event.getX()) + " Tag: " + Integer.toString((Integer)v.getTag()));
            	}catch (Exception e) {
            		Log.d("erdekel", "hiba");
            	}
            	float deltaXAbs = Math.abs(deltaX);
            	if (!mSwiping) {
            		if (deltaXAbs > mSwipeSlop) {
            			mSwiping = true;
            		}
            	}
            	if(mSwiping){
            		front.setTranslationX(event.getX()-mDownX);
            		if(deltaX > (screenWidth/5)){
            			swipeLis.onSwipeRight(v);
            		}
            	}
//                float deltaX = event.getX() - mDownX;
//                float deltaXAbs = Math.abs(deltaX);
//                if (!mSwiping) {
//                    if (deltaXAbs > mSwipeSlop) {
//                        mSwiping = true;
//                    }
//                }
//                if (mSwiping) {
//                	if(isSwipeOf){
//	                    front.setTranslationX((event.getX() - mDownX));
//                	}else{
//                		front.setTranslationX((event.getX() - mDownX) + frontTranslation);
//                	}
////                	if(front.getTranslationX() < 0)front.setTranslationX(0);
//                }
            }
            break;
        case MotionEvent.ACTION_UP:
            {
            	float deltaX = event.getX() - mDownX;
            	
            	if(deltaX < -(screenWidth/5)){
            		final View dView = v;
            		++mDismissAnimationRefCount;
            		com.nineoldandroids.view.ViewPropertyAnimator.animate(front).translationX(-screenWidth).alpha(0).setListener(new AnimatorListenerAdapter() {
            			@Override
                        public void onAnimationEnd(Animator animation) {
                            performDismiss(dView, (Integer) dView.getTag());
                        }
                    });
            	}else{
            		float deltaXAbs = Math.abs(deltaX);
            		float fractionCovered = deltaXAbs / v.getWidth();
            		long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
            		front.animate().setDuration(duration).translationX(0);
            	}
//                float deltaX = event.getX() - mDownX;
//                float deltaXAbs = Math.abs(deltaX);
//                float fractionCovered = deltaXAbs / v.getWidth();
//                float endX;
//                if(isSwipeOf) endX = deltaX < 0 ? 0 : v.getWidth();
//                else endX = deltaX <= 0 ? 0 : v.getWidth();
//                long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
//                
//                front.animate().setDuration(duration).translationX(endX);
//                mSwiping = false;
//        		v.performClick();
//                if(swipeLis != null){
//                	if(isSwipeOf) {
////                		swipeLis.onSwipeOff(v);
//                		swipeLis.onFirstClick(v);
//                	} else {
////                		swipeLis.onSwipeIn(v);
////                		swipeLis.onSecondClick(v);
//                	}
//                }
            }
            break;
        }
		return true;
	}

	class PendingDismissData implements Comparable<PendingDismissData> {
        public int position;
        public View view;

        public PendingDismissData(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public int compareTo(PendingDismissData other) {
            // Sort by descending position
            return other.position - position;
        }
    }
	
	private void performDismiss(final View dismissView, final int dismissPosition) {
        // Animate the dismissed list item to zero-height and fire the dismiss callback when
        // all dismissed list item animations have completed. This triggers layout on each animation
        // frame; in the future we may want to do something smarter and more performant.

        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
        final int originalHeight = dismissView.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(SWIPE_DURATION);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                --mDismissAnimationRefCount;
                if (mDismissAnimationRefCount == 0) {
                    // No active animations, process all pending dismisses.
                    // Sort by descending position
                    Collections.sort(mPendingDismisses);

                    int[] dismissPositions = new int[mPendingDismisses.size()];
                    for (int i = mPendingDismisses.size() - 1; i >= 0; i--) {
                        dismissPositions[i] = mPendingDismisses.get(i).position;
                    }
//                    mCallback.onDismiss(mListView, dismissPositions);
                    swipeLis.onSwipeLeft(dismissView);
                    ViewGroup.LayoutParams lp;
                    for (PendingDismissData pendingDismiss : mPendingDismisses) {
                        // Reset view presentation
                        setAlpha(pendingDismiss.view, 1f);
                        setTranslationX(pendingDismiss.view, 0);
                        lp = pendingDismiss.view.getLayoutParams();
                        lp.height = originalHeight;
                        pendingDismiss.view.setLayoutParams(lp);
                    }

                    mPendingDismisses.clear();
                }
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                dismissView.setLayoutParams(lp);
            }
        });

        mPendingDismisses.add(new PendingDismissData(dismissPosition, dismissView));
        animator.start();
    }
	
//	public void swipeIn(View v){
//		View front = v.findViewById(frontViewId);
//		if(front == null) return;
//		if(front.getTranslationX() > 50){
//			front.animate().setDuration(SWIPE_DURATION).translationX(0);
////			if(swipeLis != null) swipeLis.onSwipeIn(v);
//		}
//	}
//	
//	public void swipeOff(View v){
//		View front = v.findViewById(frontViewId);
//		if(front == null) return;
//		if(front.getTranslationX() < 50){
//			front.animate().setDuration(SWIPE_DURATION).translationX(v.getWidth() == 0 ? screenWidth : v.getWidth());
////			if(swipeLis != null) swipeLis.onSwipeOff(v);
//		}
//	}
//	
//	public void setSwipe(View v, boolean toSwipeOff){
//		View front = v.findViewById(frontViewId);
//		if(front == null) return;
//		if(toSwipeOff){
//			front.setTranslationX(v.getWidth() == 0 ? screenWidth : v.getWidth());
////			if(swipeLis != null) swipeLis.onSwipeOff(v);
//		}else{
//			front.setTranslationX(0);
////			if(swipeLis != null) swipeLis.onSwipeIn(v);
//		}
//	}
}
