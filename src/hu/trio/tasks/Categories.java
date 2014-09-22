package hu.trio.tasks;

import java.util.ArrayList;

public final class Categories {
	static ArrayList<Category> categories = new ArrayList<>();
	
	public void addCategory(Category cat){
		categories.add(cat);
	}
	
	public boolean removeCategory(Category cat){
		return categories.remove(cat);
	}
	
	public boolean containsCategory(Category cat){
		return categories.contains(cat);
	}
	
	public Category getCategory(int index){
		return (Category) categories.get(index);
	}
	
}
