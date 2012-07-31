package MultiChatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ChatServer class is the entry point for the server. It opens a server
 * socket, starts the dispatcher thread and infinitely accepts client
 * connections, creates threads for handling them and starts these threads.
 */

public class ChatServer {
	public static final int LISTENING_PORT = 6666;

	public static void main(String[] args) throws IOException {

		ServerSocket serverSocket = new ServerSocket(LISTENING_PORT);
		System.out.println("Chat server started on port "
				+ serverSocket.getLocalPort());
		ServerThread dispatcher = new ServerThread();
		dispatcher.start();

		while (true) {
			Socket clientSocket = serverSocket.accept();
			ClientListener clientListener = new ClientListener(clientSocket,
					dispatcher);
			dispatcher.addClient(clientSocket);
			clientListener.start();
		}
	}
}