package hu.trio.tasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

/**
 * This object represent a task what have a lot of properties:  id, title, description,
 * {@link hu.trio.tasks.Category}s, endDate, isDone, parentTask, subTasks, requiredTime.
 * You can access to this properties with this class. 
 * 
 * @author bendaf
 *
 */
public class Task {
	
	private static long counter;
	private final long id;
	private String title;
	private final ArrayList<Category> categories;
	private String description;
	private Date endDate;
	private boolean lastIsDone;
	private boolean isDone;
	private final Task parentTask;
	private final ArrayList<Task> subTasks;
	private Date requiredTime; // 0 perc = 1970, Január 01. 1ó 0p 0mp
	private final Date childReqTime;
	
	static{
		Calendar c = Calendar.getInstance();
		counter = c.getTimeInMillis();
	}

	public Task(long id, String title, ArrayList<Category> categories, String description,
			    Date endDate, boolean isDone, boolean lastIsDone, Task parentTask,
                ArrayList<Task> subTasks, Date requiredTime, Date childRequiredTime){
		
		this.id = id;
		this.title = title;
		this.categories = categories;
		this.description = description;
		this.endDate = endDate;
		this.isDone = isDone;
        this.lastIsDone = lastIsDone;
		this.parentTask = parentTask;
		this.subTasks = subTasks;
		this.requiredTime = requiredTime;
		this.childReqTime = childRequiredTime;
	}
	
	public boolean isLastIsDone() {
		return lastIsDone;
	}
	
	public Date getChildReqTime() {
		return childReqTime;
	}

	public Task(Task task){
		this(task.getId(),task.getTitle(),task.getCategories(),task.getDescription(),
		     task.getEndDate(),task.isDone, task.lastIsDone, task.getParentTask(),task.getSubTasks(),
		     task.getRequiredTime(),task.childReqTime);
	}
	
	public Task(String title, Task parentTask){
		this(counter++, title, new ArrayList<Category>(), null, 
			 null, false, false, parentTask, new ArrayList<Task>(),
			 null, null);
	}
	
	public Task(String title){
		this(title, null);
	}
	
	public long getId() {
		return id;
	}
	public void addToCategory(Category cat){
		categories.add(cat);
	}
	
	public boolean isInTheCategory(Category cat){
		boolean isInCategories = false;
		for(Category idCat : categories){
			if(idCat.getColor() == cat.getColor())isInCategories = true;
		}
		return isInCategories;
	}
	public ArrayList<Category> getCategories(){
		return new ArrayList<Category>(categories);
	}
	public boolean removeFromCategory(Category cat){
		return categories.remove(cat);
	}
	public void addSubTask(Task subTask){
		if(subTasks.add(subTask))
			childReqTime.setTime(childReqTime.getTime() + subTask.getRequiredTime().getTime());
		
	}
	public void addSubTask(int index, Task subTask) {
		try{
			subTasks.add(index,subTask);
			childReqTime.setTime(childReqTime.getTime() + subTask.getRequiredTime().getTime());
		}catch(IndexOutOfBoundsException e){
			Log.e("erdekel", e.getMessage());
		}
		
	}
	public ArrayList<Task> getSubTasks(){
		return new ArrayList<Task>(subTasks);
	}
	public boolean removeSubTask(Task subTask){
		if(subTask != null && subTasks.contains(subTask)){
			childReqTime.setTime(childReqTime.getTime() - subTask.getRequiredTime().getTime());
		}
		return subTasks.remove(subTask);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return description of the task. Can be null
	 */
	public String getDescription() {
		try{
			return description;
		}catch (NullPointerException e) {
			return null;
		}
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return end date of the task. Can be null
	 */
	public Date getEndDate() {
		if(endDate != null)
			return new Date(endDate.getTime());
		else
			return null;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public boolean isDone() {
		return isDone;
	}
	public void setDone(boolean isDone) {
		lastIsDone = this.isDone;
		this.isDone = isDone;
	}
	public void restoreIsDone(){
		isDone = lastIsDone;
	}
	/**
	 * @return Parent task. Can be null.
	 */
	public Task getParentTask() {
		try{
			return new Task(parentTask);
		}catch(NullPointerException e){
			return null;
		}
	}
	/**
	 * @return the required time of the task. Can be null!
	 */ 
	public Date getRequiredTime() {
		if(requiredTime != null && childReqTime != null){
			return new Date(requiredTime.getTime() > childReqTime.getTime() ? 
							requiredTime.getTime() : childReqTime.getTime());
		}else if(requiredTime != null){
			return new Date(requiredTime.getTime());
		}else 
			return null;
	}
	public void setRequiredTime(Date requiredTime) {
		this.requiredTime = requiredTime;
	}
}
