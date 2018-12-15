package JavaChat.Client;

import java.awt.SecondaryLoop;
import java.util.StringTokenizer;

enum RecieveMessageType {
	SERVER, CHAT
};

public class ReceiveCommand {

	Main client;

	public ReceiveCommand(Main main) {
		// TODO Auto-generated constructor stub
		this.client = main;

	}

	public String checkCommand(String message) {
	
		String action = message.substring(0, 1);
		String fixMessage = message.substring(1);
		
		try {
			switch (action) {
			case "&":

				return "[서버알림]" + fixMessage + "\n";

			case "#":

				return "[오류]" + fixMessage + "\n";

			default:

				return message + "\n";

			}

			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "[오류] 수신된 명령어를 분석하는 과정에서 알수 없는 오류가 발생하였습니다 \n";

		}
		/*
		 * if(client.prefix.equals("")) return client.nickName + " : "+message; else
		 * return "["+client.prefix+"] "+client.nickName + " : "+message;
		 */
	}

}
