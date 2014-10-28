package hu.trio.taskmanager;

import android.view.View;

public interface SwipeListener {
	public void onFirstClick(View v);
	public void onSecondClick(View v);
	public void onSwipeOff(View v);
	public void onSwipeIn(View v);
}
