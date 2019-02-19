package Last;

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

		  
		File file = new File("alice29.txt");
	    
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    
	    byte[] accumulator = new byte[10000];
	    byte dataGramAccumulator = 1;
	    int j = 1;
	    int k = 0;
	    
//	    System.out.println(fileContent.length);
	    
	    
	    for (int i = 1; i < fileContent.length; i++) {
	    	accumulator[0] = dataGramAccumulator;
	    	accumulator[j] = fileContent[i];
	    	j++;
	    	k++;
	    	
//	    	System.out.println("Loop #" + j);
	    		    	
	    	if (j%9999 == 0) {
	    		DatagramPacket packet = new DatagramPacket(accumulator, accumulator.length, new InetSocketAddress("localhost", 8024));
	    		socket.send(packet);
	    		
	    		System.out.println("Sequence number: " + accumulator[0] +", Offset start: " + accumulator[1] + ", Offset end: " + accumulator[9998]);
	    		dataGramAccumulator++;
	    		j=1;
	    	}
	    }
	    int temp = fileContent.length-(dataGramAccumulator-1)*10000;
	    DatagramPacket packet = new DatagramPacket(accumulator, temp, new InetSocketAddress("localhost", 8024));
		socket.send(packet);
		
		System.out.println("Sequence number: " + accumulator[0] +", Offset start: " + accumulator[1] + ", Offset end: " + accumulator[temp-1]);
	    
	    socket.close();
	  }
	}