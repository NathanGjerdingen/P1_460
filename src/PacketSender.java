/** @author Tyler, Nate, Jose
 * Program to send datagram packets and display the order they were sent to the
 * receiving program
 * Program Requirements: the program needs the input file testsheet.PNG to be passed
 * as an argument.
 */

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.file.Files;

class PacketSender {

	  public static void main(String[] args) throws Exception {
		  
		//	Initialize socket for sending and file to send...
  		DatagramSocket socket = new DatagramSocket(8080);
  		
  		//	Initialize file to send...
		File file = new File("../alice29.txt");
	    byte[] fileContent = Files.readAllBytes(file.toPath());

		//	Initialize content buffers...
	    byte[] accumulator = new byte[10000];
	    
	    //	accumulator1 is just the last unsized part of the sender...
	    byte[] accumulator1 = new byte[7284];
	    
	    // used to keep track of total datagrams sent 
	    byte dataGramAccumulator = 1;
	    
	    // j is a counter to keep track of location of buffer for each datagram
	    int j = 1; 
	    int k=0;
	    
	    //	Output for user...
	    System.out.println("packets sent:");
	    
	    // for loop used to write data from the input file into individual datagrams
	    for (int i = 1; i < fileContent.length; i++,j++) {
	    	
	    	//	Give accumulator what's currently in dataGramAccumulator @ pos i...
	    	accumulator[0] = dataGramAccumulator;
	    	accumulator[j] = fileContent[i];
	    	    	
	    	if (j%9999 == 0) {
	    		DatagramPacket packet = new DatagramPacket(accumulator, accumulator.length, new InetSocketAddress("localhost", 8024));
	    		socket.send(packet);
	    		System.out.println("Sequence number: " + accumulator[0] +", Offset start: " + (0+9999*k) + ", Offset end: " + (9998+9999*k));
	    		k++;
	    		dataGramAccumulator++;
	    		j=1;
	    	}
	    }
	    
	    // temp is the size of what is remaining in the input file after full size datagrams are sent
	    int temp = fileContent.length-(dataGramAccumulator-1)*9999;
	    
	    // send the last datagram that is not of full size
	    System.arraycopy(accumulator, 0, accumulator1, 0, 7284);
	    DatagramPacket packet = new DatagramPacket(accumulator1, temp, new InetSocketAddress("localhost", 8024));
		socket.send(packet);
		
		// Output for user...
		System.out.println("Sequence number: " + accumulator1[0] +", Offset start: " + (fileContent.length-temp) + ", Offset end: " + (fileContent.length-1));
		
	    //	Close streams so no data leaks...
	    socket.close();
	  }
	}