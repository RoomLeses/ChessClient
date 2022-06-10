package version2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ReceiveThread extends Thread {
	private Socket socket = null;

	public ReceiveThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		// 准备接受对象
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {

		}
		while (true) {
			// 接受信息
			// 开始接受。如果对面没有发送任何信息，readLine函数会阻塞
			String line = null;
			try {
				line = reader.readLine();
				System.out.println("客户收到了：" + line);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (line == null || line.trim().equals(""))
				continue;

			// 接受服务器端发来的棋桌状态信息，该信息格式如下：
			// "desktop;list;10;等待中;张三，李四"
			String[] s = line.split(";");
			if (s[0].equalsIgnoreCase("desktop")) {
				if (s[1].equalsIgnoreCase("list")) {
					int desktopno = Integer.parseInt(s[2]);

					String[] players = s[4] != null ? s[4].split(",") : new String[] { "", "" };

					if (Context.loggyDialog == null) {
						continue;
					}
					Context.loggyDialog.updateDesktop(desktopno, players);
				}

			} else if (s[0].equalsIgnoreCase("begin")) {
				// 服务器发来调试格式：“begin;先手姓名;后手姓名”
				// 1.如果游戏大厅窗口格式处于显示状态，关闭它
				if (Context.loggyDialog != null) {
					Context.loggyDialog.dispose();
					Context.loggyDialog = null;
				}
				// 2.给游戏窗口的两个玩家名称赋值
				Context.game.reset(Game.GAME_TYPE_NETWORK, s[1], s[2]);

				// 3.置游戏状态为初始状态
			} else if (s[0].equalsIgnoreCase("piece")) {
				// 服务器发来信息格式："piece;10;6"
				int row = Integer.parseInt(s[1]);
				int col = Integer.parseInt(s[2]);
				//
				try {
					Context.game.putNetworkpiece(row, col);
				} catch (Exception e) {
					System.out.println("落子失败" + line + ":" + e.getMessage());

				}
			} else if (s[0].equalsIgnoreCase("end")) {
				// 服务器发来信息格式："end;1"或者"end;2"或者"end;-1"
				if (s[1].equals("-1")) {
					JOptionPane.showMessageDialog(null, "对方中途退出");
				}
				if (s[1].equals("1")) {
					JOptionPane.showMessageDialog(null, "黑方胜出");
				}
				if (s[1].equals("2")) {
					JOptionPane.showMessageDialog(null, "白方胜出");

				}

				Context.game.curPieceColor = 0;
			}

		}

	}
}
