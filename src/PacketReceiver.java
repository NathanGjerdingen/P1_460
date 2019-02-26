/** @author Tyler, Nate, Jose
 * Program to recieve datagram packets and display the order they were received
 * then output the data recieved to a save file
 */

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


class PacketReceiver {
  public static void main(String[] args) throws Exception {
    byte[] buffer = new byte[10000];            // to store incoming individual datagram data
    byte[] buffer1 = new byte[7284];
    byte[] bufferFinal = new byte[107273];	// used to concatenate all datagram data
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    DatagramPacket packet1 = new DatagramPacket(buffer1, buffer1.length);
    DatagramSocket socket = new DatagramSocket(8024);
    int i=0;
    int size=0;
    System.out.println("packets received:");
    // loop to receive all datagrams being sent by the sending program
    while (i<11) {
       
        // final packet is a different size so this is used to store the appropriate size of the packet
        if(i==10) {
        	socket.receive(packet1);
        	buffer1 = packet1.getData();
        	int startSize = size;
            size += packet1.getData().length -2;
            System.out.println("Sequence number: " + buffer1[0] +", Offset start: " + startSize + ", Offset end: " + size);
            System.arraycopy(buffer1, 1, bufferFinal, 0+(i*9999), 7283);
         // used to store all packets of full size being received from sender
        }else {
        	socket.receive(packet);
        	buffer = packet.getData();
        	int startSize = size;
        	size += packet.getData().length -2;
        	System.out.println("Sequence number: " + buffer[0] +", Offset start: " + startSize + ", Offset end: " + size);
        	size++;
        	System.arraycopy(buffer, 1, bufferFinal, 0+(i*9999), 9999);
        }
        i++;   
   }
    //write the data to the save file
    FileOutputStream stream = new FileOutputStream("output.PNG");
    stream.write(bufferFinal);
    stream.close();
    
    
    
  }
}