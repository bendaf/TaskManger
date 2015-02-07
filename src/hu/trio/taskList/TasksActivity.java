package hu.trio.taskList;

import hu.trio.categoryEdit.CategoryArrayAdapter;
import hu.trio.categoryEdit.CategoryEditActivity;
import hu.trio.database.SQLiteHelper;
import hu.trio.taskmanager.R;
import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.devsmart.android.ui.HorizontalListView;

/**
 * This {@link android.app.Activity} does the most of the functions of the app. 
 * Displays the {@link hu.trio.tasks.Category}s, the {@link hu.trio.tasks.Task}s. 
 * The user can add new Tasks to the taskList, filter the tasks by categories, 
 * remove, set tasks done or they can search between tasks. 
 * There is a lot of possible funtion what is not implemented yet. 
 * 
 * @author Felicián
 *
 */
public class TasksActivity extends Activity implements 
				OnItemLongClickListener, OnItemClickListener, OnClickListener{
	
	// This contains the tasks and the categories
	private static final class DB{
		static ArrayList<Category> categories = new ArrayList<>();
		static ArrayList<Task> tasks = new ArrayList<>();
	}
	
	// private views
	private ListView lvTask;
	private HorizontalListView lvCategory;
	private EditText etAddNewTask;
	private RelativeLayout rtlAddNewTask;
	private Button btnSearch;
	
	// State variables
	//private Task currentTask = null;
	private Category currentCategory = null;
	private Boolean isSearching = false;
	
	// Private fields
	private TaskArrayAdapter mTaskAdapter;
	private CategoryArrayAdapter mCategoryAdapter;
	private SQLiteHelper SQLHelp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tasks);
		
		// Initialize database
		SQLHelp = new SQLiteHelper(getApplicationContext());
		SQLHelp.open();
		DB.categories=SQLHelp.getCategorys();
        DB.tasks=SQLHelp.getTasks(DB.categories);
        SQLHelp.close();
        
		// Initialize views
        /// addNewTask Button
		rtlAddNewTask = (RelativeLayout) findViewById(R.id.rtl_center);
		etAddNewTask = (EditText) findViewById(R.id.et_center);
		etAddNewTask.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// When enter is pressed add task or search and the soft keyborad stays.
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if(!isSearching){
							Task idTask = new Task(etAddNewTask.getText().toString());
							if(currentCategory!=null)idTask.addToCategory(currentCategory);
					        addTask(null, idTask);
					        etAddNewTask.setText("");
					        mTaskAdapter.notifyDataSetChanged();
					        return true;
						
					}else{
						onClick(btnSearch);
						return true;
					}
				}
				return false;
			}
		});
		
		/// search Button
		btnSearch = (Button) findViewById(R.id.btn_right);
		btnSearch.setOnClickListener(this);
		
		/// listView of Tasks
		lvTask = (ListView) findViewById(R.id.lv_tasks);
		mTaskAdapter = new TaskArrayAdapter(getApplicationContext(), DB.tasks);
		lvTask.setAdapter(mTaskAdapter);
		lvTask.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		lvTask.addHeaderView(transView(100)); // This header and foother are below the categorybar 
		lvTask.addFooterView(transView(60));  // and buttonbar.
		SwipeDismissListViewTouchListener touchListener = 
				new SwipeDismissListViewTouchListener(lvTask,
						new SwipeDismissListViewTouchListener.OnSwipeCallback() {
							@Override
							public void onDismiss(ListView listView, int[] reverseSortedPositions) {
								// Delete tasks when dismiss
								SQLHelp.open();
								for(int position : reverseSortedPositions){
									SQLHelp.deleteTask(mTaskAdapter.getItem(position));
									DB.tasks.remove(position);
									//taskAdapter.remove(taskAdapter.getItem(position));
								}
						        SQLHelp.close();
								mTaskAdapter.notifyDataSetChanged();
							}

							@Override
							public void onChangeDone(ListView listView, int pos) {
								// Set tasks done when swipe right. 
								SQLHelp.open();
								DB.tasks.get(pos).setDone(!DB.tasks.get(pos).isDone());
								SQLHelp.modifyTask(DB.tasks.get(pos));
						        SQLHelp.close();
								mTaskAdapter.notifyDataSetChanged();
							}
						}, R.id.rtl_taskItem);
		lvTask.setOnTouchListener(touchListener);
		lvTask.setOnScrollListener(touchListener.makeScrollListener());
		
		
		
		/// ListView of Category 
        lvCategory = (HorizontalListView) findViewById(R.id.lv_categories);
		mCategoryAdapter = new CategoryArrayAdapter(getApplicationContext(), DB.categories);
		lvCategory.setAdapter(mCategoryAdapter);
		lvCategory.setOnItemLongClickListener(this);
		lvCategory.setOnItemClickListener(this);
        
        if(false) loadJunkData(DB.tasks,DB.categories);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Refresh the lists 
		refreshCategoryList();
		mCategoryAdapter.notifyDataSetChanged();
		refreshTaskList();
		mTaskAdapter.notifyDataSetChanged();
		refreshAddNewTaskRTL();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
		// Starts the category edit Activity
		Intent startCategoryEdit = new Intent(this, CategoryEditActivity.class);
		startActivity(startCategoryEdit);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Change the current category
		if(position < 1){
			currentCategory = null;
		}else{
			currentCategory = mCategoryAdapter.getItem(position);
		}
		
		// Refresh the view
		refreshTaskList();
		mTaskAdapter.notifyDataSetChanged();
		refreshAddNewTaskRTL();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_right:
			if(!isSearching){			// If not searching then searching and show the soft keyboard
				isSearching = true;
				etAddNewTask.setHint(R.string.search);
				etAddNewTask.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(etAddNewTask, InputMethodManager.SHOW_IMPLICIT);
			}else{						// If searching then search on the string
				String searchString = etAddNewTask.getText().toString();
				refreshTaskList();
				Iterator<Task> itask = DB.tasks.iterator();
				while(itask.hasNext()){
					Task idTask = itask.next();
					if(!containsIgnoreCase(idTask.getTitle(),searchString)) itask.remove();
				}
				mTaskAdapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed(){
		if(isSearching){ 				// If searching then not searching;
			isSearching = false;
			etAddNewTask.setText("");
			etAddNewTask.setHint(R.string.add_new_task);
			refreshTaskList();
		}else{							// Else call super
			super.onBackPressed();
		}
	}
	
    /**
     * Refresh the category list from database but not the view! 
     * You have to notify the adapter from the changes
     */
    public void refreshCategoryList() {
		SQLHelp.open();
		DB.categories.clear();
		DB.categories.addAll(SQLHelp.getCategorys());
		SQLHelp.close();
		
	}
	
	/**
     * Refresh the task list from database but not the view! 
     * You have to notify the adapter from the changes
     */
	public void refreshTaskList() {
		SQLHelp.open();
		ArrayList<Task> idTasks = SQLHelp.getTasks(DB.categories);
		SQLHelp.close();
		if(currentCategory != null){
			Iterator<Task> itask = idTasks.iterator();
			while(itask.hasNext()){
				Task idtask = itask.next();
				if(!idtask.isInTheCategory(currentCategory))itask.remove();
			}	
		}
		DB.tasks.clear();
		DB.tasks.addAll(idTasks);
	}

	/**
	 *  Refresh the background of the AddNewTask RelativeLayout.
	 */
	public void refreshAddNewTaskRTL(){
		if(currentCategory != null){
			GradientDrawable shape = (GradientDrawable)rtlAddNewTask.getBackground();
			shape.mutate();
			shape.setColor(currentCategory.getColor());
		}else{
			rtlAddNewTask.setBackground(getResources().getDrawable(R.drawable.roundedbutton));
		}
	}
	
	/**
	 * Search in a string some other string. Not case sensitive. 
	 * 
	 * @param src The source string
	 * @param searchString The searched string
	 * @return True if the src contains the searchString
	 */
	public static boolean containsIgnoreCase(String src, String searchString) {
	    final int length = searchString.length();
	    if (length == 0)
	        return true; // Empty string is contained

	    final char firstLo = Character.toLowerCase(searchString.charAt(0));
	    final char firstUp = Character.toUpperCase(searchString.charAt(0));

	    for (int i = src.length() - length; i >= 0; i--) {
	        // Quick check before calling the more expensive regionMatches() method:
	        final char ch = src.charAt(i);
	        if (ch != firstLo && ch != firstUp)
	            continue;

	        if (src.regionMatches(true, i, searchString, 0, length))
	            return true;
	    }

	    return false;
	}
	
	// Add a the task to the database.
	protected void addTask(Task parent, Task task){
		SQLHelp.open();
		SQLHelp.addTask(task);
		SQLHelp.close();
		
		if(parent == null){
			DB.tasks.add(0,task);
		}else{
			parent.addSubTask(0,task);
		}
	}
	
	// Remove task from database.
	protected void removeTask(Task parent, Task task){
		SQLHelp.open();
		SQLHelp.deleteTask(task);
		SQLHelp.close();
		
		if(parent == null){
			DB.tasks.remove(task);
		}else{
			parent.removeSubTask(task);
		}
	}
	
	// Replace task in database
	protected void replaceTask(Task parentTask, Task task, int index){
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
	
	// Return with the tasks in category
	protected ArrayList<Task> getTasksOfCategory(Task parentTask, Category cat){
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

	// This make a view for header and footer
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

		int[] catColors = getResources().getIntArray(R.array.categories);
        categories.add(new Category("Munka",catColors[0]));
        categories.add(new Category("Tanulás",catColors[1]));
        categories.add(new Category("Család",catColors[2]));
        categories.add(new Category("Szülinapok",catColors[3]));
        categories.add(new Category("",catColors[4]));
        categories.add(new Category("",catColors[5]));
        categories.add(new Category("",catColors[6]));
        categories.add(new Category("",catColors[7]));
        categories.add(new Category("",catColors[8]));
        categories.add(new Category("",catColors[9]));
        categories.add(new Category("",catColors[10]));
        categories.add(new Category("",catColors[11]));
        categories.add(new Category("",catColors[12]));
        categories.add(new Category("",catColors[13]));
        categories.add(new Category("",catColors[14]));
        categories.add(new Category("",catColors[15]));	
        
        Random r = new Random();
		for(Task idTask : tasks){
			idTask.addToCategory(categories.get(r.nextInt(categories.size())));
		}
		
		SQLHelp.open();
		SQLHelp.reset();
		for(int i=0;i<DB.tasks.size();i++){
			SQLHelp.addTask(DB.tasks.get(i));
		}
		for(int i=0;i<DB.categories.size();i++){
			SQLHelp.addCategory(DB.categories.get(i));
		}
		SQLHelp.close();
    }
}



