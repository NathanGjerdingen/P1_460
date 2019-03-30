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
	
//	HOW TO RUN PROGRAM EXAMPLE:
//	java PacketReceiver -d 0.5 20.20.20.20 8024
	
	
//	THINGS NEEDED TO BE DONE:
//	1. Implement drop/currupt/ rate in recieving
//	2. Implement acknowledgment to Sender
	
//	LET'S 'A GO~
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

	//	Initialize static variables with default vals...
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

		// Initialize Buffer sizes
		byte[] data = new byte[10000];
		byte[] leftoverData = new byte[7284];

		// Final buffer size to contain data (data + leftoverData) 
		byte[] finalData = new byte[107273];	

		//	Datagram Packets for storage
		DatagramPacket dataPacket = new DatagramPacket(data, data.length);
		DatagramPacket leftoverDataPacket = new DatagramPacket(leftoverData, leftoverData.length);

		//	Eventually want this syntax below...
		//	DatagramSocket socket = new DatagramSocket(receiver_port, receiver_ip_addr);
		DatagramSocket dataReciever = new DatagramSocket(receiver_port);

		//	Initial looping value and size...
		int loopCounter = 0;
		int size = 0;

		//	Starting output...
		System.out.println("packets received:");
		
		//-------------------------------------------------------
		//														|
		// 	AREA BELOW IS WHERE SHIT IS DONE					|
		//														|
		//-------------------------------------------------------
		
		while (loopCounter < 11) {

			// final clause for leftover info...
			if (loopCounter == 10) {
				
				//	Recieve info...
				dataReciever.receive(leftoverDataPacket);
				leftoverData = leftoverDataPacket.getData();

				//	Set and increment size...
				int startSize = size;
				size += leftoverDataPacket.getData().length -2;

				//	Ouptut info...
				System.out.println("Sequence number: " + leftoverData[0] +", Offset start: " + startSize + ", Offset end: " + size);
				System.arraycopy(leftoverData, 1, finalData, 0+(loopCounter*9999), 7283);
				
			} else {

				//	When recieving data...
				dataReciever.receive(dataPacket);
				data = dataPacket.getData();

				//	Set and increment size...
				int startSize = size;
				size += dataPacket.getData().length -2;
				size++;

				//	Ouptut info...
				System.out.println("Sequence number: " + data[0] +", Offset start: " + startSize + ", Offset end: " + size);
				System.arraycopy(data, 1, finalData, 0+(loopCounter*9999), 9999);
				
			}

			//	Increment iterator no matter what
			loopCounter++;   
			
		}

		//	Write the data...
		FileOutputStream stream = new FileOutputStream("output.txt");
		stream.write(finalData);

		//	Close streams so no data leaks...
		dataReciever.close();
		stream.close();
		
	}

}