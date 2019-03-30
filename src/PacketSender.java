/** @author Tyler, Nate, Jose
 * Program to send datagram packets and display the order they were sent to the
 * receiving program
 * Program Requirements: the program needs the input file testsheet.PNG to be passed
 * as an argument.
 */

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;

class PacketSender {
	
	//	HOW TO RUN PROGRAM EXAMPLE:
	//	java PacketSender -s 100 -t 30000 -d 0.25 20.20.20.20 8024

	//	THINGS NEEDED TO BE DONE:
	//	1. Implement all args
	//	2. Implement drop/corrupt/discard rate in dataReceiver
	//	3. Get rid of leftover packet bullshit
	//	4. Implement timeout

	//					  LET'S 'A GO~
	//	
	//	─────────────────────███████──███████
	//	─────────────────████▓▓▓▓▓▓████░░░░░██
	//	───────────────██▓▓▓▓▓▓▓▓▓▓▓▓██░░░░░░██
	//	─────────────██▓▓▓▓▓▓████████████░░░░██
	//	───────────██▓▓▓▓▓▓████████████████░██
	//	───────────██▓▓████░░░░░░░░░░░░██████
	//	─────────████████░░░░░░██░░██░░██▓▓▓▓██
	//	─────────██░░████░░░░░░██░░██░░██▓▓▓▓██
	//	───────██░░░░██████░░░░░░░░░░░░░░██▓▓██
	//	───────██░░░░░░██░░░░██░░░░░░░░░░██▓▓██
	//	─────────██░░░░░░░░░███████░░░░██████
	//	───────────████░░░░░░░███████████▓▓██
	//	─────────────██████░░░░░░░░░░██▓▓▓▓██
	//	───────────██▓▓▓▓██████████████▓▓██
	//	─────────██▓▓▓▓▓▓▓▓████░░░░░░████
	//	───────████▓▓▓▓▓▓▓▓██░░░░░░░░░░██
	//	───────████▓▓▓▓▓▓▓▓██░░░░░░░░░░██
	//	───────██████▓▓▓▓▓▓▓▓██░░░░░░████████
	//	─────────██████▓▓▓▓▓▓████████████████
	//	───────────██████████████████████▓▓▓▓██
	//	─────────██▓▓▓▓████████████████▓▓▓▓▓▓██
	//	───────████▓▓██████████████████▓▓▓▓▓▓██
	//	───────██▓▓▓▓██████████████████▓▓▓▓▓▓██
	//	───────██▓▓▓▓██████████──────██▓▓▓▓████
	//	───────██▓▓▓▓████──────────────██████ 
	//	─────────████
	
	static int datagramsToCurrupt = 0;
	static InetAddress receiver_ip_addr;
	static int receiver_port = 8024;
	
	private static void setArgs(String[] args) throws UnknownHostException {

		//	Test if we want to set...
		if (args.length > 3 && args[0].endsWith("d")) {

			//	Set static variable from args...
			datagramsToCurrupt = (int) (Double.parseDouble(args[1]) * 100);
			receiver_ip_addr = InetAddress.getByName(args[2]);
			receiver_port = Integer.parseInt(args[3]);

		}

	}

	public static void main(String[] args) throws Exception {
		
		//	Set args...
		setArgs(args);

		//	Initialize socket for sending and file to send...
		DatagramSocket dataSender = new DatagramSocket(8080);
				
		// Initialize data sizes and Datagram Packets for storage
		byte[] data = new byte[10000];
		DatagramPacket dataPacket = new DatagramPacket(data, data.length);

		//	Initialize file to send...
		File file = new File("../alice29.txt");
		byte[] fileData = Files.readAllBytes(file.toPath());

		//	accumulator1 is just the last unsized part of the sender...
		byte[] leftoverData = new byte[7284];

		// used to keep track of total datagrams sent 
		byte dataGramAccumulator = 1;

		// j is a counter to keep track of location of buffer for each datagram
		int j = 1; 
		int k=0;

		//-------------------------------------------------------
		//														|
		// 	AREA BELOW IS WHERE SHIT IS DONE					|
		//														|
		//-------------------------------------------------------
		
		for (int i = 1; i < fileData.length; i++,j++) {

			//	Give accumulator what's currently in dataGramAccumulator @ pos i...
			data[0] = dataGramAccumulator;
			data[j] = fileData[i];

			if (j%9999 == 0) {
				dataSender.send(new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 8024)));

				System.out.println( "[SENDing]: Sequence number: " + data[0] + ", " +
									"Offset start: " + (0+9999*k) + ", " +
									"Offset end: " + (9998+9999*k));

				k++;
				dataGramAccumulator++;
				j=1;
				
//				Wait for ACK...
				byte[] ackData = new byte[1];
				DatagramPacket ackPacket = new DatagramPacket(new byte[1], 1);
				dataSender.receive(ackPacket);
				ackData = ackPacket.getData();
				if (ackData[0] == 0) {
					System.out.println("ACK");
				}
			}
		}

		// temp is the size of what is remaining in the input file after full size datagrams are sent
		int temp = fileData.length-(dataGramAccumulator-1)*9999;

		// send the last datagram that is not of full size
		System.arraycopy(data, 0, leftoverData, 0, 7284);
		dataSender.send(new DatagramPacket(leftoverData, temp, new InetSocketAddress("localhost", 8024)));
		

		// Output for user...
		System.out.println("[SENDing]: Sequence number: " + leftoverData[0] + ", " +
							"Offset start: " + (fileData.length-temp) + ", " +
							"Offset end: " + (fileData.length-1));
		
		//	Close streams so no data leaks...
		dataSender.close();
	}
}