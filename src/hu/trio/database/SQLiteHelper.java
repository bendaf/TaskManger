package hu.trio.database;

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
import android.graphics.Color;
import android.util.Log;

public class SQLiteHelper {
	/* A táblák oszlopainak neve. */
	public static final String TASK_ID = "id";
	public static final String TASK_TITLE = "title";
	public static final String TASK_DESCRIPTION = "description";
	public static final String TASK_ENDDATE = "endDate";
	public static final String TASK_LASTISDONE = "lastIsDone";
	public static final String TASK_ISDONE = "isDone";
	public static final String TASK_PARENT = "parent";
	public static final String TASK_REQUIREDTIME = "requiredtime";
	public static final String TASK_CHILDREQTIME = "childReqTime";
//	public static final String CATEGORY_ID = "id";
	public static final String CATEGORY_TITLE = "name";
	public static final String CATEGORY_COLOR = "color";
	public static final String CONNECTION_ID = "id";
	public static final String CONNECTION_TASK_ID = "taskId";
	public static final String CONNECTION_CATEGORY_COLOR = "categoryColor";
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
			TASK_ID + " LONG PRIMARY KEY, " +
			TASK_TITLE +" TEXT NOT NULL, " +
			TASK_DESCRIPTION +" TEXT, " +
			TASK_ENDDATE + " LONG NOT NULL, " +
			TASK_LASTISDONE + " BIT, " +
			TASK_ISDONE + " BIT, " +
			TASK_PARENT + " INTEGER, " +
			TASK_REQUIREDTIME + " LONG NOT NULL, " +
			TASK_CHILDREQTIME + " LONG NOT NULL);"
			);
			db.execSQL("CREATE TABLE " + DATABASE_CATEGORY + " (" +
                    CATEGORY_COLOR + " INTEGER PRIMARY KEY, " +
                    CATEGORY_TITLE +" TEXT NOT NULL);"

			);
			db.execSQL("CREATE TABLE " + DATABASE_CONNECTION + " (" +
					CONNECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					CONNECTION_TASK_ID +" INTEGER NOT NULL, " +
					CONNECTION_CATEGORY_COLOR +" INTEGER NOT NULL);"

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
	
	public void reset(){
		ourHelper.onUpgrade(ourDatabase, DATABASE_VERSION, DATABASE_VERSION+1);
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
        if(task.getDescription()!=null)cv.put(TASK_DESCRIPTION,task.getDescription());
        	else cv.put(TASK_DESCRIPTION,"");
        if(task.getEndDate()!=null)cv.put(TASK_ENDDATE,task.getEndDate().getTime());
        	else cv.put(TASK_ENDDATE, "0");
        cv.put(TASK_LASTISDONE, task.isLastIsDone());
        cv.put(TASK_ISDONE, task.isDone());
        if(task.getParentTask()!=null)cv.put(TASK_PARENT,task.getParentTask().getId());
        	else cv.put(TASK_PARENT,0);
        if(task.getRequiredTime()!=null)cv.put(TASK_REQUIREDTIME,task.getRequiredTime().getTime());
        	else cv.put(TASK_REQUIREDTIME,"0");
        if(task.getChildReqTime()!=null)cv.put(TASK_CHILDREQTIME, task.getChildReqTime().getTime());
        	else cv.put(TASK_CHILDREQTIME,"0");
        if(task.getCategories()!=null){
        	ArrayList<Category> categorys=task.getCategories();
        	for(int i=0;i<categorys.size();i++){
        		if(addConnection(task,categorys.get(i)) == -1){
        			Log.d("erdekel", "Nem sikerült a kapcsolat létrehozása!");
        		};
        	}
        }
		return ourDatabase.insert(DATABASE_TASKS, null, cv);
	}
	public long addCategory(Category cat){
		ContentValues cv = new ContentValues();
		cv.put(CATEGORY_TITLE,cat.getTitle());
		cv.put(CATEGORY_COLOR,cat.getColor());
		return ourDatabase.insert(DATABASE_CATEGORY, null, cv);
	}
    public long addConnection(Task task, Category cat){
        ContentValues cv = new ContentValues();
        cv.put(CONNECTION_TASK_ID,task.getId());
        cv.put(CONNECTION_CATEGORY_COLOR,cat.getColor());
        return ourDatabase.insert(DATABASE_CONNECTION, null, cv);
    }
	public void modifyTask(Task task){
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(TASK_TITLE, task.getTitle());
		if(task.getDescription()!=null)cvUpdate.put(TASK_DESCRIPTION,task.getDescription());
    	else cvUpdate.put(TASK_DESCRIPTION,"");
		if(task.getEndDate()!=null)cvUpdate.put(TASK_ENDDATE,task.getEndDate().getTime());
    	else cvUpdate.put(TASK_ENDDATE, "0");
		cvUpdate.put(TASK_LASTISDONE,task.isLastIsDone());
		cvUpdate.put(TASK_ISDONE,task.isDone());
		if(task.getParentTask()!=null)cvUpdate.put(TASK_PARENT,task.getParentTask().getId());
    	else cvUpdate.put(TASK_PARENT,0);
		if(task.getRequiredTime()!=null)cvUpdate.put(TASK_REQUIREDTIME,task.getRequiredTime().getTime());
    	else cvUpdate.put(TASK_REQUIREDTIME,"0");
		if(task.getChildReqTime()!=null)cvUpdate.put(TASK_CHILDREQTIME, task.getChildReqTime().getTime());
    	else cvUpdate.put(TASK_CHILDREQTIME,"0");
		ourDatabase.update(DATABASE_TASKS, cvUpdate, TASK_ID + "=" + task.getId(),null);
	}
    public void modifyCategory(Category cat){// színt lehet-e utólag változtatni vagy csak a nevét.
      ContentValues cvUpdate = new ContentValues();
      cvUpdate.put(CATEGORY_TITLE, cat.getTitle());
      cvUpdate.put(CATEGORY_COLOR, cat.getColor());
      ourDatabase.update(DATABASE_CATEGORY, cvUpdate, CATEGORY_COLOR + "=" + cat.getColor(),null);
    }
	public void deleteTask(Task task){
		ourDatabase.delete(DATABASE_TASKS, TASK_ID + "=" + task.getId(), null);
	}
	public void deleteCategory(Category cat){
		ourDatabase.delete(DATABASE_CONNECTION,CONNECTION_CATEGORY_COLOR + "=" + cat.getColor(), null);
        ourDatabase.delete(DATABASE_CATEGORY, CATEGORY_COLOR + "=" + cat.getColor(),null);
    }
    public void deleteConnection(Task task, Category cat){
        ourDatabase.delete(DATABASE_CONNECTION, TASK_ID + "=" + task.getId() +
                " AND " +CATEGORY_COLOR + "=" + cat.getColor(), null);
    }
    private Task checkParent(Task t,long id){
        Task parent=null;
        if(t.getId()!=id){
            if(t.getSubTasks()==null)return null;
            else{
                for(int i=0;i<t.getSubTasks().size();i++){
                    parent=checkParent(t.getSubTasks().get(i),id);
                    if(parent!=null)return parent;
                }
                return null;
            }
        }
        else return t;
        
	}
    
    private Category find(ArrayList<Category> categories, int color) {
		for(int i=0;i<categories.size();i++){
			if(categories.get(i).getColor()==color)return categories.get(i);
		}
		return null;
	}
	public ArrayList<Task> getTasks(ArrayList<Category> categories) {
		ArrayList<Task> tasks=new ArrayList<>();
		String[] columns=new String[]{TASK_ID,TASK_TITLE,TASK_DESCRIPTION,
				TASK_ENDDATE,TASK_ISDONE,TASK_LASTISDONE,TASK_PARENT,TASK_REQUIREDTIME,TASK_CHILDREQTIME};
		Cursor c = ourDatabase.query(DATABASE_TASKS,columns,null,null,null,null,null);
		int idRow=c.getColumnIndex(TASK_ID);
		int titleRow=c.getColumnIndex(TASK_TITLE);
		int descriptionRow=c.getColumnIndex(TASK_DESCRIPTION);
		int endDateRow=c.getColumnIndex(TASK_ENDDATE);
		int isDone=c.getColumnIndex(TASK_ISDONE);
		int lastIsDone=c.getColumnIndex(TASK_LASTISDONE);
		int parentRow=c.getColumnIndex(TASK_PARENT);
		int reqTimeRow=c.getColumnIndex(TASK_REQUIREDTIME);
		int childTime=c.getColumnIndex(TASK_CHILDREQTIME);
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			Task parent=null;//A szülõ megkeresése. felételezzük, hogy már szerepel az adatbázisban.
			for(int i=0;i<tasks.size();i++){
                parent=checkParent(tasks.get(i),c.getLong(parentRow));
			}
			ArrayList<Category> category=new ArrayList<Category>();
			String[] columns2=new String[]{CONNECTION_ID,CONNECTION_TASK_ID,CONNECTION_CATEGORY_COLOR};
			String[] sel = {Long.toString(c.getLong(idRow))};
			Cursor c2=ourDatabase.query(DATABASE_CONNECTION,columns2,
				CONNECTION_TASK_ID + " = ? ",sel,null,null,null);
//			if(c2.isNull(0))break;
			for(c2.moveToFirst();!c2.isAfterLast();c2.moveToNext()){
				Category idle=find(categories,(c2.getInt(c2.getColumnIndex(CONNECTION_CATEGORY_COLOR))));
				if(idle!=null)category.add(idle);
			}
			if(parent!=null)parent.addSubTask(new Task(c.getLong(idRow),
					c.getString(titleRow),category,c.getString(descriptionRow),
					c.getLong(endDateRow) == 0 ? null : new Date(c.getLong(endDateRow)),
					c.getInt(isDone) ==0 ? false : true, parent, new ArrayList<Task>(), 
					c.getLong(reqTimeRow) == 0 ? null : new Date(c.getLong(reqTimeRow)), null));
			else tasks.add(new Task(c.getLong(idRow),
					c.getString(titleRow),category,c.getString(descriptionRow),
					c.getLong(endDateRow) == 0 ? null : new Date(c.getLong(endDateRow)),
					c.getInt(isDone) ==0 ? false : true, parent, new ArrayList<Task>(), 
					c.getLong(reqTimeRow) == 0 ? null : new Date(c.getLong(reqTimeRow)),
					c.getLong(childTime) == 0 ? null : new Date(c.getLong(childTime))));
		}
		return tasks;
	}
	public ArrayList<Category> getCategorys() {
		ArrayList<Category> categorys=new ArrayList<>();
        String[] columns=new String[]{CATEGORY_TITLE,CATEGORY_COLOR};
        Cursor c = ourDatabase.query(DATABASE_CATEGORY,columns,null,null,null,null,null);
        int titleRow=c.getColumnIndex(CATEGORY_TITLE);
        int colorRow=c.getColumnIndex(CATEGORY_COLOR);
        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            Category u=new Category(c.getString(titleRow),0);
            u.setColor(Color.alpha((int)c.getLong(colorRow)),
            		   Color.red((int)c.getLong(colorRow)),
            		   Color.green((int)c.getLong(colorRow)),
            		   Color.blue((int)c.getLong(colorRow)));
            categorys.add(u);
        }
		return categorys;
	}
}
