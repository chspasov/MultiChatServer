package MultiChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Sender extends Thread {
	private PrintWriter mOut;
	private String name;

	public Sender(PrintWriter aOut, String name) {
		mOut = aOut;
		this.name = name;
	}

	/**
	 * Until interrupted reads a text line from the reader and sends it to the
	 * writer.
	 */
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			while (!isInterrupted()) {
				String message = in.readLine();
				message = name.concat("wrote: ").concat(message);
				mOut.println(message);
				mOut.flush();
			}
		} catch (IOException ioe) {
			// Communication is broken
			ioe.printStackTrace();
		}
	}
}
