
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.sun.jmx.snmp.Timestamp;

/**
 * Program will read a file and put the data in a byte array. Data will then be
 * sent to the receiver according the specified datagram size. It will then wait
 * for a response for a successful ack, corrupt message or dropped message from
 * the Receiver before sending any more packets. Once complete we will notify
 * the Receiver that no packets will be sent.
 * 
 * @author Tyler, Nate, Jose
 *
 */
class PacketSender {

	// HOW TO RUN PROGRAM EXAMPLE:
	// java PacketSender -s 100 -t 30000 -d 0.1 127.0.0.1 8024

	// Initialize static variables with default vals...
	static int datagramSize = 10000;
	static int datagramTimeout = 0;
	static int datagramsToCorrupt = 0;
	static InetAddress receiver_ip_addr;
	static int receiver_port = 8024;

	static final int GOOD = 0;
	static final int CORRUPT = 1;
	static final int MOVEWND = 2;
	static final int DROP = 3;
	static final int COMPLETE = 4;
	
	static boolean flag = true;

	/**
	 * This method will take 5 arguments from command line. 1st arg is datagram
	 * size, 2nd will be timeout interval, 3rd will be percentage of datagrams to
	 * corrupt, 4th is the IP address and 5th is the Port. It will assign them to
	 * the correct variables.
	 * 
	 * @param args
	 * @throws UnknownHostException
	 */
	private static void setArgs(String[] args) throws UnknownHostException {

		// Test if we want to set...
		if (args.length > 6 && args[0].endsWith("s") && args[2].endsWith("t") && args[4].endsWith("d")) {

			// Set static variable from args...
			datagramSize = Integer.parseInt(args[1]);
			datagramTimeout = Integer.parseInt(args[3]);
			datagramsToCorrupt = (int) (Double.parseDouble(args[5]) * 100);
			receiver_ip_addr = InetAddress.getByName(args[6]);
			receiver_port = Integer.parseInt(args[7]);
		}
	}

