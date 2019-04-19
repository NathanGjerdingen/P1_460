
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.Base64;

/**
 * Program will receive data packets from a Sender. It will deteermine if the
 * packets are corrupt, dropped, or good and send an ack to the Sender. It will
 * then write the packets received to an outfile.
 * 
 * @author Tyler, Nate, Jose
 *
 */
class PacketReceiver {

	// HOW TO RUN PROGRAM EXAMPLE:
	// java PacketReceiver -d 0.2 127.0.0.1 8024

	// Initialize static variables with default vals...
	static int datagramsToCorrupt = 0;
	static InetAddress receiver_ip_addr;
	static int receiver_port = 8024;

	// Flags for ACK's
	static final int GOOD = 0;
	static final int CORRUPT = 1;
	static final int MOVEWND = 2;
	static final int DROP = 3;
	static final int COMPLETE = 4;

	/**
	 * This method takes 3 arguments. 1st will be the percentage of datagrams to
	 * corrupt. 2nd is the IP address. 3rd is the Port. It will assign them to the
	 * correct variables.
	 * 
	 * @param args
	 * @throws UnknownHostException
	 */
	private static void setArgs(String[] args) throws UnknownHostException {

		// Test if we want to set...
		if (args.length > 3 && args[0].endsWith("d")) {

			// Set static variable from args...
			datagramsToCorrupt = (int) (Double.parseDouble(args[1]) * 100);
			receiver_ip_addr = InetAddress.getByName(args[2]);
			receiver_port = Integer.parseInt(args[3]);
		}
	}

	/**
	 * Main method where all the work is done. Starts by call the setArgs method.
	 * Then will start the Receiver for the packets that will be recieved. It will
	 * then receive a first packet with the information of the size of datagrams to
	 * expect. It will use the size to create a byte array to store the packets
	 * being received. It will then loop with every packet and determine if its
	 * corrupt, dropped or good and send the appropriate ack to the Sender. Loop
	 * will end when Sender indicates that it is done.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Set args...
		setArgs(args);

		// Init dataReciever...
		DatagramSocket dataReciever = new DatagramSocket(receiver_port, receiver_ip_addr);

		// Initial looping value and size...
		int size = 0;

		// Initialize Alice...
		File file = new File("alice29.txt");

		// Initialize Output...
		// PrintWriter stream = new PrintWriter("../output.txt");
		FileOutputStream stream = new FileOutputStream("output.txt");

		// Starting output...
		//System.out.println("Action:\t" + "Seq#:\t" + "Offset:\t\t" + "Time:\t\t\t" + "Result:");

		// Recieve sizing flags...
		byte[] something = new byte[1];
		DatagramPacket info = new DatagramPacket(something, something.length);
		long timestamp = 0;
		something = info.getData();
		dataReciever.receive(info);
		int dataSize = something[0];
		int loopAmount = ((int) file.length() / dataSize);

		// Initialize data sizes and Datagram Packets for storage
		byte[] currentData = new byte[dataSize];
		DatagramPacket dataRecieved = new DatagramPacket(currentData, currentData.length);

		// Final data size to contain data (data + leftoverData)
		byte[] finalData = new byte[(int) file.length()];
		byte[] writeData = new byte[dataSize - 2];

		// -------------------------------------------------------
		// |
		// AREA BELOW IS WHERE STUFF IS DONE |
		// |
		// -------------------------------------------------------

		// Allowing the loop to run until packet to break is received.
		boolean openToRecieve = true;
		while (openToRecieve) {

			// When recieving data...
			dataReciever.receive(dataRecieved);
			currentData = dataRecieved.getData();

			// Used to break from the while when packet contains flag to complete.
			if (currentData[1] == COMPLETE) {
				break;
			}
			
			timestamp = System.currentTimeMillis();

			// If the packet contains a corrupt flag then send appropriate ack to the
			// Sender so that it will try again. Display a message indicating that we got
			// a corrupt packet.
			if (currentData[1] == CORRUPT) {
				dataReciever.send(new DatagramPacket(new byte[] { CORRUPT }, 1, new InetSocketAddress("localhost", 8080)));
			//	System.out.println("[CRPT]: #" + currentData[0] + ", requesting resend...");
//				timestamp = System.currentTimeMillis();
			//	System.out.println("ACKSent: " + currentData[0] + " \t" + timestamp + "\tERR");
				System.out.println("RECV:  \t " + currentData[0] + " \t" + timestamp + "\tERR");
				
				dataReciever.receive(dataRecieved);
				currentData = dataRecieved.getData();
			}

			// If the packet has a dropped flag set then we send the appropriate ack to the
			// Sender so it will try again. Display a message indicating that a packet was
			// dropped.
			if (currentData[1] == DROP) {
				
			//	System.out.println("[DROP]: #" + currentData[0] + ", requesting resend...");
				dataReciever.send(new DatagramPacket(new byte[] { DROP }, 1, new InetSocketAddress("localhost", 8080)));
			//	System.out.println("ACKSent: " + currentData[0] + " \t" + timestamp + "\tDROP");
				dataReciever.receive(dataRecieved);
				currentData = dataRecieved.getData();
				timestamp = System.currentTimeMillis();
			}			

			// Randomizing corrupt packets. While will only run if corrupt packets are being
			// sent. Once a good packet is received then it will break from the while.
			boolean run = true;
			while (run) {
				int rand = new Random().nextInt(100);
				if (rand <= datagramsToCorrupt) {
					if (rand % 2 == 0) {
						dataReciever.send(new DatagramPacket(new byte[] { CORRUPT }, 1, new InetSocketAddress("localhost", 8080)));
						dataReciever.receive(dataRecieved);
						currentData = dataRecieved.getData();
						timestamp = System.currentTimeMillis();
						System.out.println("RECV:  \t " + currentData[0] + " \t" + timestamp + "\tRECV");
						TimeUnit.MILLISECONDS.sleep(2);
						timestamp = System.currentTimeMillis();
						System.out.println("ACKSent: " + currentData[0] + " \t" + timestamp + "\tERR");
					}
				} else {
					dataReciever.send(new DatagramPacket(new byte[] { GOOD }, 1, new InetSocketAddress("localhost", 8080)));
					run = false;
				}
			}
			// Displaying the packet information. It includes sequence number, offset start,
			// offset send.
			int startSize = size;
			size += dataRecieved.getData().length - 3;
			timestamp = System.currentTimeMillis();
			//System.out.println("RECV" + currentData[0] + "\t" + startSize + ":" + size + "\t\t" + timestamp + "\t\tRECV");
			System.out.println("RECV:  \t " + currentData[0] + " \t" + timestamp + "\tRECV");
			TimeUnit.MILLISECONDS.sleep(2);
			timestamp = System.currentTimeMillis();
			System.out.println("ACKSent: " + currentData[0] + " \t" + timestamp + "\tSENT");
			size++;
			// Getting a copy of the data that was received and writing it to the output
			// file.
			
			System.arraycopy(currentData, 2, writeData, 0, dataSize - 2);
			stream.write(writeData);
		}

		// Closing the receiver and stream.
		dataReciever.close();
		stream.close();
	}
}