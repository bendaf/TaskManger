package hu.trio.taskmanager;

import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskArrayAdapter extends BaseAdapter {

	
    TextView tvTaskName;
	
	private ArrayList<Task> taskList = new ArrayList<Task>();
	private SwipeTouchListener listener;
    private View swiped = null;
    private Integer swipedPos = -1;
    Context context;

    public TaskArrayAdapter(Context context, ArrayList<Task> tasks) {
        this.taskList=tasks;
        this.context=context;
        this.listener=new SwipeTouchListener(context, R.id.rtl_taskItem, new SwipeListener() {
			public void onFirstClick(View v) {}
			
			@Override
			public void onSecondClick(View v) {}

			@Override
			public void onSwipeOff(View v) {
				if(swiped != null) listener.swipeIn(swiped);
				swiped = v;
				swipedPos = 1;
			}

			@Override
			public void onSwipeIn(View v) {
				swiped = null;
				swipedPos = -1;
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
        TextView tvTaskTitle = null;
        boolean isSwiped = position == swipedPos ? true : false;
//	    if (convertView == null) {
			// futasi idoben betoltunk egy layout-ot a LayoutInfalter-el:
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.task_item, null);
			/*
			 * Ezzel a betoltessel hatarozzuk meg, hogy milyen szerkezete legyen a lista elemeknek
			 */
	
			tvTaskTitle = (TextView) convertView.findViewById(R.id.tv_taskTitle);
	        convertView.setOnTouchListener(listener);
	        if(isSwiped){
	        	listener.setSwipe(convertView,true);
	        	swiped = convertView;
	        }
//			convertView.setTag(tvTaskTitle);
//	    } else {
//	    	tvTaskTitle = (TextView) convertView.getTag();
//	    }
	    // valodi tartalom atadasa
	    tvTaskTitle.setText(taskList.get(position).getTitle().toString());
	    GradientDrawable shape = (GradientDrawable)convertView.getBackground();
	    if(taskList.get(position).getCategories().size()>0){
	    	if(taskList.get(position).getCategories().size()>1){
	    		List<Integer> colorArray = new ArrayList<>();
		    	for(Category idCategory : taskList.get(position).getCategories()){
		    		colorArray.add(idCategory.getColor());
		    	}
		    	shape.mutate();
		    	shape.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
		    	shape.setColors(convertIntegers(colorArray));
	    	}else{
	    		shape.mutate();
	    		shape.setColor(taskList.get(position).getCategories().get(0).getColor());
	    	}	
	    }else{
	    	shape.mutate();
	    	shape.setColor(Color.GRAY);
	    	Log.d("erdekel", taskList.get(position).getTitle().toString());
	    }
	    return convertView;
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