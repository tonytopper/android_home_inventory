package com.venerationtechnology.homeinventory;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.activeandroid.EntityAdapter;
import com.activeandroid.Query;
import com.venerationtechnology.homeinventory.R;
import com.venerationtechnology.homeinventory.Category;
import com.venerationtechnology.homeinventory.Item;

public class HomeinventoryActivity extends ListActivity {
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// MEMBERS
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
	final private int ADD_CATEGORY = 0;
	final private int PICK_RANDOM = 1;
	final private int ABOUT = 2;
	
	final private int EDIT_ID = 0;
	final private int DELETE_ID = 1;
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// PUBLIC METHODS
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        registerForContextMenu(getListView());
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		loadCategories();
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, PICK_RANDOM, 0, "Pick random item").setIcon(android.R.drawable.ic_menu_help);
    	menu.add(0, ADD_CATEGORY, 0, "Add category").setIcon(android.R.drawable.ic_menu_add);
    	menu.add(0, ABOUT, 0, "About Home Inventory").setIcon(android.R.drawable.ic_menu_info_details);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case ADD_CATEGORY:
				editCategory(null);
				return true;
			case PICK_RANDOM:
				pickRandom();
				return true;
			case ABOUT:
				showAboutDialog();
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
		
		Category category = new Category(this);
		category.load(info.id);
		
		menu.setHeaderTitle(category.Name);	
		menu.add(0, EDIT_ID, 0, "Edit").setIcon(android.R.drawable.ic_menu_edit);
		menu.add(0, DELETE_ID, 0,  "Delete").setIcon(android.R.drawable.ic_menu_delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			switch (item.getItemId()) {
				case EDIT_ID:
					editCategory(info.id);
					return true;
				case DELETE_ID:
					deleteCategory(info.id);
					return true;
				default:
					return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, CategoryActivity.class);
		intent.putExtra(Category.Columns.Id, id);

		startActivity(intent);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// PRIVATE METHODS
	//
	///////////////////////////////////////////////////////////////////////////////////////

	private void loadCategories() {
    	Query<Category> q = new Query<Category>(this, Category.class);
    	q.orderBy(Category.Columns.Name, Query.OrderDirection.Ascending);
    	
    	ListAdapter adapter = new EntityAdapter(
    			this, 
    			q.loadMap(), 
    			android.R.layout.simple_list_item_1,
    			new String[] { Category.Columns.Name },
    			new int[] { android.R.id.text1 }
    			);
    	
    	setListAdapter(adapter);
    }

	private void editCategory(final Long id) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		
		if(id != null) {
			Category category = new Category(this);
			category.load(id);
			input.setText(category.Name);
			alert.setTitle("Edit category");
		} else {
			alert.setTitle("New category");
		}

		alert.setMessage("Enter a new category name");
		alert.setView(input);
		
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				
				Category category = new Category(HomeinventoryActivity.this);
				
				if(id != null) {
					category.load(id);
				}
				
				category.Name = value;
				category.save();
				
				loadCategories();
			}
		});
		
		alert.setNegativeButton("Cancel", null);
		alert.show();
	}
	
	private void deleteCategory(final Long id) {
		Category category = new Category(this);
		category.load(id);
		category.delete();
		
		Query<Item> q = new Query<Item>(this, Item.class);
		q.where(Item.Columns.CategoryId).isEqualTo(id);
		q.delete();
		
		loadCategories();
	}

	private void pickRandom() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		String title = "Pickrand";
		String message = "There are no items to pick from";
		Query<Item> q = new Query<Item>(this, Item.class);
		
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
	
	private void showAboutDialog() {
		ScrollView scrollView = new ScrollView(this);
		TextView textView = new TextView(this);

		textView.setAutoLinkMask(Linkify.ALL);
		textView.setPadding(5, 5, 5, 5);
		textView.setTextColor(Color.WHITE);		
		textView.setText(Html.fromHtml(
				"<b>Created by Veneration Technology</b><br />" +
				"<b>Thanks to ActiveAndroid</b><br />" +
				"www.activeandroid.com<br /><br />"
				));
		
		scrollView.addView(textView);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder
	        .setIcon(R.drawable.icon)
	        .setTitle("About Home Inventory")
	        .setView(scrollView)
			.setCancelable(false)
			.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
		        }
			});
		
		AlertDialog alert = builder.create();
		alert.show();
	}
}