/** @author Tyler, Nate, Jose
 * Program to recieve datagram packets and display the order they were received
 * then output the data recieved to a save file
 */

import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;


class PacketReceiver {

	//	Initialize static variables with default vals...
	static double datagramsToCurrupt = 0;
	static InetAddress receiver_ip_addr;
	static int receiver_port = 8024;

	private static void getArgs(String[] args) throws UnknownHostException {
		datagramsToCurrupt = Double.parseDouble(args[0]);
		receiver_ip_addr = InetAddress.getByName(args[1]);
		receiver_port = Integer.parseInt(args[2]);
	}


	public static void main(String[] args) throws Exception {

		getArgs(args);

		// Initialize Buffer sizes
		//	WHERE DO WE GET THESE SIZES?!?! Make then calculate-able. 
		byte[] buffer = new byte[10000];
		byte[] buffer1 = new byte[7284];

		// Final buffer size to contain data (buffer + buffer1) 
		byte[] bufferFinal = new byte[107273];	

		//	Datagram Packets for storage
		//	HOW ARE WE INITIALIZING THESE TO MAKE SENSE?
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		DatagramPacket packet1 = new DatagramPacket(buffer1, buffer1.length);

		//	Something...? Maybe starting a socket on this port?
		//	Eventually want this syntax below...
//		DatagramSocket socket = new DatagramSocket(receiver_port, receiver_ip_addr);
		DatagramSocket socket = new DatagramSocket(receiver_port);

		//	Initial looping value and size...
		int i=0;
		int size=0;

		//	Starting output...
		System.out.println("packets received:");

		// Loop for when datapackets are recieved...
		while (i<11) {

			// final clause...
			if (i==10) {
				//	Recieve info...
				socket.receive(packet1);
				buffer1 = packet1.getData();

				//	Set and increment size...
				int startSize = size;
				size += packet1.getData().length -2;

				//	Ouptut info...
				System.out.println("Sequence number: " + buffer1[0] +", Offset start: " + startSize + ", Offset end: " + size);
				System.arraycopy(buffer1, 1, bufferFinal, 0+(i*9999), 7283);
			} else {

				//	Recieve info...
				socket.receive(packet);
				buffer = packet.getData();

				//	Set and increment size...
				int startSize = size;
				size += packet.getData().length -2;
				size++;

				//	Ouptut info...
				System.out.println("Sequence number: " + buffer[0] +", Offset start: " + startSize + ", Offset end: " + size);
				System.arraycopy(buffer, 1, bufferFinal, 0+(i*9999), 9999);
			}

			//	Increment iterator no matter what
			i++;   
		}

		//	Write the data...
		FileOutputStream stream = new FileOutputStream("output.txt");
		stream.write(bufferFinal);

		//	Close streams so no data leaks...
		socket.close();
		stream.close();
	}



}