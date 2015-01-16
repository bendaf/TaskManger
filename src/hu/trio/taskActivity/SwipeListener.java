package hu.trio.taskActivity;

import android.view.View;

public interface SwipeListener {
	public void onFirstClick(View v);
//	public void onSecondClick(View v);
	public void onLongClick(View v);
	public void onSwipeLeft(View v);
	public void onSwipeRight(View v);
}
