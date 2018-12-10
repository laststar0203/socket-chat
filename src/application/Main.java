package application;
	
import java.awt.Button;
import java.awt.Font;
import java.awt.TextArea;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	public static ExecutorService threadPool; //여러개의 쓰레드를 효과적으로 다루도록 해주는 클래스
	public static Vector<Client> clients = new Vector<Client>();
	
	ServerSocket serverSocket;
	
	//서버를 구동시켜서 클라이언트의 연결을 기다리는 메소드입니다.
	public void startServer(String IP, int port) {
		try {
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(IP, port));
		}catch (Exception e) {
		
			e.printStackTrace();
			if (!serverSocket.isClosed()) {
			 stopServer();
				
			}
			return;
			
		}
		//클라이언트가 접속할수 있을때까지 기다리는 쓰레드입니다.
		Runnable thraed = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						clients.add(new Client(socket));
						System.out.println("[클라이언트 접속]"
								+socket.getRemoteSocketAddress()
								+" : "+Thread.currentThread().getName());
					} catch (Exception e) {
						// TODO: handle exception
						if (!serverSocket.isClosed()) {
							stopServer();
							
						}
						break;
					}
				}
			}
		};
		threadPool = Executors.newCachedThreadPool(); //쓰레드 초기화
		threadPool.submit(thraed);
		
		}
	// 서버의 작동을 중지시키는 메소드입니다.
	public void stopServer() {
		try {
			//현재 작동중인 모든 소켓 종료
			Iterator<Client> iterator = clients.iterator();
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			//서버 소켓 닫기
			if(serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			//쓰레드 풀 종료하기\
			if(threadPool != null && !threadPool.isShutdown()) {
				threadPool.shutdown();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	//UI를 생성하고, 실질적으로 프로그램을 동작시킨느 메소드입니다.
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));
		
		javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea();
		textArea.setEditable(false);
		textArea.setFont(new javafx.scene.text.Font("Serif", 15));
		root.setCenter(textArea);
		
		
		
		javafx.scene.control.Button toggleButton = new javafx.scene.control.Button();
		toggleButton.setMaxWidth(Double.MAX_VALUE);
		BorderPane.setMargin(toggleButton, new Insets(1,0,0,0));
		root.setBottom(toggleButton);
		
		String IP = "127.0.0.1";
		int port = 9876;
		
		toggleButton.setOnAction(event ->{
			if(toggleButton.getText().equals("시작하기")) {
				startServer(IP, port);
				Platform.runLater(()->{
					String messege = String.format("[서버 시작]\n", IP, port);
				});
			}
		});
		
		
		
	}
	
	//프로그램의 진입점입니다.
	public static void main(String[] args) {
		launch(args);
	}
}
