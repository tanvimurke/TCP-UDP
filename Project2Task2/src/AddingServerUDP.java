/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

public class AddingServerUDP{
    //declares sum
    private static int sum = 0;
    public static void main(String args[]){
        //prints start of code
        System.out.println("The UDP server is running.");
        //initializes datagram socket
        DatagramSocket aSocket = null;
        //create bytearray buffer
        byte[] buffer = new byte[1000];

        try{
            //get port from user
            System.out.println("Enter port number (e.g. 6789)");
            Scanner sc = new Scanner(System.in);
            int port = sc.nextInt();
            //bind it to socket
            aSocket = new DatagramSocket(port);
            //create datagram requuest object
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            while(true){
                //receive request in socket
                aSocket.receive(request);
                // convert byte array to String and then parse to integer value
                String input = new String(request.getData()).trim();
                int num = Integer.parseInt(input);
                // add number to sum
                int newsum = add(num);
                //print console output
                System.out.println("Adding: " + num + " to " + (sum-num));
                System.out.println("Returning sum of " + newsum + " to client");
                //convert new sum to String and then to byte array
                String responseString = Integer.toString(newsum);
                byte[] requestArray = responseString.getBytes();
                //create bytearray request
                byte[] requestByteArray = new byte[request.getLength()];
                //copies array
                System.arraycopy(request.getData(), request.getOffset(), requestByteArray, 0, request.getLength());
                //create datagram object reply
                DatagramPacket reply = new DatagramPacket(requestArray, requestArray.length, request.getAddress(), request.getPort());
                //send reply using socket
                aSocket.send(reply);
            }
            //closes socket
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}

    }
    //add method to add number to total sum
    private static int add(int num) {
        sum += num;
        return sum;
    }
}
