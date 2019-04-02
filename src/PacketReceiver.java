/** @author Tyler, Nate, Jose
 * Program to recieve datagram packets and display the order they were received
 * then output the data recieved to a save file
 */

import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;

class PacketReceiver {

	//	HOW TO RUN PROGRAM EXAMPLE:
	//	java PacketReceiver -d 0.5 127.0.0.1 8024


	//	Initialize static variables with default vals...
	static int datagramsToCurrupt = 0;
	static InetAddress receiver_ip_addr;
	static int receiver_port = 8024;

	//	Flags for ACK's
	static final int GOOD = 0;
	static final int CORRUPT = 1;
	static final int MOVEWND = 2;
	static final int DROP = 3;

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

		//	Init dataReciever...
		DatagramSocket dataReciever = new DatagramSocket(receiver_port, receiver_ip_addr);

		//	Initial looping value and size...
		//		int loopCounter = 0;
		int size = 0;

		// Initialize Alice...
		File file = new File("alice29.txt");

		// Initialize Output...
		//		PrintWriter stream = new PrintWriter("../output.txt");
		FileOutputStream stream = new FileOutputStream("output.txt");

		//	Starting output...
		System.out.println("Awaiting data...");

		//	Recieve sizing flags...
		byte[] something = new byte[1];
		DatagramPacket info = new DatagramPacket(something, something.length);
		something = info.getData();
		dataReciever.receive(info);
		int dataSize = something[0];		



		// Initialize data sizes and Datagram Packets for storage
		byte[] currentData = new byte[dataSize];
		DatagramPacket dataRecieved = new DatagramPacket(currentData, currentData.length);

		// Final data size to contain data (data + leftoverData) 
		byte[] writeData = new byte[dataSize-2];

		//-------------------------------------------------------
		//														|
		// 	AREA BELOW IS WHERE STUFF IS DONE					|
		//														|
		//-------------------------------------------------------


		while (true) {


			//	When recieving data...
			dataReciever.receive(dataRecieved);
			currentData = dataRecieved.getData();

			if (currentData[1] == CORRUPT) {
				dataReciever.send(new DatagramPacket(new byte[] {CORRUPT}, 1, new InetSocketAddress("localhost", 8080)));
				System.out.println("[CRPT]: Sequence number: " + currentData[0] + " requesting resend");
				dataReciever.receive(dataRecieved);
				currentData = dataRecieved.getData();
			}

			if (currentData[1] == DROP) {
				dataReciever.send(new DatagramPacket(new byte[] {DROP}, 1, new InetSocketAddress("localhost", 8080)));
				System.out.println("[TO]: Sequence number: " + currentData[0] + " requesting resend");
				dataReciever.receive(dataRecieved);
				currentData = dataRecieved.getData();
			}


			boolean run= true;
			while(run) {
				int rand = new Random().nextInt(101);
				if (rand <= datagramsToCurrupt) {
					if (rand%2 == 0) {
						dataReciever.send(new DatagramPacket(new byte[] {CORRUPT}, 1, new InetSocketAddress("localhost", 8080)));
						dataReciever.receive(dataRecieved);
						currentData = dataRecieved.getData();
					} else {
						dataReciever.send(new DatagramPacket(new byte[] {GOOD}, 1, new InetSocketAddress("localhost", 8080)));
						run = false;
					}
				}

				int startSize = size;
				size += dataRecieved.getData().length -3;
				size++;
				System.out.println("[RECV]: Sequence number: " + currentData[0] +", Offset start: " + startSize + ", Offset end: " + size);
				System.arraycopy(currentData, 2, writeData, 0, dataSize-2 );
				stream.write(writeData);

			}
		}
	}
}