	/**
	 * Main method is where all the work is done. Begins with calling the setArgs
	 * method. Sets timeout interval. Creates byte array with datagram size given.
	 * Opens file that will be sent and reads all the bytes. It will send the
	 * receiver the size of the datagrams it should expect.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Set args...
		setArgs(args);

		// Initialize socket for sending and file to send...
		DatagramSocket dataSender = new DatagramSocket(8080);
		dataSender.setSoTimeout(datagramTimeout);

		// Initialize data sizes and Datagram Packets for storage
		byte[] data = new byte[datagramSize];

		// Initialize file to send...
		File file = new File("alice29.txt");
		byte[] fileData = Files.readAllBytes(file.toPath());
		byte dataGramAccumulator = 1;

		// j is a counter to keep track of location of buffer for each datagram
		int j = 2;
		int k = 0;

		// -------------------------------------------------------
		// |
		// AREA BELOW IS WHERE STUFF IS DONE |
		// |
		// -------------------------------------------------------
		
		//System.out.println("Action:\t" + "Seq#:\t" + "Offset:\t\t" + "Time:\t\t\t" + "Result:");

		// Send Receiver info.
		dataSender.send(new DatagramPacket(new byte[] { (byte) datagramSize }, 1, new InetSocketAddress("localhost", 8024)));

		for (int i = 1; i < fileData.length; i++, j++) {

			// Give accumulator what's currently in dataGramAccumulator @ pos i...
			data[0] = dataGramAccumulator;
			data[j] = fileData[i];

			// Creating a packet to send to the Receiver.
			DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 8024));

			long timestamp = 0;
			
			// Checks if location of buffer has reached the size limit.
			if (j % (datagramSize - 1) == 0) {

				// Randomizing if the packet will corrupt or if it will be dropped or if its a
				// good packet.
				int rand = new Random().nextInt(100);

				if (rand <= datagramsToCorrupt) {
					if (rand % 2 == 0) {
						data[1] = CORRUPT;
						packet.setData(data);
						timestamp = System.currentTimeMillis();
						System.out.println("SENDing: " + data[0] + "  " + (0 + (datagramSize - 2) * k) + 
								":" + ((datagramSize - 3) + (datagramSize - 2) * k) + "  " + timestamp + "  ERR");
						System.out.println("Timeout: " + data[0]);
						TimeUnit.MILLISECONDS.sleep(datagramTimeout);
						
						timestamp = System.currentTimeMillis();
						System.out.println("RESEND:  " + data[0] + "  " + (0 + (datagramSize - 2) * k) + 
								":" + ((datagramSize - 3) + (datagramSize - 2) * k) + "  " + timestamp + "  SENT");
						dataSender.send(packet);
					} else {
						data[1] = DROP;
						packet.setData(data);
						timestamp = System.currentTimeMillis();
						System.out.println("SENDing: " + data[0] + "  " + (0 + (datagramSize - 2) * k) + 
								":" + ((datagramSize - 3) + (datagramSize - 2) * k) + "  " + timestamp + "  DROP");
						System.out.println("Timeout: " + data[0]);
						TimeUnit.MILLISECONDS.sleep(datagramTimeout);
						
						timestamp = System.currentTimeMillis();
						System.out.println("RESEND:  " + data[0] + "  " + (0 + (datagramSize - 2) * k) + 
								":" + ((datagramSize - 3) + (datagramSize - 2) * k) + "  " + timestamp + "  SENT");
						dataSender.send(packet);
					}
				} else {
					data[1] = GOOD;
					packet.setData(data);
					timestamp = System.currentTimeMillis();
					dataSender.send(packet);
					System.out.println("SENDing: " + data[0] + "  " + (0 + (datagramSize - 2) * k) + 
							":" + ((datagramSize - 3) + (datagramSize - 2) * k) + "  " + timestamp + "  SENT");

				}

				// Displaying the information of the packets sent.
//				System.out.print("SENDing: " + data[0] + "  " + (0 + (datagramSize - 2) * k) + 
//						":" + ((datagramSize - 3) + (datagramSize - 2) * k) + "  " + timestamp);

				// Increment ALL the things
				k++;
				dataGramAccumulator++;
				j = 2;

				// -------------------------------------------------------
				// |
				// ACK POINT |
				// |
				// -------------------------------------------------------

				// Creating an ack packet of 1 byte
				byte[] ackData = new byte[1];
				DatagramPacket ackPacket = new DatagramPacket(new byte[1], 1);

				// Receiving an ack packet and reading whether its a good, corrupt or dropped.
				dataSender.receive(ackPacket);
				ackData = ackPacket.getData();

				// If its a good packet, displays that successful ack was received.
				if (ackData[0] == 0) {
//					System.out.println("  SENT");
					System.out.println("AckRcvd: " + data[0] + "  Movewnd");
				}

				// While we're not getting a successful ack, we will sit in this loop then we
				// check if its either getting corrupt or dropped.
				// We will display that we got an error packet and attempt sending the packet.
				// Once we finally get a successful packet we will display the success and move
				// on.
				while (ackData[0] != 0) {
					
					// Handle if we get a corrupt packet error.
					if (ackData[0] == CORRUPT) {
//						System.out.println("  ERR");
						data[1] = 0;
						packet.setData(data);
						dataSender.send(packet);
//						TimeUnit.MILLISECONDS.sleep(datagramTimeout);
						timestamp = System.currentTimeMillis();
						System.out.println("AckRcvd  " + data[0] + "  ErrAck.");
						System.out.println("RESEND:\t " + data[0] + "  " + (0 + (datagramSize - 2) * (k-1)) + 
								":" + ((datagramSize - 3) + (datagramSize - 2) * (k-1)) + "  " + timestamp + "  SENT");
						dataSender.receive(ackPacket);
						ackData = ackPacket.getData();
						// If we get a successful ack display messge
						if (ackData[0] == 0) {
							System.out.println("AckRcvd: " + data[0] + "  Movewnd");
						}
						// Handle if we get dropped packet error
					} else if (ackData[0] == DROP) {
//						System.out.println("  DROP");
						data[1] = 0;
						packet.setData(data);
						dataSender.send(packet);
//						TimeUnit.MILLISECONDS.sleep(datagramTimeout);
						timestamp = System.currentTimeMillis();
						//System.out.println("AckRcvd " + data[0] + " Dupl");
//						System.out.println("RESEND:\t " + data[0] + "  " + (0 + (datagramSize - 2) * (k-1)) + 
//								":" + ((datagramSize - 3) + (datagramSize - 2) * (k-1)) + "  " + timestamp + "  SENT");
						dataSender.receive(ackPacket);
						ackData = ackPacket.getData();
						// If we get a successful ack display messge
						if (ackData[0] == 0) {
							System.out.println("AckRcvd: " + data[0] + "  Movewnd");
						}
					}
				}
			}
			// If we're at the end of the file we will send the last of the data.
			if (i == file.length() - 1) {
				dataSender.send(packet);
			}
		}
		// Indicating that we are done sending packets.
		data[1] = COMPLETE;
		DatagramPacket closePacket = new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 8024));
		dataSender.send(closePacket);

		// Closing the sender.
		dataSender.close();
	}
}