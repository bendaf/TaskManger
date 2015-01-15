package hu.trio.taskmanager;

import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskArrayAdapter extends BaseAdapter {

	class VH{
		TextView tvTaskTitle;
		TextView tvTaskEndDate;
		TextView tvTaskReqTime;
	}
	
	private ArrayList<Task> taskList = new ArrayList<Task>();
	private SwipeTouchListener listener;
    private View swiped = null;
    private Integer swipedPos = -1;
    private LayoutInflater inflater;
    private Resources res;
    private SQLiteHelper SQLHelp;

    public TaskArrayAdapter(Context context, ArrayList<Task> tasks) {
        this.taskList=tasks;
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        SQLHelp = new SQLiteHelper(context);
        this.listener=new SwipeTouchListener(context, R.id.rtl_taskItem, new SwipeListener() {
			public void onFirstClick(View v) {}
			
//			@Override
//			public void onSecondClick(View v) {}

			@Override
			public void onSwipeRight(View v) {
				int pos = (Integer)v.getTag();
				taskList.get(pos).setDone(true);
				notifyDataSetChanged();
				
//				if(swiped != null) listener.swipeIn(swiped);
//				swiped = v;
//				swipedPos = 1;
			}

			@Override
			public void onSwipeLeft(View v) {
				SQLHelp.open();
				SQLHelp.deleteTask(taskList.get((Integer) v.getTag()));
		        SQLHelp.close();
				taskList.remove(taskList.get((Integer) v.getTag()));
				notifyDataSetChanged();
//				swiped = null;
//				swipedPos = -1;
			}

			@Override
			public void onLongClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
    }
    
	@Override
	public int getCount() {
		return taskList.size();
	}

	@Override
	public Task getItem(int position) {
		return taskList.get(position);
	}

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        VH taskView = new VH();
        boolean isSwiped = position == swipedPos ? true : false;
//	    if (convertView == null) {
			// futasi idoben betoltunk egy layout-ot a LayoutInfalter-el:
//			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.task_item, parent, false);
			/*
			 * Ezzel a betoltessel hatarozzuk meg, hogy milyen szerkezete legyen a lista elemeknek
			 */
	
			taskView.tvTaskTitle = (TextView) convertView.findViewById(R.id.tv_taskTitle);
			taskView.tvTaskEndDate = (TextView) convertView.findViewById(R.id.tv_taskEndDate);
			taskView.tvTaskReqTime = (TextView) convertView.findViewById(R.id.tv_taskReqTime);
	        convertView.setOnTouchListener(listener);
	        convertView.setTag(position);
//	        if(isSwiped){
//	        	listener.setSwipe(convertView,true);
//	        	swiped = convertView;
//	        }
//			convertView.setTag(tvTaskTitle);
//	    } else {
//	    	tvTaskTitle = (TextView) convertView.getTag();
//	    }
	        
	    //Set taskitem's fields
	    taskView.tvTaskTitle.setText(taskList.get(position).getTitle().toString());
	    try{
	    	Date now = new Date();
	    	if(now.getTime() < taskList.get(position).getEndDate().getTime()){
	    		long diff = taskList.get(position).getEndDate().getTime() - now.getTime();
	    		taskView.tvTaskEndDate.setText(
	    				formatDate(new Date(diff),res.getString(R.string.remaining)));
	    	}else{
	    		taskView.tvTaskEndDate.setText(res.getString(R.string.expired));
	    	}
	    }catch(NullPointerException e){
	    	taskView.tvTaskEndDate.setText("");
	    }
	    try{
	    	taskView.tvTaskReqTime.setText(formatDate(taskList.get(position).getRequiredTime(),
	    								   res.getString(R.string.task_req_time)));
	    }catch(NullPointerException e){
	    	taskView.tvTaskReqTime.setText("");
	    }
	    
	    //Set background shape of taskitem
	    GradientDrawable shape = (GradientDrawable)convertView.getBackground();
	    boolean isDone = taskList.get(position).isDone();
	    if(taskList.get(position).getCategories().size()>0){
	    	if(taskList.get(position).getCategories().size()>1){
	    		List<Integer> colorArray = new ArrayList<>();
		    	for(Category idCategory : taskList.get(position).getCategories()){
		    		colorArray.add(isDone ? idCategory.getDarkerColor() : idCategory.getColor());
		    	}
		    	shape.mutate();
		    	shape.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
		    	shape.setColors(convertIntegers(colorArray));
	    	}else{
	    		shape.mutate();
	    		shape.setColor(isDone ? taskList.get(position).getCategories().get(0).getDarkerColor() :
	    								taskList.get(position).getCategories().get(0).getColor());
	    	}	
	    }else{
	    	shape.mutate();
	    	shape.setColor(isDone ? Category.darkerColor(Color.GRAY) : Color.GRAY);
//	    	Log.d("erdekel", taskList.get(position).getTitle().toString());
	    }
	    return convertView;
    }

	private String formatDate(Date endDate, String comment) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		String month = 
				cal.get(Calendar.MONTH)>0 ? 
						Integer.toString(cal.get(Calendar.MONTH) + 1) + " " + 
						res.getString(R.string.month) + " " : "";
//		Log.i("erdekel", "mo: " + Integer.toString(cal.get(Calendar.MONTH)));
		String day =
				cal.get(Calendar.DAY_OF_MONTH) > 1 ?
						Integer.toString(cal.get(Calendar.DAY_OF_MONTH) - 1) + 
						" " + res.getString(R.string.day) + " " : "";
//		Log.i("erdekel", "d: " + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
		String hour = 
				cal.get(Calendar.HOUR_OF_DAY) > 1 ? 
						Integer.toString(cal.get(Calendar.HOUR_OF_DAY) - 1) + 
						" " + res.getString(R.string.hour) + " " : "";
//		Log.i("erdekel", "h: " + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
		String minute =
				cal.get(Calendar.MINUTE) > 0 ? 
						Integer.toString(cal.get(Calendar.MINUTE)) + " " + 
						res.getString(R.string.minute) + " " : "";
//		Log.i("erdekel", "mi: " +Integer.toString(cal.get(Calendar.MINUTE)));
		String together = month + day + hour + minute; 
		return together.isEmpty() ? "" : together + comment;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getSelected() {
		return swipedPos;
	}
	
	public static int[] convertIntegers(List<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
}