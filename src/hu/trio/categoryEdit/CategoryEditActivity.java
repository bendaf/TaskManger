package hu.trio.categoryEdit;

import hu.trio.database.SQLiteHelper;
import hu.trio.taskmanager.R;
import hu.trio.taskmanager.R.id;
import hu.trio.taskmanager.R.layout;
import hu.trio.taskmanager.R.string;
import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class CategoryEditActivity extends Activity implements OnClickListener, OnKeyListener {

	private TableLayout tlCategories;
	private CategoryArrayAdapter catAdapter;
	private ArrayList<Category> categories;
	private SQLiteHelper SQLHelp;
	private View selectedView = null;
	private EditText etCategoryName = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_category_edit);
		
		SQLHelp = new SQLiteHelper(getApplicationContext());
		SQLHelp.open();
		this.categories=SQLHelp.getCategorys();
        SQLHelp.close();
        etCategoryName = (EditText) findViewById(R.id.et_category_name);
        etCategoryName.setOnKeyListener(this);
		tlCategories = (TableLayout) findViewById(R.id.tl_categories);
		catAdapter = new CategoryArrayAdapter(getApplicationContext(), categories);
		drawTable();
	}

	@Override
	public void onClick(View v) {
		int tag = -1;
		try{
			tag = (Integer) v.getTag();
			selectCategory(tag);

			try{
				changeBackgroundOfView(selectedView, categories.get((Integer)selectedView.getTag()).getDarkerColor());
			}catch(NullPointerException e){
				Log.d("erdekel", "There isn't a selected view. " + e.toString());
			}catch (IndexOutOfBoundsException e) {
				changeBackgroundOfView(selectedView, Category.darkerColor(Color.GRAY));
			}
			changeBackgroundOfView(v,Color.WHITE);
			selectedView = v;
		}catch (NullPointerException e) {
			Log.d("erdekel", "View don't have tag. " + e.toString());
		}catch (ClassCastException e) {
			Log.d("erdekel", "The tag isn't a number. " + e.toString());
		}
	}

	private void changeBackgroundOfView(View v, int color) {
			View outerCircle = (View) v.findViewById(R.id.v_categoryOuterCircle);
			GradientDrawable oCircle = (GradientDrawable) outerCircle.getBackground();
			oCircle.mutate();
			oCircle.setColor(color);
	}
	
	private void selectCategory(int pos){
		try{
			etCategoryName.setText(categories.get(pos).getTitle());
		}catch(IndexOutOfBoundsException e){
			etCategoryName.setText("");
			etCategoryName.setHint(getResources().getText(R.string.name));
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
			
			try{
				int pos = (Integer)selectedView.getTag();
				String newName = etCategoryName.getText().toString();
	    	    try{
	    	    	Category idCategory = categories.get(pos);
	    	  	    idCategory.setTitle(newName);
	    		    SQLHelp.open();
	    		    SQLHelp.modifyCategory(idCategory);
	    		    SQLHelp.close();
	    	    }catch(IndexOutOfBoundsException|NullPointerException e){
	    		    int[] catColors = getResources().getIntArray(R.array.categories);
	    		    Category idCategory = new Category(newName,catColors[pos]);
	    		    categories.add(idCategory);
	    		    SQLHelp.open();
	    		    SQLHelp.addCategory(idCategory);
	    		    SQLHelp.close();
	    	    }
	            catAdapter.notifyDataSetChanged();
	            drawTable();
			}catch(NullPointerException e){
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.select_cat_first),
						   Toast.LENGTH_LONG).show();
			}
			
            return true;
		}
		return false;
	}
	
	private void drawTable(){
//		int numOfCategories = categories.size();
		tlCategories.removeAllViews();
		TableRow.LayoutParams rowParams=new TableRow.LayoutParams 
				(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
		
		for(int i = 0; i<4; i++){
			TableRow tr = new TableRow(getApplicationContext());
			tr.setLayoutParams(rowParams);
			tr.setTag(2);
			for(int j = 0; j<4; j++ ){ 
				View v = catAdapter.getView(i*4+j + 1, null, tr);
				v.setTag(i*4+j);
				v.setOnClickListener(this);
				tr.addView(v);
			}
			tlCategories.addView(tr);
		}
	}
}
