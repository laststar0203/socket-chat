package Chat_Client;
	
import java.awt.Button;
import java.awt.TextField;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


public class Main extends Application {
	
	Socket socket;
	TextArea textArea;
	
	//클라이언트 동작 메소드 입니다.
	public void startClient(String IP , int port) {
		Thread thread = new Thread() {
			public void run() {
				
				try {
					socket = new Socket(IP, port);
					receive(); //초기화후 서버로부터 어떠한 메시지를 받기 위해
					
				} catch (Exception e) {
					// TODO: handle exception
					if(!socket.isClosed()) {
						stopClient();
						System.out.println("[서버 접속 실패]");
						Platform.exit();
					}
				}
			};
		};
		thread.start();
	}
	//클라이언트 프로그램 종료 메소드입니다.
	public void stopClient() {
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	//서버로부터 메시지를 전달받는 메소드입니다.
	public void receive() {
		while (true) {
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[512];
				int lengh = in.read(buffer);
				if(lengh == -1) throw new IOException();
				String meString = new String(buffer, 0, lengh, "UTF-8");
				Platform.runLater(()->{
					//System.out.println(meString);
					textArea.appendText(meString);
				});
			} catch (Exception e) {
				// TODO: handle exception
				stopClient();
				break;
			}
		}
	}
	//서버로부터 메시지를 전송하는 메소드입니다.
	public void send(String message) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
					
				} catch (Exception e) {
					// TODO: handle exception
					stopClient();
				}
			}
		};
		thread.start();
	}
	
	//실제로 프로그램을 동작시키는 메소드 입니다.
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));
		
		HBox hBox = new HBox(); //Borderpene 안에 하나의 레이아웃을 추가
		hBox.setSpacing(5); //약간의 여백을 추가
		
		javafx.scene.control.TextField userName = new javafx.scene.control.TextField();
		userName.setPrefWidth(150); //너비지정
		userName.setPromptText("닉네임을 입려하세요"); //이 메시지가 보이게끔 함
		HBox.setHgrow(userName, Priority.ALWAYS); //Hobx 내부에서 해당 textfield가 출력할 수 있게끔 함 가 아니라 너비크기 맞추는거랑 관련있음
		
		javafx.scene.control.TextField ipText = new javafx.scene.control.TextField("127.0.0.1");
		javafx.scene.control.TextField portText = new javafx.scene.control.TextField("9876");
		
		portText.setPrefWidth(80);
		
		hBox.getChildren().addAll(userName , ipText , portText); //hBox 내부에 세 개의 텍스트필드가 추가될수 있도록 함
		root.setTop(hBox);
		
		textArea = new TextArea();
		textArea.setEditable(false); //textAreat가 수정될수 없게 함
		root.setCenter(textArea);
		
		javafx.scene.control.TextField input = new javafx.scene.control.TextField();
		input.setPrefWidth(Double.MAX_VALUE);
		input.setDisable(true); //접속 하기 이전에는 메시지를 전송할수 없도록 함
		input.setOnAction(event ->{
			send(userName.getText() + ": "+ input.getText() + "\n" );
			input.setText("");
			input.requestFocus(); //다시 메시지를 전송할수 있도록 함
		}); //엔터버튼을 눌렀을때
		
		javafx.scene.control.Button sendBtn = new javafx.scene.control.Button("보내기");
		sendBtn.setDisable(true);
		
		sendBtn.setOnAction(event ->{
			send(userName.getText() + ": "+ input.getText() + "\n" );
			input.setText("");
			input.requestFocus(); //다시 메시지를 전송할수 있도록 함 그니깐 채팅쪽에 커서가 게속 유지됨
		});//버튼을 눌렀을때
		
		javafx.scene.control.Button connectionButton = new javafx.scene.control.Button("접속하기");
		connectionButton.setOnAction(event ->{
			if(connectionButton.getText().equals("접속하기")) {
				int port = 9876;
				try {
					port = Integer.parseInt(portText.getText());
				} catch (Exception e) {
					// TODO: handle exception
				}
				startClient(ipText.getText(), port);
				Platform.runLater(()->{
					textArea.appendText("[채팅방 접속]\n");
				});
				connectionButton.setText("종료하기");
				input.setDisable(false);
				sendBtn.setDisable(false); //사용자가 버튼을 눌렀을때 처리할수 있도록 false로 바꿔줌
				input.requestFocus();
				
			}else {
				stopClient();
				Platform.runLater(()->{
					textArea.appendText(" [채팅방 퇴장] \n");
				});
				connectionButton.setText("접속하기");
				input.setDisable(true);
				sendBtn.setDisable(true); //입력 값 및 버튼을 누를 수 없도록 함
			}
		});
		BorderPane pane = new BorderPane();
		pane.setLeft(connectionButton);
		pane.setCenter(input);
		pane.setRight(sendBtn);
		
		root.setBottom(pane);
		Scene scene = new Scene(root , 400 ,400);
		primaryStage.setTitle("[채팅 프로그램]");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(e ->{
			stopClient();
		});
		primaryStage.show();
		
		connectionButton.requestFocus();
	}
	
	//프로그램의 진입점 입니다.
	public static void main(String[] args) {
		launch(args);
	}
}
