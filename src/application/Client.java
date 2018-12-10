package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

	Socket socket;
	
	public Client(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		receive();
	}
	//클라이언트로부터 메시지를 전달 받는 메소드
	public void receive() {
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while (true) {
						InputStream in = socket.getInputStream();
						byte[] buffer = new byte[512];
						int length = in.read(buffer); //read함수를 통해 전달을 받음
						while(length == -1) throw new IOException();
						System.out.println("[메시지 수신 성공] "
								+socket.getRemoteSocketAddress()
								+" : "+Thread.currentThread().getName()); //메시지를 보낸 클라이언트 주소
						String messege = new String(buffer, 0, length, "UTF-8");
						for (Client client : Main.clients) {
							client.send(messege);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					try {
						System.out.println("[메시지 수신 실패]"
								+socket.getRemoteSocketAddress()
								+" : "+Thread.currentThread().getName());
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
			}
		};
		Main.threadPool.submit(thread); //쓰레드를 쓰레드풀에 저장
		
	}
	//클라이언트에게 메시지를 전송하는 메소드
	public void send(String messeage) {
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = messeage.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				
				} catch (Exception e) {
					// TODO: handle exception
					try {
						
						System.out.println("[메시지 송신 실패"
								+socket.getRemoteSocketAddress()
								+" : "+Thread.currentThread().getName());
						Main.clients.remove(Client.this);
						socket.close();
						
					} catch (Exception e2) {
						// TODO: handle exception
						
					}
				}
				
			}
		};
		
	}

}
