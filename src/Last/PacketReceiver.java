package Last;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

class PacketReceiver {
  public static void main(String[] args) throws Exception {
    byte[] buffer = new byte[10000];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    DatagramSocket socket = new DatagramSocket(8024);
    FileWriter file = new FileWriter("output.txt");
    PrintWriter out = new PrintWriter(file);
    int i=0;
    while(i<17) {
        socket.receive(packet);
        System.out.println(packet.getSocketAddress());
        buffer = packet.getData();
        out.println(new String(buffer));
        System.out.println("Sequence number: " + buffer[0] +", Offset start: " + buffer[1] + ", Offset end: " + buffer[9998]);
        //buffer = null;
        //Arrays.fill(buffer, (byte)0);
        i++;
        
    }
    
    
  }
}