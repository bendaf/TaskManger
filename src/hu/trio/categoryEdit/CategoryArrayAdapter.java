package hu.trio.categoryEdit;

import hu.trio.taskmanager.R;
import hu.trio.tasks.Category;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This extension of {@link android.widget.BaseAdapter} make a specific adapter for 
 * {@link hu.trio.tasks.Category}.
 *  
 * This adapter is specific because acts like if it would hold only the categories with names
 * but it holds all 16 categories and displays only the ones with name. 
 * The 0. category is always the 'all' category what is not in the category array. 
 * 
 * @author bendaf
 *
 */
public class CategoryArrayAdapter extends BaseAdapter {

	//contains the views of a category
	private class VH{
		View vInnerCircle;
		View vOuterCircle;
		TextView tvTitle;
	}
	
	// this array contains the category colors
	public int[] catColors;
	
	//private fields
	private LayoutInflater inflater;
	private ArrayList<Category> categories = new ArrayList<Category>();
	private ArrayList<Integer> reservedCategories = new ArrayList<Integer>();
	private Category all;

	public CategoryArrayAdapter(Context context, ArrayList<Category> categories) {
		inflater = LayoutInflater.from(context);
		this.categories = categories;
		all = new Category(context.getString(R.string.all),Color.GRAY);
		catColors=context.getResources().getIntArray(R.array.categories);
		refreshResCategories();
	}
	
	@Override
	public int getCount() {
		return reservedCategories.size() + 1;
	}

	@Override
	public Category getItem(int position) {
		if(position == 0)
			return all;
		return categories.get(reservedCategories.get(position - 1));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		VH categoryView;
		position--;  //because of the all category
		
		if(!(parent.getTag() != null && (Integer)parent.getTag() == 2)){
			// In a specific case the getView returns with all the view in the index of all the 16 
			// category
			if(position!=-1) position = reservedCategories.get(position);
		}
		
		if (convertView == null) {
            convertView = inflater.inflate(R.layout.category_item, parent, false);
            categoryView = new VH();
            categoryView.tvTitle = (TextView) convertView.findViewById(R.id.tv_categoryTitle);
            categoryView.vInnerCircle = convertView.findViewById(R.id.v_categoryInnerCircle);
            categoryView.vOuterCircle = convertView.findViewById(R.id.v_categoryOuterCircle);
            convertView.setTag(categoryView);

        } else categoryView = (VH) convertView.getTag();
		
		//Set the properties of the category view
		try{
			categoryView.tvTitle.setText(categories.get(position).getTitle());
			GradientDrawable iCircle = (GradientDrawable)categoryView.vInnerCircle.getBackground();
			GradientDrawable oCircle = (GradientDrawable)categoryView.vOuterCircle.getBackground();
			setCategoryColor(iCircle, oCircle, categories.get(position).getColor());	
		}catch(IndexOutOfBoundsException e){
			GradientDrawable iCircle = (GradientDrawable)categoryView.vInnerCircle.getBackground();
			GradientDrawable oCircle = (GradientDrawable)categoryView.vOuterCircle.getBackground();
			if(position == - 1 ){
				categoryView.tvTitle.setText(all.getTitle());
				setCategoryColor(iCircle, oCircle, all.getColor());
			}else{
				categoryView.tvTitle.setText("");
				setCategoryColor(iCircle, oCircle, catColors[position-1]);
			}
		}
        return convertView;
	}
	
	//Set the color of the inner and outer circle of the category
	private void setCategoryColor(GradientDrawable iCircle, GradientDrawable oCircle, int color){
		iCircle.mutate();
		iCircle.setColor(color);
		oCircle.setColor(Category.darkerColor(color));
		
	}
	@Override
	public void notifyDataSetChanged(){
		super.notifyDataSetChanged();
		refreshResCategories();
	}
	
	//Refresh the indexes of the available categories from categories array to reservedCategories
	private void refreshResCategories(){
		reservedCategories.clear();
		for(int i=0;i<categories.size();i++){
			if(!categories.get(i).getTitle().equals("")){
				reservedCategories.add(i);
			}
		}
	}
}
