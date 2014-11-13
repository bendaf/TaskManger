package hu.trio.taskmanager;

import java.sql.Date;
import java.util.ArrayList;

import hu.trio.tasks.Category;
import hu.trio.tasks.Task;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper {
	/* A t�bl�k oszlopainak neve. */
	public static final String TASK_ID = "id";
	public static final String TASK_TITLE = "title";
	public static final String TASK_DESCRIPTION = "description";
	public static final String TASK_ENDDATE = "endDate";
	public static final String TASK_PARENT = "parent";
	public static final String TASK_REQUIREDTIME = "requiredtime";
	public static final String CATEGORY_ID = "id";
	public static final String CATEGORY_TITLE = "name";
	public static final String CATEGORY_COLOR = "color";
	public static final String CONNECTION_ID = "id";
	public static final String CONNECTION_TASK_ID = "taskId";
	public static final String CONNECTION_CATEGORY_ID = "categoryId";
	/* Az adatb�zis �s t�bl�k. */
	private static final String DATABASE_NAME = "Database";
	private static final String DATABASE_TASKS = "Tasks";
	private static final String DATABASE_CATEGORY = "Category";
	private static final String DATABASE_CONNECTION = "Connection";
	private static final int DATABASE_VERSION = 1;
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	/* Az sql parancsok haszn�lat�hoz. */
	
	private static class DbHelper extends SQLiteOpenHelper{

		public DbHelper(Context contex) {
			super(contex, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TASKS + " (" +
			TASK_ID + " INTEGER PRIMARY KEY, " +
			TASK_TITLE +" TEXT NOT NULL, " +
			TASK_DESCRIPTION +" TEXT, " +
			TASK_ENDDATE + " TEXT NOT NULL, " +
			TASK_PARENT + " INTEGER, " +
			TASK_REQUIREDTIME + " TEXT NOT NULL);"
			);
			db.execSQL("CREATE TABLE " + DATABASE_CATEGORY + " (" +
					CATEGORY_ID + " INTEGER PRIMARY KEY, " +
					CATEGORY_TITLE +" TEXT NOT NULL, " +
					CATEGORY_COLOR +" TEXT NOT NULL);"
			);
			db.execSQL("CREATE TABLE " + DATABASE_CONNECTION + " (" +
					CONNECTION_ID + " INTEGER PRIMARY KEY, " +
					CONNECTION_TASK_ID +" INTEGER NOT NULL, " +
					CONNECTION_CATEGORY_ID +" INTEGER NOT NULL);"
			);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TASKS);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CATEGORY);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CONNECTION);
			onCreate(db);
		}
	}
	
	public SQLiteHelper(Context c){
		ourContext=c;
	}
	public SQLiteHelper open() throws SQLException{
		ourHelper= new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	public void close(){
		ourHelper.close();
	}

	public long addTask(Task task){
		ContentValues cv = new ContentValues();
		cv.put(TASK_ID,task.getId());
		cv.put(TASK_TITLE,task.getTitle());
		cv.put(TASK_DESCRIPTION,task.getDescription());
		cv.put(TASK_ENDDATE,task.getEndDate().toString());
		cv.put(TASK_PARENT,task.getParentTask().getId());
		cv.put(TASK_REQUIREDTIME,task.getRequiredTime().toString());
		return ourDatabase.insert(DATABASE_TASKS, null, cv);
	}
	public long addCategory(Category cat){
		ContentValues cv = new ContentValues();
		cv.put(CATEGORY_TITLE,cat.getTitle());
		cv.put(CATEGORY_COLOR,cat.getColor());
		return ourDatabase.insert(DATABASE_CATEGORY, null, cv);
	}
//	public long addConnection(Task task,Category cat){
//		ContentValues cv = new ContentValues();
//		cv.put(TASK_ID,task.getId());
//		cv.put(CATEGORY_ID,cat.getId());
//		return ourDatabase.insert(DATABASE_CONNECTION, null, cv);
//	}
	public void modifyTask(Task task){
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(TASK_TITLE, task.getTitle());
		cvUpdate.put(TASK_DESCRIPTION,task.getDescription());
		cvUpdate.put(TASK_ENDDATE,task.getEndDate().toString());
		cvUpdate.put(TASK_PARENT,task.getParentTask().getId());
		cvUpdate.put(TASK_REQUIREDTIME,task.getRequiredTime().toString());
		ourDatabase.update(DATABASE_TASKS, cvUpdate, TASK_ID + "=" + task.getId(),null);
	}
	public void modifyCategory(Category cat){
		//ContentValues cvUpdate = new ContentValues();
		//cvUpdate.put(CATEGORY_TITLE, cat.getTitle());
		//cvUpdate.put(CATEGORY_COLOR, cat.getColor());
		//ourDatabase.update(DATABASE_CATEGORY, cvUpdate, CATEGORY_ID + "=" + cat.getId(),null);
	}
	public void deleteTask(Task task){
		ourDatabase.delete(DATABASE_TASKS, TASK_ID + "=" + task.getId(), null);
	}
	public void deleteCategory(Category cat){
		//ourDatabase.delete(DATABASE_CATEGORY, CATEGORY_ID + "=" + cat.getId(),null);
	}
//	public void deleteConnection(Task task, Category cat){
//		ourDatabase.delete(DATABASE_CONNECTION, TASK_ID + "=" + task.getId() +
//				" AND " +CATEGORY_ID + "=" + cat.getId(), null);
//	}
	public ArrayList<Task> getTasks() {
		ArrayList<Task> tasks=new ArrayList<>();
		String[] columns=new String[]{TASK_ID,TASK_TITLE,TASK_DESCRIPTION,
				TASK_ENDDATE,TASK_PARENT,TASK_REQUIREDTIME};
		Cursor c = ourDatabase.query(DATABASE_TASKS,columns,null,null,null,null,null);
		int idRow=c.getColumnIndex(TASK_ID);
		int titleRow=c.getColumnIndex(TASK_TITLE);
		int descriptionRow=c.getColumnIndex(TASK_DESCRIPTION);
		int endDateRow=c.getColumnIndex(TASK_ENDDATE);
		int parentRow=c.getColumnIndex(TASK_PARENT);
		int reqTimeRow=c.getColumnIndex(TASK_REQUIREDTIME);
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			Task parent=null;//A sz�l� megkeres�se. fel�telezz�k, hogy m�r szerepel az adatb�zisban.
			for(int i=0;i<tasks.size();i++){
				if(tasks.get(i).getId()==c.getLong(parentRow)){
					parent=tasks.get(i);
				}
			}
			if(parent!=null)parent.addSubTask(new Task(c.getLong(idRow),
					c.getString(titleRow),null,c.getString(descriptionRow),
					Date.valueOf(c.getString(endDateRow)),
					false,parent, null, Date.valueOf(c.getString(reqTimeRow)), null));
			else tasks.add(new Task(c.getLong(idRow),
					c.getString(titleRow),null,c.getString(descriptionRow),
					Date.valueOf(c.getString(endDateRow)),
					false,parent, null, Date.valueOf(c.getString(reqTimeRow)), null));
		}
		return tasks;
	}
	public ArrayList<Category> getCategorys() {
		ArrayList<Category> categorys=new ArrayList<>();
//		String[] columns=new String[]{CATEGORY_ID,CATEGORY_TITLE,CATEGORY_COLOR};
//		Cursor c = ourDatabase.query(DATABASE_CATEGORY,columns,null,null,null,null,null);
//		int idRow=c.getColumnIndex(CATEGORY_ID);
//		int titleRow=c.getColumnIndex(CATEGORY_TITLE);
//		int colorRow=c.getColumnIndex(CATEGORY_COLOR);
//		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
//			categorys.add(new Category(c.getLong(idRow),c.getString(titleRow)),
//					c.getString(colorRow));
//		}
		return categorys;
	}
}
