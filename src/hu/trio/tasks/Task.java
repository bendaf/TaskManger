package hu.trio.tasks;

import java.util.ArrayList;
import java.util.Date;

public class Task {
	//private int number;
	private String title;
	private ArrayList<Category> categories;
	private String description;
	private Date endDate;
	private boolean isDone;
	private ArrayList<Task> subTasks;
	private int requiredTime; // in minutes
	
	public Task(String title){
		categories = new ArrayList<>();
		subTasks = new ArrayList<>();
		this.title=title;
		isDone=false;
	}
	public void addToCategory(Category cat){
		categories.add(cat);
	}
	
	//valami
	public boolean isInTheCategory(Category cat){
		return categories.contains(cat);
	}
	public boolean removeFromCategory(Category cat){
		return categories.remove(cat);
	}
	public void addSubTask(Task subTask){
		subTasks.add(subTask);
	}
	public ArrayList<Task> getSubTasks(){
		return subTasks;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public boolean isDone() {
		return isDone;
	}
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
	public int getRequiredTime() {
		refreshReqTime();
		return requiredTime;
	}
	private void refreshReqTime() {
//		requiredTime = 0;
		int childRequiredTime = 0;
		for(Task task : subTasks){
			childRequiredTime += task.getRequiredTime();
		}
		if(childRequiredTime > requiredTime)
			requiredTime = childRequiredTime;
	}
	public void setRequiredTime(int requiredTime) {
		this.requiredTime = requiredTime;
	}
}
