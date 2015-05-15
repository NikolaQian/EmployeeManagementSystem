package com.example.employeemanagementsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

public class QueryAllActivity extends Activity {
	private DBManager dbmgr;
	private ListView mListView = null;
	private static final int GET_CODE = 1;
	
	private OnItemClickListener mListView_ls = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			HashMap<String, Object> map = (HashMap<String, Object>)mListView.getItemAtPosition(arg2);
			
			Intent intent = new Intent();
			intent.setClass(QueryAllActivity.this, AddOrEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("isEdit", true);
			String miID = ((String)map.get("miID")).substring(5);
			bundle.putString("miID", miID);
			
			intent.putExtras(bundle);
			startActivityForResult(intent, GET_CODE);
			
		}
	};
	
	//listview长按监听事件
			private OnItemLongClickListener mListView_long_ls = new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						final int arg2, long arg3) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(QueryAllActivity.this);  
					builder.setMessage("是否删除此员工数据？")  
					       .setCancelable(false)  
					       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
					           public void onClick(DialogInterface dialog, int id) { 
					        	   HashMap<String, Object> map = (HashMap<String, Object>)mListView.getItemAtPosition(arg2);
					        	   DBManager dbmgr = new DBManager(QueryAllActivity.this);
					        	   dbmgr.delete(((String)map.get("miID")).substring(5));
					        	   dbmgr.close();
					        	   displayAllData();
					           }  
					       })  
					       .setNegativeButton("No", new DialogInterface.OnClickListener() {  
					           public void onClick(DialogInterface dialog, int id) {  
					                dialog.cancel();  
					           }  
					       });  
					builder.show();
					return false;
				}
			};
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GET_CODE) {
			if(resultCode == RESULT_CANCELED){
			}else {
				displayAllData();
			}
		}
	}

	private void displayAllData(){
		dbmgr = new DBManager(this);
		List<Employee> employees = dbmgr.queryAll();
		dbmgr.close();
		ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String,Object>>();
		
		for(Employee e : employees){
			HashMap<String, Object> map = new HashMap<String, Object>();			
            map.put("name", e.getName());
            map.put("miID", "手环ID:" + e.getMiID());
            map.put("department", "部门:" + e.getDepartment());
            map.put("phoneNum", "手机号:" + e.getPhoneNum());
            map.put("position", "职位:" + e.getPosition());
            map.put("age", "年龄:" + e.getAge());
            map.put("photo", e.getPhoto());
			lst.add(map);
		}
		
		mListView = (ListView)findViewById(R.id.list_view);
		SimpleAdapter adapter = new SimpleAdapter(QueryAllActivity.this, lst, R.layout.list_view, 
									new String[]{"name", "miID", "department", "position", "phoneNum", "age", "photo"}, 
									new int[]{R.id.name_qa, R.id.miID_qa, R.id.department_qa, R.id.position_qa, R.id.phoneNum_qa, R.id.age_qa, R.id.img_qa});
		
		adapter.setViewBinder(new ViewBinder() {  
			
            public boolean setViewValue(View view, Object data,  
                    String textRepresentation) {  
                //判断是否为我们要处理的对象  
                if(view instanceof ImageView  && data instanceof Bitmap){  
                    ImageView iv = (ImageView) view;  
                  
                    iv.setImageBitmap((Bitmap) data);  
                    return true;  
                }else  
                return false;  
            }  
        });
		mListView.setAdapter(adapter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("显示所有数据");
		setContentView(R.layout.query_all);
		
		displayAllData();
		
		mListView.setOnItemClickListener(mListView_ls);
		mListView.setOnItemLongClickListener(mListView_long_ls);
	}
	
}
