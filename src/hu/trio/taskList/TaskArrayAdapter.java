package hu.trio.taskList;

import hu.trio.taskmanager.R;
import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
	
	private ArrayList<Task> mTaskList = new ArrayList<Task>();
	private Context mcontext;
	
	public TaskArrayAdapter(Context context, ArrayList<Task> tasks) {
		mTaskList = tasks;
        this.mcontext = context;
		
	}
	@Override
	public int getCount() {
		return mTaskList.size();
	}

	@Override
	public Task getItem(int position) {
		return mTaskList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        VH taskView = new VH();
	    if (convertView == null) {
			// futasi idoben betoltunk egy layout-ot a LayoutInfalter-el:
			LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.task_item, parent, false);
			/*
			 * Ezzel a betoltessel hatarozzuk meg, hogy milyen szerkezete legyen a lista elemeknek
			 */
			
			taskView.tvTaskTitle = (TextView) convertView.findViewById(R.id.tv_taskTitle);
			taskView.tvTaskEndDate = (TextView) convertView.findViewById(R.id.tv_taskEndDate);
			taskView.tvTaskReqTime = (TextView) convertView.findViewById(R.id.tv_taskReqTime);
			
			convertView.setTag(taskView);
	    } else {
	    	taskView = (VH) convertView.getTag();
	    }
	        
	    //Set taskitem's fields
	    taskView.tvTaskTitle.setText(mTaskList.get(position).getTitle().toString());
	    
	    try{ 
	    	Date now = new Date();
	    	if(now.getTime() < mTaskList.get(position).getEndDate().getTime()){
	    		long diff = mTaskList.get(position).getEndDate().getTime() - now.getTime();
	    		taskView.tvTaskEndDate.setText(
	    				formatDate(new Date(diff),mcontext.getResources().getString(R.string.remaining)));
	    	}else{
	    		taskView.tvTaskEndDate.setText(mcontext.getResources().getString(R.string.expired));
	    	}
	    }catch(NullPointerException e){
	    	taskView.tvTaskEndDate.setText("");
	    }
	    
	    try{
	    	taskView.tvTaskReqTime.setText(formatDate(mTaskList.get(position).getRequiredTime(),
	    								   mcontext.getResources().getString(R.string.task_req_time)));
	    }catch(NullPointerException e){
	    	taskView.tvTaskReqTime.setText("");
	    }
	    
	    //Set background shape of taskitem
	    GradientDrawable shape = (GradientDrawable)convertView.getBackground();
	    boolean isDone = mTaskList.get(position).isDone();
	    ArrayList<Category> categoriesOfTask = mTaskList.get(position).getCategories();
	    if(categoriesOfTask.size()>0){
	    	if(categoriesOfTask.size()>1){
	    		List<Integer> colorArray = new ArrayList<>();
		    	for(Category idCategory : categoriesOfTask){
		    		if(!idCategory.getTitle().equals(""))
		    			colorArray.add(isDone ? idCategory.getDarkerColor() : idCategory.getColor());
		    	}
		    	shape.mutate();
		    	shape.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
		    	shape.setColors(convertIntegers(colorArray));
	    	}else{
	    		if(!categoriesOfTask.get(0).getTitle().equals("")){
	    			setColorOfShape(shape, categoriesOfTask.get(0).getColor(), isDone);
	    		}else{
	    	    	setColorOfShape(shape, Color.GRAY, isDone);
	    		}
	    	}	
	    }else{
	    	setColorOfShape(shape, Color.GRAY, isDone);
	    }
	    return convertView;
	}
	
	private void setColorOfShape(GradientDrawable shape, int color, boolean isDone){
		shape.mutate();
    	shape.setColor(isDone ? Category.darkerColor(color) : color);
	}
	
	private String formatDate(Date date, String comment) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String month = 
				cal.get(Calendar.MONTH)>0 ? 
						Integer.toString(cal.get(Calendar.MONTH) + 1) + " " + 
						mcontext.getResources().getString(R.string.month) + " " : "";
//		Log.i("erdekel", "mo: " + Integer.toString(cal.get(Calendar.MONTH)));
		String day =
				cal.get(Calendar.DAY_OF_MONTH) > 1 ?
						Integer.toString(cal.get(Calendar.DAY_OF_MONTH) - 1) + 
						" " + mcontext.getResources().getString(R.string.day) + " " : "";
//		Log.i("erdekel", "d: " + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
		String hour = 
				cal.get(Calendar.HOUR_OF_DAY) > 1 ? 
						Integer.toString(cal.get(Calendar.HOUR_OF_DAY) - 1) + 
						" " + mcontext.getResources().getString(R.string.hour) + " " : "";
//		Log.i("erdekel", "h: " + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
		String minute =
				cal.get(Calendar.MINUTE) > 0 ? 
						Integer.toString(cal.get(Calendar.MINUTE)) + " " + 
						mcontext.getResources().getString(R.string.minute) + " " : "";
//		Log.i("erdekel", "mi: " +Integer.toString(cal.get(Calendar.MINUTE)));
		String together = month + day + hour + minute; 
		return together.isEmpty() ? "" : together + comment;
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
