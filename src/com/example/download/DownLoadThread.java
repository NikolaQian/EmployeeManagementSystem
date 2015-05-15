package com.example.download;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import com.example.employeemanagementsystem.DownLoadActivity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownLoadThread extends Thread {
	//IP��ַ
	private static final String IP_ADRESS = "10.12.4.249";
	
	//�ļ�����
	private static final long FILE_LENGTH = 16470685;
	
	private Socket socket;

	private String filePath;

	private String fileName;

	public final static int BUFFER = 8 * 1024;

	private Handler handler;

	private boolean start = true;


	public DownLoadThread(Handler handler, String filePath, String fileName) {
		this.handler = handler;
		this.filePath = filePath;
		this.fileName = fileName;
	}

	/*
	 * ����������������󣬷����ļ�����
	 */
//	private long request() throws IOException {
//		DataInputStream in = new DataInputStream(socket.getInputStream());
//
//		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//
//		String params = filePath + "@" + fileName;
//
//		// ������������
//		out.writeUTF(params);
//
//		out.flush();
//
//		// �����ļ�����
//		return in.readLong();
//
//	}

	/**
	 * ���ձ����ļ�
	 * 
	 * @param message
	 * 
	 * @param localFile
	 *            �ļ�
	 * @param tempFile
	 *            ��ʱ�ļ�
	 * @param fileLength
	 *            �ļ�����
	 * @param message
	 */
	private void receiveFile(File localFile, File tempFile, long fileLength)
			throws IOException {

		// ��ȡsocket����������װ��bufferedInputStream
		BufferedInputStream in = new BufferedInputStream(
				socket.getInputStream());

		// ��ȡ���ع�������ʱ�ļ���
		FileOutputStream out = new FileOutputStream(tempFile);

		byte[] buf = new byte[BUFFER];

		// ÿ�����صĳ���
		int len;

		// �ۼ����صĳ���
		int count = 0;
		

		while (start && ((len = in.read(buf)) >= 0)) {

			out.write(buf, 0, len);

			out.flush();

			count += len;

			Message message = new Message();

			message.what = DownLoadActivity.DOWNLOAD_UPDATE;

			// ��ǰ����ֵ
			int progress = (int) (((float) count / fileLength) * 100);

			// ���������ļ���С
			String currentSize = DownLoadThread.formatFileSize(count);

			Map<String, Object> map = new HashMap<String, Object>();

			map.put("progress", progress);

			map.put("currentSize", currentSize);

			map.put("fileSize", DownLoadThread.formatFileSize(fileLength));

			message.obj = map;

			handler.sendMessage(message);
			
			if(count == fileLength) break;
			
		}
		
		out.close();

		in.close();

		// ����������
		if (count == fileLength) {
			// ��ʱ�ļ�������
			if (tempFile.renameTo(localFile)) {

				Message message = new Message();

				message.what = DownLoadActivity.DOWNLOAD_SUCCESS;

				message.obj = localFile.getAbsolutePath();

				handler.sendMessage(message);
				
			}
		}

//		// ��ʱ�ļ�������
//		if (tempFile.renameTo(localFile)) {
//
//			Message message = new Message();
//
//			message.what = DownLoadActivity.DOWNLOAD_SUCCESS;
//
//
//			handler.sendMessage(message);
//
//		}	
		
		// �����ͣ���� ɾ�����е���ʱ�ļ�
		if (!start) {

			if (tempFile.exists()) {
				tempFile.delete();
			}
		}

	}
	
	/**
	 * ת���ļ���С
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */	
	public static String formatFileSize(long fileS) {

		if (fileS == 0) {
			return "0.00B";
		}

		DecimalFormat dFormat = new DecimalFormat("#.00");

		String fileSizeString = "";

		if (fileS < 1024) {
			fileSizeString = dFormat.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = dFormat.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = dFormat.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = dFormat.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	// �ӷ����������ļ�
	private void download() {
		try {

			String localFilePath = filePath + fileName;
			
			File file = new File(localFilePath);
			
			// ����һ���µ�Ŀ¼
			if(new File(filePath).exists()){
				new File(filePath).mkdirs();
			}
			
			if(file.exists()){
				file.delete();
			}	
			
			
			String tempFilePath = filePath + fileName.substring(0, fileName.indexOf(".")) + ".tmp";

			// ��ʱ�ļ�
			File tempFile = new File(tempFilePath);

			socket = new Socket(IP_ADRESS, 1234);
			
			// ���浽����
			receiveFile(file, tempFile, FILE_LENGTH);
			
//			// �ļ�����
//			long fileLength = request();
//			
//
//			if (fileLength > 0) {
//
//				// ���浽����
//				receiveFile(file, tempFile, fileLength);
//
//			} else {
//				handler.sendEmptyMessage(DownLoadActivity.DOWNLOAD_FAILL);
//			}
		} catch (IOException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(DownLoadActivity.DOWNLOAD_FAILL);
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		download();
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}
}
