package JavaChat.Server;

import java.util.Iterator;
import java.util.StringTokenizer;

public class Command {

	private Command() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	public static void switchCommand(String comdline1 , StringTokenizer tokenizer , String serverSendMessage) throws Exception{
		
		
		
		switch (comdline1.toLowerCase()) {
		case "help":
			Command.helpAction(serverSendMessage);
			
		case "setPrefix":
			
			Command.setInfo(Info.PREFIX, tokenizer , serverSendMessage);
				
			break;
		case "setnickname":
			
			Command.setInfo(Info.NICKNAME, tokenizer , serverSendMessage);
			
			break;
		case "players":
			
			serverSendMessage = Main.clients.size()+"";
			
			break;
		case "playerlist":
			
			Command.playerListInfo(serverSendMessage);
			
			break;
		case "vote":
			Command.voteAction(tokenizer);
			break;
		case "luckey":
			
			break;
		case "rank":
			break;
		case "y":
			if(!Main.isVoteTime) return;
			Main.yes++;
			break;
		case "n":
			if(!Main.isVoteTime) return;
			Main.no++;
			break;
		default:
			break;
		}
		

	}
	
	
	public static void helpAction(String message) {
		final String helpMessage = 
				"/help 명령어들의 설명을 볼 수 있습니다"+
				"/setprefix (바꿀이름) (색깔) 칭호를 변경하실 수 있습니다."+
				"/setnickname (바꿀이름) (색깔) 닉네임을 변경하실 수 있습니다."+
				"/players 현재 채팅방에 있는 인원 수를 확인할 수 있습니다."+
				"/playerlist 현재 채팅방에 있는 유저들이 누군지 확인할 수 있습니다"+
				"/vote (투표주제) 채팅방에서 투표를 시작할수 있습니다."+
				"/luckey 도박이 가능합니다."+
				"/rank 자신의 점수 랭킹을 볼 수 있습니다.";
		message = helpMessage;
	}
	
	public static void setInfo(Info info , StringTokenizer tokenizer , String message) {
		String comdline2 = tokenizer.nextToken();
		String comdline3 = tokenizer.nextToken();
		switch (info) {
		case NICKNAME:
		message = "$0000"+"&"+comdline2+"&"+comdline3;
			
		case PREFIX:
		message =  "$0001"+"&"+comdline2+"&"+comdline3;
	
		}
		
		
	}
	public static void playerListInfo(String message) {
		String nameslist = "";
		Iterator<String> nickNames = Main.users.keySet().iterator();
		while (nickNames.hasNext()) {
			nameslist += nickNames.next() + ",";
		}
		message = nameslist;
	}
	
	
	
	public static void voteAction(StringTokenizer tokenizer) {
		if(Main.isVoteTime) {
			try {
				Client.broadcastMessage("다음 투표때 신청해주세요");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		String comdline2 = tokenizer.nextToken();
		Client.broadcastMessage("투표가 시작되었습니다 !! \n 주제:"
				+comdline2+"\n"
				+"찬성은 \\y 반대는 \\n 입력해주세요!");
		Main.yes = 0;
		Main.no = 0;
		
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					int yes = Main.yes;
					int no = Main.no;
					if(yes + no >= Main.clients.size()) {
						Client.broadcastMessage("[투표 결과]\n"+
									"찬성:"+yes+"  반대 :"+no+"\n"+
									(yes == no) != null ? "동점 입니다!" : (yes > no) ? "찬성 승리!" : "반대 승리!");
					}
					break;
				}
			}
		};
		Main.threadPool.submit(thread);
	
	}	

}
