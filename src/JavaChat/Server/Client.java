package JavaChat.Server;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BrokenBarrierException;



enum Info {PREFIX , NICKNAME};

public class Client {
	
	Socket socket;
	String nickName;

	
	
	public Client(Socket socket , String nickName) {
		this.socket = socket;
		this.nickName = nickName;
		
		receive();
	}

	private void receive() {
		// TODO Auto-generated method stub
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while (true) {
						DataInputStream in = new DataInputStream(socket.getInputStream());
						byte[] buffer = new byte[512];
						
						char action = (char)in.read();
						
						int length = in.read(buffer);
						if(length == -1) new IOException();
						
						System.out.println("[메시지 수신 완료]"+
								socket.getRemoteSocketAddress() +
								Thread.currentThread().getName());

						String mesage = new String(buffer , 0 , length , "UTF-8");
						
						if(action == '/') command(mesage);
						else broadcastMessage(action + mesage);
						
						/*
						String messege = new String(buffer, 0, length, "UTF-8");
						for (Client client : Main.clients) {
							client.send
						}
						*/
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
		};
		Main.threadPool.submit(thread);
	}
	
	private void command(String command) {
		StringTokenizer tokenizer = new StringTokenizer(command, " ");
		String serverSendMessage = "";
		try {	
			String comdline1 = tokenizer.nextToken();
			
			Command.switchCommand(comdline1, tokenizer , serverSendMessage);
			
			sendMessage(serverSendMessage);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			try {
				
			} catch (NullPointerException e2) {
				// TODO: handle exception
			} catch (Exception e3) {
				
			}
		}
		
		
		
	}
	
	
	private void sendMessage(String message) {
		
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				
				// TODO Auto-generated method stub
				byte[] buffer = new byte[512];
				
				try {
					Main.users.get(nickName).writeUTF(message);
					Main.users.get(nickName).flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Main.threadPool.submit(thread);
		
	}
	
	public static void broadcastMessage(String message){
		
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Iterator<String> i = Main.users.keySet().iterator();
				while(i.hasNext()) {
					try {
						String key = i.next();
						
						Main.users.get(key).writeUTF(message);
						Main.users.get(key).flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Main.threadPool.submit(thread);
	
	}
	
	
	
	
}
