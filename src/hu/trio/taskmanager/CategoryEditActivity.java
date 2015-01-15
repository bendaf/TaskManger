package hu.trio.taskmanager;

import hu.trio.tasks.Category;
import hu.trio.tasks.Task;

import java.util.ArrayList;

import android.app.Activity;
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
//		SQLHelp.reset();
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
			etCategoryName.setText(getResources().getText(R.string.name));
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
	          if(etCategoryName.getText().toString().equals("")){
	        	  try{
	        		  SQLHelp.open();
	        		  SQLHelp.deleteCategory(categories.get((Integer)selectedView.getTag()));
	        		  SQLHelp.close();
	        		  categories.remove(categories.get((Integer)selectedView.getTag()));
	        	  }catch(IndexOutOfBoundsException e){
	        		  e.printStackTrace();
	        	  }
	          }else{
	        	  try{
	        		  categories.get((Integer)selectedView.getTag())
	        	  					.setTitle(etCategoryName.getText().toString());
	        		  SQLHelp.open();
	        		  SQLHelp.modifyCategory(categories.get((Integer)selectedView.getTag()));
	        		  SQLHelp.close();
	        	  }catch(IndexOutOfBoundsException e){
	        		  categories.add(new Category(etCategoryName.getText().toString()));
	        		  SQLHelp.open();
	        		  SQLHelp.addCategory(categories.get(categories.size()-1));
	        		  SQLHelp.close();
	        	  }catch(NullPointerException e){
	        		  categories.add(new Category(etCategoryName.getText().toString()));
	        		  SQLHelp.open();
	        		  SQLHelp.addCategory(categories.get(categories.size()-1));
	        		  SQLHelp.close();
	        	  }
	          }
	          catAdapter.notifyDataSetChanged();
	          drawTable();
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
