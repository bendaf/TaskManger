package hu.trio.tasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Task {
	
	private static long counter;
	private final long id;
	private String title;
	private ArrayList<Category> categories;
	private String description;
	private Date endDate;
	private boolean lastIsDone;
	private boolean isDone;
	private final Task parentTask;
	private ArrayList<Task> subTasks;
	private Date requiredTime; // 0 perc = 1970, Január, 0, 0, 0
	private Date childReqTime; 
	
	static{
		Calendar c = Calendar.getInstance();
		counter = c.getTimeInMillis();
	}
	
	public Task(long id, String title, ArrayList<Category> categories, String description, 
			    Date endDate, boolean isDone, Task parentTask, ArrayList<Task> subTasks,
			    Date requiredTime, Date childRequiredTime){
		
		this.id = id;
		this.title = title;
		this.categories = categories;
		this.description = description;
		this.endDate = endDate;
		this.isDone = isDone;
		this.parentTask = parentTask;
		this.subTasks = subTasks;
		this.requiredTime = requiredTime;
		this.childReqTime = childRequiredTime;
	}
	
	public Task(Task task){
		this(task.getId(),task.getTitle(),task.getCategories(),task.getDescription(),
		     task.getEndDate(),task.isDone,task.getParentTask(),task.getSubTasks(),
		     task.getRequiredTime(),task.childReqTime);
	}
	
	public Task(String title, Task parentTask){
		this(counter++, title, new ArrayList<Category>(), null, null, false, 
			 parentTask, new ArrayList<Task>(), null, null);
	}
	
	public Task(String title){
		this(title, null);
	}
	
	public long getId() {
		return Long.valueOf(id);
	}
	public void addToCategory(Category cat){
		categories.add(cat);
	}
	
	//valami
	public boolean isInTheCategory(Category cat){
		return categories.contains(cat);
	}
	public ArrayList<Category> getCategories(){
		return new ArrayList<Category>(categories);
	}
	public boolean removeFromCategory(Category cat){
		return categories.remove(cat);
	}
	public void addSubTask(Task subTask){
		childReqTime.setTime(childReqTime.getTime() + subTask.getRequiredTime().getTime());
		subTasks.add(subTask);
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
		return new String(title);
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return new String(description);
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getEndDate() {
		return new Date(endDate.getTime());
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public boolean isDone() {
		return Boolean.valueOf(isDone);
	}
	public void setDone(boolean isDone) {
		lastIsDone = this.isDone;
		this.isDone = isDone;
	}
	public void restoreIsDone(){
		isDone = lastIsDone;
	}
	public Task getParentTask() {
		return new Task(parentTask);
	}
	public Date getRequiredTime() {
		return new Date(requiredTime.getTime() > childReqTime.getTime() ? 
						requiredTime.getTime() : childReqTime.getTime());
	}
	public void setRequiredTime(Date requiredTime) {
		this.requiredTime = requiredTime;
	}
}
