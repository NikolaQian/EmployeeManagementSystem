package com.example.employeemanagementsystem;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import com.example.download.DownLoadThread;
import com.example.employeemanagementsystem.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownLoadActivity extends Activity {
	//���ݿ�洢·��
	public static final String FILE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/";
	//���ݿ�����
	public static final String FILE_NAME = "employees_manager.db";
	// ����ʧ��״̬
	public final static int DOWNLOAD_FAILL = -1;
	// ���سɹ�״̬
	public final static int DOWNLOAD_SUCCESS = 1;
	// ���ظ���״̬
	public final static int DOWNLOAD_UPDATE = 2;
	// �����ļ�
	public final static int DOWNLOAD_EXITS = 3;
	// �Ƿ���ͻȻ��ͣ������
	private boolean isStop = true;
	
	private ProgressBar progress;
	
	private TextView fileNameText, progressText;
	
	private Socket socket;
	
	private Handler handler;
	
	private DownLoadThread runnable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("�������ݿ�");
		setContentView(R.layout.download);
		
		initView();
		
		downLoad();
	}
	
	private void initView(){
		progress = (ProgressBar)findViewById(R.id.download_progress);
		fileNameText = (TextView)findViewById(R.id.download_filename);
		progressText = (TextView)findViewById(R.id.download_progress_text);
		
		fileNameText.setText(FILE_NAME);
	}
	
	private void downLoad(){
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == DOWNLOAD_FAILL) {
					Toast.makeText(DownLoadActivity.this, "���س���", Toast.LENGTH_SHORT).show();

					isStop = false;
				}else if (msg.what == DOWNLOAD_UPDATE) {

					Map<String, Object> map = (HashMap<String, Object>) msg.obj;

					progress.setProgress((Integer) map.get("progress"));

					progressText.setText(map.get("currentSize") + "/"
							+ map.get("fileSize"));

					isStop = true;

				}else if (msg.what == DOWNLOAD_SUCCESS){
					progressText.setText("���ظ��³ɹ�");
					
					isStop = false;
				}
				
			}
		};
		
		runnable = new DownLoadThread(handler, FILE_PATH, FILE_NAME);
		
		runnable.start();
		
	}
	
	/**
	 * ֹͣ�����߳�
	 */
	public void stopDownTread() {

		if (isStop) {
			runnable.setStart(false);
//			Toast.makeText(DownLoadActivity.this, "�߳�ֹͣ", Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void onDestroy() {
		stopDownTread();
		Intent intent = new Intent();
		intent.setClass(DownLoadActivity.this, MainActivity.class);
		startActivity(intent);
		super.onDestroy();
		finish();
	}
}
