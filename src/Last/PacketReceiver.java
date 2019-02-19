package Last;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class PacketReceiver {
  public static void main(String[] args) throws Exception {
    byte[] buffer = new byte[10000];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    DatagramSocket socket = new DatagramSocket(4446);
    
    while(true) {
        socket.receive(packet);
        System.out.println(packet.getSocketAddress());
        buffer = packet.getData();
        System.out.println(new String(buffer));
    }
  }
}