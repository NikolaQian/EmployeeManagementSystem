package com.example.employeemanagementsystem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class AddOrEditActivity extends Activity {
	private static final int CAMERA_WITH_DATA = 3023;
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	//�洢������Ƭ��λ��
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	//���յõ���ͼƬ
	private File mCurrentPhotoFile;
	
	boolean isEdit = false;
	boolean isAdd = false;
	String oldMiID = "";
	private Employee oldEmployee;
	private Employee employee;
	private EditText edit_name;
	private ImageView image_view_photo;
	private EditText edit_miID;
	private Spinner spinner_department;
	private EditText edit_position;
	private EditText edit_phoneNum;
	private EditText edit_age;
	private Bitmap photo;
	
	private OnClickListener btn_ok_ls = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			String name = edit_name.getText().toString();
			String miID = edit_miID.getText().toString();
			String department = spinner_department.getSelectedItem().toString();
			String position = edit_position.getText().toString();
			String phoneNum = edit_phoneNum.getText().toString();
			String age = edit_age.getText().toString();
			DBManager dbmgr = new DBManager(AddOrEditActivity.this);
			
			if(0 == name.length()){
				Toast.makeText(AddOrEditActivity.this, "������������", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(0 == miID.length()){
				Toast.makeText(AddOrEditActivity.this, "�������ֻ�ID��", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if((dbmgr.query(miID).size() != 0) && (!miID.equals(oldMiID))){
				Toast.makeText(AddOrEditActivity.this, "��С���ֻ�ID�Ѿ����ڣ������ٴ�ע��", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if((0 == department.length()) || (department.equals("��ѡ��һ������"))){
				Toast.makeText(AddOrEditActivity.this, "��ѡ��һ�����ţ�", Toast.LENGTH_SHORT).show();
				return;
			}
			
//			if((0 != name.length()) && (0 != miID.length()) && (0 != department.length()) && (!department.equals("��ѡ��һ������"))){
//				employee = new Employee();
//				employee.setName(name);
//				employee.setMiID(miID);
//				employee.setDepartment(department);
//				
//				if((dbmgr.query(miID).size() != 0) && (!miID.equals(oldMiID))){
//					Toast.makeText(AddOrEditActivity.this, "��С���ֻ�ID�Ѿ����ڣ������ٴ�ע��", Toast.LENGTH_SHORT).show();
//					return;
//				}
//			}else {				
//				Toast.makeText(AddOrEditActivity.this, "����������ǰ��������", Toast.LENGTH_SHORT).show();
//				return;
//			}
			
			employee = new Employee();
			
			employee.setName(name);
			employee.setMiID(miID);
			employee.setDepartment(department);
			employee.setPosition(position);
			employee.setPhoneNum(phoneNum);
			if(age.length() == 0){
				age = "0";
			}
			employee.setAge(Integer.parseInt(age));
			employee.setPhoto(photo);			
			
			if(isEdit){
				dbmgr.update(employee, oldEmployee);
//				Toast.makeText(AndOrEditActivity.this, "�޸ĳɹ�", Toast.LENGTH_SHORT).show();
			}
			
			if(isAdd){
				dbmgr.addOne(employee);
//				Toast.makeText(AndOrEditActivity.this, "��ӳɹ�", Toast.LENGTH_SHORT).show();
			}
			
			
			dbmgr.close();
			
//			Intent intent = new Intent();
//			intent.setClass(AndOrEditActivity.this, QueryAllActivity.class);
//			startActivity(intent);
//			setResult(RESULT_OK);
			
			//�����µ�miID����һ��activity
			Intent intent = AddOrEditActivity.this.getIntent();
			Bundle bundle = intent.getExtras();
			bundle.putString("newMiID", miID);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			
			finish();
		}
	};
	
	//�����Ƭ
		private OnClickListener image_view_photo_ls = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doPickPhotoAction();
			}
		};
		
		//��ȡ��Ƭ
		private void doPickPhotoAction(){
			Context dialogContext = new ContextThemeWrapper(AddOrEditActivity.this, android.R.style.Theme_Light);
			String cancal = "����";
			String[] choices = {"����", "�������ѡ��"};
			ListAdapter adapter = new ArrayAdapter<>(dialogContext, 
											android.R.layout.simple_expandable_list_item_1,choices);
			AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
			builder.setTitle("ѡ���ȡ��Ƭ��ʽ");
			builder.setSingleChoiceItems(adapter, -1, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							switch (which) {
							case 0:{
								String status = Environment.getExternalStorageState();
								if(status.equals(Environment.MEDIA_MOUNTED)){//�ж��Ƿ���sd��
									doTakePhoto();//�������ȡ��Ƭ����
								}else {
									Toast.makeText(AddOrEditActivity.this, "û��sd��", Toast.LENGTH_SHORT).show();//û��sd��
								}
							}								
								break;
							case 1:
								doPickPhotoFromGallery();//������ȡ��Ƭ
								break;
							
							default:
								break;
							}
						}
					});
			builder.setNegativeButton(cancal, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			});
			
			builder.create().show();
		}
		
		//���ջ�ȡͼƬ
		protected void doTakePhoto(){
			try{
				if(!PHOTO_DIR.exists())
				PHOTO_DIR.mkdirs();//������Ƭ�洢Ŀ¼
				mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());//������Ƭ����
				Intent intent = getTakePickIntent(mCurrentPhotoFile);
				startActivityForResult(intent, CAMERA_WITH_DATA);
			}catch(ActivityNotFoundException e) {
				Toast.makeText(this, "TakePhotoNotFind", Toast.LENGTH_LONG).show();
			}
		}
		
		//������ȡ��Ƭ
		protected void doPickPhotoFromGallery(){
			try{
			Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
			} catch(ActivityNotFoundException e){
				Toast.makeText(this, "PickPhotoFromGalleryNotFind", Toast.LENGTH_LONG).show();
			}
		}

		public static Intent getTakePickIntent(File f){
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			return intent;
		}
		
		//�õ�ǰʱ���ͼƬ����
		private String getPhotoFileName(){
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("'IMG'_yyyy-MM-dd HH:mm:ss");
			
			return sdf.format(date) + ".jpg";
		}
		
		
		
		//���Gallery��Intent
		public static Intent getPhotoPickIntent(){
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 160);
			intent.putExtra("outputY", 160);
			intent.putExtra("return-data", true);
			return intent;
		}
		
		//�жϲ�������պ����ĸ��Է������	
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(resultCode != RESULT_OK){
				return;
			}
			switch (requestCode) {
			case PHOTO_PICKED_WITH_DATA:{//������������õ���Ƭ
				photo = data.getParcelableExtra("data");
				image_view_photo.setImageBitmap(photo);
				
			}			
				break;
			case CAMERA_WITH_DATA:{
				doCropPhoto(mCurrentPhotoFile);//�ü�������������Ƭ
			}
				break;
			default:
				break;
			}
			
		}

		//�ü���Ƭ
		protected void doCropPhoto(File f) {
			try{
			Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
			} catch(ActivityNotFoundException e){
				Toast.makeText(this, "CropPhotoNotFind", Toast.LENGTH_LONG).show();
			}
		}
		
		//�����ֻ��Դ��ü�����
		public static Intent getCropImageIntent(Uri photoUri){
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(photoUri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 160);
			intent.putExtra("outputY", 160);
			intent.putExtra("return-data", true);
			return intent;
		}

	
	//�������
	private void doPutData(){
		edit_name.setText(oldEmployee.getName());
		image_view_photo.setImageBitmap(oldEmployee.getPhoto());
		edit_miID.setText(oldEmployee.getMiID());
		setSpnnerItemSelectedByvalue(spinner_department, oldEmployee.getDepartment());//���ò���Ĭ��ѡ��
		edit_position.setText(oldEmployee.getPosition());
		edit_phoneNum.setText(oldEmployee.getPhoneNum());
		edit_age.setText(String.valueOf(oldEmployee.getAge()));
	}
	
	//���ݲ��ŵ�ֵ������Ĭ�ϵ�ѡ��
	private void setSpnnerItemSelectedByvalue(Spinner spinner, String value){
		SpinnerAdapter adapter = spinner.getAdapter();
		int size = adapter.getCount();
		for(int i=0; i<size; i++){
			if(value.equals(adapter.getItem(i).toString())){
				spinner.setSelection(i, true);
				return;
			}
		}
	}
	
	//��ø����ؼ�
	private void getWidget(){
		edit_name = (EditText)findViewById(R.id.name_edit);
		image_view_photo = (ImageView)findViewById(R.id.icon_edit);
		edit_miID = (EditText)findViewById(R.id.miID_edit);
		spinner_department = (Spinner)findViewById(R.id.spinner_department_edit);
		edit_position = (EditText)findViewById(R.id.position_edit); 
		edit_phoneNum = (EditText)findViewById(R.id.phoneNum_edit);
		edit_age = (EditText)findViewById(R.id.age_edit);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Ա��");
		setContentView(R.layout.add_or_edit);
		
		//�õ��ϸ�activity��������
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();		
		isEdit = bundle.getBoolean("isEdit");
		isAdd = bundle.getBoolean("isAdd");
//		Toast.makeText(AndOrEditActivity.this, oldMiID, Toast.LENGTH_SHORT).show();
		photo = ((BitmapDrawable)getResources().getDrawable(R.drawable.unknown)).getBitmap();
		
		//��ø����ؼ�
		getWidget();		
		
		if(isEdit){//�ж��Ƿ�Ϊ�޸����ݴӶ������Ƿ��������
			oldMiID = bundle.getString("miID");
			DBManager dbmgr = new DBManager(AddOrEditActivity.this);
			List<Employee> lst = dbmgr.query(oldMiID);
			oldEmployee = lst.get(0);
			photo = oldEmployee.getPhoto();
			doPutData();
		}
		
		
		
		Button btn_ok = (Button)findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(btn_ok_ls);//��ɰ�ť�ļ����¼�
		Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {//ȡ����ť�ļ����¼�
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		image_view_photo.setOnClickListener(image_view_photo_ls);
	}
}
