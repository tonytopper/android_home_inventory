package com.venerationtechnology.homeinventory;


import android.app.AlertDialog;
import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.activeandroid.EntityAdapter;
import com.activeandroid.Query;
import com.venerationtechnology.homeinventory.R;
import com.venerationtechnology.homeinventory.Category;
import com.venerationtechnology.homeinventory.Item;

public class CategoryActivity extends ListActivity {
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// MEMBERS
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
	final private int ADD_ITEM = 0;
	final private int PICK_RANDOM = 1;
	
	final private int EDIT_ID = 0;
	final private int DELETE_ID = 1;
	
	private static final int SELECT_IMAGE = 0;
	
	private Category mCategory;
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// PUBLIC METHODS
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        
        registerForContextMenu(getListView());
    }
    
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, ItemActivity.class);
		intent.putExtra(Item.Columns.Id, id);

		startActivity(intent);
	}
	    
    @Override
	protected void onResume() {
		super.onResume();
		
		Bundle extras = this.getIntent().getExtras();
		long categoryId = extras.getLong(Category.Columns.Id);
		
		mCategory = new Category(this);
		mCategory.load(categoryId);
		
		loadCategoryItems();
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, ADD_ITEM, 0, "Add item").setIcon(android.R.drawable.ic_menu_add);
    	menu.add(0, PICK_RANDOM, 0, "Pick random item").setIcon(android.R.drawable.ic_menu_help);
    	
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case ADD_ITEM:
				editItem(null);
				return true;
			case PICK_RANDOM:
				pickRandom();
				return true;
			default:
				break;
		}

		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		
		Item item = new Item(this);
		item.load(info.id);
		
		menu.setHeaderTitle(item.Name);		
		menu.add(0, EDIT_ID, 0, "Edit").setIcon(android.R.drawable.ic_menu_edit);
		menu.add(0, DELETE_ID, 0,  "Delete").setIcon(android.R.drawable.ic_menu_delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			switch (item.getItemId()) {
				case EDIT_ID:
					editItem(info.id);
					return true;
				case DELETE_ID:
					deleteItem(info.id);
					return true;
				default:
					return super.onContextItemSelected(item);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// PRIVATE METHODS
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void loadCategoryItems() {
    	Query<Item> q = new Query<Item>(this, Item.class);
    	q.where(Item.Columns.CategoryId).isEqualTo(mCategory.Id);
    	q.orderBy(Item.Columns.Name, Query.OrderDirection.Ascending);
    	
    	ListAdapter adapter = new EntityAdapter(
    			this, 
    			q.loadMap(), 
    			R.layout.item_row,
    			new String[] { Category.Columns.Name },
    			new int[] { R.id.text1 }
    			);
    	
    	
    	setListAdapter(adapter);
    }

	private void editItem(final Long id) {
		// TODO Make this it's own page instead of an alert.
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		
		if(id != null) {
			Item item = new Item(this);
			item.load(id);
			input.setText(item.Name);
			alert.setTitle("Edit item");
		} else {
			alert.setTitle("New item");
		}
		alert.setMessage("Enter a new item name");
		alert.setView(input);
		
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
								
				Item item = new Item(CategoryActivity.this);
				
				if(id != null) {
					item.load(id);
				}
				
				item.Name = value;
				item.Category = mCategory;
				item.save();
				
				loadCategoryItems();
			}
		});
		
		alert.setNegativeButton("Cancel", null);
		alert.show();
	}
	
	private void deleteItem(final Long id) {
		Item item = new Item(this);
		item.load(id);
		item.delete();
		
		loadCategoryItems();
	}

	private void pickRandom() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		String title = "Pickrand";
		String message = "There are no items to pick from";
		Query<Item> q = new Query<Item>(this, Item.class);
		
		q.where(Item.Columns.CategoryId).isEqualTo(mCategory.Id);
		q.orderBy(null, Query.OrderDirection.Random);
		q.limit(1);
		
		Item item = q.loadSingle();
		
		if(item != null) {
			title = item.Category.Name;
			message = item.Name;
		}
		
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setCancelable(false);
		alert.setNegativeButton("OK", null);
		alert.show();
	}
}