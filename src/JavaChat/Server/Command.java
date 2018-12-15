package JavaChat.Server;

import java.util.Iterator;
import java.util.StringTokenizer;

enum SendType {
	BROADCAST, PERSONAL, NONE
};

public class Command {

	private Command() {
		// TODO Auto-generated constructor stub
	}

	// sendMessage로 보내야 할것은 1 broadcast로 보내야할것은 0 무반응은 -1
	public static SendType switchCommand(String comdline1, StringTokenizer tokenizer, String serverSendMessage,
			Client client) throws Exception {

		switch (comdline1.toLowerCase()) {
		case "help":
			client.serverSendMessage = helpAction();

			return SendType.PERSONAL;

		case "setprefix":

			client.serverSendMessage = setInfo(Info.PREFIX, tokenizer, serverSendMessage);

			return SendType.PERSONAL;

		case "setnickname":

			client.serverSendMessage = setInfo(Info.NICKNAME, tokenizer, serverSendMessage);

			return SendType.PERSONAL;
		case "players":

			client.serverSendMessage = Main.clients.size() + "";

			return SendType.PERSONAL;
		case "playerlist":

			client.serverSendMessage = playerListInfo(serverSendMessage);

			return SendType.PERSONAL;
		case "vote":
			voteAction(tokenizer);

			return SendType.NONE;
		case "luckey":

			return SendType.PERSONAL;
		case "rank":

			return SendType.PERSONAL;
		case "y":
			if (!Main.isVoteTime)
				return SendType.NONE;
			Main.yes++;
			client.serverSendMessage = "찬성 선택!";
			return SendType.PERSONAL;
		case "n":
			if (!Main.isVoteTime)
				return SendType.NONE;
			Main.no++;
			client.serverSendMessage = "반대 선택!";

			return SendType.PERSONAL;
		default:
			client.serverSendMessage = "현재 사용할 수 없는 명령어입니다!\n";
			return SendType.NONE;
		}

	}

	public static String helpAction() {
		final String helpMessage = "\n/help 명령어들의 설명을 볼 수 있습니다\n" + "/setprefix (바꿀이름) (색깔) 칭호를 변경하실 수 있습니다.\n"
				+ "/setnickname (바꿀이름) (색깔) 닉네임을 변경하실 수 있습니다.\n" + "/players 현재 채팅방에 있는 인원 수를 확인할 수 있습니다.\n"
				+ "/playerlist 현재 채팅방에 있는 유저들이 누군지 확인할 수 있습니다\n" + "/vote (투표주제) 채팅방에서 투표를 시작할수 있습니다.\n"
				+ "/luckey 도박이 가능합니다.\n" + "/rank 자신의 점수 랭킹을 볼 수 있습니다";

		return helpMessage;

	}

	public static String setInfo(Info info, StringTokenizer tokenizer, String message) throws Exception {

		String comdline2 = tokenizer.nextToken();
		String comdline3 = tokenizer.nextToken();
		switch (info) {
		case NICKNAME:
			message = "0000" + "&" + comdline2 + "&" + comdline3;

		case PREFIX:
			message = "0001" + "&" + comdline2 + "&" + comdline3;

		}

		return message;

	}

	public static String playerListInfo(String message) {
		String nameslist = "";
		Iterator<String> nickNames = Main.users.keySet().iterator();
		while (nickNames.hasNext()) {
			nameslist += nickNames.next() + ",";
		}
		message = nameslist;

		return message;
	}

	public static void voteAction(StringTokenizer tokenizer) throws Exception {

		if (Main.isVoteTime)
			Client.broadcastMessage("다음 투표때 신청해주세요" , SendMessageType.SERVER);

		String comdline2 = tokenizer.nextToken();
		Client.broadcastMessage("투표가 시작되었습니다 !! \n 주제:" + comdline2 + "\n" + "찬성은 \\y 반대는 \\n 입력해주세요!" , SendMessageType.SERVER);
		Main.yes = 0;
		Main.no = 0;

		Runnable thread = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					int yes = Main.yes;
					int no = Main.no;
					if (yes + no >= Main.clients.size()) {
						Client.broadcastMessage(
								"찬성:" + yes + "  반대 :" + no + "\n" + (yes == no) != null ? "동점 입니다!"
										: (yes > no) ? "찬성 승리!" : "반대 승리!" , SendMessageType.SERVER);
					}
					break;
				}
			}
		};
		Main.threadPool.submit(thread);

	}

}
