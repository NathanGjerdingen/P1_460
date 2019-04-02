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
import java.util.Random;

class PacketSender {

	//	HOW TO RUN PROGRAM EXAMPLE:
	//	java PacketSender -s 100 -t 30000 -d 0.25 127.0.0.1 8024


	//	Initialize static variables with default vals...
	static int datagramSize = 10000;
	static int datagramTimeout = 0; 
	static int datagramsToCurrupt = 0;
	static InetAddress receiver_ip_addr;
	static int receiver_port = 8024;

	static final int GOOD = 0;
	static final int CORRUPT = 1;
	static final int MOVEWND = 2;
	static final int DROP = 3;



	private static void setArgs(String[] args) throws UnknownHostException {

		//	Test if we want to set...
		if (args.length > 6  && args[0].endsWith("s") && 
				args[2].endsWith("t") && args[4].endsWith("d")) {

			//	Set static variable from args...
			datagramSize = Integer.parseInt(args[1]);
			datagramTimeout = Integer.parseInt(args[3]); 
			datagramsToCurrupt = (int) (Double.parseDouble(args[1]) * 100);
			receiver_ip_addr = InetAddress.getByName(args[6]);
			receiver_port = Integer.parseInt(args[7]);
		}
	}

	public static void main(String[] args) throws Exception {

		//	Set args...
		setArgs(args);

		//	Initialize socket for sending and file to send...
		DatagramSocket dataSender = new DatagramSocket(8080);
		dataSender.setSoTimeout(datagramTimeout);

		// Initialize data sizes and Datagram Packets for storage
		byte[] data = new byte[datagramSize];

		//	Initialize file to send...
		File file = new File("alice29.txt");
		byte[] fileData = Files.readAllBytes(file.toPath());

		// used to keep track of total datagrams sent 
		byte dataGramAccumulator = 1;

		int j = 2; 
		int k=0;

		//-------------------------------------------------------
		//														|
		// 	AREA BELOW IS WHERE STUFF IS DONE					|
		//														|
		//-------------------------------------------------------

		// Send Receiver info. 
		dataSender.send(new DatagramPacket(new byte[] {(byte) datagramSize}, 1, new InetSocketAddress("localhost", 8024)));

		System.out.println(datagramsToCurrupt);
		for (int i = 1; i < fileData.length; i++,j++) {

			//	Give accumulator what's currently in dataGramAccumulator @ pos i...
			data[0] = dataGramAccumulator;
			data[j] = fileData[i];


			DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 8024));

			if (j%(datagramSize-1) == 0) {

				int rand = new Random().nextInt(101);

				if (rand <= datagramsToCurrupt) {
					if (rand%2 == 0) {
						data[1] = CORRUPT;
						packet.setData(data);
						dataSender.send(packet);
					} else {
						data[1] = DROP;
						packet.setData(data);
						dataSender.send(packet);
					}
				} else {
					data[1] = GOOD;
					packet.setData(data);
					dataSender.send(packet);
				}

				System.out.println( "[SENDing]: Sequence number: " + data[0] + ", " +
						"Offset start: " + (0+(datagramSize-2)*k) + ", " +
						"Offset end: " + ((datagramSize-3)+(datagramSize-2)*k));

				// Increment ALL the things
				k++;
				dataGramAccumulator++;
				j=2;

				//-------------------------------------------------------
				//														|
				//  ACK POINT											|
				//														|
				//-------------------------------------------------------

				byte[] ackData = new byte[1];
				DatagramPacket ackPacket = new DatagramPacket(new byte[1], 1);
				dataSender.receive(ackPacket);
				ackData = ackPacket.getData();
				
				if (ackData[0] ==0) {
					System.out.println("[AckRcvd]: " + (dataGramAccumulator-1) + " [MoveWnd]");
				} else {
					while(ackData[0] !=0) {
						if (ackData[0] == CORRUPT) {
							System.out.println("[ErrAck]: " + (dataGramAccumulator-1));
							data[1] = 0;
							packet.setData(data);
							dataSender.send(packet);
							System.out.println( "[RESENDing]: Sequence number: " + data[0] + ", " +
									"Offset start: " + (0+(datagramSize-2)*k) + ", " +
									"Offset end: " + ((datagramSize-3)+(datagramSize-2)*k));
							dataSender.receive(ackPacket);
							ackData = ackPacket.getData();
							if (ackData[0] == 0) {
								System.out.println("[AckRcvd]: " + (dataGramAccumulator-1) + " [MoveWnd]");
							} 
						} else if (ackData[0] == DROP) {
							System.out.println("[ErrAck]: " + (dataGramAccumulator-1));
							data[1] = 0;
							packet.setData(data);
							dataSender.send(packet);
							System.out.println( "[RESENDing]: Sequence number: " + data[0] + ", " +
									"Offset start: " + (0+(datagramSize-2)*k) + ", " +
									"Offset end: " + ((datagramSize-3)+(datagramSize-2)*k));
							dataSender.receive(ackPacket);
							ackData = ackPacket.getData();
							if (ackData[0] == 0) {
								System.out.println("[AckRcvd]: " + (dataGramAccumulator-1) + " [MoveWnd]");
							}
						}
					}
				}
			}

			if (i == file.length()-1) {
				dataSender.send(packet);
			}

		}

		dataSender.close();
	}
}