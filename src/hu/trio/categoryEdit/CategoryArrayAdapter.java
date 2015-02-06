package hu.trio.categoryEdit;

import hu.trio.taskmanager.R;
import hu.trio.taskmanager.R.id;
import hu.trio.taskmanager.R.layout;
import hu.trio.taskmanager.R.string;
import hu.trio.tasks.Category;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryArrayAdapter extends BaseAdapter {

	class VH{
		View vInnerCircle;
		View vOuterCircle;
		TextView tvTitle;
	}
	
	public int[] categoryColors;
	
	LayoutInflater inflater;
	ArrayList<Category> categories = new ArrayList<>(); 
	ArrayList<Integer> reservedCategories = new ArrayList<>();
	Category all;

	public CategoryArrayAdapter(Context context, ArrayList<Category> categories) {
		inflater = LayoutInflater.from(context);
		this.categories = categories;
		all = new Category(context.getString(R.string.all),Color.GRAY);
		categoryColors=context.getResources().getIntArray(R.array.categories);
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
		position--;
		if(parent.getTag() != null && (Integer)parent.getTag() == 2){
			Log.d("erdekel", Integer.toString(position));
		}else{
			if(position!=-1) position = reservedCategories.get(position);
		}
//		if (convertView == null) {
            convertView = inflater.inflate(R.layout.category_item, parent, false);
            categoryView = new VH();
            categoryView.tvTitle = (TextView) convertView.findViewById(R.id.tv_categoryTitle);
            categoryView.vInnerCircle = (View) convertView.findViewById(R.id.v_categoryInnerCircle);
            categoryView.vOuterCircle = (View) convertView.findViewById(R.id.v_categoryOuterCircle);
//            convertView.setTag(categoryView);

//        } else {
//        	categoryView = (VH) convertView.getTag();
//        }
		try{
//			Log.d("erdekel", categories.get(position).getTitle().toString());
			categoryView.tvTitle.setText(categories.get(position).getTitle().toString());

			GradientDrawable iCircle = (GradientDrawable)categoryView.vInnerCircle.getBackground();
			GradientDrawable oCircle = (GradientDrawable)categoryView.vOuterCircle.getBackground();
			iCircle.mutate();
			iCircle.setColor(categories.get(position).getColor());
			oCircle.setColor(categories.get(position).getDarkerColor());
		}catch(IndexOutOfBoundsException e){
			//Log.d("erdekel", "No such a category: " + Integer.toString(position));
			if(position == - 1 ){
				categoryView.tvTitle.setText(all.getTitle().toString());
				GradientDrawable iCircle = (GradientDrawable)categoryView.vInnerCircle.getBackground();
				GradientDrawable oCircle = (GradientDrawable)categoryView.vOuterCircle.getBackground();
				iCircle.mutate();
				iCircle.setColor(Color.GRAY);
				oCircle.setColor(Category.darkerColor(Color.GRAY));
			}else{
				categoryView.tvTitle.setText("");
				GradientDrawable iCircle = (GradientDrawable)categoryView.vInnerCircle.getBackground();
				GradientDrawable oCircle = (GradientDrawable)categoryView.vOuterCircle.getBackground();
				iCircle.mutate();
				iCircle.setColor(categoryColors[position-1]);
				oCircle.setColor(Category.darkerColor(categoryColors[position-1]));
			}
		}
        return convertView;
	}
	
	@Override
	public void notifyDataSetChanged(){
		super.notifyDataSetChanged();
		refreshResCategories();
	}
	
	private void refreshResCategories(){
		reservedCategories.clear();
		for(int i=0;i<categories.size();i++){
			if(!categories.get(i).getTitle().equals("")){
//				Log.d("erdekel", Integer.toString(i));
				reservedCategories.add(i);
			}
		}
	}
}
