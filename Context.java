package version2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Context {
	public static Game game = null;
	public static Socket socket = null;
	public static BufferedWriter writer = null;
	public static BufferedReader reader = null;

	public static String account = null;

	public static GameLoggyDialog loggyDialog = null;

	public static void send(String msg) {
		if (socket == null)
			return;
		// 准备发送对象
		if (writer == null) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (Exception e) {

			}
		}

		try {
			writer.write(msg + "\n");
			writer.flush();
		} catch (IOException e) {

		}
	}
}
