/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class EavesdropperUDP {
    public static void main(String[] args) {
        //initialize socket
        DatagramSocket clientSocket = null;
        DatagramSocket serverSocket = null;
        //declares bytearray buffer
        byte[] buffer = new byte[1000];

        try {
            //start of the code
            System.out.println("The Eavesdropper is running.");
            //get listening port from user
            System.out.println("Enter the listening port (e.g. 6798)");
            Scanner sc = new Scanner(System.in);
            int listenPort = sc.nextInt();
            //get server port from user
            System.out.println("Enter the server port (e.g. 6789)");
            Scanner sc1 = new Scanner(System.in);
            int serverPort = sc1.nextInt();
            //creates InetAddress object
            InetAddress aHost = InetAddress.getByName("localhost");
            //creates datagram socket
            serverSocket = new DatagramSocket();
            clientSocket = new DatagramSocket(listenPort);

            while (true) {
                //creates datagram object
                DatagramPacket clientRequest = new DatagramPacket(buffer, buffer.length);
                //receives request
                clientSocket.receive(clientRequest);
                //creates bytearray of request
                byte[] clientRequestByteArray = new byte[clientRequest.getLength()];
                //copies the array
                System.arraycopy(clientRequest.getData(), clientRequest.getOffset(), clientRequestByteArray, 0, clientRequest.getLength());
                //converts bytearray to string
                String clientRequestString = new String(clientRequestByteArray);
                //prints the string
                System.out.println("Echoing: " + clientRequestString);
                String serverRequestString = "";
                //checks if the string is halt
                if (clientRequestString.equals("halt!")) {
                    //if halt then send as it is
                    serverRequestString = "halt!";
                } else {
                    //if not halt then append !
                    serverRequestString = clientRequestString + "!";
                }
                //create datagram request object
                DatagramPacket serverRequest = new DatagramPacket(serverRequestString.getBytes(),
                        serverRequestString.length(), aHost, serverPort);
                // clientSocket.send(serverRequest);
                serverSocket.send(serverRequest);
                //creates bytearray
                byte[] serverBuffer = new byte[1000];
                //creates reply datagram object
                DatagramPacket serverReply = new DatagramPacket(serverBuffer, serverBuffer.length);
                //receive reply in socket
                serverSocket.receive(serverReply);
                //create bytearray
                byte[] serverReplyByteArray = new byte[serverReply.getLength()];
                //copies the array
                System.arraycopy(serverReply.getData(), serverReply.getOffset(), serverReplyByteArray, 0, serverReply.getLength());
                //converts array to string
                String serverReplyString = new String(serverReplyByteArray);
                String clientReplyString = "";
                //checks for halt condition
                if (serverReplyString.endsWith("!")) {
                    if (serverReplyString.contains("halt")) {
                        //if halt then send halt!
                        clientReplyString = "halt!";
                    } else {
                        //if not halt then replace !
                        clientReplyString = serverReplyString.replace("!", "");
                    }
                }
                //create reply datagram object
                DatagramPacket clientReply = new DatagramPacket(clientReplyString.getBytes(),
                        clientReplyString.length(), clientRequest.getAddress(), clientRequest.getPort());
                //send reply
                clientSocket.send(clientReply);
                //prints reply
                System.out.println("Server Reply: " + serverReplyString);
                //checks if halted
                if (clientRequestString.equals("halt!")) {
                    //if yes then break
                    break;
                }
            }
            //closes the socket
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}


