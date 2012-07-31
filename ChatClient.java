package MultiChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
	// host
	public static final String SERVER_HOSTNAME = "localhost";
	// the port that we use from the server
	public static final int SERVER_PORT = 6666;

	public static void main(String[] args)

	{

		BufferedReader in = null;
		PrintWriter out = null;
		Socket socket = null;

		try {
			// Connect to Chat Server
			socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			System.out.println("Connected to server " + SERVER_HOSTNAME + ":"
					+ SERVER_PORT);

		} catch (IOException ioe) {
			System.err.println("Can not establish connection to "
					+ SERVER_HOSTNAME + ":" + SERVER_PORT);
			ioe.printStackTrace();
			System.exit(-1);
		}

		// Client name
		String name;
		System.out.print("Enter your name: ");
		Scanner ins = new Scanner(System.in);
		name = ins.nextLine();

		// Create and start Sender thread
		Sender sender = new Sender(out, name);

		/**
		 * In Java, any thread can be a Daemon thread. Daemon threads are like a
		 * service providers for other threads or objects running in the same
		 * process as the daemon thread. Daemon threads are used for background
		 * supporting tasks and are only needed while normal threads are
		 * executing. If normal threads are not running and remaining threads
		 * are daemon threads then the interpreter exits.
		 */
		//sender.setDaemon(true);
		sender.start();
		String address = socket.getLocalAddress().getHostAddress() + ":"
				+ socket.getLocalPort();
		try {
			// Read messages from the server and print them
			String message;
			while ((message = in.readLine()) != null) {
				if (message.length() >= address.length()
						&& !message.substring(0, address.length()).equals(
								address))
					System.out.println(message);
			}
		} catch (IOException ioe) {
			System.err.println("Connection to server broken.");
			ioe.printStackTrace();
		}
	}
}
