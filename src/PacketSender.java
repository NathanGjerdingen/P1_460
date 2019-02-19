/** @author Tyler, Nate, Jose
 * Program to send datagram packets and display the order they were sent to the
 * receiving program
 * Program Requirements: the program needs the input file alice29.txt to be passed
 * as an argument.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.io.File;
import java.nio.file.Files;

class PacketSender {

	  public static void main(String[] args) throws Exception {
		  
  		DatagramSocket socket = new DatagramSocket(8080);
		File file = new File(args[0]);
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    byte[] accumulator = new byte[10000];
	    // used to keep track of total datagrams sent 
	    byte dataGramAccumulator = 1;
	    // j is a counter to keep track of location of buffer for each datagram
	    int j = 1; 
	    System.out.println("packets sent:");
	    // for loop used to write data from the input file into individual datagrams
	    for (int i = 1; i < fileContent.length; i++) {
	    	accumulator[0] = dataGramAccumulator;
	    	accumulator[j] = fileContent[i];
	    	j++;
	    	    	
	    	if (j%9999 == 0) {
	    		DatagramPacket packet = new DatagramPacket(accumulator, accumulator.length, new InetSocketAddress("localhost", 8024));
	    		socket.send(packet);
	    		
	    		System.out.println("Sequence number: " + accumulator[0] +", Offset start: " + accumulator[1] + ", Offset end: " + accumulator[9998]);
	    		dataGramAccumulator++;
	    		j=1;
	    	}
	    }
	    // temp is the size of what is remaining in the input file after full size datagrams are sent
	    int temp = fileContent.length-(dataGramAccumulator-1)*10000;
	    // send the last datagram that is not of full size
	    DatagramPacket packet = new DatagramPacket(accumulator, temp, new InetSocketAddress("localhost", 8024));
		socket.send(packet);
		System.out.println("Sequence number: " + accumulator[0] +", Offset start: " + accumulator[1] + ", Offset end: " + accumulator[temp-1]);
		// close the socket being used
	    socket.close();
	  }
	}