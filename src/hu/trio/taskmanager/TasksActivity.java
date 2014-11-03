package hu.trio.taskmanager;

import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.devsmart.android.ui.HorizontalListView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

//TROLLOLOOOOO

public class TasksActivity extends Activity {
	
	private static final class DB{
		static ArrayList<Category> categories = new ArrayList<>();
		static ArrayList<Task> tasks = new ArrayList<>();
	}
	
	private Task currentTask = null;
	private Category currentCategory = null;
	private TaskArrayAdapter taskAdapter;
	private CategoryArrayAdapter categoryAdapter;
	private ListView taskListView;
	private HorizontalListView categoryListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tasks);
		
		currentTask = null;
		taskListView = (ListView) findViewById(R.id.lv_tasks);
        taskAdapter= new TaskArrayAdapter(getApplicationContext(), DB.tasks);
		taskListView.setAdapter(taskAdapter);
		
        categoryListView = (HorizontalListView) findViewById(R.id.lv_categories);
		categoryAdapter = new CategoryArrayAdapter(getApplicationContext(), DB.categories);
		categoryListView.setAdapter(categoryAdapter);
		
		DB.tasks.add(new Task("Bevásárlás"));
		DB.tasks.add(new Task("Séta"));
		DB.tasks.add(new Task("Futás"));
		DB.tasks.add(new Task("Alma vásárlása a kisboldban"));
		DB.tasks.add(new Task("Krisz felköszönt"));
		DB.tasks.add(new Task("Ajándékot venni"));
		DB.tasks.add(new Task("Jogsi"));
		DB.tasks.add(new Task("Szallagavató zene"));
		DB.tasks.add(new Task("Kiskutya"));
		DB.tasks.add(new Task("Kép nyomtat"));
		DB.tasks.add(new Task("Szervízbe vinni a kocsit"));
		DB.tasks.add(new Task("Fésülködés"));
		DB.tasks.add(new Task("Fogmosás"));
		DB.tasks.add(new Task("Gazsi felhív"));
		
		
		DB.categories.add(new Category("Saját"));
		DB.categories.add(new Category("Szülinapok"));
		DB.categories.add(new Category("Állat"));
		DB.categories.add(new Category("Család"));
		DB.categories.add(new Category("Tanulás"));
		DB.categories.add(new Category("Vasutasok"));
		DB.categories.add(new Category("Munka"));

		DB.tasks.get(0).setEndDate(new Date());
		DB.tasks.get(2).setRequiredTime(new Date(3600));
		
		Random r = new Random();
		for(Task idTask : DB.tasks){
			idTask.addToCategory(DB.categories.get(r.nextInt(DB.categories.size())));
		}
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
	
	public void addTask(Task parent, Task task){
		if(parent == null){
			DB.tasks.add(task);
		}else{
			parent.addSubTask(task);
		}
	}
	
	public void removeTask(Task parent, Task task){
		if(parent == null){
			DB.tasks.remove(task);
		}else{
			parent.removeSubTask(task);
		}
	}
	
	public void replaceTask(Task parentTask, Task task, int index){
		if(parentTask == null){
			if(DB.tasks.remove(task)){
				DB.tasks.add(index, task);
			}
		}else{
			if(parentTask.removeSubTask(task)){
				parentTask.addSubTask(index, task);
			}
		}
	}
	
	/// Visszatér a fában lévő és az adott kategóriába tartozó taskokkal.
	public ArrayList<Task> getTasksOfCategory(Task parentTask, Category cat){
		ArrayList<Task> reqCat = new ArrayList<>();
		if(parentTask == null){
			for(Task idTask : DB.tasks){
				reqCat.addAll(getTasksOfCategory(idTask, cat));
				if(idTask.isInTheCategory(cat))reqCat.add(idTask);
			}
		}else {
			for(Task idTask : parentTask.getSubTasks()){
				reqCat.addAll(getTasksOfCategory(idTask, cat));
				if(idTask.isInTheCategory(cat))reqCat.add(idTask);
			}
		}
		return reqCat;
	}
}
