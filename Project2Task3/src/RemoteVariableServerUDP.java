/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class RemoteVariableServerUDP{

    //initialize sum
    private static int sum = 0;
    //initialize TreeMap
    private static Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
    public static void main(String args[]){
        //start the code
        System.out.println("The UDP server is running.");
        //initialize datagram socket
        DatagramSocket aSocket = null;
        //create buffer bytearray
        byte[] buffer = new byte[1000];

        try{
            //gets port from user
            System.out.println("Enter port number (e.g. 6789)");
            Scanner sc = new Scanner(System.in);
            int port = sc.nextInt();
            //bind the port to socket
            aSocket = new DatagramSocket(port);
            //creates datagram request object
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            while(true){
                //receive request using socket
                aSocket.receive(request);
                // Parse request packet
                String requestStr = new String(request.getData(), 0, request.getLength());
                //get response from the operations method
                String responseString = doOperations(requestStr);
                //create bytearray from string
                byte[] requestArray = responseString.getBytes();
                //create bytearray
                byte[] requestByteArray = new byte[request.getLength()];
                //copies the array
                System.arraycopy(request.getData(), request.getOffset(), requestByteArray, 0, request.getLength());
                //creates datagram object
                DatagramPacket reply = new DatagramPacket(requestArray, requestArray.length,
                        request.getAddress(), request.getPort());
                //converts bytearray to string
                String requestString = new String(requestByteArray);
                //sends reply using socket
                aSocket.send(reply);
                //prints the output on server console
                System.out.println("Request: " + requestString + " Response: " + responseString);
                //sends reply using socket
                aSocket.send(reply);
            }
            //closes the socket
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }

    //operations method
    private static String doOperations(String requestStr) {
        //split the request string using ,
        String[] requests = requestStr.split(",");
        //store id
        int id = Integer.parseInt(requests[0]);
        //store operation
        String operation = requests[1];
        //store number
        int number = 0;
        if (operation.equals("get")) {
            // If operation is get return the sum for the given id
            if (map.containsKey(id)) {
                number = map.get(id);
            }
        } else {
            // If operation is add or subtract, update the sum for the given id
            number = Integer.parseInt(requests[2]);
            if (!map.containsKey(id)) {
                map.put(id, 0);
            }
            int sum = map.get(id);
            if (operation.equals("add")) {
                //performs addition
                sum += number;
            } else if (operation.equals("subtract")) {
                //performs subtraction
                sum -= number;
            }
            //insert in map
            map.put(id, sum);
        }
        //store in the response string and return it
        String responseString = Integer.toString(map.get(id));
        return responseString;
    }
}