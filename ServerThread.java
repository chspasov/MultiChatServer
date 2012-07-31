package MultiChatServer;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


/**
 * ServerDispatcher class is purposed to listen for messages received from the
 * clients and to dispatch them to all the clients connected to the chat server.
 */

class ServerThread extends Thread {
	private ArrayList<Socket> mClients = new ArrayList<Socket>();
	private ArrayList<String> mMsgQueue = new ArrayList<String>();

	// Adds given client to the server's client list.
	public synchronized void addClient(Socket aClientSocket) {
		mClients.add(aClientSocket);
	}

	// Deletes given client from the server's client list if
	// the client is in the list.
	public synchronized void deleteClient(Socket aClientSock) {
		int i = mClients.indexOf(aClientSock);
		if (i != -1) {
			mClients.remove(i);
			try {
				aClientSock.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Adds given message to the dispatcher's message queue and notifies this
	 * thread to wake up the message queue reader (getNextMessageFromQueue
	 * method). dispatchMessage method is called by other threads
	 * (ClientListener) when a message is arrived.
	 */
	public synchronized void dispatchMsg(Socket aSocket, String aMsg) {

		String IP = aSocket.getInetAddress().getHostAddress();
		String port = "" + aSocket.getPort();
		aMsg = IP + ":" + port + " : " + aMsg + "\n\r";
		mMsgQueue.add(aMsg);
		notify();
	}

	/**
	 * @return and deletes the next message from the message queue. If there is
	 *         no messages in the queue, falls in sleep until notified by
	 *         dispatchMessage method.
	 */
	private synchronized String getNextMsgFromQueue()
			throws InterruptedException {
		while (mMsgQueue.size() == 0)
			wait();
		String msg = mMsgQueue.get(0);
		mMsgQueue.remove(0);
		return msg;

	}

	/**
	 * Sends given message to all clients in the client list. Actually the
	 * message is added to the client sender thread's message queue and this
	 * client sender thread is notified to process it.
	 */
	public synchronized void sendMsgToAllClients(String aMsg) {
		for (int i = 0; i < mClients.size(); i++) {
			Socket socket = mClients.get(i);
			try {
				java.io.OutputStream out = socket.getOutputStream();
				out.write(aMsg.getBytes());
				out.flush();
			} catch (IOException ioe) {
				deleteClient(socket);
			}
		}
	}

	/**
	 * Infinitely reads messages from the queue and dispatches them to all
	 * clients connected to the server.
	 */
	public void run() {
		try {
			while (true) {
				String msg = getNextMsgFromQueue();
				sendMsgToAllClients(msg);
			}
		} catch (InterruptedException ie) {
			// Thread interrupted. Do nothing
		}
	}
}