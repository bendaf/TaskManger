package hu.trio.taskActivity;

import hu.trio.categoryEditView.CategoryArrayAdapter;
import hu.trio.categoryEditView.CategoryEditActivity;
import hu.trio.database.SQLiteHelper;
import hu.trio.taskmanager.R;
import hu.trio.taskmanager.R.id;
import hu.trio.taskmanager.R.layout;
import hu.trio.taskmanager.R.string;
import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import com.devsmart.android.ui.HorizontalListView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TasksActivity extends Activity implements OnKeyListener, OnItemLongClickListener, OnItemClickListener{
	
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
	private RelativeLayout addNewTaskRtl;
	
	private SQLiteHelper SQLHelp;
	/* SQLHelp.open(); SQLHelp.close(); */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tasks);
		
		SQLHelp = new SQLiteHelper(getApplicationContext());
		addNewTaskRtl = (RelativeLayout) findViewById(R.id.rtl_center);
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
		
		SQLHelp.open();
		SQLHelp.reset();
		//SQLHelp.Load(DB.tasks,DB.categories);//Not working.
		DB.categories=SQLHelp.getCategorys();
        DB.tasks=SQLHelp.getTasks(DB.categories);
        SQLHelp.close();
        
		taskListView = (ListView) findViewById(R.id.lv_tasks);
        taskAdapter = new TaskArrayAdapter(getApplicationContext(), DB.tasks);
		taskListView.setAdapter(taskAdapter);
		taskListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		taskListView.addHeaderView(transView(100));
		taskListView.addFooterView(transView(60));
		
        categoryListView = (HorizontalListView) findViewById(R.id.lv_categories);
		categoryAdapter = new CategoryArrayAdapter(getApplicationContext(), DB.categories);
		categoryListView.setAdapter(categoryAdapter);
		categoryListView.setOnItemLongClickListener(this);
		categoryListView.setOnItemClickListener(this);
        
		
        boolean junkData=true;
        if(junkData){
            loadJunkData(DB.tasks,DB.categories);
	
			// itt a generált adatokat berakom.
			try{
				SQLHelp.open();
				for(int i=0;i<DB.tasks.size();i++){
					SQLHelp.addTask(DB.tasks.get(i));
				}
				for(int i=0;i<DB.categories.size();i++){
					SQLHelp.addCategory(DB.categories.get(i));
				}
				//DB.tasks=SQLHelp.getTasks(DB.categories);
				SQLHelp.close();
				
				//ha sikerült megy tovább. amúgy lehet hibaüzenet.
				Dialog d=new Dialog(this);
				TextView tv=new TextView(this);
				d.setTitle("Task adding");
				tv.setText("Worked");
				d.setContentView(tv);
//				d.show();
			}catch(Exception e){ ///TODO 
				
			}
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SQLHelp.open();
		DB.categories.clear();
		DB.categories.addAll(SQLHelp.getCategorys());
//        DB.tasks=SQLHelp.getTasks(DB.categories);
		SQLHelp.close();
//		taskAdapter.notifyDataSetChanged();
		categoryAdapter.notifyDataSetChanged();
//		Log.d("erdekel", "Notify");
	}
	
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
        
        categories.add(new Category("Szülinapok"));
        categories.add(new Category("Család"));
        categories.add(new Category("Tanulás"));
        categories.add(new Category("Munka"));
        
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 2);
//		tasks.get(0).setEndDate(c.getTime());
//		tasks.get(2).setRequiredTime(new Date(360000*2));
        
        Random r = new Random();
		for(Task idTask : tasks){
			idTask.addToCategory(categories.get(r.nextInt(categories.size())));
			//
		}
    }

	private View transView(int height) {
		LinearLayout view = new LinearLayout(getApplicationContext());
		view.setOrientation(LinearLayout.HORIZONTAL);
		//convert dp to pixels
		LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						 (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
						  height, 
						  getResources().getDisplayMetrics()));
		view.setLayoutParams(lp);
		return view;
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
			Task idTask = new Task(addNewTaskEt.getText().toString());
			if(currentCategory!=null)idTask.addToCategory(currentCategory);
	        addTask(null, idTask);
	        addNewTaskEt.setText("");
	        taskAdapter.notifyDataSetChanged();
	        return true;
		}
		return false;
	}

	private void refreshView() {
		SQLHelp.open();
		ArrayList<Task> idTasks = SQLHelp.getTasks(DB.categories);
		SQLHelp.close();
		if(currentCategory != null){
			Iterator<Task> itask = idTasks.iterator();
			while(itask.hasNext()){
				Task idtask = itask.next();
				if(!idtask.isInTheCategory(currentCategory))itask.remove();
			}
			GradientDrawable shape = (GradientDrawable)addNewTaskRtl.getBackground();
			shape.mutate();
    		shape.setColor(currentCategory.getColor());
		}else{
			addNewTaskRtl.setBackground(getResources().getDrawable(R.drawable.roundedbutton));
		}
		DB.tasks.clear();
		DB.tasks.addAll(idTasks);
		taskAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
//		Log.d("erdekel", Integer.toString(v.getId()) +" "+ Integer.toString(R.id.lv_categories));
//		if(v.getId() == R.id.lv_categories){
			Intent startCategoryEdit = new Intent(this, CategoryEditActivity.class);
			startActivity(startCategoryEdit);
//		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(position < 1){
			currentCategory = null;
		}else{
			currentCategory = DB.categories.get(position-1);
		}
		refreshView();
	}
}


