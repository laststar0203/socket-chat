package JavaChat.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ClientInfoStatus;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.plaf.synth.SynthSpinnerUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public class Main extends Application {

	public static ExecutorService threadPool;
	public static Vector<Client> clients = new Vector<Client>();
	public static Map<String, DataOutputStream> users = new HashMap<String, DataOutputStream>();
	public static Map<String, Integer> userScores = new HashMap<String, Integer>();
	public static boolean isVoteTime;
	public static int yes = 0, no = 0;

	// DataOutputStream을 입력한 이유는 개별적으로 사용자에게 보내기 위해서

	ServerSocket serverSocket;

	void startServer(String IP, int port) {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(IP, port));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if (!serverSocket.isClosed()) {
				stopServer();
			}
		}

		Runnable thread = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						DataInputStream in = new DataInputStream(socket.getInputStream());
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						String nickname = in.readUTF();

						users.put(nickname, out);
						userScores.put(nickname, 0);

						clients.add(new Client(socket, nickname));
						System.out.println(
								"[클라이언트 접속]" + socket.getRemoteSocketAddress() + Thread.currentThread().getName());

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
		threadPool = Executors.newCachedThreadPool();
		threadPool.submit(thread);

	}

	void stopServer() {
		try {
			Iterator<Client> iterator = clients.iterator();
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}

			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			if (threadPool != null && !threadPool.isShutdown()) {
				threadPool.shutdown();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void start(Stage primaryStage) {

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));

		TextArea textArea = new TextArea();
		textArea.setEditable(true);
		textArea.setFont(new Font("Serif", 15));
		root.setCenter(textArea);

		Button toggleButton = new Button("시작하기");
		toggleButton.setMaxWidth(Double.MAX_VALUE);
		BorderPane.setMargin(toggleButton, new Insets(1, 0, 0, 0));
		root.setBottom(toggleButton);

		String IP = "127.0.0.1";
		int port = 9876;

		toggleButton.setOnAction(event -> {
			if (toggleButton.getText().equals("시작하기")) {
				startServer(IP, port);
				Platform.runLater(() -> {
					String message = String.format("[서버시작]\n", IP, port);
					textArea.appendText(message);
					toggleButton.setText("종료하기");
				});

			} else {
				stopServer();
				Platform.runLater(() -> {
					String message = String.format("[서버종료]\n", IP, port);
					textArea.appendText(message);
					toggleButton.setText("시작하기");
				});
			}
		});
		Scene scene = new Scene(root, 400, 400);
		primaryStage.setTitle("[채팅 서버]");
		primaryStage.setOnCloseRequest(event -> {
			stopServer();
		});
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
