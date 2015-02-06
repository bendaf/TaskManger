package hu.trio.tasks;

import android.graphics.Color;
import android.nfc.FormatException;

/**
 * This class describe a category for {@link hu.trio.tasks.Task}. A category must have title and color. 
 * The color is the id of the category.
 * @author bendaf
 *
 */
public class Category {
	private String title;
	private int color;  //Uses android.graphics.color to convert
	
	/**
	 * @param title The title of the category, string.
	 * @param color The color of the category in rgb, integer.
	 */
	public Category(String title, int color) {
		this.title=title;
		this.color=color;
	}
	public String getTitle() {
		return new String(title);
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getColor() {
		return Integer.valueOf(color);
	}
	public int getDarkerColor() {
		return darkerColor(this.color);
	}
	public void setColor(int alpha, int red, int green, int blue) {
		this.color = Color.argb(alpha, red, green, blue);
	}
	public void setColor(String colorString){
		this.color = Color.parseColor(colorString);
	}
	/**
	 * Darker the color with ratio used in the app.
	 * @param color The source color
	 * @return the darker color.
	 */
	public static int darkerColor(int color){
		try {
			color = darkerColor(color,0.8f);
		} catch (FormatException e) {}
		return color;
	}
	/**
	 * Darker the color with a ratio.
	 * @param color The source color.
	 * @param ratio The ratio between 0 and 1. 0 is the black, and 1 is the same.
	 * @return the darker color. 
	 * @throws FormatException throwed when ratio is not between 0 and 1
	 */
	public static int darkerColor(int color, float ratio) throws FormatException{
		if(ratio>1 || ratio<0) throw new FormatException("Ratio must be between 0 and 1");
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= ratio; // value component
		return Color.HSVToColor(hsv);
		
	}
	
}
