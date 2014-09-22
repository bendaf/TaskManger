package hu.trio.taskmanager;

import hu.trio.tasks.Categories;
import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class TasksActivity extends Activity {
	private ArrayList<Task> tasks;
	private Categories categories;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tasks);
		
		tasks = new ArrayList<>();
		categories = new Categories();
		
		tasks.add(new Task("Bevásárlás"));
		tasks.get(0).setRequiredTime(30);
		tasks.add(new Task("Séta"));
		
		categories.addCategory(new Category("Otthoni"));
		tasks.get(0).addToCategory(categories.getCategory(0));
		Log.d("erdekel", tasks.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tasks, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addTask(Task task){
		tasks.add(task);
	}
	
	public void removeTask(int index){
		tasks.remove(index);
	}
	
	public void finishTask(int index){
		tasks.get(index).setDone(true);
	}
	
	public int indexOfTask(Task task){
		return tasks.indexOf(task);
	}
	
	public void replaceTask(Task task, int index){
		if(tasks.remove(task)){
			tasks.add(index, task);
		}
	}
	/// Visszatér a tasks listában lévő és az adott kategóriába tartozó taskokkal.
	public ArrayList<Task> getTasksOfCategory(Category cat){
		ArrayList<Task> reqCat = new ArrayList<>();
		for(Task idTask : tasks){
			if(idTask.isInTheCategory(cat))reqCat.add(idTask);
		}
		return reqCat;
	}
}
