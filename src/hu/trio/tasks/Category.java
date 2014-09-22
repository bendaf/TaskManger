package hu.trio.tasks;

import android.graphics.Color;

public class Category {
	private String title;
	private Color color;
	
	public Category(String title){
		this.title=title;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

}
