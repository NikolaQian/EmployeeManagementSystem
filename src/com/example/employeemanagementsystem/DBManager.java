package com.example.employeemanagementsystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DBManager {
	//数据库在sd卡中的路径
	private static final String DB_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/data";
	//数据库名称
	private static final String DB_NAME = "employees_manager.db";
	
	private SQLiteDatabase db;
	private Context context;
	
	public DBManager(Context context){
		this.context = context;
		this.db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + DB_NAME, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS employees (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCGHAR, miID VARCHAR, " +
								"department VARCHAR, position VARCHAR, phoneNum VARCHAR, age INTEGER, photo BLOB DEFAULT NULL)");
		
	}
	
	
	
	//增加单个元素
	public void addOne(Employee e){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		e.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, os);
		db.execSQL("INSERT INTO employees VALUES(NULL,?,?,?,?,?,?,?)", new Object[]{e.getName(), e.getMiID(), e.getDepartment(), e.getPosition(),
																							e.getPhoneNum(), e.getAge(), os.toByteArray()});	
	}
	
	//增加多个元素
	public void add(List<Employee> es){
		db.beginTransaction();
		
		for(Employee e : es){
			addOne(e);
		}
		
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	//删除元素
	public int delete(String miID){
		return db.delete("employees", "miID = ?", new String[]{miID});		
	}
	
	//更新数据
	public void update(Employee e, Employee oldEmployee){
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		e.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, os);
//		db.execSQL("UPDATE employees SET name=?,miID=?,department=?,phoneNum=?,age=?,photo=? WHERE miID=?", 
//										new Object[]{e.getName(), e.getMiID(), e.getDepartment(),
//										e.getPhoneNum(), e.getAge(), os.toByteArray(), oldMiID});
		
		ContentValues cv = new ContentValues();
		cv.put("name", e.getName());
		cv.put("miID", e.getMiID());
		cv.put("department", e.getDepartment());
		cv.put("position", e.getPosition());
		cv.put("phoneNum", e.getPhoneNum());
		cv.put("age", e.getAge());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		e.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, os);
		cv.put("photo", os.toByteArray());
		
		db.update("employees", cv, "miID=?", new String[]{oldEmployee.getMiID()});
	}
	
	//通过miID查询数据
	public List<Employee> query(String miID){
		ArrayList<Employee> lst = new ArrayList<Employee>();
		Cursor c = db.rawQuery("SELECT * FROM employees WHERE miID=?", new String[]{String.valueOf(miID)});
		while(c.moveToNext()){
			Employee person = new Employee();
    		person.set_id(c.getInt(c.getColumnIndex("_id")));
    		person.setName(c.getString(c.getColumnIndex("name")));
    		person.setMiID(c.getString(c.getColumnIndex("miID")));
    		person.setDepartment(c.getString(c.getColumnIndex("department")));
    		person.setPosition(c.getString(c.getColumnIndex("position")));
    		person.setPhoneNum(c.getString(c.getColumnIndex("phoneNum")));
    		person.setAge(c.getInt(c.getColumnIndex("age")));
    		if(c.getBlob(c.getColumnIndex("photo")) == null){
    			person.setPhoto(((BitmapDrawable)context.getResources().getDrawable(R.drawable.img)).getBitmap());
    		}else {
    			byte[] photo = c.getBlob(c.getColumnIndex("photo"));
        		person.setPhoto(BitmapFactory.decodeByteArray(photo, 0, photo.length));
    		}    			
    		lst.add(person);
		}
		
		return lst;
	}
		
	//查询所有数据
	public List<Employee> queryAll(){
		ArrayList<Employee> lst = new ArrayList<Employee>();
		Cursor c = db.rawQuery("SELECT * FROM employees", null);
		while(c.moveToNext()){
			Employee person = new Employee();
    		person.set_id(c.getInt(c.getColumnIndex("_id")));
    		person.setName(c.getString(c.getColumnIndex("name")));
    		person.setMiID(c.getString(c.getColumnIndex("miID")));
    		person.setDepartment(c.getString(c.getColumnIndex("department")));
    		person.setPosition(c.getString(c.getColumnIndex("position")));
    		person.setPhoneNum(c.getString(c.getColumnIndex("phoneNum")));
    		person.setAge(c.getInt(c.getColumnIndex("age")));
    		if(c.getBlob(c.getColumnIndex("photo")) == null){
    			person.setPhoto(((BitmapDrawable)context.getResources().getDrawable(R.drawable.img)).getBitmap());
    		}else {
    			byte[] photo = c.getBlob(c.getColumnIndex("photo"));
        		person.setPhoto(BitmapFactory.decodeByteArray(photo, 0, photo.length));
    		} 
    		lst.add(person);
		}
		
		return lst;
	}
	
	public void close(){
		db.close();
	}
}
