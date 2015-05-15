package com.example.employeemanagementsystem;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocket {
	private String ip;
	private int port;
	private Socket socket = null;
	DataOutputStream out = null;
	DataInputStream getMessageStream = null;
	
	public ClientSocket(){
	}

	public ClientSocket(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public void createConnection(){
		try {
			socket = new Socket(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}finally{
		}
		
	}
	
	public void sendMessage(String sendMessage){
		try {
			out = new DataOutputStream(socket.getOutputStream());
			
			if(sendMessage.equals("Windows")){
				out.writeByte(0x01);
				out.flush();
				return;
			}
			
			if(sendMessage.equals("Windows")){
				out.writeByte(0x02);
				out.flush();
				return;
			}
			
			if(sendMessage.equals("Windows")){
				out.writeByte(0x03);
				out.flush();
				return;
			}else {
				out.writeUTF(sendMessage);
				out.flush();
			}			
			
		} catch (IOException e) {
			e.printStackTrace();
			
			if(out != null){
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public DataInputStream getMessageStream(){
		
		try {
			getMessageStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			
			if(getMessageStream != null){
				try {
					getMessageStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return getMessageStream;
		
	}
	
	public void shutDownConnevtion(){
		
		if(out != null){
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(getMessageStream != null){
			try {
				getMessageStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
