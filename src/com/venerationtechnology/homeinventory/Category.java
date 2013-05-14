package com.venerationtechnology.homeinventory;

import android.content.Context;
import com.activeandroid.Entity;
import java.util.List;

public class Category extends Entity<Category> {
    public Category(Context context) { super(context); }

    public List<Item> Items;
    public String Name;

    public class Columns extends Entity.Columns {
        public static final String Name = "Name";

        public class FullyQualified {
            public static final String Id = "Category.Id";
            public static final String Name = "Category.Name";
        }
    }
}
