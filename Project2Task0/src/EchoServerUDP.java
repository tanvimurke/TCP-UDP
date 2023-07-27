/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EchoServerUDP{
    public static void main(String args[]){
        //code from Coulouris text
        //prints start of code
        System.out.println("The UDP server is running.");
        //initialize datagram socket
        DatagramSocket aSocket = null;
        //declares byte array of buffer
        byte[] buffer = new byte[1000];
        //halt message
        String halt = "halt!";

        try{
            //get port from user
            System.out.println("Enter port number (e.g. 6789)");
            Scanner sc = new Scanner(System.in);
            int port = sc.nextInt();
            //binds socket to that port
            aSocket = new DatagramSocket(port);
            //creates datagram object
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            while(true){
                //receives packet
                aSocket.receive(request);
                //extract data into byte array
                byte[] requestByteArray = new byte[request.getLength()];
                //copies the array
                System.arraycopy(request.getData(), request.getOffset(), requestByteArray, 0, request.getLength());
                //creates datagram object for reply
                DatagramPacket reply = new DatagramPacket(requestByteArray, request.getLength(), request.getAddress(), request.getPort());
                //converts bytearray into string
                String requestString = new String(requestByteArray);
                //prints the output
                System.out.println("Echoing: "+requestString);
                //sends reply
                aSocket.send(reply);
                //if halt is entered
                if(requestString.equals(halt)){
                    //create datagram haltreply
                    DatagramPacket haltReply = new DatagramPacket(halt.getBytes(), halt.length(), request.getAddress(), request.getPort());
                    //send reply
                    aSocket.send(haltReply);
                    //if halt then prints quitting
                    System.out.println("UDP Server side quitting");
                    break;
                }
            }
            //closes the socket
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }
}
