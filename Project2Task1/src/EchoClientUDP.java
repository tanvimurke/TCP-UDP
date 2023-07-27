/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EchoClientUDP {
    public static void main(String args[]) {
        // args give message contents and server hostname
        //code from Coulouris text
        //prints start of code
        System.out.println("The UDP client is running.");
        //initialize datagram socket
        DatagramSocket aSocket = null;
        //initialize datagram socket
        String halt = "halt!";
        try {
            //creates InetAddress object
            InetAddress aHost = InetAddress.getByName("localhost");
            //get port from user
            System.out.println("Enter server port number (e.g. 6789 or 6798 for eavesdropper)");
            Scanner sc = new Scanner(System.in);
            //binds socket to that port
            int serverPort = sc.nextInt();
            aSocket = new DatagramSocket();
            //reads input from console
            String nextLine;
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            while ((nextLine = typed.readLine()) != null) {
                //creates byte array from inout
                byte[] m = nextLine.getBytes();
                //creates datagram object for request
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
                //send request using socket
                aSocket.send(request);
                //creates buffer bytearray
                byte[] buffer = new byte[1000];
                //creates datagram object for reply
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                //send the reply using socket
                aSocket.receive(reply);
                //get data in a string
                String replyString = new String(reply.getData(), 0, reply.getLength());
                //if string is halt then quit client
                if (replyString.equals(halt)) {
                    System.out.println("UDP Client side quitting");
                    break;
                }
                //prints reply
                System.out.println("Reply from server: " + new String(replyString));
            }
            //closes the socket
        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }
}

