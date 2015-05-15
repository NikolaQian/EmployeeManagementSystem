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
	//IP地址
	private static final String IP_ADRESS = "10.12.4.249";
	
	//文件长度
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
	 * 服务器提出下载请求，返回文件长度
	 */
//	private long request() throws IOException {
//		DataInputStream in = new DataInputStream(socket.getInputStream());
//
//		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//
//		String params = filePath + "@" + fileName;
//
//		// 发出下载请求
//		out.writeUTF(params);
//
//		out.flush();
//
//		// 返回文件长度
//		return in.readLong();
//
//	}

	/**
	 * 接收保存文件
	 * 
	 * @param message
	 * 
	 * @param localFile
	 *            文件
	 * @param tempFile
	 *            临时文件
	 * @param fileLength
	 *            文件长度
	 * @param message
	 */
	private void receiveFile(File localFile, File tempFile, long fileLength)
			throws IOException {

		// 获取socket的输入流包装成bufferedInputStream
		BufferedInputStream in = new BufferedInputStream(
				socket.getInputStream());

		// 获取本地关联的临时文件流
		FileOutputStream out = new FileOutputStream(tempFile);

		byte[] buf = new byte[BUFFER];

		// 每次下载的长度
		int len;

		// 累计下载的长度
		int count = 0;
		

		while (start && ((len = in.read(buf)) >= 0)) {

			out.write(buf, 0, len);

			out.flush();

			count += len;

			Message message = new Message();

			message.what = DownLoadActivity.DOWNLOAD_UPDATE;

			// 当前进度值
			int progress = (int) (((float) count / fileLength) * 100);

			// 当先下载文件大小
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

		// 如果下载完成
		if (count == fileLength) {
			// 临时文件重命名
			if (tempFile.renameTo(localFile)) {

				Message message = new Message();

				message.what = DownLoadActivity.DOWNLOAD_SUCCESS;

				message.obj = localFile.getAbsolutePath();

				handler.sendMessage(message);
				
			}
		}

//		// 临时文件重命名
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
		
		// 如果暂停下载 删除已有的临时文件
		if (!start) {

			if (tempFile.exists()) {
				tempFile.delete();
			}
		}

	}
	
	/**
	 * 转换文件大小
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

	// 从服务器下载文件
	private void download() {
		try {

			String localFilePath = filePath + fileName;
			
			File file = new File(localFilePath);
			
			// 建立一个新的目录
			if(new File(filePath).exists()){
				new File(filePath).mkdirs();
			}
			
			if(file.exists()){
				file.delete();
			}	
			
			
			String tempFilePath = filePath + fileName.substring(0, fileName.indexOf(".")) + ".tmp";

			// 临时文件
			File tempFile = new File(tempFilePath);

			socket = new Socket(IP_ADRESS, 1234);
			
			// 保存到本地
			receiveFile(file, tempFile, FILE_LENGTH);
			
//			// 文件长度
//			long fileLength = request();
//			
//
//			if (fileLength > 0) {
//
//				// 保存到本地
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
