package FinalPKG;

import java.io.*;
import java.net.*;

/**
 * Server class
 * 
 * 
 * @author Nate, Jose, Tyler
 *
 *         Source:
 *         https://github.com/Michaelcj10/Java-UDP-File-Transfer-Made-Reliable/tree/master/src
 */
public class Server {

	/**
	 * Main method. Will call the appropriate method to set up the port.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {

		final int port = 8080;
		System.out.println("Ready!");
		setUp(port);
	}

	/**
	 * setUp method accepts an integer to setUp the port. Will decode any symbols or
	 * characters.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public static void setUp(int port) throws IOException {

		DatagramSocket socket = new DatagramSocket(port);

		byte[] receiveFileNameChoice = new byte[1024];
		DatagramPacket receiveFileNameChoicePacket = new DatagramPacket(receiveFileNameChoice,
				receiveFileNameChoice.length);
		socket.receive(receiveFileNameChoicePacket);

	}

}