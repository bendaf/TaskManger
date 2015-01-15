package hu.trio.taskmanager;

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
	
	LayoutInflater inflater;
	ArrayList<Category> categories = new ArrayList<>(); 
	Category all;
	
	public CategoryArrayAdapter(Context context, ArrayList<Category> categories) {
		inflater = LayoutInflater.from(context);
		this.categories = categories;
		all = new Category(context.getString(R.string.all));
	}

	@Override
	public int getCount() {
		return categories.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if(position == 0)
			return all;
		return categories.get(position - 1 );
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		position--; //because the 0th is our own category
		VH categoryView;
		if (convertView == null) {
            convertView = inflater.inflate(R.layout.category_item, parent, false);
            categoryView = new VH();
            categoryView.tvTitle = (TextView) convertView.findViewById(R.id.tv_categoryTitle);
            categoryView.vInnerCircle = (View) convertView.findViewById(R.id.v_categoryInnerCircle);
            categoryView.vOuterCircle = (View) convertView.findViewById(R.id.v_categoryOuterCircle);
            convertView.setTag(categoryView);

        } else {
        	categoryView = (VH) convertView.getTag();
        }
		try{
//			Log.d("erdekel", categories.get(position).getTitle().toString());
			categoryView.tvTitle.setText(categories.get(position).getTitle().toString());

			GradientDrawable iCircle = (GradientDrawable)categoryView.vInnerCircle.getBackground();
			GradientDrawable oCircle = (GradientDrawable)categoryView.vOuterCircle.getBackground();
			iCircle.mutate();
			iCircle.setColor(categories.get(position).getColor());
			oCircle.setColor(categories.get(position).getDarkerColor());
		}catch(IndexOutOfBoundsException e){
			if(position == -1){
				categoryView.tvTitle.setText(all.getTitle().toString());

				GradientDrawable iCircle = (GradientDrawable)categoryView.vInnerCircle.getBackground();
				GradientDrawable oCircle = (GradientDrawable)categoryView.vOuterCircle.getBackground();
				iCircle.mutate();
				iCircle.setColor(Color.GRAY);
				oCircle.setColor(darkerColor(Color.GRAY));
				
			}
		}
        return convertView;
	}

	private int darkerColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		color = Color.HSVToColor(hsv);
		return color;
	}
}
