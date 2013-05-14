package com.venerationtechnology.homeinventory;

import android.content.Context;
import com.activeandroid.Entity;

public class Item extends Entity<Item> {
	public Item(Context context) { super(context); }
	 
    public String Name;
    public String SerialNumber;
    public Category Category;

    public class Columns extends Entity.Columns {
        public static final String Name = "Name";
        public static final String SerialNumber = "SerialNumber";
        public static final String CategoryId = "CategoryId";

       public class FullyQualified {
           public static final String Id = "Item.Id";
           public static final String Name = "Item.Name";
           public static final String SerialNumber = "Item.SerialNumber";
           public static final String CategoryId = "Item.CategoryId";
       }
   }
}
