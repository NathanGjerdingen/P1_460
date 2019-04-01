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
	//	java PacketSender -s 100 -t 30000 -d 0.25 127.0.0.1 8024

	//	THINGS NEEDED TO BE DONE:
	//	1. Implement drop/corrupt/discard send rate
	//	2. Implement resend if packet lost
	//	3. Implement variable packet size
	//	4. Get rid of leftover packet bullshit (optional maybe?)


	
	
	//	Initialize static variables with default vals...
	static int datagramSize = 10000;
	static int datagramTimeout = 0; 
	static int datagramsToCurrupt = 0;
	static InetAddress receiver_ip_addr;
	static int receiver_port = 8024;

	private static void setArgs(String[] args) throws UnknownHostException {

		//	Test if we want to set...
		if (args.length > 6  && args[0].endsWith("s") && 
				args[2].endsWith("t") && args[4].endsWith("d")) {

			//	Set static variable from args...
			datagramSize = Integer.parseInt(args[1]);
			datagramTimeout = Integer.parseInt(args[3]); 
			datagramsToCurrupt = (int) (Double.parseDouble(args[5]) * 100);
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
//		byte[] data = new byte[10000];

		//	Initialize file to send...
		File file = new File("alice29.txt");
		byte[] fileData = Files.readAllBytes(file.toPath());

		//	leftoverData is just the last unsized part of the sender...
		byte[] leftoverData = new byte[7284];

		// used to keep track of total datagrams sent 
		byte dataGramAccumulator = 1;
		
		// checksum for corrupt packets
		byte checksum = 0;
		
		int corruptPacketCounter = 0;

		// j is a counter to keep track of location of buffer for each datagram
		int j = 2; 
		int k=0;

		//-------------------------------------------------------
		//														|
		// 	AREA BELOW IS WHERE SHIT IS DONE					|
		//														|
		//-------------------------------------------------------

		for (int i = 1; i < fileData.length; i++,j++) {

			//	Give accumulator what's currently in dataGramAccumulator @ pos i...
			data[0] = dataGramAccumulator;
			if (corruptPacketCounter < datagramsToCurrupt) {
				data[1] = 1;
				corruptPacketCounter++;
			} else {
				data[1] = 1;
			}
			data[j] = fileData[i];

		//	if (j%9999 == 0) {
			if (j%(datagramSize-1) == 0) {
				
				//	Here is send GOOD data
				DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 8024));
				dataSender.send(packet);
				//dataSender.send(new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 8024)));
				//	Figure out how to send DROP data
//				dataSender.send(new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 8024)));
				//	Figure out how to send ERRR data
//				dataSender.send(new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 8024)));

				/*
				System.out.println( "[SENDing]: Sequence number: " + data[0] + ", " +
						"Offset start: " + (0+9998*k) + ", " +
						"Offset end: " + (9997+9998*k));
						*/
				System.out.println("[Sending]: Sequence number: " + data[0] + ", " +
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
				
				if (ackData[0] == 0) {
					System.out.println("[AckRcvd]: " + dataGramAccumulator);
				} else if (ackData[0] == 1) {
					System.out.println("[ErrAck]: " + dataGramAccumulator);
					data[1] = 0;
					packet.setData(data);
					dataSender.send(packet);
					System.out.println( "[SENDing]: Sequence number: " + data[0] + ", " +
							"Offset start: " + (0+9998*k) + ", " +
							"Offset end: " + (9997+9998*k));
					dataSender.receive(ackPacket);
					ackData = ackPacket.getData();
					if (ackData[0] == 0) {
						System.out.println("[AckRcvd]: " + dataGramAccumulator);
					}
					
				} else {
					System.out.println("[MoveWnd]: " + dataGramAccumulator);
				}
			}
			
		}

		// temp is the size of what is remaining in the input file after full size datagrams are sent
		//int temp = fileData.length-(dataGramAccumulator-1)*9998;
		int temp = fileData.length-(dataGramAccumulator-1)*(datagramSize-2);
		
		// send the last datagram that is not of full size
		System.arraycopy(data, 0, leftoverData, 0, temp);
		dataSender.send(new DatagramPacket(leftoverData, temp, new InetSocketAddress("localhost", 8024)));


		// Output for user...
		System.out.println("[SENDing]: Sequence number: " + leftoverData[0] + ", " +
				"Offset start: " + (fileData.length-temp) + ", " +
				"Offset end: " + (fileData.length-1));

		//	Close streams so no data leaks...
		dataSender.close();
	}
}