/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class AddingClientUDP {
    public static void main(String args[]) {
        // args give message contents and server hostname
        //start the code
        System.out.println("The UDP client is running.");
        //get user from user
        System.out.println("Enter server port number (e.g. 6789)");
        Scanner sc = new Scanner(System.in);
        int serverPort = sc.nextInt();
        //bind port to the socket
        int sum = add(serverPort);
        //server responds sum
        System.out.println("The server returned " + sum + ".");

    }

    //add method
    public static int add(int serverPort) {
        //initializes datagram
        DatagramSocket aSocket = null;
        //halt string
        String halt = "halt!";
        //initialize sum
        int sum = 0;
        try {
            //creates InetAddress object
            InetAddress aHost = InetAddress.getByName("localhost");
            Scanner sc = new Scanner(System.in);
            //creates socket
            aSocket = new DatagramSocket();
            while (true) {
                //gets value from console
                String value = sc.nextLine();
                //if halt then quit
                if (value.equals(halt)) {
                    System.out.println("UDP Client side quitting");
                    break;
                }
                //get bytearray
                byte[] m = value.getBytes();
                //create datagram object
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
                //send request using socket
                aSocket.send(request);
                //create bytearray buffer
                byte[] buffer = new byte[1000];
                //create datagram object reply
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                //receive reply using socket
                aSocket.receive(reply);
                // convert byte array to String and then parse to integer value
                String responseString = new String(reply.getData()).trim();
                sum = Integer.parseInt(responseString);
                //print sum
                System.out.println("The server returned " + sum + ".");
            }
            //closes socket
        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
        //returns sum
        return sum;
    }
}

