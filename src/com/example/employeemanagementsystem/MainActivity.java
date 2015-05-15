package com.example.employeemanagementsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

public class MainActivity extends Activity {
	private ListView mListView = null;
	private String miID;
	private static final int GET_CODE = 3;
	private SimpleAdapter adapter;
	private boolean isDelete = false;
	private EditText password;
	private Socket socket;
	private boolean start = true;
	//IP��ַ
		private static final String IP_ADRESS = "10.12.4.249";
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			TextView text = (TextView)findViewById(R.id.text_net_state);
			switch (msg.what) {
			case 0:
				MainActivity.this.miID = (String)msg.obj;
				selectEmployee();
				break;
			case 1:
				Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case 2:
				text.setText((String)msg.obj);
				break;

			default:
				break;
			}
		}
		
	};

	
	//��׽������յ����ݣ������д���
	private Thread mThread = new Thread(){

		@Override
		public void run() {
//			text = (TextView)findViewById(R.id.text_view2); 
			Message message = new Message();
			int connectTime = 0;
			while(true && start){
			try {
				socket = new Socket();
				connectTime = 0;
                //�����ͻ���socket,ע��:������localhost��127.0.0.1��Androidģ�������Լ���Ϊlocalhost  
                socket.connect(new InetSocketAddress(IP_ADRESS,1234), 0);
                connectTime = 1;
                
                Message message1 = new Message();
            	message1.what = 2;
            	message1.obj = "���ӳɹ�";
            	handler.sendMessage(message1);
            	Message message2 = new Message();
                message2.what = 1;
            	message2.obj = "���ӳɹ�";
            	handler.sendMessage(message2);           	
            	
                
                
            	while(true){
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        			
                    int count;
                    char[] buffer = new char[10];
                    
                    if ((count = in.read(buffer)) > 0){                     	
                    	
                    	String msg = getInfoBuff(buffer, count);
                    	
                    	Message message3 = new Message();
                    	message3.what = 0;
                    	message3.obj = msg;
                    	handler.sendMessage(message3);                    	
                    	Thread.sleep(1);
                    	
                    	if(msg.equals("exit")){
                        	message.what = 0;
                    		message.obj = "exit";
                        	handler.sendMessage(message);
                        	
                    		in.close();
                    		if (null != socket){  
                                socket.close();  
                            }
                    		
                    		
                    	}else { 
                    }
                    }  
            	} 
            	
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            	try {
            		if(connectTime == 0){
            			Message message4 = new Message();
            			message4.what = 1;
    	            	message4.obj = "�ȴ����ӳ�ʱ,�����ر�";
    	            	handler.sendMessage(message4);            	
    					Thread.sleep(1000);
    					finish();
    					break;
            		}else {
            			if(start){
            				Message message5 = new Message();
                			message5.what = 1;
        	            	message5.obj = "���ӶϿ�";
        	            	handler.sendMessage(message5); 
            			}            			
    					
    					Message message6 = new Message();
    					message6.what = 2;
    	            	message6.obj = "�ȴ�����";
    	            	handler.sendMessage(message6);
            		}	            	
				} catch (InterruptedException e1) {
				}
            } catch (InterruptedException e) {
			}
			}
            
			
		}
		
	};
	
	private String getInfoBuff(char[] buff, int count)  
    {  
        char[] temp = new char[count];  
        for(int i=0; i<count; i++)  
        {  
            temp[i] = buff[i];  
        }  
        return new String(temp);  
    }	
	
