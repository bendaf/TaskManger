package hu.trio.tasks;

import android.graphics.Color;

public class Category {
	private String title;
	private int color;  //Uses android.graphics.color to convert
	
	public Category(String title, int rgb) {
		this.title=title;
		this.color=rgb;
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
	public static int darkerColor(int color){
		return darkerColor(color,0.8f);
	}
	public static int darkerColor(int color, float ratio){
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= ratio; // value component
		return Color.HSVToColor(hsv);
		
	}
	
}
