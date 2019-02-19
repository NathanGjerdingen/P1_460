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
		  
  		DatagramSocket socket = new DatagramSocket(4447);

		  
		// Instead of just a string, we want to get a file
	    byte[] buffer = "data".getBytes();
	    
	    File file = new File("alice29.txt");
	    
	    
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    
	    byte[] accumulator = new byte[10000];
	    byte dataGramAccumulator = 1;
	    int j = 0;
	    int k = 0;
	    
    	accumulator[0] = dataGramAccumulator;
	    
	    for (int i = 1; i < fileContent.length; i++) {
	    	accumulator[i] = fileContent[k];
	    	j++;
	    	k++;
	    	
	    	System.out.println("Loop #" + j);
	    		    	
	    	if (j%9999 == 0) {
	    		DatagramPacket packet = new DatagramPacket(accumulator, accumulator.length, new InetSocketAddress("localhost", 4446));
//	    		socket.send(packet);
	    		dataGramAccumulator++;
	    		
	    		System.out.println("Sequence number: " + accumulator[0] +", Offset start: " + accumulator[1] + ", Offset end: " + accumulator[9998]);
	    		i=1;
	    		j=0;
	    	}
		}
	  }
	}