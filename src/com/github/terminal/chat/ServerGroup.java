package com.github.terminal.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Terminal Chat Server Group
 * based on TCP, only for learning.
 *
 * @author AkaneMurakawa
 * @date 2017-10-5
 */
public class ServerGroup {
	private ServerSocket server;
	/**
	 * client OutputStream Collection. 
	 */
	private List<PrintWriter> clientOut;
	
	public ServerGroup() throws IOException{
		/**
		 * apply for port
		 */
		System.out.println("Welcome to Terminal Chat!\n");
		System.out.println("Starting Server Group...");
		server = new ServerSocket(8088);
		System.out.println("Started success! input 'exit' to exit! Wait for Connecting...\n");
		clientOut = new ArrayList<>();
	}
	
	/**
	 * start your server
	 */
	public void startServer(){
		while(true){
			try {
				Socket socket = server.accept();
				System.out.println(socket.getInetAddress().getHostName()+" online!\n");
				Thread t = new Thread(new ClientHandler(socket));
				t.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * this thread gets all client OutputStream all the time.
	 * @author HanaeYuuma
	 *
	 */
	private class ClientHandler implements Runnable{
		private Socket socket;
		PrintWriter pw = null;
		public ClientHandler(Socket socket){
			this.socket = socket;
		}
		public void run(){
			try {
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				
				OutputStream os = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
				//autoFlush = true.very important.!!!!!!!!!!!!!!!!!!!!!!!!!!
				pw = new PrintWriter(osw,true);
				
				synchronized(clientOut){
					clientOut.add(pw);
				}
				
				String msg;
				while((msg = br.readLine()) != null){
					synchronized(clientOut){
						for (PrintWriter out : clientOut) {
							out.println(msg);
						}
					}
				}
			} catch (Exception e) {
			}finally{
				System.out.println(socket.getInetAddress().getHostName() + " offline!");
				synchronized(clientOut){
					clientOut.remove(pw);
				}
				if (!socket.isClosed()) {
					try {
						socket.close();
					} catch (Exception e2) {
						// do nothing
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			ServerGroup server = new ServerGroup();
			server.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
