//	//listview��������¼�
//	private OnItemClickListener mListView_ls = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			HashMap<String, Object> map = (HashMap<String, Object>)mListView.getItemAtPosition(arg2);
//			Intent intent = new Intent();
//			intent.setClass(MainActivity.this, AddOrEditActivity.class);
//			Bundle bundle = new Bundle();
//			bundle.putBoolean("isEdit", true);
//			bundle.putString("miID", MainActivity.this.miID);
//			
//			intent.putExtras(bundle);
//			startActivityForResult(intent, GET_CODE);
//		}
//		
//	};
//	
//	//listview���������¼�
//	private OnItemLongClickListener mListView_long_ls = new OnItemLongClickListener() {
//
//		@Override
//		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//				final int arg2, long arg3) {
//			
//			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);  
//			builder.setMessage("�Ƿ�ɾ����Ա�����ݣ�")  
//			       .setCancelable(false)  
//			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
//			           public void onClick(DialogInterface dialog, int id) { 
//			        	   HashMap<String, Object> map = (HashMap<String, Object>)mListView.getItemAtPosition(arg2);
//			        	   DBManager dbmgr = new DBManager(MainActivity.this);
//			        	   dbmgr.delete(((String)map.get("miID")).substring(5));
//			        	   dbmgr.close();
//			        	   isDelete = true;
//			        	   selectEmployee();
//			           }  
//			       })  
//			       .setNegativeButton("No", new DialogInterface.OnClickListener() {  
//			           public void onClick(DialogInterface dialog, int id) {  
//			                dialog.cancel();  
//			           }  
//			       });  
//			builder.show();
//			return false;
//		}
//	};
	
	
	
	//��ѯ����ʾ����
	protected void selectEmployee(){
		DBManager dbmgr = new DBManager(MainActivity.this);
		List<Employee> employees = dbmgr.query(miID);
		dbmgr.close();
		
		
		if(0 == employees.size()){
			if(isDelete){//�Ƿ��Ѿ�ɾ����ɾ����Ͳ�����ʾ�������ֻ�
				employees.clear();
				isDelete = false;
			}else {
				Toast.makeText(MainActivity.this, "�����ڴ��ֻ�ID", Toast.LENGTH_SHORT).show();			
				return;
			}			
		}
			ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String,Object>>();			
						
			for(Employee e : employees){
				HashMap<String, Object> map = new HashMap<String, Object>();			
	            map.put("name", e.getName());
	            map.put("miID", "�ֻ�ID:" + e.getMiID());
	            map.put("department", "����:" + e.getDepartment());
	            map.put("position", "�ֻ���:" + e.getPosition());
	            map.put("position", "ְλ:" + e.getPosition());
	            map.put("age", "����:" + e.getAge());
	            map.put("photo", e.getPhoto());
				lst.add(map);
			}			
			
			TextView text_net_state = (TextView)findViewById(R.id.text_net_state);
			text_net_state.setText("Employee:");
			adapter = new SimpleAdapter(MainActivity.this, lst, R.layout.list_view, 
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
	
	//���շ������ݲ�����ʾ
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == GET_CODE){
			if(resultCode == RESULT_CANCELED){
			}else {
				
//				Bundle bundle = data.getExtras();
//				miID = bundle.getString("newMiID");//��ȡAddOrEditActivity�ķ���ֵ���µ�miID
//				selectEmployee();
			}
		}
	}
	
	//�����˵�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(Menu.NONE, Menu.FIRST+1, 1, "����Ա��").setIcon(android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, Menu.FIRST+2, 2, "��ѯԱ��").setIcon(android.R.drawable.ic_menu_search);
		menu.add(Menu.NONE, Menu.FIRST+3, 3, "��ʾ����Ա��").setIcon(android.R.drawable.ic_dialog_dialer);
		menu.add(Menu.NONE, Menu.FIRST+4, 4, "���ظ���");
		
		return true;
	}
	
	//�˵�ѡ���¼�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		
		password = new EditText(this);
		switch (item.getItemId()) {
		case Menu.FIRST+1:{
			new AlertDialog.Builder(this).setTitle("Enter password").setIcon(
					android.R.drawable.ic_dialog_info).setView(
							password).setPositiveButton("ok", new OnClickListener() {								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									if(!password.getText().toString().equals(
														MainActivity.this.getResources().getString(R.string.password))){
										Toast.makeText(MainActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
									}else {
										Intent intent = new Intent();
										intent.setClass(MainActivity.this, AddOrEditActivity.class);
										Bundle bundle = new Bundle();
										bundle.putBoolean("isAdd", true);
										intent.putExtras(bundle);
										startActivity(intent);
									}
								}
							}).setNegativeButton("cancel", null).show();
			
		}			
			break;
		case Menu.FIRST+2:{
			new AlertDialog.Builder(this).setTitle("Enter password").setIcon(
					android.R.drawable.ic_dialog_info).setView(
							password).setPositiveButton("ok", new OnClickListener() {								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									if(!password.getText().toString().equals(
														MainActivity.this.getResources().getString(R.string.password))){
										Toast.makeText(MainActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
									}else {
										Intent intent = new Intent();
										intent.setClass(MainActivity.this, QueryActivity.class);
										startActivity(intent);
									}
								}
							}).setNegativeButton("cancel", null).show();
			
		}			
			break;
		case Menu.FIRST+3:{
			new AlertDialog.Builder(this).setTitle("Enter password").setIcon(
					android.R.drawable.ic_dialog_info).setView(
							password).setPositiveButton("ok", new OnClickListener() {								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									if(!password.getText().toString().equals(
														MainActivity.this.getResources().getString(R.string.password))){
										Toast.makeText(MainActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
									}else {										
										Intent intent = new Intent();
										intent.setClass(MainActivity.this, QueryAllActivity.class);
										startActivity(intent);
									}
								}
							}).setNegativeButton("cancel", null).show();			
			
		}			
			break;
		case Menu.FIRST+4:{
			new AlertDialog.Builder(this).setTitle("Enter password").setIcon(
					android.R.drawable.ic_dialog_info).setView(
							password).setPositiveButton("ok", new OnClickListener() {								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									if(!password.getText().toString().equals(
														MainActivity.this.getResources().getString(R.string.password))){
										Toast.makeText(MainActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
									}else {
										try {
											start = false;
											socket.close();											
										} catch (IOException e) {
											e.printStackTrace();
										}
										
										Intent intent = new Intent();
										intent.setClass(MainActivity.this, DownLoadActivity.class);
										startActivityForResult(intent, GET_CODE);
										finish();
									}
								}
							}).setNegativeButton("cancel", null).show();			
			
		}			
			break;
		default:
			break;
		}
		return false;
	}
	
	/*
	 * ���������η��ؼ��˳�
	 * 
	 */
	private long firstTime;
	
	@Override
	public void onBackPressed() {
		if(System.currentTimeMillis() - firstTime < 3000){
			finish();
		}else {
			firstTime = System.currentTimeMillis();
			Toast.makeText(MainActivity.this, "�ٰ�һ���˳�Ӧ�ã�", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("����ͨ��");
		setContentView(R.layout.activity_main);
		
		
		mListView = (ListView)findViewById(R.id.net_list_view);
//		mListView.setOnItemClickListener(mListView_ls);
//		mListView.setOnItemLongClickListener(mListView_long_ls);
		
		mThread.start();
	}
}
