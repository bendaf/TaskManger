package hu.trio.taskmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper {
	/* A táblák oszlopainak neve. */
	public static final String TASK_ID = "id";
	public static final String TASK_TITLE = "title";
	public static final String TASK_DESCRIPTION = "description";
	public static final String TASK_ENDDATE = "endDate";
	public static final String TASK_PARENT = "parent";
	public static final String TASK_REQUIREDTIME = "requiredtime";
	public static final String CATEGORY_ID = "id";
	public static final String CATEGORY_NAME = "name";
	public static final String CATEGORY_COLOR = "color";
	public static final String CONNECTION_ID = "id";
	public static final String CONNECTION_TASK_ID = "taskId";
	public static final String CONNECTION_CATEGORY_ID = "categoryId";
	/* Az adatbázis és táblák. */
	private static final String DATABASE_NAME = "Database";
	private static final String DATABASE_TASKS = "Tasks";
	private static final String DATABASE_CATEGORY = "Category";
	private static final String DATABASE_CONNECTION = "Connection";
	private static final int DATABASE_VERSION = 1;
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	/* Az sql parancsok használatához. */
	
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
			TASK_DESCRIPTION +" TEXT NOT NULL, " +
			TASK_ENDDATE + " DATE NOT NULL, " +
			TASK_PARENT + " INTEGER, " +
			TASK_REQUIREDTIME + " DATE);"
			);
			db.execSQL("CREATE TABLE " + DATABASE_CATEGORY + " (" +
					CATEGORY_ID + " INTEGER PRIMARY KEY, " +
					CATEGORY_NAME +" TEXT NOT NULL, " +
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
			onCreate(db);
		}
	}
	
	public SQLiteHelper(Context c){
		ourContext=c;
	}
	public SQLiteHelper open(){
		ourHelper= new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	public void close(){
		ourHelper.close();
	}
	
	public void addTask(){
		//TODO
	}
	public void deleteTask(){
		//TODO
	}
}
