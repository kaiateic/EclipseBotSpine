package com.ble.eic.git.botspine;

import java.util.ArrayList;
import java.util.List;
 

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "deviceManager";
 
    // StoreDatas table name
    private static final String TABLE_DEVICE = "devices";
 
    // StoreDatas Table Columns names
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_HAS = "has_password";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_DEVICE + "("
                + KEY_ADDRESS + " TEXT PRIMARY KEY," + KEY_PASSWORD + " TEXT"
                +")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new contact
    void addStoreData(StoreData contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        Log.d("ddd","add data");
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, contact.getAddress()); // StoreData Name
        values.put(KEY_PASSWORD, contact.getPassword()); // StoreData Phone
       // values.put(KEY_HAS, contact.getHasPassword());
        // Inserting Row
        db.insert(TABLE_DEVICE, null, values);
        db.close(); // Closing database connection
    }
 
    
    public boolean recordExists(String address) {
    	 SQLiteDatabase db = this.getReadableDatabase();
    	 Cursor cursor = db.query(TABLE_DEVICE, new String[] { KEY_ADDRESS,
         		KEY_PASSWORD}, KEY_ADDRESS + "=?",
                 new String[] {address}, null, null, null, null);
    	   
    	   boolean exists = (cursor.getCount() > 0);
    	   cursor.close();
    	   return exists;
    	}
   public boolean checkExist(String address){
//	   SQLiteDatabase db = this.getReadableDatabase();
//	   String Query = "Select * from " + TABLE_DEVICE + " where " + KEY_ADDRESS + "="
//               + address;
//	   Cursor cursor = db.rawQuery(Query, null);
//       if(cursor.getCount()<=0){
//    	   return false;
//      }
//   return true;
	   SQLiteDatabase db = this.getReadableDatabase();
	   
	   Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_DEVICE, null);
	   Boolean rowExists;

	   if (mCursor.moveToFirst())
	   {
	      // DO SOMETHING WITH CURSOR
	     rowExists = true;

	   } else
	   {
	      // I AM EMPTY
	      rowExists = false;
	   }
	   return rowExists;
   }
    // Getting single contact
    StoreData getStoreData(String address) {
       
    	 SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_DEVICE, new String[] { KEY_ADDRESS,
        		KEY_PASSWORD}, KEY_ADDRESS + "=?",
                new String[] {address}, null, null, null, null);
        String temp = null;
        StoreData contact = null;
        if (cursor != null){
            cursor.moveToFirst();
            //temp = cursor.getString(0);
            temp = cursor.getString(1);
        	contact = new StoreData(address,
                cursor.getString(1));
        	 
        }
        return contact;
        // return contact
       
    }
     
    // Getting All StoreDatas
    public List<StoreData> getAllStoreDatas() {
        List<StoreData> contactList = new ArrayList<StoreData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICE;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                StoreData contact = new StoreData();
                contact.setAddress(cursor.getString(0));
                contact.setPassword(cursor.getString(1));
               // contact.setHasPassword(Integer.parseInt(cursor.getString(2)));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return contactList;
    }
 
    // Updating single contact
    public int updateStoreData(StoreData contact) {
    	Log.d("ddd","update");
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, contact.getPassword());
        //values.put(KEY_HAS, contact.getPassword());
 
        // updating row
        return db.update(TABLE_DEVICE, values, KEY_ADDRESS + " = ?",
                new String[] { String.valueOf(contact.getAddress()) });
    }
 
    // Deleting single contact
    public void deleteStoreData(StoreData contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEVICE, KEY_ADDRESS + " = ?",
                new String[] { String.valueOf(contact.getAddress()) });
        db.close();
    }
 
 
    // Getting contacts Count
    public int getStoreDatasCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DEVICE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
 
}
