package MultiChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * ClientListener class listens for client messages and forwards them to
 * ServerDispatcher.
 */
public class ClientListener extends Thread {

	private Socket mSocket;
	private ServerThread mDispatcher;
	private BufferedReader mSocketReader;
	PrintStream os = null;
	public static String name;
	
//	dsd

	public ClientListener(Socket aSocket,
			ServerThread aServerMsgDispatcher) throws IOException {
		mSocket = aSocket;
		mSocketReader = new BufferedReader(new InputStreamReader(
				mSocket.getInputStream()));
		mDispatcher = aServerMsgDispatcher;
	}

	/**
	 * Until interrupted, reads messages from the client socket, forwards them
	 * to the server dispatcher's queue and notifies the server dispatcher.
	 */
	public void run() {
		try {
			while (!isInterrupted()) {
				String msg = mSocketReader.readLine();
				if (msg == null)
					break;
				mDispatcher.dispatchMsg(mSocket, msg);
			}
		} catch (IOException ioex) {
			System.err.println("Error communicating "
					+ "with some of the clients.");
		}
		mDispatcher.deleteClient(mSocket);
	}
}
