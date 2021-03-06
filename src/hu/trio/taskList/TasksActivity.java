package hu.trio.taskList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;

import java.util.ArrayList;
import java.util.Iterator;

import hu.trio.categoryEdit.CategoryArrayAdapter;
import hu.trio.categoryEdit.CategoryEditActivity;
import hu.trio.database.SQLiteHelper;
import hu.trio.taskmanager.R;
import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

/**
 * This {@link android.app.Activity} does the most of the functions of the app. 
 * Displays the {@link hu.trio.tasks.Category}s, the {@link hu.trio.tasks.Task}s. 
 * The user can add new Tasks to the taskList, filter the tasks by categories, 
 * remove, set tasks done or they can search between tasks. 
 * There is a lot of possible function what is not implemented yet.
 *
 * @author Felician
 *
 */
public class TasksActivity extends Activity implements
        OnItemLongClickListener, OnItemClickListener, OnClickListener{

    // This contains the tasks and the categories
    private static final class LD{
        static ArrayList<Category> categories = new ArrayList<Category>();
        static ArrayList<Task> tasks = new ArrayList<Task>();
    }

    // private views
    private ListView lvTask;
    @SuppressWarnings("FieldCanBeLocal")
    private HorizontalListView lvCategory;
    private EditText etAddNewTask;
    private RelativeLayout rtlAddNewTask;
    private Button btnSearch;
    @SuppressWarnings("FieldCanBeLocal")
    private Button btnGoogle;

    // State variables
    //private Task currentTask = null;
    private Category currentCategory = null;
    private Boolean isSearching = false;
    private Boolean isTaskListFresh = true;
    private Boolean isAnimating = false;

    // Private fields
    private TaskArrayAdapter mTaskAdapter;
    private CategoryArrayAdapter mCategoryAdapter;
    private SQLiteHelper SQLHelp;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tasks);

        // Initialize database and shared preferences
        SQLHelp = new SQLiteHelper(getApplicationContext());
        SharedPreferences mPrefs = getSharedPreferences("hu.trio.taskmanager", MODE_PRIVATE);
        mPrefs.edit().putBoolean("firstRun", true).commit();

        // Setup categories and tutorial
        if (mPrefs.getBoolean("firstRun", true)) {
            loadFirstData();
            mPrefs.edit().putBoolean("firstRun", false).apply();
        }

        // Load data from database
        SQLHelp.open();
        LD.categories=SQLHelp.getCategories();
        LD.tasks=SQLHelp.getTasks(LD.categories);
        SQLHelp.close();

        // Initialize views
        /// addNewTask Button
        rtlAddNewTask = (RelativeLayout) findViewById(R.id.rtl_center);
        etAddNewTask = (EditText) findViewById(R.id.et_center);
        etAddNewTask.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // When enter is pressed add task or search and the soft keyboard stays.
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(!isSearching){
                        if(!etAddNewTask.getText().toString().equals("")){
                            Task idTask = new Task(etAddNewTask.getText().toString());
                            if(currentCategory!=null)idTask.addToCategory(currentCategory);
                            addTask(null, idTask);
                            etAddNewTask.setText("");
                            mTaskAdapter.notifyDataSetChanged();
                            return true;
                        }
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

        /// google Button
        btnGoogle = (Button) findViewById(R.id.btn_left);
        btnGoogle.setOnClickListener(this);

        /// listView of Tasks
        lvTask = (ListView) findViewById(R.id.lv_tasks);
        mTaskAdapter = new TaskArrayAdapter(getApplicationContext(), LD.tasks);
        lvTask.setAdapter(mTaskAdapter);
        lvTask.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        lvTask.addHeaderView(transView(100)); // This header and footer are below the categoryBar
        lvTask.addFooterView(transView(60));  // and buttonBar.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(lvTask,
                        new SwipeDismissListViewTouchListener.OnSwipeCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                // Delete tasks when dismiss
                                SQLHelp.open();
                                for(int position : reverseSortedPositions){
                                    SQLHelp.deleteTask(mTaskAdapter.getItem(position));
                                    LD.tasks.remove(position);
                                }
                                SQLHelp.close();
                                mTaskAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChangeDone(ListView listView, int pos) {
                                // Set tasks done when swipe right.
                                SQLHelp.open();
                                LD.tasks.get(pos).setDone(!LD.tasks.get(pos).isDone());
                                SQLHelp.modifyTask(LD.tasks.get(pos));
                                SQLHelp.close();
                                mTaskAdapter.notifyDataSetChanged();
                            }
                        }, R.id.rtl_taskItem);
        lvTask.setOnTouchListener(touchListener);
        lvTask.setOnScrollListener(touchListener.makeScrollListener());


        /// ListView of Category
        lvCategory = (HorizontalListView) findViewById(R.id.lv_categories);
        mCategoryAdapter = new CategoryArrayAdapter(getApplicationContext(), LD.categories);
        lvCategory.setAdapter(mCategoryAdapter);
        lvCategory.setOnItemLongClickListener(this);
        lvCategory.setOnItemClickListener(this);
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
        startCategoryEdit.putExtra("Category", position);
        startActivity(startCategoryEdit);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!isAnimating) {
            isAnimating = true;
            animateTaskListOut(new AnimatorListener() {
                public void onAnimationStart(Animator animation) {}
                public void onAnimationRepeat(Animator animation) {}
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isTaskListFresh) {
                        // Refresh the view
                        refreshTaskList();
                        mTaskAdapter.notifyDataSetChanged();
                        refreshAddNewTaskRTL();
                        animateTaskListIn();
                    }
                }

            });
        }
        // Change the current category
        if(position < 1){
            currentCategory = null;
        }else{
            currentCategory = mCategoryAdapter.getItem(position);
        }
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
                    if(!isAnimating) {
                        isAnimating = true;
                        animateTaskListOut(new AnimatorListener() {
                            public void onAnimationStart(Animator animation) {}
                            public void onAnimationRepeat(Animator animation) {}
                            public void onAnimationCancel(Animator animation) {}
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (!isTaskListFresh) {
                                    String searchString = etAddNewTask.getText().toString();
                                    refreshTaskList();
                                    Iterator<Task> iTask = LD.tasks.iterator();
                                    while (iTask.hasNext()) {
                                        Task idTask = iTask.next();
                                        if (!containsIgnoreCase(idTask.getTitle(), searchString))
                                            iTask.remove();
                                    }
                                    mTaskAdapter.notifyDataSetChanged();
                                    animateTaskListIn();
                                }
                            }
                        });
                    }
                }
                break;

            case R.id.btn_left:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.under_const),
                        Toast.LENGTH_LONG).show();
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
    private void refreshCategoryList() {
        SQLHelp.open();
        LD.categories.clear();
        LD.categories.addAll(SQLHelp.getCategories());
        SQLHelp.close();

    }

    /**
     * Refresh the task list from database but not the view! 
     * You have to notify the adapter from the changes
     */
    private void refreshTaskList() {
        SQLHelp.open();
        ArrayList<Task> idTasks = SQLHelp.getTasks(LD.categories);
        SQLHelp.close();
        if(currentCategory != null){
            Iterator<Task> iTask = idTasks.iterator();
            while(iTask.hasNext()){
                Task idTask = iTask.next();
                if(!idTask.isInTheCategory(currentCategory))iTask.remove();
            }
        }
        LD.tasks.clear();
        LD.tasks.addAll(idTasks);
    }

    /**
     *  Refresh the background of the AddNewTask RelativeLayout.
     */
    private void refreshAddNewTaskRTL(){
        if(currentCategory != null){
            GradientDrawable shape = (GradientDrawable)rtlAddNewTask.getBackground();
            shape.mutate();
            shape.setColor(currentCategory.getColor());
        }else{
            rtlAddNewTask.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        }
    }

    /**
     * Search in a string some other string. Not case sensitive.
     *
     * @param src The source string
     * @param searchString The searched string
     * @return True if the src contains the searchString
     */
    private static boolean containsIgnoreCase(String src, String searchString) {
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
    private void addTask(Task parent, Task task){
        SQLHelp.open();
        SQLHelp.addTask(task);
        SQLHelp.close();

        if(parent == null){
            LD.tasks.add(0,task);
        }else{
            parent.addSubTask(0,task);
        }
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

    private void loadFirstData() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        ArrayList<Category> categories = new ArrayList<Category>();

        tasks.add(new Task(getResources().getString(R.string.swipe_left_to_dismiss)));
        tasks.add(new Task(getResources().getString(R.string.swipe_right_to_mark_done)));
        tasks.add(new Task(getResources().getString(R.string.long_press_category_to_edit)));


        int[] catColors = getResources().getIntArray(R.array.categories);
        categories.add(new Category(getResources().getString(R.string.starter),catColors[3]));
        categories.add(new Category(getResources().getString(R.string.home),catColors[2]));
        categories.add(new Category(getResources().getString(R.string.work),catColors[4]));
        categories.add(new Category("",catColors[1]));
        categories.add(new Category("",catColors[0]));
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

        for(Task idTask : tasks){
            idTask.addToCategory(categories.get(0));
        }
        tasks.add(2,new Task(getResources().getString(R.string.press_category_to_filet_tasks)));

        SQLHelp.open();
        SQLHelp.reset();
        for(int i=0;i<tasks.size();i++){
            SQLHelp.addTask(tasks.get(i));
        }
        for(int i=0;i<categories.size();i++){
            SQLHelp.addCategory(categories.get(i));
        }
        SQLHelp.close();
    }

    private void animateTaskListOut(AnimatorListener listener){
        isTaskListFresh = false;
        lvTask.animate().y(lvTask.getHeight())
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(listener);
    }
    private void animateTaskListIn(){
        lvTask.animate().y(0)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListener() {
                    public void onAnimationStart(Animator animator) {}
                    public void onAnimationCancel(Animator animator) {}
                    public void onAnimationRepeat(Animator animator) {}
                    public void onAnimationEnd(Animator animator) {
                        isTaskListFresh = true;
                        isAnimating = false;
                    }
                });
    }
}