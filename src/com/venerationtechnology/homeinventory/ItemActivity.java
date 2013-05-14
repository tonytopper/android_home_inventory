package com.venerationtechnology.homeinventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Gallery;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import com.venerationtechnology.homeinventory.R;
import com.venerationtechnology.homeinventory.Category;
import com.venerationtechnology.homeinventory.Item;
import java.util.ArrayList;

public class ItemActivity extends Activity {
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// MEMBERS
	//
	///////////////////////////////////////////////////////////////////////////////////////

	private static final int SELECT_IMAGE = 0;
		
	private Item mItem;
	private EditText mName;
	private EditText mSerialNumber;
	private Button selectImageButton;
	private Button saveButton;
	private Gallery itemGallery;
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// PUBLIC METHODS
	//
	///////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);
        mName = (EditText) findViewById(R.id.EditName);
        mSerialNumber = (EditText) findViewById(R.id.EditSerialNumber);
        itemGallery = (Gallery) findViewById(R.id.gallery);
        itemGallery.setAdapter(new ImageAdapter(this));

        itemGallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(ItemActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  if (requestCode == SELECT_IMAGE) {
	    if (resultCode == Activity.RESULT_OK) {
	      Uri selectedImage = data.getData();
	      // TODO Save the image the application private content provider in a way that 
	      // links it to item we are currently working with 
	      if (selectedImage != null) {
	    	  Cursor cursor = getContentResolver().query(selectedImage, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
	    	  cursor.moveToFirst();
	    	  String imageFilePath = cursor.getString(0);
	    	  // TODO Get this drawable into the itemGallery 
	    	  Drawable d = Drawable.createFromPath(imageFilePath);
	    	  Log.i("ItemActivity.onActivityResult", "We need to save this somewhere: " + imageFilePath);
	    	  cursor.close();
	      }
	    }
	  }
	}
    
    @Override
	protected void onResume() {
		super.onResume();
		
		Bundle extras = this.getIntent().getExtras();
		long id = extras.getLong(Item.Columns.Id);
		
		mItem = new Item(this);
		mItem.load(id);
		Log.i("ItemActivity.onResume()", "Loading: " + mItem);
		
		mName.setText(mItem.Name);
		mSerialNumber.setText(mItem.SerialNumber);
		
		this.selectImageButton = (Button)this.findViewById(R.id.SelectImage);
		this.selectImageButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Intent addImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				startActivityForResult(addImage, SELECT_IMAGE);
		    }
		});
		this.saveButton = (Button)this.findViewById(R.id.saveBtn);
		this.saveButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	mItem.Name = mName.getText().toString();
		    	mItem.SerialNumber = mSerialNumber.getText().toString();
 		    	mItem.save();
		    	finish();
		    }
		});
		
        	
	}
    
    public class ImageAdapter extends BaseAdapter {
        
    	int mGalleryItemBackground;
        
        // TODO Evaluate if there is a better container for these items
        public Integer[] mImageIds = {
                R.drawable.icon
        };
        
        public ArrayList<Drawable> mImageDrawables = new ArrayList<Drawable>(); 
    	
        private Context mContext;
        
        public ImageAdapter(Context c) {
            mContext = c;
            this.addItem(getResources().getDrawable(R.drawable.icon));
            //TypedArray a = obtainStyledAttributes(android.R.styleable.Theme);
            //mGalleryItemBackground = a.getResourceId(android.R.styleable.Theme_galleryItemBackground, 0);
            //a.recycle();
        }
        
        // TODO Make this work
        public void addItem(Drawable item) {
        	mImageDrawables.add(item);
        	//Integer i = getResources().getIdentifier(item.toString(), "drawable", getPackageName());
        	//mImageIds[this.getCount()] = i;
        }
        
        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        
        // TODO Understand how this works
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);

            i.setImageDrawable(mImageDrawables.get(position));
            //i.setImageResource(mImageIds[position]);
            i.setLayoutParams(new Gallery.LayoutParams(150, 100));
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            i.setBackgroundResource(mGalleryItemBackground);

            return i;
        }
    }
}