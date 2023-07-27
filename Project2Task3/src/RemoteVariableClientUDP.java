/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class RemoteVariableClientUDP {
    public static void main(String args[]) {
        // args give message contents and server hostname
        //start the code
        System.out.println("The UDP client is running.");
        //gets port from user
        System.out.println("Enter server port number (e.g. 6789)");
        Scanner sc = new Scanner(System.in);
        //get server port
        int serverPort = sc.nextInt();
        //initialize flag
        boolean flag = true;
        while (flag) {
            //prints console output for user to choose from the menu
            System.out.println("1. Add a value to your sum.");
            System.out.println("2. Subtract a value from your sum.");
            System.out.println("3. Get your sum.");
            System.out.println("4. Exit client.");
            //gets choice from user
            Scanner sc1 = new Scanner(System.in);
            int choice = sc1.nextInt();
            //initializes id, number, operation
            int id = 0;
            int number = 0;
            String operation = "";
            //check the choice
            switch (choice) {
                case 1:
                    //get addition number from user
                    System.out.print("Enter value to add: ");
                    number = sc1.nextInt();
                    //get id
                    System.out.print("Enter your ID (between 0-999): ");
                    id = sc1.nextInt();
                    //set operation as add
                    operation = "add";
                    break;
                case 2:
                    //enter subtraction number from user
                    System.out.print("Enter value to subtract: ");
                    number = sc1.nextInt();
                    //get id
                    System.out.print("Enter your ID (between 0-999): ");
                    id = sc1.nextInt();
                    //sets subtraction
                    operation = "subtract";
                    break;
                case 3:
                    //get id
                    System.out.print("Enter your ID (between 0-999): ");
                    id = sc1.nextInt();
                    //set operation as get
                    operation = "get";
                    break;
                case 4:
                    //exit
                    flag = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
            String requestString = "";
            if (!operation.equals("get")) {
                //if operation is add or subtract then pass id, operation, operand
                requestString = id + "," + operation + "," + number;
            } else {
                //if operation is get then pass id, opration
                requestString = id + "," + operation;
            }
            //get sum from the connection method
            int sum = connection(serverPort, requestString);
            if (choice != 4) {
                //this will be printed only if choice is not exit
                System.out.println("The result is " + sum + ".");
            }
        }
    }

    //connection method that encapsulates the socket communication
    public static int connection(int serverPort, String requestString) {
        //declare datagram socket
        DatagramSocket aSocket = null;
        //initialize sum
        int sum = 0;
        try {
            //create InetAddress object
            InetAddress aHost = InetAddress.getByName("localhost");
            //create socket
            aSocket = new DatagramSocket();
            //create bytearray request
            byte[] requestStringBuffer = requestString.getBytes();
            //create request datagram
            DatagramPacket request = new DatagramPacket(requestStringBuffer, requestStringBuffer.length, aHost, serverPort);
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
            //closes the socket
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