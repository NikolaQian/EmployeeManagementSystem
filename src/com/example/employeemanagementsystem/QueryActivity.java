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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SimpleAdapter.ViewBinder;

public class QueryActivity extends Activity {
	ListView mListView = null;
	private static final int GET_CODE = 2;
	private String miID;
	private boolean isDelete = false;
	
	private OnItemClickListener mListView_ls = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			HashMap<String, Object> map = (HashMap<String, Object>)mListView.getItemAtPosition(arg2);
			Intent intent = new Intent();
			intent.setClass(QueryActivity.this, AddOrEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("isEdit", true);
			String miID = ((String)map.get("miID")).substring(5);
			bundle.putString("miID", miID);
			
			intent.putExtras(bundle);
			startActivityForResult(intent, GET_CODE);
			
//			finish();
		}
	};
	
	private OnClickListener query_cencal_button_ls = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			miID = ((EditText)findViewById(R.id.edit_query_miID)).getText().toString();
			if(miID.equals("")){
				Toast.makeText(QueryActivity.this, "����Ϊ��", Toast.LENGTH_SHORT).show();
				return;
			}
					
			displayData();
		}
	};
	
	//listview���������¼�
		private OnItemLongClickListener mListView_long_ls = new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(QueryActivity.this);  
				builder.setMessage("�Ƿ�ɾ����Ա�����ݣ�")  
				       .setCancelable(false)  
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int id) { 
				        	   HashMap<String, Object> map = (HashMap<String, Object>)mListView.getItemAtPosition(arg2);
				        	   DBManager dbmgr = new DBManager(QueryActivity.this);
				        	   dbmgr.delete(((String)map.get("miID")).substring(5));
				        	   dbmgr.close();
				        	   isDelete = true;
				        	   displayData();
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
	
	private void displayData(){
		DBManager dbmgr = new DBManager(QueryActivity.this);
		List<Employee> employees = dbmgr.query(miID);
		dbmgr.close();
		
		if(0 == employees.size()){
			if(isDelete){//�Ƿ��Ѿ�ɾ����ɾ����Ͳ�����ʾ�������ֻ�
				employees.clear();
				isDelete = false;
			}else {
				Toast.makeText(QueryActivity.this, "�����ڴ��ֻ�ID", Toast.LENGTH_SHORT).show();			
				return;
			}
		}
			ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String,Object>>();
			
			for(Employee e : employees){
				HashMap<String, Object> map = new HashMap<String, Object>();			
	            map.put("name", e.getName());
	            map.put("miID", "�ֻ�ID:" + e.getMiID());
	            map.put("department", "����:" + e.getDepartment());
	            map.put("position", "ְλ:" + e.getPosition());
	            map.put("phoneNum", "�ֻ���:" + e.getPhoneNum());
	            map.put("age", "����:" + e.getAge());
	            map.put("photo", e.getPhoto());
				lst.add(map);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(QueryActivity.this, lst, R.layout.list_view, 
											new String[]{"name", "miID", "department", "position", "phoneNum", "age", "photo"}, 
											new int[]{R.id.name_qa, R.id.miID_qa, R.id.department_qa, R.id.position_qa, R.id.phoneNum_qa, R.id.age_qa, R.id.img_qa});
			
			adapter.setViewBinder(new ViewBinder() {  
				
	            public boolean setViewValue(View view, Object data,  
	                    String textRepresentation) {  
	                //�ж��Ƿ�Ϊ����Ҫ����Ķ���  
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == GET_CODE){
			if(resultCode == RESULT_CANCELED){
			}else {
				Bundle bundle = data.getExtras();
				miID = bundle.getString("newMiID");//��ȡAddOrEditActivity�ķ���ֵ���µ�miID
				displayData();
			}
		}
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("��ѯ");
		setContentView(R.layout.query);
		
		Button query_cencal_button = (Button)findViewById(R.id.query_cencal_button);
		query_cencal_button.setOnClickListener(query_cencal_button_ls);
		
		mListView = (ListView)findViewById(R.id.query_list_view);
		mListView.setOnItemClickListener(mListView_ls);
		mListView.setOnItemLongClickListener(mListView_long_ls);
	}

}
