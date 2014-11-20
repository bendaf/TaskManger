package hu.trio.taskmanager;

import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.devsmart.android.ui.HorizontalListView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TasksActivity extends Activity implements OnKeyListener{
	
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
	private EditText addNewTaskEt;
	
	private SQLiteHelper SQLHelp=new SQLiteHelper(this);
	/* SQLHelp.open(); SQLHelp.close(); */
	
	private void loadJunkData(ArrayList<Task> tasks, ArrayList<Category> categories) {
		tasks.add(new Task("Bevásárlás"));
		tasks.add(new Task("Séta"));
		tasks.add(new Task("Futás"));
		tasks.add(new Task("Alma vásárlása a kisboldban"));
		tasks.add(new Task("Krisz felköszönt"));
		tasks.add(new Task("Ajándékot venni"));
		tasks.add(new Task("Jogsi"));
		tasks.add(new Task("Szallagavató zene"));
		tasks.add(new Task("Kiskutya"));
		tasks.add(new Task("Kép nyomtat"));
		tasks.add(new Task("Szervízbe vinni a kocsit"));
		tasks.add(new Task("Fésülködés"));
		tasks.add(new Task("Fogmosás"));
		tasks.add(new Task("Gazsi felhív"));
		
		categories.add(new Category("Saját"));
		categories.add(new Category("Szülinapok"));
		categories.add(new Category("Állat"));
		categories.add(new Category("Család"));
		categories.add(new Category("Tanulás"));
		categories.add(new Category("Vasutasok"));
		categories.add(new Category("Munka"));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tasks);
		
		addNewTaskEt = (EditText) findViewById(R.id.et_center);
		addNewTaskEt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v.getId() == addNewTaskEt.getId() && addNewTaskEt.getText().
											toString().equals(getResources().getString(R.string.add_new_task))){
					addNewTaskEt.setText("");
				}
			}
		});
		addNewTaskEt.setOnKeyListener(this);
		
		taskListView = (ListView) findViewById(R.id.lv_tasks);
        taskAdapter = new TaskArrayAdapter(getApplicationContext(), DB.tasks);
		taskListView.setAdapter(taskAdapter);
		taskListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		LinearLayout viewHeader = new LinearLayout(getApplicationContext());
		viewHeader.setOrientation(LinearLayout.HORIZONTAL);
		//convert dp to pixels
		LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						 (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
						  100, 
						  getResources().getDisplayMetrics()));
		viewHeader.setLayoutParams(lp);
		taskListView.addHeaderView(viewHeader);
		
        categoryListView = (HorizontalListView) findViewById(R.id.lv_categories);
		categoryAdapter = new CategoryArrayAdapter(getApplicationContext(), DB.categories);
		categoryListView.setAdapter(categoryAdapter);
		
		SQLHelp.open();
		DB.tasks=SQLHelp.getTasks();
		DB.categories=SQLHelp.getCategorys();
		SQLHelp.close();
		
		boolean junkData=false;
		if(junkData){
			loadJunkData(DB.tasks,DB.categories);
			boolean works=true;
			// itt a generált adatokat berakom.
			try{
				SQLHelp.open();
				for(int i=0;i<DB.tasks.size();i++){
					SQLHelp.addTask(DB.tasks.get(i));
				}
				DB.tasks=SQLHelp.getTasks();
				for(int i=0;i<DB.categories.size();i++){
					SQLHelp.addCategory(DB.categories.get(i));
				}
				DB.categories=SQLHelp.getCategorys();
				SQLHelp.close();
			}catch(Exception e){
				works=false;
			}finally{
				if(works){
					//ha sikerült megy tovább. amúgy lehet hibaüzenet.
					Dialog d=new Dialog(this);
					TextView tv=new TextView(this);
					d.setTitle("Task adding");
					tv.setText("Worked");
					d.setContentView(tv);
					d.show();
				}
			}
		}
		
//		DB.tasks.get(0).setEndDate(new Date());
//		DB.tasks.get(2).setRequiredTime(new Date(3600));
		
//		Random r = new Random();
//		for(Task idTask : DB.tasks){
//			idTask.addToCategory(DB.categories.get(r.nextInt(DB.categories.size())));
//		}
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
		SQLHelp.open();
		SQLHelp.addTask(task);
		SQLHelp.close();
		
		if(parent == null){
			DB.tasks.add(task);
		}else{
			parent.addSubTask(task);
		}
	}
	
	public void removeTask(Task parent, Task task){
		SQLHelp.open();
		SQLHelp.deleteTask(task);
		SQLHelp.close();
		
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

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
	          addTask(null, new Task(addNewTaskEt.getText().toString()));
	          addNewTaskEt.setText("");
	          taskAdapter.notifyDataSetChanged();
	          return true;
		}
		return false;
	}
}
