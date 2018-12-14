package JavaChat.Client;

import java.awt.SecondaryLoop;
import java.util.StringTokenizer;



enum FixInfo {PREFIX , NICKNAME};

public class ReceiveCommand {

	Main client;
	
	
	public ReceiveCommand(Main main) {
		// TODO Auto-generated constructor stub
		this.client = main;
		
	}
	
	
	public void checkCommand(String message) {
		String action = message.substring(0, 1);
		try {
			switch (action) {
			case "&":
				StringTokenizer tokenizer = new StringTokenizer(message, "&");
				String comdline1 = tokenizer.nextToken();
				
				swithCommand(comdline1, tokenizer , message);
				
				break;

			default:
				
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			try {
				
			} catch (NullPointerException e2) {
				// TODO: handle exception
				
				message = "[오류] 커멘더가 제대로 입력되지 않았습니다!!";
			} catch (Exception e2) {
				
				e2.printStackTrace();
				message = "[오류] 알수 없는 오류!!";
			}
		
		}
		
		
	}
	
	public void swithCommand(String comdline1 , StringTokenizer tokenizer , String message) throws Exception {
		
		
			String comdline2 = tokenizer.nextToken();
			String comdline3 = tokenizer.nextToken();
			//TextArea 글자 색 어떻게 변경하는지 몰라서 잠시 보류
			switch (comdline1) {
			case "0000":
				setInfo(comdline2, comdline3, FixInfo.NICKNAME);
				message = "[서버알림] 변경되었습니다!";
				break;
			case "0001":
				setInfo(comdline2, comdline3, FixInfo.PREFIX);
				message = "[서버알림] 변경되었습니다!";
				break;

			default:
				break;
			}
		
		
	}
	
	public void setInfo(String name, String color , FixInfo info) throws Exception{
		//color는 TextArea 글자 색 어떻게 변경하는지 몰라서 잠시 보류
		switch(info) {
		case NICKNAME:		
			
			client.nickName = name;
			break;
		case PREFIX:
			
			client.prefix = name;
		
			break;
		}
		
	}
		
}
