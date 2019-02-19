/** @author Tyler, Nate, Jose
 * Program to recieve datagram packets and display the order they were received
 * then output the data recieved to a save file
 */

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


class PacketReceiver {
  public static void main(String[] args) throws Exception {
    byte[] buffer = new byte[10000];            // to store incoming individual datagram data
    byte[] bufferFinal = new byte[10000*16];	// used to concatenate all datagram data
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    DatagramSocket socket = new DatagramSocket(8024);
    FileWriter file = new FileWriter("output.txt");
    PrintWriter out = new PrintWriter(file);
    int i=0;
    System.out.println("packets received:");
    // loop to receive all datagrams being sent by the sending program
    while (i<16) {
        socket.receive(packet);
        // final packet is a different size so this is used to store the appropriate size of the packet
        if(i==15) {
        	buffer = packet.getData();
            System.out.println("Sequence number: " + buffer[0] +", Offset start: " + buffer[1] + ", Offset end: " + buffer[9998]);
            System.arraycopy(buffer, 0, bufferFinal, 0+(i*9999), 2089);
         // used to store all packets of full size being received from sender
        }else {
        buffer = packet.getData();
        System.out.println("Sequence number: " + buffer[0] +", Offset start: " + buffer[1] + ", Offset end: " + buffer[9998]);
        System.arraycopy(buffer, 0, bufferFinal, 0+(i*9999), 10000);
        }
        i++;
        
        
   }
    //write the data to the save file
    out.println(new String(bufferFinal));
    
    
  }
}